package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.weather.WeatherShield;

/**
 * A simple shelter / windbreak ground.
 *
 * <p>Standing on this tile makes the actor almost fully protected from
 * all defined weathers.</p>
 */
public class StormShelter extends Ground implements WeatherShield {

    private static final char DISPLAY_CHAR = 'H';
    private static final String NAME = "Storm Shelter";

    /** We just cap at 0.1 exposure for everything. */
    private static final double SHELTER_EXPOSURE = 0.1;

    public StormShelter() {
        super(DISPLAY_CHAR, NAME);
    }

    @Override
    public double getWeatherProtection(String weatherId) {
        // treat all weathers the same for now
        return SHELTER_EXPOSURE;
    }
}
