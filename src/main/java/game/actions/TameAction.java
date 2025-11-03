package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Player;
import game.taming.Tameable;

/**
 * Player attempts to tame an adjacent animal.
 *
 * <p>Delegates to the target via {@link Tameable#attemptTame(Player, Location)} to avoid
 * instanceof switching and keep each species' rule encapsulated.</p>
 */
public class TameAction extends Action {

    private final Actor target;
    private final String direction;

    /**
     * @param target the adjacent wildlife to tame
     * @param direction menu label (e.g., "north")
     */
    public TameAction(Actor target, String direction) {
        this.target = target;
        this.direction = direction == null ? "" : direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!(actor instanceof Player)) {
            return actor + " can't tame.";
        }
        if (!(target instanceof Tameable)) {
            return target + " cannot be tamed.";
        }
        Player player = (Player) actor;
        Location playerLoc = map.locationOf(player);
        return ((Tameable) target).attemptTame(player, playerLoc);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " tries to tame " + target + (direction.isEmpty() ? "" : " (" + direction + ")");
    }
}
