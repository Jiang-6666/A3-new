package game.items.clothing;

/**
 * A coat that offers decent protection against all three weathers.
 *
 * <p>Sample mapping:
 * <ul>
 *     <li>rain → 0.4 (60% blocked)</li>
 *     <li>wind → 0.5</li>
 *     <li>blizzard → 0.5</li>
 * </ul>
 * </p>
 */
public class AllWeatherParka extends Coat {

    private static final String NAME = "All-Weather Parka";

    /** Protection constants. */
    private static final double RAIN_PROTECTION = 0.4;
    private static final double WIND_PROTECTION = 0.5;
    private static final double BLIZZARD_PROTECTION = 0.5;

    public AllWeatherParka() {
        super(NAME);
    }

    @Override
    public double getWeatherProtection(String weatherId) {
        if ("rain".equalsIgnoreCase(weatherId)) {
            return RAIN_PROTECTION;
        }
        if ("wind".equalsIgnoreCase(weatherId)) {
            return WIND_PROTECTION;
        }
        if ("blizzard".equalsIgnoreCase(weatherId)) {
            return BLIZZARD_PROTECTION;
        }
        return 1.0;
    }
}
