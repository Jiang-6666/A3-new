package game.items.clothing;

import edu.monash.fit2099.engine.items.Item;
import game.weather.WeatherShield;

/**
 * Base class for weather-protective coats/jackets.
 *
 * <p>This is an {@link Item}, so it satisfies the engine's item system, and also a
 * {@link WeatherShield}, so the weather system can "see" it via reflection.</p>
 *
 * <p>For simplicity we assume: if the actor is carrying the coat, they benefit
 * from its protection.</p>
 */
public abstract class Coat extends Item implements WeatherShield {

    /** Default display char for clothing. */
    private static final char DISPLAY_CHAR = 'w';

    /** Portable by default. */
    private static final boolean PORTABLE = true;

    /**
     * Creates a coat with the given name.
     *
     * @param name item name
     */
    protected Coat(String name) {
        super(name, DISPLAY_CHAR, PORTABLE);
    }

    /**
     * Default implementation returns full exposure (no protection). Subclasses
     * should override.
     *
     * @param weatherId id of the weather, such as "rain"
     * @return factor in [0,1]
     */
    @Override
    public double getWeatherProtection(String weatherId) {
        return 1.0;
    }
}
