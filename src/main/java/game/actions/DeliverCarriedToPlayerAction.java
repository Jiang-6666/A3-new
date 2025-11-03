package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.animals.Deer;
import game.actors.Player;
import edu.monash.fit2099.engine.items.Item;

/** Deer drops its carried item onto the player's tile. */
public class DeliverCarriedToPlayerAction extends Action {
    private final Player owner;

    public DeliverCarriedToPlayerAction(Player owner) {
        this.owner = owner;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!(actor instanceof Deer)) return actor + " can't deliver items.";
        Deer d = (Deer) actor;
        if (!d.isCarrying()) return d + " has nothing to deliver.";

        Item carried = d.dropCarriedItem();  // clear carried slot
        Location ownerLoc = map.locationOf(owner);
        ownerLoc.addItem(carried);
        return d + " gives " + carried + " to " + owner + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " gives carried item to " + owner;
    }
}

