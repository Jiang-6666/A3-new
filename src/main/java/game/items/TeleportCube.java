package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.UseTeleportCubeAction; // Make sure this import exists
import game.teleport.TeleportDestination;
import java.util.ArrayList;
import java.util.List;

/**
 * Teleport cube (□) - A mysterious portable item for teleportation.
 * Can only be used when held in inventory.
 * Has a 50% chance to malfunction and teleport to a random location.
 */
public class TeleportCube extends Item {
    private final List<TeleportDestination> destinations = new ArrayList<>();

    /**
     * Constructor for TeleportCube.
     */
    public TeleportCube() {
        super("Teleport cube", '□', true); // portable = true
    }

    /**
     * Add a destination to this teleport cube.
     *
     * @param destination The destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns available actions when this cube is in actor's inventory.
     * Note: UseTeleportCubeAction will be implemented in next commit.
     *
     * @param owner The actor holding the item
     * @param map The map the actor is on
     * @return list of actions to use the cube for teleportation
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        // UPDATED: Now properly adds UseTeleportCubeAction
        for (TeleportDestination dest : destinations) {
            actions.add(new UseTeleportCubeAction(dest, this));
        }
        return actions;
    }

    /**
     * Get list of destinations for this cube.
     *
     * @return List of TeleportDestination objects
     */
    public List<TeleportDestination> getDestinations() {
        return new ArrayList<>(destinations);
    }
}