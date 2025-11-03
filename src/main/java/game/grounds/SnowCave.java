package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.weather.WeatherShield;

/**
 * Temporary snow cave. Best against blizzards, weaker against other weather.
 */
public class SnowCave extends Ground implements WeatherShield {

    private static final char DISPLAY_CHAR = 'O';
    private static final String NAME = "Snow Cave";

    private static final double BLIZZARD_EXPOSURE = 0.05;
    private static final double OTHER_EXPOSURE = 0.4;

    public SnowCave() {
        super(DISPLAY_CHAR, NAME);
    }

    @Override
    public double getWeatherProtection(String weatherId) {
        if ("blizzard".equalsIgnoreCase(weatherId)) {
            return BLIZZARD_EXPOSURE;
        }
        return OTHER_EXPOSURE;
    }
}
