package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction; // Make sure this import exists
import game.teleport.TeleportDestination;
import java.util.ArrayList;
import java.util.List;

/**
 * Teleportation circle (O) - A mystical circle drawn on the ground.
 * Using the circle burns one random surrounding location at the SOURCE.
 */
public class TeleportationCircle extends Ground {
    private final List<TeleportDestination> destinations = new ArrayList<>();

    /**
     * Constructor for TeleportationCircle.
     */
    public TeleportationCircle() {
        super('O', "Teleportation circle");
    }

    /**
     * Add a destination to this teleportation circle.
     *
     * @param destination The destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns available teleport actions when actor is on this circle.
     * Note: TeleportAction will be implemented in next commit.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return list of teleport actions to various destinations
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        // UPDATED: Now properly adds TeleportAction
        for (TeleportDestination dest : destinations) {
            actions.add(new TeleportAction(dest, "Teleportation circle", false, true, location));
        }
        return actions;
    }

    /**
     * Actors can enter/stand on the teleportation circle.
     *
     * @param actor the Actor to check
     * @return true (always enterable)
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Get list of destinations for this circle.
     *
     * @return List of TeleportDestination objects
     */
    public List<TeleportDestination> getDestinations() {
        return new ArrayList<>(destinations);
    }
}