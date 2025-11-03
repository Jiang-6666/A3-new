package game.weather;

/**
 * REQ5: API-driven "Clear" effect (LSP).
 * This is the default/fallback weather.
 * - Implements "Dynamic Warmth" from ApiWeatherBase.
 * - Has no base penalties for warmth or hydration.
 * - Has no ground effects.
 */
public class ApiClearWeather extends ApiWeatherBase {

    public ApiClearWeather(double temperature, String description) {
        super(temperature, description, "api_clear", "Clear");
    }

    /**
     * Clear weather has no *base* warmth penalty.
     * All warmth changes will come purely from the real-world temperature.
     */
    @Override
    public int getBaseWarmthDelta() {
        return 0;
    }

    /**
     * Clear weather has no *base* hydration effect.
     * (You might set this to -1 if your game has default hydration decay)
     */
    @Override
    public int getBaseHydrationDelta() {
        return 0;
    }

    // No applyToGround() override is needed.
}
