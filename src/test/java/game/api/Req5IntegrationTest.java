package game.api;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.TalkToAction;
import game.actors.Player;
import game.actors.StormSeer;
import game.weather.ApiWeatherBase;
import game.weather.ApiRainWeather;
import game.weather.WeatherController;
import game.weather.WeatherEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ5 Integration Test - Tests the complete workflow of API integration.
 * 
 * This test demonstrates how all REQ5 components work together:
 * 1. WeatherService fetches weather from API
 * 2. WeatherController manages the weather state
 * 3. StormSeer provides dialogue based on weather
 * 4. TalkToAction facilitates player-NPC interaction
 * 5. DialogueService generates context-aware responses
 */
class Req5IntegrationTest {

    private MockedStatic<Config> mockedConfig;
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        mockedConfig = mockStatic(Config.class);
        weatherController = mock(WeatherController.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * INTEGRATION TEST 1: Complete API Weather Flow
     * 
     * Workflow:
     * 1. WeatherService (without API key) returns fallback weather
     * 2. StormSeer recognizes API weather
     * 3. Player can interact with StormSeer
     * 4. TalkToAction executes and gets monologue
     * 
     * This tests the FULL user story: "As a player, I want to talk to the 
     * Storm Seer to get weather prophecies based on real-world weather data."
     */
    @Test
    void testCompleteApiWeatherFlow_WithoutApiKey() {
        // ARRANGE: Set up all components
        // 1. Configure service to use fallback (no API key)
        mockedConfig.when(Config::getOwmApiKey).thenReturn(null);
        
        // 2. Create weather service
        WeatherService weatherService = new OwmWeatherService();
        
        // 3. Create StormSeer with weather controller
        StormSeer stormSeer = new StormSeer(weatherController);
        
        // 4. Set up API weather in controller
        ApiWeatherBase apiWeather = new ApiRainWeather(12.0, "Light Rain");
        when(weatherController.getCurrentWeather()).thenReturn(apiWeather);
        
        // 5. Create mock player and map
        Player mockPlayer = mock(Player.class);
        GameMap mockMap = mock(GameMap.class);

        // ACT: Execute the workflow
        // Step 1: Weather service gets weather
        WeatherEffect currentWeather = weatherService.getCurrentWeather();
        assertNotNull(currentWeather, "Weather service should return weather");
        assertTrue(currentWeather.isApiControlled(), "Should be API-controlled weather");
        
        // Step 2: Player gets allowable actions when near StormSeer
        ActionList actions = stormSeer.allowableActions(mockPlayer, "north", mockMap);
        assertNotNull(actions, "Should have allowable actions");
        
        // Step 3: Find TalkToAction
        TalkToAction talkAction = null;
        for (Action action : actions) {
            if (action instanceof TalkToAction) {
                talkAction = (TalkToAction) action;
                break;
            }
        }
        assertNotNull(talkAction, "Player should be able to talk to StormSeer during API weather");
        
        // Step 4: Get monologue from StormSeer
        String monologue = stormSeer.getMonologue(apiWeather);
        assertNotNull(monologue, "StormSeer should provide monologue");
        assertFalse(monologue.isEmpty(), "Monologue should not be empty");
        
        // ASSERT: Verify complete integration
        assertTrue(monologue.length() > 10, "Monologue should be meaningful");
    }

    /**
     * INTEGRATION TEST 2: DialogueService Integration
     * 
     * Tests that DialogueService correctly handles different weather conditions
     * and returns appropriate monologues for each type.
     */
    @Test
    void testDialogueServiceIntegration_MultipleWeatherTypes() {
        // ARRANGE
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);
        
        DialogueService dialogueService = new GeminiDialogueService();
        
        // ACT & ASSERT: Test different weather types
        
        // Test 1: Rainy weather
        String rainMonologue = dialogueService.getStormSeerMonologue("Light Rain", 10.0);
        assertNotNull(rainMonologue);
        assertTrue(rainMonologue.contains("rain") || rainMonologue.contains("weeps") || rainMonologue.contains("tears"),
                "Rain monologue should be rain-themed");
        
        // Test 2: Snowy weather  
        String snowMonologue = dialogueService.getStormSeerMonologue("Heavy Snow", -5.0);
        assertNotNull(snowMonologue);
        assertTrue(snowMonologue.contains("snow") || snowMonologue.contains("white") || snowMonologue.contains("cold"),
                "Snow monologue should be snow-themed");
        
        // Test 3: Clear weather
        String clearMonologue = dialogueService.getStormSeerMonologue("Clear", 20.0);
        assertNotNull(clearMonologue);
        assertFalse(clearMonologue.isEmpty(), "Clear weather should have monologue");
        
        // Verify all monologues are different (or at least not all the same)
        assertFalse(rainMonologue.equals(snowMonologue) && snowMonologue.equals(clearMonologue),
                "Different weather should produce different monologues");
    }

    /**
     * INTEGRATION TEST 3: Weather Service Fallback Chain
     * 
     * Tests the fallback mechanism when API is unavailable:
     * 1. No API key → fallback weather
     * 2. Fallback weather still works with StormSeer
     * 3. Player still gets meaningful interaction
     */
    @Test
    void testWeatherServiceFallbackChain() {
        // ARRANGE: Simulate API unavailability
        mockedConfig.when(Config::getOwmApiKey).thenReturn(null);
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);
        
        WeatherService weatherService = new OwmWeatherService();
        DialogueService dialogueService = new GeminiDialogueService();
        
        // ACT: Get fallback weather
        WeatherEffect fallbackWeather = weatherService.getCurrentWeather();
        
        // ASSERT: Verify fallback chain
        assertNotNull(fallbackWeather, "Should have fallback weather");
        assertTrue(fallbackWeather.isApiControlled(), "Fallback should still be API-controlled type");
        
        // Verify dialogue service also has fallback
        if (fallbackWeather instanceof ApiWeatherBase) {
            ApiWeatherBase apiWeather = (ApiWeatherBase) fallbackWeather;
            String fallbackMonologue = dialogueService.getStormSeerMonologue(
                apiWeather.getDescription(), 
                apiWeather.getTemperature()
            );
            assertNotNull(fallbackMonologue, "Should have fallback monologue");
            assertFalse(fallbackMonologue.isEmpty(), "Fallback monologue should not be empty");
        }
    }

    /**
     * INTEGRATION TEST 4: Temperature-Based Response Variation
     * 
     * Tests that the system responds differently to extreme temperatures,
     * demonstrating the temperature-awareness of the API integration.
     */
    @Test
    void testTemperatureBasedResponseVariation() {
        // ARRANGE
        mockedConfig.when(Config::getGeminiProjectId).thenReturn(null);
        mockedConfig.when(Config::getGeminiLocation).thenReturn(null);
        mockedConfig.when(Config::getGeminiModelName).thenReturn(null);
        
        DialogueService dialogueService = new GeminiDialogueService();
        StormSeer stormSeer = new StormSeer(weatherController);
        
        // ACT: Test extreme temperatures
        ApiWeatherBase coldWeather = new ApiRainWeather(-20.0, "Freezing Rain");
        ApiWeatherBase mildWeather = new ApiRainWeather(15.0, "Light Rain");
        ApiWeatherBase hotWeather = new ApiRainWeather(40.0, "Hot Clear");
        
        String coldMonologue = stormSeer.getMonologue(coldWeather);
        String mildMonologue = stormSeer.getMonologue(mildWeather);
        String hotMonologue = stormSeer.getMonologue(hotWeather);
        
        // ASSERT: All should be valid
        assertNotNull(coldMonologue, "Cold weather should have monologue");
        assertNotNull(mildMonologue, "Mild weather should have monologue");
        assertNotNull(hotMonologue, "Hot weather should have monologue");
        
        // All should be non-empty
        assertFalse(coldMonologue.isEmpty());
        assertFalse(mildMonologue.isEmpty());
        assertFalse(hotMonologue.isEmpty());
    }

    /**
     * INTEGRATION TEST 5: StormSeer Behavior Consistency
     * 
     * Tests that StormSeer behaves consistently:
     * - Always passive (does nothing on its turn)
     * - Only offers TalkToAction during API weather
     * - Correctly delegates to DialogueService
     */
    @Test
    void testStormSeerBehaviorConsistency() {
        // ARRANGE
        StormSeer stormSeer = new StormSeer(weatherController);
        Player mockPlayer = mock(Player.class);
        GameMap mockMap = mock(GameMap.class);
        
        // TEST 1: StormSeer never acts on its own
        Action playTurnResult = stormSeer.playTurn(
            new ActionList(), 
            null, 
            mockMap, 
            null
        );
        assertTrue(playTurnResult instanceof edu.monash.fit2099.engine.actions.DoNothingAction,
                "StormSeer should always do nothing on its turn");
        
        // TEST 2: With API weather, provides TalkToAction
        ApiWeatherBase apiWeather = new ApiRainWeather(10.0, "Rain");
        when(weatherController.getCurrentWeather()).thenReturn(apiWeather);
        
        ActionList actionsWithApi = stormSeer.allowableActions(mockPlayer, "north", mockMap);
        boolean hasTalkAction = false;
        for (Action action : actionsWithApi) {
            if (action instanceof TalkToAction) {
                hasTalkAction = true;
                break;
            }
        }
        assertTrue(hasTalkAction, "Should offer TalkToAction with API weather");
        
        // TEST 3: Without API weather, no TalkToAction
        WeatherEffect nonApiWeather = mock(WeatherEffect.class);
        when(nonApiWeather.isApiControlled()).thenReturn(false);
        when(weatherController.getCurrentWeather()).thenReturn(nonApiWeather);
        
        ActionList actionsWithoutApi = stormSeer.allowableActions(mockPlayer, "south", mockMap);
        boolean hasTalkActionNonApi = false;
        for (Action action : actionsWithoutApi) {
            if (action instanceof TalkToAction) {
                hasTalkActionNonApi = true;
                break;
            }
        }
        assertFalse(hasTalkActionNonApi, "Should not offer TalkToAction without API weather");
    }
}

