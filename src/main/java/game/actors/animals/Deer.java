package game.actors.animals;

import edu.monash.fit2099.engine.items.Item;
import game.actors.Player;
import game.behaviours.FetchAndDeliverBehaviour;
import game.behaviours.FollowPlayerBehaviour;
import game.behaviours.WanderBehaviour;
import game.items.Apple;
import game.items.Hazelnut;
import game.traits.Gatherer;

/** Deer (d) — 50 HP. Wanders by default.
 * <p>Tamable by dropping an {@link game.items.Apple} on the player's tile and using
 * {@link game.actions.TameAction}. After taming, fetches fruit/nuts and delivers to the owner,
 * then follows.</p>
 *
 *  * @author @awan0091
 */
public class Deer extends Animal implements Gatherer {
    private Item carriedItem = null;

    /**
     * Constructs a new {@code Deer} with base attributes and default wander behaviour.
     */
    public Deer() {
        super("Deer", 'd', 50, 10);
        behaviours.add(new WanderBehaviour());
    }

    // Carrying helpers (package-private for behaviours)
    public boolean isCarrying() { return carriedItem != null; }
    public void setCarriedItem(Item item) { this.carriedItem = item; }
    public Item dropCarriedItem() { Item i = carriedItem; carriedItem = null; return i; }

    @Override
    protected Class<?extends Item> getTamingItemType() { return Apple.class; }

    @Override
    protected String getTamedMessage() {
        return "You offer an Apple. The Deer is tamed and now follows you.";
    }

    @Override
    protected void onTamed(Player owner) {
        behaviours.add(new FetchAndDeliverBehaviour(owner, Apple.class, Hazelnut.class));
        behaviours.add(new FollowPlayerBehaviour(owner));
        behaviours.add(new WanderBehaviour());
    }
}
