package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Blizzard: severe cold, optional mild dehydration.
 */
public class BlizzardWeather implements WeatherEffect {

    private static final String ID = "blizzard";
    private static final String NAME = "Blizzard";

    /** Strong hypothermia pressure. */
    private static final int BASE_WARMTH_DELTA = -2;

    /** Choose 0 or -1; we use 0 here. */
    private static final int BASE_HYDRATION_DELTA = 0;

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
