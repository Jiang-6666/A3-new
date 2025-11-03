package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.weapons.BareFist;
import game.items.Bedroll;
import game.items.Bottle;


/** The controllable Explorer character.
 *
 * <p>Tracks hit points, hydration and warmth. Hydration and warmth decrease by one each
 * turn; the game ends when either reaches zero. The player can sleep on a dropped
 * {@link game.items.Bedroll} and drink from a {@link game.items.Bottle}.</p>
 */

public class Player extends Actor {

    // Survival stats (REQ1)
    private int hydration = 20;
    private final int maxHydration = 20;
    private int warmth = 30;
    private final int maxWarmth = 30;

    // Sleep state
    private int sleepTurnsRemaining = 0;

    /** Creates the Explorer with 100 HP, bare fist, bedroll and bottle. */

    public Player() {
        super("Explorer", 'ඞ', 100);
        this.setIntrinsicWeapon(new BareFist());
        this.addItemToInventory(new Bedroll());
        this.addItemToInventory(new Bottle());
    }

    // ---------- Helpers used by actions/items ----------

    /** Begin sleeping for the given number of turns; pauses survival decay. */
    public void startSleeping(int turns) {
        if (turns > 0) {
            sleepTurnsRemaining = Math.max(sleepTurnsRemaining, turns);
        }
    }

    /** Increase hydration by amount (clamped to max). */
    public void increaseHydration(int amount) {
        if (amount > 0) {
            hydration = Math.min(maxHydration, hydration + amount);
        }
    }

    /** Increase warmth by amount (clamped to max). Not required but handy. */
    public void increaseWarmth(int amount) {
        int newWarmth = Math.min(maxWarmth, warmth + amount);
        warmth = Math.max(0, newWarmth);
    }

    // ---------- Turn logic ----------

    /**
     * Do survival updates and show the stats banner, then present the menu.
     * We update survival here to avoid overriding an engine-specific tick signature.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Survival update once per player turn
        if (sleepTurnsRemaining > 0) {
            sleepTurnsRemaining--;
        } else {
            hydration = Math.max(0, hydration - 1);
            warmth = Math.max(0, warmth - 1);
        }

        // Player is unconscious
        if (hydration == 0 || warmth == 0) {
            display.println(String.format(
                    "[HP: %d]  [Hydration: %d/%d]  [Warmth: %d/%d]",
                    this.getAttribute(BaseAttributes.HEALTH), hydration, maxHydration, warmth, maxWarmth));
            display.println("Explorer cannot survive any longer...");
            map.removeActor(this);
            System.out.println("Game Over.");
            System.exit(0);
        }

        // Stats banner
        int hp = this.getAttribute(BaseAttributes.HEALTH);
        display.println(String.format(
                "[HP: %d]  [Hydration: %d/%d]  [Warmth: %d/%d] %s",
                hp, hydration, maxHydration, warmth, maxWarmth,
                (sleepTurnsRemaining > 0 ? "(sleeping " + sleepTurnsRemaining + ")" : "")));

        // continue multi-turn actions if any
        if (lastAction != null && lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }
        return new Menu(actions).showMenu(this, display);
    }
}