package game.disease;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract ground that can provide disease treatment to actors standing on it.
 *
 * <p>Subclasses (e.g. {@link Campfire}, {@link SteamHut}) override {@link #tick(Location)}
 * to attach the right therapy status to actors on this tile.</p>
 */
public abstract class TreatmentStation extends Ground {

    /**
     * Creates a treatment station with the given display char and name.
     *
     * @param displayChar map char
     * @param name        ground name
     */
    protected TreatmentStation(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Default tick does nothing; subclasses will add therapy statuses to actors.
     *
     * @param location location of this ground
     */
    @Override
    public void tick(Location location) {
        // default: no-op
    }
}
