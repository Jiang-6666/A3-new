package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.effects.PoisonedEffect;

/**
 * Yew Berry (x): a poisonous berry.
 *
 * <p>Aligned with {@link PoisonedEffect}: eating the berry now applies the same DoT that
 * is used in weapon coatings, instead of hard-coded instant death. This removes the
 * "no relationship" smell between the item and the effect.</p>
 */
public class YewBerry extends Item implements Edible {

    /** Display character for the yew berry. */
    private static final char DISPLAY_CHAR = 'x';

    /** Whether the item is portable. */
    private static final boolean PORTABLE = true;

    public YewBerry() {
        super("Yew Berry", DISPLAY_CHAR, PORTABLE);
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = super.allowableActions(owner, map);
        // still edible, but no longer "eat = die instantly"
        list.add(new ConsumeAction(this, this, false));
        return list;
    }

    @Override
    public String consume(Actor actor, GameMap map) {
        // apply the standard "yewberry poison" profile
        actor.addStatus(new PoisonedEffect(
                PoisonedEffect.DEFAULT_DAMAGE_FROM_YEWBERRY,
                PoisonedEffect.DEFAULT_DURATION_FROM_YEWBERRY
        ));
        return actor + " eats a Yew Berry and is poisoned.";
    }
}
