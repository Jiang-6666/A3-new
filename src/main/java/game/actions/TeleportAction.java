package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.teleport.TeleportDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Action to teleport using Tele-doors or Teleportation Circles.
 * Handles burning effects at source or destination based on teleport method.
 */
public class TeleportAction extends Action {
    private final TeleportDestination destination;
    private final String sourceType;
    private final boolean burnsDestination;
    private final boolean burnsSource;
    private final Location sourceLocation;
    private final Random random = new Random();

    /**
     * Constructor for TeleportAction.
     *
     * @param destination The target destination
     * @param sourceType Name of the teleportation method
     * @param burnsDestination Whether to burn destination surroundings
     * @param burnsSource Whether to burn source surroundings
     * @param sourceLocation The location of the teleportation source
     */
    public TeleportAction(TeleportDestination destination, String sourceType,
                          boolean burnsDestination, boolean burnsSource, Location sourceLocation) {
        this.destination = destination;
        this.sourceType = sourceType;
        this.burnsDestination = burnsDestination;
        this.burnsSource = burnsSource;
        this.sourceLocation = sourceLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location destLocation = destination.getLocation();
        String result = "";

        // Move actor to destination
        try {
            if (map != destination.getMap()) {
                // Cross-map teleportation
                map.removeActor(actor);
                destination.getMap().addActor(actor, destLocation);
            } else {
                // Same map teleportation
                map.moveActor(actor, destLocation);
            }
            result = actor + " teleported from (" + sourceLocation.x() + ", " + sourceLocation.y() + ") on " + map +
                    " to (" + destLocation.x() + ", " + destLocation.y() + ") on " + destination.getMap() + ".";
        } catch (Exception e) {
            return actor + " failed to teleport: " + e.getMessage();
        }

        // Handle burning effects
        if (burnsDestination) {
            burnSurroundings(destLocation);
            result += " This burns the surrounding locations of (" + destLocation.x() + ", " + destLocation.y() + ") on " + destination.getMap();
        }
        if (burnsSource) {
            Location burnedLocation = burnRandomSurrounding(sourceLocation);
            result += " A fire starts on (" + burnedLocation.x() + ", " + burnedLocation.y() + ") on " + map;
        }

        return result;
    }

    /**
     * Burn all surrounding locations.
     */
    private void burnSurroundings(Location center) {
        for (Exit exit : center.getExits()) {
            Location loc = exit.getDestination();
            loc.setGround(new Fire());
        }
    }

    /**
     * Burn one random surrounding location.
     * @return The location that was burned.
     */
    private Location burnRandomSurrounding(Location center) {
        List<Location> surroundings = new ArrayList<>();
        for (Exit exit : center.getExits()) {
            surroundings.add(exit.getDestination());
        }
        if (!surroundings.isEmpty()) {
            Location toBurn = surroundings.get(random.nextInt(surroundings.size()));
            toBurn.setGround(new Fire());
            return toBurn;
        }
        return center;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses " + sourceType + " to teleport to (" + destination.getX() + ", " + destination.getY() + ") on " + destination.getMap();
    }
}