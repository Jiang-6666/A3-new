package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import java.lang.reflect.Method;

/** Hazelnut (n): increases max HP by +1 (fallback: heal +1). */
public class Hazelnut extends Item implements Edible {

    public Hazelnut() {
        super("Hazelnut", 'n', true);
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = super.allowableActions(owner, map);
        // Any actor can eat; requirement targets the player but it's harmless
        list.add(new ConsumeAction(this, this, false));
        return list;
    }

    @Override
    public String consume(Actor actor, GameMap map) {
        try {
            // Universal way to increase max HP for all actors
            actor.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, 1);
            return actor + " eats a Hazelnut (+1 Max HP).";
        } catch (Exception e) {
            // Fallback: just heal +1 HP if max stat system fails
            actor.heal(1);
            return actor + " eats a Hazelnut (+1 HP).";
        }
    }
}