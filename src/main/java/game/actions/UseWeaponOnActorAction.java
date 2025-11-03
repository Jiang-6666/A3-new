package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Action that lets an actor attack an adjacent target using a specific carried {@link Weapon}.
 *
 * <p>The carried {@link Item} (the weapon) provides this action to attack a concrete adjacent
 * {@link Actor} target.</p>
 *
 * <p><b>Engine untouched:</b> This class adapts to the engine API without changes.</p>
 */
public class UseWeaponOnActorAction extends Action {
    private final Weapon weapon;
    private final Item weaponItem;
    private final Actor target;
    private final String directionText;

    /**
     * Creates a melee attack action against a single adjacent target using the given weapon.
     *
     * @param weapon the weapon logic used to perform the attack
     * @param weaponItem the carried item representing the weapon (for menu text)
     * @param target the adjacent target actor
     * @param directionText optional direction text for menu display (e.g., "N")
     */
    public UseWeaponOnActorAction(
            Weapon weapon, Item weaponItem, Actor target, String directionText) {
        this.weapon = weapon;
        this.weaponItem = weaponItem;
        this.target = target;
        this.directionText = directionText == null ? "" : directionText;
    }

    /**
     * Executes the attack by delegating to {@link Weapon#attack(Actor, Actor, GameMap)}.
     *
     * @param actor the attacker (owner of the weapon)
     * @param map the map where the action occurs
     * @return a result string describing the outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return weapon.attack(actor, target, map);
    }

    /**
     * Describes the action for menus.
     *
     * @param actor the actor performing the action
     * @return a human-readable menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        String itemName = weaponItem == null ? "weapon" : weaponItem.toString();
        return "Attack " + target + " with " + itemName + (directionText.isEmpty() ? "" : " (" + directionText + ")");
    }
}
