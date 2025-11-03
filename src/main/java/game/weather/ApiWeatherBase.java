package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * REQ5: Abstract base class for API-driven weather effects (OCP).
 * Stores API-provided data (temp, description) and implements the
 * common "Dynamic Warmth" logic based on real-world temperature.
 */
public abstract class ApiWeatherBase implements WeatherEffect {

    protected final double temperature;
    protected final String description;
    private final String id;
    private final String name;

    /**
     * Base constructor for all API weather types.
     * @param temperature The real-world temperature (Celsius).
     * @param description The real-world weather description (e.g., "light rain").
     * @param id The game's internal ID (e.g., "api_rain").
     * @param name The game's display name (e.g., "Rain").
     */
    public ApiWeatherBase(double temperature, String description, String id, String name) {
        this.temperature = temperature;
        this.description = description;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getDisplayName() {
        // e.g., "Rain (10.5°C)"
        return String.format("%s (%.1f°C)", name, temperature);
    }

    /**
     * Gets the raw description from the API (e.g., "light rain").
     * @return The weather description.
     */
    public String getDescription() { return description; }

    /**
     * Gets the raw temperature from the API.
     * @return The temperature in Celsius.
     */
    public double getTemperature() { return temperature; }

    @Override
    public boolean isApiControlled() {
        return true; // Mark this as an API-controlled weather
    }

    /**
     * REQ5 Core Logic: "Dynamic Warmth"
     * Applies warmth/hydration changes based on real-world temperature.
     */
    @Override
    public void applyToActor(Actor actor, GameMap map, ExposureCalculator exposureCalculator) {
        // 1. Calculate temperature-based warmth delta
        // 15°C is considered "neutral" (no change)
        int tempWarmthDelta = 0;
        if (temperature < 5.0) {
            tempWarmthDelta = -2; // Severe cold
        } else if (temperature < 15.0) {
            tempWarmthDelta = -1; // Mild cold
        } else if (temperature > 25.0) {
            tempWarmthDelta = 1;  // Hot (slows warmth decay)
        }

        // 2. Get base deltas from the concrete class (e.g., rain adds -1 warmth)
        int baseWarmthDelta = getBaseWarmthDelta();
        int baseHydrationDelta = getBaseHydrationDelta();

        // 3. Use REQ3/4's ExposureCalculator to get the final factor
        double factor = exposureCalculator.computeExposureFor(actor, this);

        // 4. Combine deltas and apply
        int finalWarmthDelta = (int) Math.round((baseWarmthDelta + tempWarmthDelta) * factor);
        int finalHydrationDelta = (int) Math.round(baseHydrationDelta * factor);

        WeatherReflection.applyWarmthDelta(actor, finalWarmthDelta);
        WeatherReflection.applyHydrationDelta(actor, finalHydrationDelta);
    }

    // getBaseWarmthDelta() and getBaseHydrationDelta() must be implemented by children.
    // applyToGround() is optional.
}
