package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CarryPickUpAction;
import game.actions.DeliverCarriedToPlayerAction;
import game.actors.animals.Deer;
import game.actors.Player;

import java.util.Arrays;
import java.util.List;

/**
 * For tamed gatherers (Deer):
 * 1) If carrying and adjacent to owner, deliver to owner's tile.
 * 2) Else if an item of interest is on current tile, pick it up into carried slot.
 * 3) Else if an item of interest is adjacent, move there.
 * 4) Otherwise, let other behaviours (e.g., Follow) act.
 */
public class FetchAndDeliverBehaviour implements Behaviour {

    private final Player owner;
    private final List<Class<?>> wanted;

    public FetchAndDeliverBehaviour(Player owner, Class<?>... wantedItemClasses) {
        this.owner = owner;
        this.wanted = Arrays.asList(wantedItemClasses);
    }

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        if (!(actor instanceof Deer)) return null;
        Deer deer = (Deer) actor;
        if (owner == null || !map.contains(owner)) return null;

        Location here = map.locationOf(deer);
        Location ownerLoc = map.locationOf(owner);

        // 1) Deliver if carrying and adjacent
        if (deer.isCarrying() && isAdjacent(here, ownerLoc)) {
            return new DeliverCarriedToPlayerAction(owner);
        }

        // 2) Pick up from current tile
        if (!deer.isCarrying()) {
            for (Item it : here.getItems()) {
                if (isWanted(it)) {
                    return new CarryPickUpAction(it, here);
                }
            }
        }

        // 3) Move toward adjacent tile that has a wanted item
        for (Exit e : here.getExits()) {
            Location dst = e.getDestination();
            for (Item it : dst.getItems()) {
                if (isWanted(it)) {
                    return new MoveActorAction(dst, e.getName());
                }
            }
        }

        return null;
    }

    private boolean isWanted(Item it) {
        for (Class<?> c : wanted) if (c.isInstance(it)) return true;
        return false;
    }

    private boolean isAdjacent(Location a, Location b) {
        int dx = Math.abs(a.x() - b.x());
        int dy = Math.abs(a.y() - b.y());
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }
}
