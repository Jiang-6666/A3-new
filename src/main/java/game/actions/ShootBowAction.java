package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.weapons.Bow;

/**
 * Action that performs a ranged bow shot up to 3 tiles away (Manhattan distance).
 *
 * <p><b>REQ3</b>: Enables Bow to attack non-adjacent targets without changing engine code.</p>
 */
public class ShootBowAction extends Action {
    private final Bow bow;
    private final Actor target;

    /**
     * Creates a new ranged shot action.
     *
     * @param bow the bow used to attack
     * @param target the target actor to shoot at
     */
    public ShootBowAction(Bow bow, Actor target) {
        this.bow = bow;
        this.target = target;
    }

    /**
     * Executes the shot if the target is within Manhattan distance ≤ 3; otherwise returns a miss
     * message.
     *
     * @param actor the shooter
     * @param map the map of the world
     * @return a result string describing the outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location a = map.locationOf(actor);
        Location t = map.locationOf(target);
        int dist = Math.abs(a.x() - t.x()) + Math.abs(a.y() - t.y());
        if (dist > 3) {
            return actor + " tries to shoot " + target + " but they are too far.";
        }
        return bow.rangedAttack(actor, target, map);
    }

    /**
     * Describes the action for menus.
     *
     * @param actor the actor performing the action
     * @return a human-readable menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Shoot " + target + " with Bow (≤3 tiles)";
    }
}
