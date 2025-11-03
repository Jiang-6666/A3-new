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
 * Tele-door (#) - A door that allows teleportation between and within maps.
 * Using the door burns the surroundings at the DESTINATION location.
 */
public class TeleDoor extends Ground {
    private final List<TeleportDestination> destinations = new ArrayList<>();

    /**
     * Constructor for TeleDoor.
     */
    public TeleDoor() {
        super('#', "Tele-door");
    }

    /**
     * Add a destination to this Tele-door.
     *
     * @param destination The destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns available teleport actions when actor is on this door.
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
            actions.add(new TeleportAction(dest, "Tele-door", true, false, location));
        }
        return actions;
    }

    /**
     * Actors can enter/stand on the Tele-door.
     *
     * @param actor the Actor to check
     * @return true (always enterable)
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Get list of destinations for this door.
     *
     * @return List of TeleportDestination objects
     */
    public List<TeleportDestination> getDestinations() {
        return new ArrayList<>(destinations);
    }
}