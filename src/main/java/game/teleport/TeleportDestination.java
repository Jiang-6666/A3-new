package game.teleport;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a teleportation destination with coordinates and description.
 * Used by all teleportation methods to specify target locations.
 */
public class TeleportDestination {
    private final GameMap targetMap;
    private final int x;
    private final int y;
    private final String description;

    /**
     * Creates a new teleport destination.
     *
     * @param targetMap The map containing the destination
     * @param x X-coordinate on the target map
     * @param y Y-coordinate on the target map
     * @param description Human-readable description of the destination
     */
    public TeleportDestination(GameMap targetMap, int x, int y, String description) {
        this.targetMap = targetMap;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    /**
     * Get the actual Location object for this destination.
     *
     * @return Location at the specified coordinates on the target map
     */
    public Location getLocation() {
        return targetMap.at(x, y);
    }

    /**
     * Get the map containing this destination.
     *
     * @return The target GameMap
     */
    public GameMap getMap() {
        return targetMap;
    }

    /**
     * Get the human-readable description of this destination.
     *
     * @return Description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the x-coordinate of this destination.
     *
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y-coordinate of this destination.
     *
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return description + " at (" + x + ", " + y + ") on " + targetMap;
    }
}