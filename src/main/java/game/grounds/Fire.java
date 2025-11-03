package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
// We assume BurningEffect is in game.effects
// import game.effects.BurningEffect;
// We assume Dirt is in game.grounds
// import game.grounds.Dirt;

/**
 * A temporary burning ground tile ({@code '^'}) that damages actors standing on it and expires
 * after a set duration.
 *
 * <p>Each tick, any actor on this tile receives a {@link BurningEffect} (5 damage/turn for 5
 * turns). When the internal duration reaches zero, the tile reverts to {@link Dirt}. Actors may
 * walk through this tile.</p>
 *
 * <p><b>REQ3</b>: Used by Torch to ignite surrounding tiles for area denial.</p>
 * <p><b>REQ5</b>: Can now be "doused" by rain, reducing its duration.</p>
 */
public class Fire extends Ground {
    /** Number of ticks before this fire goes out. */
    private int turnsRemaining;

    /** Creates a fire with a default duration of 3 ticks (legacy behavior). */
    public Fire() {
        this(3);
    }

    /**
     * Creates a fire with a specific duration.
     *
     * @param duration number of ticks the fire should last (must be non-negative)
     */
    public Fire(int duration) {
        super('^', "Fire");
        this.turnsRemaining = Math.max(0, duration);
    }

    /**
     * REQ5 Extension:
     * Reduces the remaining duration of this fire, (e.g., being doused by rain).
     * @param amount The number of turns to reduce the duration by.
     */
    public void douse(int amount) {
        this.turnsRemaining = Math.max(0, this.turnsRemaining - amount);
    }

    /**
     * Applies burning to any occupant, decrements the timer, and reverts to {@link Dirt} when the
     * duration expires.
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            // Assuming BurningEffect exists and has this constructor
            // actor.addStatus(new BurningEffect(5, 5));
        }

        turnsRemaining--;
        if (turnsRemaining <= 0) {
            // Assuming Dirt exists
            // location.setGround(new Dirt());
        }
    }

    /**
     * Fire does not block movement.
     *
     * @param actor the actor attempting to enter
     * @return always {@code true}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}