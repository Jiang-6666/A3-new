package game.actors.animals;

import edu.monash.fit2099.engine.items.Item;
import game.actors.Player;
import game.behaviours.*;
import game.items.Meat;
import game.traits.Predator;

/** Wolf (e) — 100 HP. Hostile by default; {@code bites} for 50 damage at 50% hit chance.
 * <p>Tamable by dropping {@link game.items.Meat} on the player's tile and using
 * {@link game.actions.TameAction}. Once tamed, follows and defends the owner.</p>
 *
 * @author @awan0091
 */
public class Wolf extends Animal implements Predator {

    private static final int DMG = 50;
    private static final int HIT = 50;
    private static final String VERB = "bites";

    /**
     * Constructs a new {@code Wolf} with base attributes and default behaviours.
     */
    public Wolf() {
        super("Wolf", 'e', 100, 25);
        behaviours.add(new AttackBehaviour(DMG, HIT, VERB));
        behaviours.add(new WanderBehaviour());
    }

    @Override
    protected Class<?extends Item> getTamingItemType() { return Meat.class; }

    @Override
    protected String getTamedMessage() {
        return "You offer Meat. The Wolf is tamed and now follows you.";
    }

    @Override
    protected void onTamed(Player owner) {
        behaviours.add(new DefendPlayerBehaviour(DMG, HIT, VERB));
        behaviours.add(new FollowPlayerBehaviour(owner));
        behaviours.add(new WanderBehaviour());
    }
}