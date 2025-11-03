package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Rainstorm: slightly cold, but helps hydration (or at least slows dehydration).
 */
public class RainstormWeather implements WeatherEffect {

    private static final String ID = "rain";
    private static final String NAME = "Rainstorm";

    /** Slight cold. */
    private static final int BASE_WARMTH_DELTA = -1;

    /** Mitigate dehydration: +1 vs baseline, so we set to 0 or +1 depending on your engine.
     * Here we model as +1 (i.e. increase hydration by 1). */
    private static final int BASE_HYDRATION_DELTA = +1;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public int getBaseWarmthDelta() {
        return BASE_WARMTH_DELTA;
    }

    @Override
    public int getBaseHydrationDelta() {
        return BASE_HYDRATION_DELTA;
    }

    @Override
    public void applyToActor(Actor actor, GameMap map, ExposureCalculator exposureCalculator) {
        if (actor == null || map == null) {
            return;
        }
        double factor = exposureCalculator.computeExposureFor(actor, this);
        int warmthChange = (int) Math.round(getBaseWarmthDelta() * factor);
        int hydrationChange = (int) Math.round(getBaseHydrationDelta() * factor);
        WeatherReflection.applyWarmthDelta(actor, warmthChange);
        WeatherReflection.applyHydrationDelta(actor, hydrationChange);
    }
}
