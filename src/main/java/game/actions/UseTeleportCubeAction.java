package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.TeleportCube;
import game.teleport.TeleportDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Action to use a Teleport Cube from inventory.
 * Has a 50% chance to malfunction and teleport randomly.
 */
public class UseTeleportCubeAction extends Action {
    private final TeleportDestination destination;
    private final TeleportCube cube;
    private final Random random = new Random();

    /**
     * Constructor for UseTeleportCubeAction.
     *
     * @param destination The intended destination
     * @param cube The teleport cube being used
     */
    public UseTeleportCubeAction(TeleportDestination destination, TeleportCube cube) {
        this.destination = destination;
        this.cube = cube;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location sourceLocation = map.locationOf(actor);
        // 50% chance of malfunction
        if (random.nextBoolean()) {
            // Malfunction - teleport to random location on current map
            List<Location> possibleLocations = new ArrayList<>();
            for (int x : map.getXRange()) {
                for (int y : map.getYRange()) {
                    Location loc = map.at(x, y);
                    if (loc.canActorEnter(actor) && !loc.containsAnActor()) {
                        possibleLocations.add(loc);
                    }
                }
            }

            if (!possibleLocations.isEmpty()) {
                Location randomDest = possibleLocations.get(random.nextInt(possibleLocations.size()));
                map.moveActor(actor, randomDest);
                return actor + "'s Teleport Cube malfunctioned! They teleported from (" + sourceLocation.x() + ", " + sourceLocation.y() + ") on " + map + " to random location (" + randomDest.x() + ", " + randomDest.y() + ") on " + map + ".";
            } else {
                return actor + " uses Teleport Cube but it MALFUNCTIONS! No valid destination found!";
            }
        }

        // Normal teleportation (50% success)
        try {
            Location destLocation = destination.getLocation();
            if (map != destination.getMap()) {
                map.removeActor(actor);
                destination.getMap().addActor(actor, destLocation);
            } else {
                map.moveActor(actor, destLocation);
            }
            return actor + " teleported from (" + sourceLocation.x() + ", " + sourceLocation.y() + ") on " + map + " to (" + destLocation.x() + ", " + destLocation.y() + ") on " + destination.getMap() + " using the Teleport Cube.";
        } catch (Exception e) {
            return actor + " uses Teleport Cube but teleportation failed: " + e.getMessage();
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses Teleport Cube to teleport to (" + destination.getX() + ", " + destination.getY() + ") on " + destination.getMap();
    }
}