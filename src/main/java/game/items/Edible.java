package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Items that can be eaten implement this and apply their effect on consume. */
public interface Edible {
    /**
     * Apply this consumable's effect to the actor, and return a result string.
     * The item should be removed from inventory by the action after consumption.
     */
    String consume(Actor actor, GameMap map);
}
