package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents a single weather state (e.g. rainstorm, windstorm, blizzard).
 *
 * <p>The controller selects exactly one active {@code WeatherEffect} at a time and
 * calls {@link #applyToActor(Actor, GameMap, ExposureCalculator)} once per turn for
 * every actor on every visible map.</p>
 *
 * <p>Implementations should only describe the <b>base</b> effect; any per-actor shielding
 * or exposure reduction is handled by {@link ExposureCalculator}.</p>
 */
public interface WeatherEffect {

    /**
     * A stable identifier for this weather, e.g. "rain", "wind", "blizzard".
     * Used by shielding code to match what to reduce.
     *
     * @return non-null weather id
     */
    String getId();

    /**
     * Human-friendly name, useful for logging/UI.
     *
     * @return display name
     */
    String getDisplayName();

    /**
     * Base warmth delta this weather would apply to a fully exposed actor.
     * Negative values mean "lose warmth".
     *
     * @return warmth delta per turn
     */
    int getBaseWarmthDelta();

    /**
     * Base hydration delta this weather would apply to a fully exposed actor.
     * Negative values mean "lose hydration".
     *
     * @return hydration delta per turn
     */
    int getBaseHydrationDelta();

    /**
     * Called when this weather becomes active (previous weather has exited).
     *
     * @param maps maps currently under control; may be empty
     */
    default void onEnter(GameMap... maps) {
        // no-op by default
    }

    /**
     * Called when this weather is being replaced by another weather.
     *
     * @param maps maps currently under control; may be empty
     */
    default void onExit(GameMap... maps) {
        // no-op by default
    }

    /**
     * Apply this weather's effect to a single actor, taking into account actual exposure.
     *
     * @param actor actor to affect
     * @param map map containing the actor
     * @param exposureCalculator calculator that knows actor+ground shielding
     */
    void applyToActor(Actor actor, GameMap map, ExposureCalculator exposureCalculator);
}
