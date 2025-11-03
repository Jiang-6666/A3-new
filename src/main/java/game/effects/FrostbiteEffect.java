package game.effects;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A stacking status that reduces an entity's warmth by 1 each tick.
 *
 * <p>Creatures that are considered "cold resistant" (either via a public method
 * {@code isColdResistant()} or a {@code boolean} field named {@code coldResistant}) are
 * unaffected.</p>
 *
 * <p><b>REQ4</b>: Applied when a weapon coated with snow hits a non-immune target.</p>
 */
public class FrostbiteEffect implements Status {

    /** Default duration used when snow is the source of the frostbite. */
    public static final int DEFAULT_DURATION_FROM_SNOW = 3;

    /** Warmth reduced per tick (do not hardcode 1 everywhere). */
    private static final int WARMTH_REDUCTION_PER_TICK = 1;

    /** Name of the "is cold resistant" method. */
    private static final String METHOD_IS_COLD_RESISTANT = "isColdResistant";

    /** Name of the "cold resistant" field. */
    private static final String FIELD_COLD_RESISTANT = "coldResistant";

    /** Number of ticks remaining before the effect expires. */
    private int turnsRemaining;

    /**
     * Creates a frostbite effect.
     *
     * @param duration number of ticks the effect should last (must be non-negative)
     */
    public FrostbiteEffect(int duration) {
        this.turnsRemaining = Math.max(0, duration);
    }

    /**
     * Reduces warmth of the affected entity (unless cold resistant) and decreases the remaining
     * duration.
     *
     * @param currEntity the entity being updated
     * @param location the entity's location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining <= 0) {
            return;
        }
        if (!isColdResistant(currEntity)) {
            reduceWarmth(currEntity, WARMTH_REDUCTION_PER_TICK);
        }
        turnsRemaining--;
    }

    /**
     * Returns whether the status is still active.
     *
     * @return {@code true} if there are ticks remaining; otherwise {@code false}
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    /**
     * Duck-typed cold resistance:
     *  - Prefer public boolean method isColdResistant()
     *  - Fallback boolean field "coldResistant" (any visibility)
     * If neither exists, assume NOT cold-resistant.
     *
     * <p>No {@code instanceof} is used; we only compare to {@link Boolean#TRUE}.</p>
     */
    private static boolean isColdResistant(GameEntity entity) {
        // method first
        try {
            Method m = entity.getClass().getMethod(METHOD_IS_COLD_RESISTANT);
            Object v = m.invoke(entity);
            if (Boolean.TRUE.equals(v)) {
                return true;
            }
        } catch (Throwable ignored) {
            // fall through
        }

        // field fallback
        try {
            Field f = entity.getClass().getDeclaredField(FIELD_COLD_RESISTANT);
            f.setAccessible(true);
            Object v = f.get(entity);
            if (Boolean.TRUE.equals(v)) {
                return true;
            }
        } catch (Throwable ignored) {
            // fall through
        }

        return false;
    }

    /**
     * Attempts to decrease warmth on the entity by {@code amt}, using best-effort reflection hooks
     * (no engine changes required).
     *
     * @param entity the entity whose warmth should be reduced
     * @param amt amount to reduce warmth by (e.g., 1)
     */
    private static void reduceWarmth(GameEntity entity, int amt) {
        // 1) dedicated decreaseWarmth()
        try {
            Method m = entity.getClass().getDeclaredMethod("decreaseWarmth");
            m.setAccessible(true);
            for (int i = 0; i < amt; i++) {
                m.invoke(entity);
            }
            return;
        } catch (Throwable ignored) {
            // fall through
        }

        // 2) increaseWarmth(int) with negative amount
        try {
            Method m = entity.getClass().getMethod("increaseWarmth", int.class);
            m.invoke(entity, -amt);
            return;
        } catch (Throwable ignored) {
            // fall through
        }

        // 3) field "warmth"
        try {
            Field f = entity.getClass().getDeclaredField("warmth");
            f.setAccessible(true);
            Object v = f.get(entity);
            if (v instanceof Integer) {
                int w = (Integer) v;
                int nw = Math.max(0, w - amt);
                f.set(entity, nw);
            }
        } catch (Throwable ignored) {
            // final fallback; silently continue
        }
    }

    @Override
    public String toString() {
        return "Frostbite (" + turnsRemaining + " turns, -" + WARMTH_REDUCTION_PER_TICK + " warmth/turn)";
    }
}
