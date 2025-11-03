package game.api;

/**
 * REQ5: Interface (ISP, DIP) for a narrative API service.
 * Responsible for generating dialogue for NPCs.
 */
public interface DialogueService {
    /**
     * Generates a cryptic monologue for the Storm Seer based on the current weather.
     *
     * @param weatherDescription A brief weather description (e.g., "Light Rain")
     * @param temperature The current temperature (Celsius)
     * @return An AI-generated monologue, or a default fallback message on failure.
     */
    String getStormSeerMonologue(String weatherDescription, double temperature);
}
