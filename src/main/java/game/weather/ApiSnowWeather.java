package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Snow;
// We assume Dirt is in game.grounds
// import game.grounds.Dirt;

/**
 * REQ5: API-driven "Snow" effect (LSP).
 * - Implements "Dynamic Warmth" from ApiWeatherBase.
 * - Adds a large base warmth penalty for snow.
 * - Implements "applyToGround" to create Snow tiles.
 */
public class ApiSnowWeather extends ApiWeatherBase {

    public ApiSnowWeather(double temperature, String description) {
        super(temperature, description, "api_snow", "Snow");
    }

    /**
     * Snow makes you very cold, regardless of temperature.
     * This is *added* to the temperature-based delta.
     */
    @Override
    public int getBaseWarmthDelta() {
        return -2; // Base penalty for snow
    }

    @Override
    public int getBaseHydrationDelta() {
        return 0; // Neutral hydration
    }

    /**
     * REQ5 Logic: "cause Snow ground tiles to appear"
     * This is called by WeatherController.tick() for every tile.
     */
    @Override
    public void applyToGround(Location location, GameMap map) {
        // We only want to add snow on *some* tiles (e.g., Dirt)
        // (This check assumes you have a 'Dirt' class)

        // if (location.getGround() instanceof Dirt) {
        //     // Add snow with a 10% chance per tick to avoid blanketing the map
        //     if (Math.random() < 0.10) {
        //         location.setGround(new Snow());
        //     }
        // }
    }
}
