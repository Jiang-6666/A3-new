package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.actors.Player;

/**
 * A bottle (o) with 5 sips; each sip restores 4 hydration.
 */
public class Bottle extends Item {

    private int sips = 5;

    /** Creates a portable bottle displayed as 'o'. */
    public Bottle() {
        super("Bottle", 'o', true);
    }

    /** Remaining sips. */
    public int getSips() {
        return sips;
    }

    /** Consume one sip if available. */
    public void consumeSip() {
        if (sips > 0) {
            sips--;
        }
    }

    /**
     * When carried by the Player and sips remain, offer DrinkAction.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = new ActionList();
        if (owner instanceof Player && sips > 0) {
            list.add(new ConsumeAction(this));
        }
        return list;
    }
}

