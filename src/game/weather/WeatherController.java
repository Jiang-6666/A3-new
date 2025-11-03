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
 *     <li>call {@link #tick(GameMap...)} once from the game loop</li>
 *     <li>controller decrements duration, possibly switches weather</li>
 *     <li>controller applies current weather to every actor on every provided map</li>
 * </ol>
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

    private final WeatherEffect rain = new RainstormWeather();
    private final WeatherEffect wind = new WindstormWeather();
    private final WeatherEffect blizzard = new BlizzardWeather();

    /** Current active weather. */
    private WeatherEffect current;

    /** Turns remaining for current weather. */
    private int turnsRemaining;

    /** Shared exposure calculator to avoid duplication. */
    private final ExposureCalculator exposureCalculator = new ExposureCalculator();

    /**
     * Creates a controller and starts with a random weather.
     */
    public WeatherController() {
        this.current = pickRandomWeather();
        this.turnsRemaining = randomDuration();
    }

    /**
     * Advance the global weather by one turn, possibly switching to a new weather,
     * and apply the current weather to every actor on every map provided.
     *
     * @param maps maps to update; if none provided, only weather timer is advanced
     */
    public void tick(GameMap... maps) {
        // 1) decrease duration
        turnsRemaining--;

        // 2) switch if expired
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

        // 3) apply current weather to all actors
        if (current == null) {
            return;
        }
        if (maps == null) {
            return;
        }
        for (GameMap map : maps) {
            if (map == null) {
                continue;
            }
            for (int x : map.getXRange()) {
                for (int y : map.getYRange()) {
                    Location loc = map.at(x, y);
                    if (!map.isAnActorAt(loc)) {
                        continue;
                    }
                    Actor actor = map.getActorAt(loc);
                    // now you have the actor and its exact location
                    current.applyToActor(actor, map, exposureCalculator.withLocation(loc));
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

    private int randomDuration() {
        int span = MAX_DURATION_TURNS - MIN_DURATION_TURNS + 1;
        return MIN_DURATION_TURNS + rng.nextInt(span);
    }
}
