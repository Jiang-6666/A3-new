package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.TalkToAction;
import game.weather.ApiWeatherBase;
import game.weather.WeatherController;
import game.weather.WeatherEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ5 Unit Tests for StormSeer NPC.
 * Tests NPC behavior, player interactions, and monologue generation.
 */
class StormSeerTest {

    private WeatherController mockWeatherController;
    private Player mockPlayer;
    private GameMap mockGameMap;

    @BeforeEach
    void setUp() {
        mockWeatherController = mock(WeatherController.class);
        mockPlayer = mock(Player.class);
        mockGameMap = mock(GameMap.class);
    }

    /**
     * TEST 1: Normal case - StormSeer does nothing on its turn
     * Expected: playTurn returns DoNothingAction
     */
    @Test
    void testPlayTurn_ReturnsDoNothingAction() {
        // Arrange
        StormSeer seer = new StormSeer(mockWeatherController);
        ActionList actions = new ActionList();
        Action lastAction = new DoNothingAction();
        Display display = new Display();

        // Act
        Action result = seer.playTurn(actions, lastAction, mockGameMap, display);

        // Assert
        assertNotNull(result, "Action should not be null");
        assertTrue(result instanceof DoNothingAction, "StormSeer should do nothing");
    }

    /**
     * TEST 2: Normal case - Player can talk to StormSeer during API weather
     * Expected: allowableActions includes TalkToAction
     */
    @Test
    void testAllowableActions_ApiWeather_IncludesTalkAction() {
        // Arrange
        StormSeer seer = new StormSeer(mockWeatherController);
        
        // Mock API weather
        ApiWeatherBase mockApiWeather = mock(ApiWeatherBase.class);
        when(mockApiWeather.isApiControlled()).thenReturn(true);
        when(mockWeatherController.getCurrentWeather()).thenReturn(mockApiWeather);

        // Act
        ActionList actions = seer.allowableActions(mockPlayer, "north", mockGameMap);

        // Assert
        assertNotNull(actions);
        assertTrue(actions.size() > 0, "Should have at least one action");
        
        // Check if TalkToAction is present
        boolean hasTalkAction = false;
        for (Action action : actions) {
            if (action instanceof TalkToAction) {
                hasTalkAction = true;
                break;
            }
        }
        assertTrue(hasTalkAction, "Should include TalkToAction for API weather");
    }

    /**
     * TEST 3: Edge case - Non-API weather means no TalkToAction
     * Expected: allowableActions does not include TalkToAction
     */
    @Test
    void testAllowableActions_NonApiWeather_NoTalkAction() {
        // Arrange
        StormSeer seer = new StormSeer(mockWeatherController);
        
        // Mock non-API weather
        WeatherEffect mockNonApiWeather = mock(WeatherEffect.class);
        when(mockNonApiWeather.isApiControlled()).thenReturn(false);
        when(mockWeatherController.getCurrentWeather()).thenReturn(mockNonApiWeather);

        // Act
        ActionList actions = seer.allowableActions(mockPlayer, "south", mockGameMap);

        // Assert
        assertNotNull(actions);
        
        // Check that TalkToAction is NOT present
        boolean hasTalkAction = false;
        for (Action action : actions) {
            if (action instanceof TalkToAction) {
                hasTalkAction = true;
                break;
            }
        }
        assertFalse(hasTalkAction, "Should not include TalkToAction for non-API weather");
    }

    /**
     * TEST 4: Edge case - Null weather means no TalkToAction
     * Expected: Handles gracefully, no TalkToAction
     */
    @Test
    void testAllowableActions_NullWeather_NoTalkAction() {
        // Arrange
        StormSeer seer = new StormSeer(mockWeatherController);
        when(mockWeatherController.getCurrentWeather()).thenReturn(null);

        // Act
        ActionList actions = seer.allowableActions(mockPlayer, "east", mockGameMap);

        // Assert
        assertNotNull(actions);
        
        // Should not crash and should not have TalkToAction
        boolean hasTalkAction = false;
        for (Action action : actions) {
            if (action instanceof TalkToAction) {
                hasTalkAction = true;
                break;
            }
        }
        assertFalse(hasTalkAction, "Should not include TalkToAction when weather is null");
    }

    /**
     * TEST 5: Normal case - getMonologue delegates to DialogueService
     * Expected: Returns non-null monologue string
     */
    @Test
    void testGetMonologue_ReturnsNonNullString() {
        // Arrange
        StormSeer seer = new StormSeer(mockWeatherController);
        
        // Mock weather with data
        ApiWeatherBase mockWeather = mock(ApiWeatherBase.class);
        when(mockWeather.getDescription()).thenReturn("Light Rain");
        when(mockWeather.getTemperature()).thenReturn(12.5);

        // Act
        String result = seer.getMonologue(mockWeather);

        // Assert
        assertNotNull(result, "Monologue should not be null");
        assertFalse(result.isEmpty(), "Monologue should not be empty");
    }

    /**
     * TEST 6: Boundary case - StormSeer has very high HP
     * Expected: HP is set correctly to prevent death
     */
    @Test
    void testStormSeer_HasHighHitpoints() {
        // Arrange & Act
        StormSeer seer = new StormSeer(mockWeatherController);

        // Assert
        assertTrue(seer.getAttribute(BaseAttributes.HEALTH) > 1000, "StormSeer should have very high HP");
    }

    /**
     * TEST 7: Boundary case - StormSeer display properties
     * Expected: Correct name and display character
     */
    @Test
    void testStormSeer_DisplayProperties() {
        // Arrange & Act
        StormSeer seer = new StormSeer(mockWeatherController);

        // Assert
        // Actor.toString() returns "Name (HP/MaxHP)" format
        assertTrue(seer.toString().contains("Storm Seer"), "Name should contain 'Storm Seer'");
        assertEquals('§', seer.getDisplayChar(), "Display char should be '§'");
    }
}

