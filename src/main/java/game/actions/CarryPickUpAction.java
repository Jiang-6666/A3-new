package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.animals.Deer;

/** Deer picks an item from the ground into its carried slot (no inventory access). */
public class CarryPickUpAction extends Action {
    private final Item item;
    private final Location from;

    public CarryPickUpAction(Item item, Location from) {
        this.item = item;
        this.from = from;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!(actor instanceof Deer)) return actor + " can't carry items.";
        Deer d = (Deer) actor;
        if (d.isCarrying()) return d + " is already carrying something.";
        if (!from.getItems().contains(item)) return d + " finds nothing to pick up.";

        from.removeItem(item);
        d.setCarriedItem(item);
        return d + " picks up " + item + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " picks up " + item;
    }
}
