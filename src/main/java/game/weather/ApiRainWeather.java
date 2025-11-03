package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;

/**
 * REQ5: API-driven "Rain" effect (LSP).
 * - Implements "Dynamic Warmth" from ApiWeatherBase.
 * - Adds a base warmth penalty for being wet.
 * - Mitigates hydration loss.
 * - Implements "applyToGround" to douse Fire tiles.
 */
public class ApiRainWeather extends ApiWeatherBase {

    public ApiRainWeather(double temperature, String description) {
        super(temperature, description, "api_rain", "Rain");
    }

    /**
     * Rain makes you cold, regardless of temperature.
     * This is *added* to the temperature-based delta.
     */
    @Override
    public int getBaseWarmthDelta() {
        return -1; // Base penalty for being wet
    }

    /**
     * Rain mitigates dehydration.
     */
    @Override
    public int getBaseHydrationDelta() {
        return 1; // Gain 1 hydration (or slow decay)
    }

    /**
     * REQ5 Logic: "Rain will extinguish fires faster"
     * This is called by WeatherController.tick() for every tile.
     */
    @Override
    public void applyToGround(Location location, GameMap map) {
        // Check if the ground is Fire
        if (location.getGround() instanceof Fire) {
            // Call the douse() method we added to Fire.java
            ((Fire) location.getGround()).douse(1);
        }
    }
}
