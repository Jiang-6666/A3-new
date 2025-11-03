package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.effects.FrostbiteEffect;

/**
 * A class representing snow on the ground.
 *
 * <p>Aligned with {@link FrostbiteEffect}: both use the same duration constant to avoid
 * magic numbers and reduce connascence-by-literal.</p>
 */
public class Snow extends Ground {

    /** Display character for snow tiles. */
    private static final char DISPLAY_CHAR = '.';

    /** Descriptive name. */
    private static final String NAME = "Snow";

    /**
     * If standing on/near snow applies a frostbite-like effect elsewhere in the system,
     * this is the canonical duration for that effect.
     */
    public static final int FROSTBITE_DURATION_FROM_SNOW =
            FrostbiteEffect.DEFAULT_DURATION_FROM_SNOW;

    public Snow() {
        super(DISPLAY_CHAR, NAME);
    }
}
