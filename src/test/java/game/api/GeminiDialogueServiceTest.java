package game.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ5 Unit Tests for GeminiDialogueService.
 * Tests AI dialogue generation, fallback handling, and error recovery.
 */
class GeminiDialogueServiceTest {

    private MockedStatic<Config> mockedConfig;

    @BeforeEach
    void setUp() {
        mockedConfig = mockStatic(Config.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * TEST 1: Normal case - No API configured, rainy weather
     * Expected: Returns default rain monologue
     */
    @Test
    void testGetStormSeerMonologue_NoApi_RainWeather() {
        // Arrange: No API credentials
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act
        String result = service.getStormSeerMonologue("Light Rain", 10.5);

        // Assert
        assertNotNull(result, "Monologue should not be null");
        assertTrue(result.contains("rain") || result.contains("weeps") || result.contains("tears"),
                "Should return rain-themed default monologue");
        assertFalse(result.isEmpty(), "Monologue should not be empty");
    }

    /**
     * TEST 2: Edge case - No API configured, snowy weather
     * Expected: Returns default snow monologue
     */
    @Test
    void testGetStormSeerMonologue_NoApi_SnowWeather() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act
        String result = service.getStormSeerMonologue("Heavy Snow", -5.0);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("snow") || result.contains("white") || result.contains("cold"),
                "Should return snow-themed default monologue");
    }

    /**
     * TEST 3: Boundary case - No API configured, clear weather
     * Expected: Returns default clear/generic monologue
     */
    @Test
    void testGetStormSeerMonologue_NoApi_ClearWeather() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act
        String result = service.getStormSeerMonologue("Clear Sky", 20.0);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("wind") || result.contains("change"),
                "Should return default generic monologue for clear weather");
    }

    /**
     * TEST 4: Edge case - Extreme cold temperature
     * Expected: Returns appropriate monologue
     */
    @Test
    void testGetStormSeerMonologue_ExtremeCold() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act: Very cold temperature
        String result = service.getStormSeerMonologue("Blizzard", -30.0);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * TEST 5: Edge case - Extreme hot temperature
     * Expected: Returns appropriate monologue
     */
    @Test
    void testGetStormSeerMonologue_ExtremeHeat() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act: Very hot temperature
        String result = service.getStormSeerMonologue("Clear", 45.0);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * TEST 6: Invalid input - Null weather description
     * Expected: Handles gracefully with fallback
     */
    @Test
    void testGetStormSeerMonologue_NullWeatherDescription() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act: Null description should not crash
        String result = service.getStormSeerMonologue(null, 15.0);

        // Assert: Should handle gracefully
        assertNotNull(result, "Should handle null description gracefully");
    }

    /**
     * TEST 7: Edge case - Empty weather description
     * Expected: Returns default monologue
     */
    @Test
    void testGetStormSeerMonologue_EmptyWeatherDescription() {
        // Arrange
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);

        GeminiDialogueService service = new GeminiDialogueService();

        // Act
        String result = service.getStormSeerMonologue("", 15.0);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}

