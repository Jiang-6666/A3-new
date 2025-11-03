package game.weather;

/**
 * Describes something that can reduce the effect of certain weathers.
 *
 * <p>We do NOT check this with {@code instanceof}; instead, code will duck-type by
 * attempting to call {@code getWeatherProtection(String)} via reflection.</p>
 */
public interface WeatherShield {

    /**
     * Returns a protection factor for the given weather id.
     *
     * <p>1.0 means "no protection", 0.0 means "fully protected". Typical values
     * are between 0.2 and 0.8.</p>
     *
     * @param weatherId id such as "rain", "wind", "blizzard"
     * @return protection factor in [0, 1], where smaller is better
     */
    double getWeatherProtection(String weatherId);
}
