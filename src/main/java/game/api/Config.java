package game.api;

/**
 * REQ5: Centralizes API keys and configuration, read from environment variables.
 * Strictly adheres to the "DO NOT commit API keys" rule.
 */
public class Config {

    /**
     * Fetches the OpenWeatherMap API key from the "OWM_API_KEY" environment variable.
     * @return The API key, or null if not set.
     */
    public static String getOwmApiKey() {
        // e.g., "ab123cde456..."
        return System.getenv("OWM_API_KEY");
    }

    /**
     * Fetches the Google Cloud Project ID from the "GEMINI_PROJECT_ID" environment variable.
     * @return The Project ID, or null if not set.
     */
    public static String getGeminiProjectId() {
        // e.g., "my-game-project-12345"
        return System.getenv("GEMINI_PROJECT_ID");
    }

    /**
     * Fetches the Google Cloud Location from the "GEMINI_LOCATION" environment variable.
     * @return The Location (e.g., "us-central1"), or null if not set.
     */
    public static String getGeminiLocation() {
        // e.g., "us-central1"
        return System.getenv("GEMINI_LOCATION");
    }

    /**
     * Fetches the Gemini Model Name from the "GEMINI_MODEL_NAME" environment variable.
     * @return The Model Name (e.g., "gemini-1.5-flash-001"), or null if not set.
     */
    public static String getGeminiModelName() {
        // e.g., "gemini-1.5-flash-001"
        return System.getenv("GEMINI_MODEL_NAME");
    }

    /**
     * The default city to query for the OpenWeatherMap API.
     * (e.g., your TA's city)
     */
    public static final String WEATHER_LOCATION_CITY = "Kuala Lumpur";

    /**
     * The default country code for the city query.
     */
    public static final String WEATHER_LOCATION_COUNTRY = "MALAYSIA";
}
