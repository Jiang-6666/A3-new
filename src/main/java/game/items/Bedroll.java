package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.SleepAction;
import game.actors.Player;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A bedroll (=). Must be dropped on the ground to be used; offers SleepAction at the Player's tile.
 */
public class Bedroll extends Item {

    /** Creates a portable bedroll displayed as '='. */
    public Bedroll() {
        super("Bedroll", '=', true);
    }

    /**
     * When on the ground at a location containing the Player, offer SleepAction (6–10 turns).
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList list = new ActionList();
        if (location.containsAnActor() && location.getActor() instanceof Player) {
            int turns = ThreadLocalRandom.current().nextInt(6, 11); // [6,10]
            list.add(new SleepAction(turns));
        }
        return list;
    }

    /**
     * When carried, bedroll cannot be used (must be dropped first).
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList();
    }
}

