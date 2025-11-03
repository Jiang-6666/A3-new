package game.taming;

import edu.monash.fit2099.engine.positions.Location;
import game.actors.Player;

/**
 * Contract for wildlife that can be tamed by the player.
 *
 * <p>Implementations decide the exact food/conditions needed. To keep the engine
 * unmodified and to avoid inventory APIs, the player’s {@link Location} is provided so
 * implementations can check food dropped on the ground at the player's tile.</p>
 */
public interface Tameable {

    /**
     * Attempt to tame this actor.
     *
     * @param owner          the player attempting to tame
     * @param playerLocation the player's current location (food is dropped here)
     * @return human-readable result (success or failure reason)
     */
    String attemptTame(Player owner, Location playerLocation);

    /**
     * Whether this actor has already been tamed.
     *
     * @return true if tamed
     */
    boolean isTamed();
}
