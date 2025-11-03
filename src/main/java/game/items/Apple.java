package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.actors.Player;

/** Apple (a): heal +3 HP and +2 Hydration when eaten. */
public class Apple extends Item implements Edible {

    public Apple() {
        super("Apple", 'a', true); // portable
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = super.allowableActions(owner, map);
        list.add(new ConsumeAction(this, this, false));
        return list;
    }

    @Override
    public String consume(Actor actor, GameMap map) {
        // Heal +3
        actor.heal(3);
        // Hydration +2 (only for our Player which has hydration stat)
        if (actor instanceof Player) {
            ((Player) actor).increaseHydration(2);
        }
        return actor + " eats an Apple (+3 HP, +2 Hydration).";
    }
}
