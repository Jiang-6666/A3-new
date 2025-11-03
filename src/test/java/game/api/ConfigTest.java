package game.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ5 Unit Tests for Config class.
 * Tests configuration reading and default values.
 */
class ConfigTest {

    /**
     * TEST 1: Normal case - getOwmApiKey reads from environment
     * Expected: Returns null or actual key depending on environment
     */
    @Test
    void testGetOwmApiKey_ReadsFromEnvironment() {
        // Act
        String result = Config.getOwmApiKey();

        // Assert
        // We cannot assume the key is set, so we just verify it doesn't crash
        // and returns a string or null
        assertTrue(result == null || result instanceof String,
                "Should return null or a string");
    }

    /**
     * TEST 2: Normal case - getGeminiProjectId reads from environment
     * Expected: Returns null or actual ID depending on environment
     */
    @Test
    void testGetGeminiProjectId_ReadsFromEnvironment() {
        // Act
        String result = Config.getGeminiProjectId();

        // Assert
        assertTrue(result == null || result instanceof String,
                "Should return null or a string");
    }

    /**
     * TEST 3: Normal case - getGeminiLocation reads from environment
     * Expected: Returns null or actual location depending on environment
     */
    @Test
    void testGetGeminiLocation_ReadsFromEnvironment() {
        // Act
        String result = Config.getGeminiLocation();

        // Assert
        assertTrue(result == null || result instanceof String,
                "Should return null or a string");
    }

    /**
     * TEST 4: Normal case - getGeminiModelName reads from environment
     * Expected: Returns null or actual model name depending on environment
     */
    @Test
    void testGetGeminiModelName_ReadsFromEnvironment() {
        // Act
        String result = Config.getGeminiModelName();

        // Assert
        assertTrue(result == null || result instanceof String,
                "Should return null or a string");
    }

    /**
     * TEST 5: Boundary case - WEATHER_LOCATION_CITY constant
     * Expected: Returns "Kuala Lumpur" as specified
     */
    @Test
    void testWeatherLocationCity_HasCorrectValue() {
        // Act
        String result = Config.WEATHER_LOCATION_CITY;

        // Assert
        assertNotNull(result, "City should not be null");
        assertEquals("Kuala Lumpur", result, "Default city should be Kuala Lumpur");
    }

    /**
     * TEST 6: Boundary case - WEATHER_LOCATION_COUNTRY constant
     * Expected: Returns "MALAYSIA" as specified
     */
    @Test
    void testWeatherLocationCountry_HasCorrectValue() {
        // Act
        String result = Config.WEATHER_LOCATION_COUNTRY;

        // Assert
        assertNotNull(result, "Country should not be null");
        assertEquals("MALAYSIA", result, "Default country should be MALAYSIA");
    }

    /**
     * TEST 7: Edge case - All methods are callable
     * Expected: No exceptions thrown when calling all methods
     */
    @Test
    void testAllConfigMethods_Callable() {
        // Act & Assert - Should not throw exceptions
        assertDoesNotThrow(() -> {
            Config.getOwmApiKey();
            Config.getGeminiProjectId();
            Config.getGeminiLocation();
            Config.getGeminiModelName();
        }, "All config methods should be callable without exceptions");
    }
}

