package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Player;
import game.actors.StormSeer;
import game.weather.ApiWeatherBase;
import game.weather.WeatherController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ5 Unit Tests for TalkToAction.
 * Tests player-NPC interaction and dialogue execution.
 */
class TalkToActionTest {

    private StormSeer mockStormSeer;
    private ApiWeatherBase mockWeather;
    private Actor mockActor;
    private GameMap mockGameMap;

    @BeforeEach
    void setUp() {
        // Create mock WeatherController
        WeatherController mockController = mock(WeatherController.class);
        
        // We need a real StormSeer for some tests, but can stub its methods
        mockStormSeer = mock(StormSeer.class);
        mockWeather = mock(ApiWeatherBase.class);
        mockActor = mock(Actor.class);
        mockGameMap = mock(GameMap.class);
    }

    /**
     * TEST 1: Normal case - Execute action calls getMonologue
     * Expected: execute() invokes StormSeer.getMonologue()
     */
    @Test
    void testExecute_CallsGetMonologue() {
        // Arrange
        when(mockStormSeer.getMonologue(mockWeather)).thenReturn("The winds whisper secrets...");
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockActor.toString()).thenReturn("Player");

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String result = action.execute(mockActor, mockGameMap);

        // Assert
        verify(mockStormSeer, times(1)).getMonologue(mockWeather);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("prophecy"), "Result should mention prophecy");
    }

    /**
     * TEST 2: Normal case - Returns correct message format
     * Expected: Result string contains actor and StormSeer
     */
    @Test
    void testExecute_ReturnsCorrectMessage() {
        // Arrange
        when(mockStormSeer.getMonologue(mockWeather)).thenReturn("Rain approaches...");
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockActor.toString()).thenReturn("Player");

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String result = action.execute(mockActor, mockGameMap);

        // Assert
        assertTrue(result.contains("Player"), "Result should contain actor name");
        assertTrue(result.contains("Storm Seer"), "Result should contain StormSeer name");
        assertTrue(result.contains("listens"), "Result should describe listening");
    }

    /**
     * TEST 3: Edge case - Menu description shows correct text
     * Expected: menuDescription includes StormSeer info
     */
    @Test
    void testMenuDescription_ShowsCorrectText() {
        // Arrange
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockStormSeer.getDisplayChar()).thenReturn('§');

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String menuText = action.menuDescription(mockActor);

        // Assert
        assertNotNull(menuText, "Menu description should not be null");
        assertTrue(menuText.contains("Talk to"), "Should say 'Talk to'");
        assertTrue(menuText.contains("Storm Seer"), "Should mention StormSeer");
        assertTrue(menuText.contains("§"), "Should show display character");
    }

    /**
     * TEST 4: Boundary case - Cold weather monologue
     * Expected: Handles cold weather context correctly
     */
    @Test
    void testExecute_ColdWeatherContext() {
        // Arrange
        when(mockWeather.getDescription()).thenReturn("Heavy Snow");
        when(mockWeather.getTemperature()).thenReturn(-10.0);
        when(mockStormSeer.getMonologue(mockWeather)).thenReturn("Ice grips the land...");
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockActor.toString()).thenReturn("Player");

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String result = action.execute(mockActor, mockGameMap);

        // Assert
        verify(mockStormSeer).getMonologue(mockWeather);
        assertNotNull(result);
    }

    /**
     * TEST 5: Boundary case - Hot weather monologue
     * Expected: Handles hot weather context correctly
     */
    @Test
    void testExecute_HotWeatherContext() {
        // Arrange
        when(mockWeather.getDescription()).thenReturn("Clear Sky");
        when(mockWeather.getTemperature()).thenReturn(35.0);
        when(mockStormSeer.getMonologue(mockWeather)).thenReturn("The sun burns bright...");
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockActor.toString()).thenReturn("Player");

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String result = action.execute(mockActor, mockGameMap);

        // Assert
        verify(mockStormSeer).getMonologue(mockWeather);
        assertNotNull(result);
    }

    /**
     * TEST 6: Edge case - Empty monologue
     * Expected: Handles gracefully
     */
    @Test
    void testExecute_EmptyMonologue() {
        // Arrange
        when(mockStormSeer.getMonologue(mockWeather)).thenReturn("");
        when(mockStormSeer.toString()).thenReturn("Storm Seer");
        when(mockActor.toString()).thenReturn("Player");

        TalkToAction action = new TalkToAction(mockStormSeer, mockWeather);

        // Act
        String result = action.execute(mockActor, mockGameMap);

        // Assert
        assertNotNull(result, "Should handle empty monologue gracefully");
    }
}

