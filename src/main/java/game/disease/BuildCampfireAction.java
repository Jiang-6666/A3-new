package game.disease;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Builds a {@link Campfire} on the actor's current tile by consuming one {@link WoodBundle}.
 */
public class BuildCampfireAction extends Action {

    /** Fully qualified class name for the required wood item. */
    private static final String WOOD_CLASS = "game.disease.WoodBundle";

    @Override
    public String execute(Actor actor, GameMap map) {
        Item wood = findWood(actor);
        if (wood == null) {
            return actor + " has no wood to build a campfire.";
        }

        actor.removeItemFromInventory(wood);

        Location here = map.locationOf(actor);
        here.setGround(new Campfire());
        return actor + " builds a campfire.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Build a campfire (consume wood)";
    }

    @Override
    public String hotkey() {
        return "";
    }

    /**
     * Find a WoodBundle in the actor inventory (by class name, no instanceof).
     */
    private Item findWood(Actor actor) {
        List<Item> items = readInventoryByReflection(actor);
        if (items == null) {
            return null;
        }
        for (Item it : items) {
            // compare by fully qualified class name
            if (it != null && WOOD_CLASS.equals(it.getClass().getName())) {
                return it;
            }
        }
        return null;
    }

    /**
     * Calls actor.getInventory() via reflection and trusts the result to be a List.
     * No instanceof is used.
     */
    @SuppressWarnings("unchecked")
    private List<Item> readInventoryByReflection(Actor actor) {
        try {
            Method m = actor.getClass().getMethod("getInventory");
            Object result = m.invoke(actor);
            // engine's Player returns List<Item>, so we just cast
            return (List<Item>) result;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
