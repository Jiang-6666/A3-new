package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Global controller that manages current weather, remaining duration, and per-turn application.
 *
 * <p>Usage pattern (per game turn):
 * <ol>
 * <li>call {@link #tick(GameMap...)} once from the game loop</li>
 * <li>controller decrements duration, possibly switches weather</li>
 * <li>controller applies current weather to every actor on every provided map</li>
 * </ol>
 * </p>
 *
 * <p><b>REQ5 Extension:</b>
 * Now supports an "external control" mode. When an API-driven weather is set
 * via {@link #setExternalWeather(WeatherEffect)}, the internal random timer is paused,
 * allowing the API to govern the weather state.
 * </p>
 */
public class WeatherController {

    /** Minimum and maximum duration (in turns) for a single weather spell. */
    private static final int MIN_DURATION_TURNS = 8;
    private static final int MAX_DURATION_TURNS = 12;

    /** Probability weights (must be positive). */
    private static final int PROB_RAIN = 40;
    private static final int PROB_WIND = 30;
    private static final int PROB_BLIZZARD = 30;

    private final Random rng = new Random();

    // REQ3/4 Internal Weather
    private final WeatherEffect rain = new RainstormWeather();
    private final WeatherEffect wind = new WindstormWeather();
    private final WeatherEffect blizzard = new BlizzardWeather();

    /** Current active weather. */
    private WeatherEffect current;

    /** Turns remaining for current weather. */
    private int turnsRemaining;

    /** Shared exposure calculator to avoid duplication. */
    private final ExposureCalculator exposureCalculator = new ExposureCalculator();

    /** REQ5 Flag: If true, internal random timer is disabled. */
    private boolean externalControl = false;

    /**
     * Creates a controller and starts with a random weather.
     */
    public WeatherController() {
        this.current = pickRandomWeather();
        this.turnsRemaining = randomDuration();
        this.externalControl = false;
    }

    /**
     * REQ5 OCP Extension:
     * Allows an external system (like an API service) to forcibly set the weather.
     * This will pause the internal random weather cycle.
     *
     * @param newWeather The new WeatherEffect to set (e.g., ApiRainWeather).
     * @param maps The game maps to notify of the change (for onEnter/onExit).
     */
    public void setExternalWeather(WeatherEffect newWeather, GameMap... maps) {
        if (newWeather == null) return;

        // Only swap if the *type* of weather is different
        // (prevents spamming onEnter/onExit every tick)
        if (current == null || !current.getId().equals(newWeather.getId())) {
            WeatherEffect old = current;

            // Swap
            current = newWeather;
            externalControl = current.isApiControlled(); // Activate external control if weather is from API
            turnsRemaining = 999; // Set a high duration

            // Notify both ends
            if (old != null) {
                old.onExit(maps);
            }
            current.onEnter(maps);
        }
    }


    /**
     * Advance the global weather by one turn, possibly switching to a new weather,
     * and apply the current weather to every actor and ground tile on every map provided.
     *
     * @param maps maps to update; if none provided, only weather timer is advanced
     */
    public void tick(GameMap... maps) {
        // 1) Decrease duration, but only if NOT externally controlled
        if (!externalControl) {
            turnsRemaining--;

            // 2) Switch if expired (and NOT externally controlled)
            if (turnsRemaining <= 0) {
                WeatherEffect old = current;
                WeatherEffect next = pickRandomWeather();
                // swap
                current = next;
                turnsRemaining = randomDuration();

                // notify both ends
                if (old != null) {
                    old.onExit(maps);
                }
                if (current != null) {
                    current.onEnter(maps);
                }
            }
        }
        // If externalControl is true, the timer never decrements, so it never switches.

        // 3) Apply current weather to all actors AND ground tiles
        if (current == null || maps == null) {
            return;
        }

        for (GameMap map : maps) {
            if (map == null) {
                continue;
            }
            for (int x : map.getXRange()) {
                for (int y : map.getYRange()) {
                    Location loc = map.at(x, y);

                    // REQ5: Apply effect to Ground (e.g., douse fire, create snow)
                    current.applyToGround(loc, map);

                    // REQ3/4: Apply effect to Actor
                    if (map.isAnActorAt(loc)) {
                        Actor actor = map.getActorAt(loc);
                        // now you have the actor and its exact location
                        current.applyToActor(actor, map, exposureCalculator.withLocation(loc));
                    }
                }
            }
        }
    }

    /**
     * @return current active weather (never null after construction)
     */
    public WeatherEffect getCurrentWeather() {
        return current;
    }

    /**
     * @return turns remaining in current spell (>= 1)
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Picks a random (internal) weather based on probabilities.
     * @return A new WeatherEffect instance.
     */
    private WeatherEffect pickRandomWeather() {
        int total = PROB_RAIN + PROB_WIND + PROB_BLIZZARD;
        int roll = rng.nextInt(total);
        if (roll < PROB_RAIN) {
            return rain;
        } else if (roll < PROB_RAIN + PROB_WIND) {
            return wind;
        } else {
            return blizzard;
        }
    }

    /**
     * @return A random duration between MIN and MAX.
     */
    private int randomDuration() {
        int span = MAX_DURATION_TURNS - MIN_DURATION_TURNS + 1;
        return MIN_DURATION_TURNS + rng.nextInt(span);
    }
}