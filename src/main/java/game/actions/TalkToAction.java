package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.ApiWeatherBase;
import game.actors.StormSeer; // REQ5: Added import for StormSeer

/**
 * REQ5: An Action that allows the Player (actor) to talk to the StormSeer (target).
 * This action fetches the monologue from the StormSeer and prints it to the console.
 */
public class TalkToAction extends Action {

    /** The NPC we are talking to. */
    private final StormSeer target;

    /** The current weather, passed in to get context for the monologue. */
    private final ApiWeatherBase weather;

    /**
     * Constructor.
     * @param target The StormSeer NPC.
     * @param weather The current API-driven weather.
     */
    public TalkToAction(StormSeer target, ApiWeatherBase weather) {
        this.target = target;
        this.weather = weather;
    }

    /**
     * Executes the action.
     * 1. Gets the AI monologue from the StormSeer.
     * 2. Prints it to the display for the player to read.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // We must create a new Display object to print to the console
        Display display = new Display();

        // 1. Print standard flavour text
        display.println(String.format("\n%s looks at you, then gestures to the sky:", target));

        // 2. Call the API (this may cause a slight, realistic pause)
        // This delegates the API call to the StormSeer, who delegates to the service.
        display.println("..."); // Indicate loading
        String monologue = target.getMonologue(weather);

        // 3. Print the AI-generated monologue
        display.println(String.format(" \" %s \" \n", monologue));

        // Return the result string for the game log
        return String.format("%s listens to the %s's prophecy.", actor, target);
    }

    /**
     * The text displayed in the player's action menu.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("Talk to %s (%s)", target, target.getDisplayChar());
    }
}