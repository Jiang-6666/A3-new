package game.actors.animals;

import edu.monash.fit2099.engine.items.Item;
import game.actors.Player;
import game.behaviours.AttackBehaviour;
import game.behaviours.DefendPlayerBehaviour;
import game.behaviours.FollowPlayerBehaviour;
import game.behaviours.WanderBehaviour;
import game.items.Meat;
import game.traits.Predator;

/** Bear (B) — 200 HP. Hostile by default; {@code claws} for 75 damage at 80% hit chance.
 * <p>Tamable by dropping {@link game.items.Meat} on the player's tile and using
 * {@link game.actions.TameAction}. Once tamed, follows and defends the owner.</p>
 *
 * @author @awan0091
 */
public class Bear extends Animal implements Predator {

    private static final int DMG = 75;
    private static final int HIT = 80;
    private static final String VERB = "claws";

    /**
     * Constructs a new {@code Bear} with base attributes and default behaviours.
     */
    public Bear() {
        super("Bear", 'B', 200, 50);
        behaviours.add(new AttackBehaviour(DMG, HIT, VERB));
        behaviours.add(new WanderBehaviour());
    }

    @Override
    protected Class<?extends Item> getTamingItemType() { return Meat.class; }

    @Override
    protected String getTamedMessage() {
        return "You offer Meat. The Bear is tamed and now follows you.";
    }

    @Override
    protected void onTamed(Player owner) {
        behaviours.add(new DefendPlayerBehaviour(DMG, HIT, VERB));
        behaviours.add(new FollowPlayerBehaviour(owner));
        behaviours.add(new WanderBehaviour());
    }
}
