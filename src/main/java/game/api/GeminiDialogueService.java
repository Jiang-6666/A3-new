package game.api;

// Import the required Google Cloud Vertex AI libraries
// Ensure these are in your pom.xml
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;

/**
 * REQ5: Implementation of DialogueService using the Google Gemini (Vertex AI) API.
 * Adheres to SRP: This class's only job is to contact Gemini.
 */
public class GeminiDialogueService implements DialogueService {

    private final String projectId;
    private final String location;
    private final String modelName;

    /**
     * Constructor. Loads configuration from the Config class.
     */
    public GeminiDialogueService() {
        this.projectId = Config.getGeminiProjectId();
        this.location = Config.getGeminiLocation();
        this.modelName = Config.getGeminiModelName();

        if (projectId == null || location == null || modelName == null) {
            System.err.println("REQ5 WARNING: Gemini API environment variables (PROJECT_ID, LOCATION, MODEL_NAME) are not set. Dialogue service will use fallbacks.");
        }
    }

    @Override
    public String getStormSeerMonologue(String weatherDescription, double temperature) {
        // Fallback if API is not configured
        if (projectId == null) {
            return getDefaultMonologue(weatherDescription);
        }

        // Try-with-resources to manage the VertexAI client
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);

            // The prompt sent to the AI
            String prompt = String.format(
                    "You are a mystical, cryptic Storm Seer. You speak in short (1-2 sentence), poetic prophecies. " +
                            "Generate a prophecy about the current weather, which is: %s, %.1f°C.",
                    weatherDescription, temperature
            );

            GenerateContentResponse response = model.generateContent(prompt);
            return ResponseHandler.getText(response);

        } catch (IOException e) {
            System.err.println("REQ5 ERROR: Failed to call Gemini API: " + e.getMessage());
            return getDefaultMonologue(weatherDescription);
        } catch (Exception e) {
            // Catch other potential runtime errors (e.g., auth failure)
            System.err.println("REQ5 CRITICAL ERROR: GeminiDialogueService failed: " + e.getMessage());
            return getDefaultMonologue(weatherDescription);
        }
    }

    /**
     * Provides a default, hard-coded monologue if the API call fails or is not configured.
     * @param weather A simple weather string (e.g., "rain", "snow")
     * @return A fallback prophecy.
     */
    private String getDefaultMonologue(String weather) {
        String simpleWeather = weather.toLowerCase().contains("rain") ? "rain" :
                weather.toLowerCase().contains("snow") ? "snow" : "clear";

        switch (simpleWeather) {
            case "rain":
                return "The sky weeps... but these tears, are they for cleansing, or for drowning?";
            case "snow":
                return "A white silence descends... the world holds its breath in the cold.";
            default:
                return "The winds... they carry the scent of change.";
        }
    }
}
