package game.effects;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A stacking poison damage-over-time (DoT) effect.
 *
 * <p>Each active instance hurts the affected entity once per tick. Instances stack
 * additively.</p>
 *
 * <p><b>REQ4</b>: Applied when a weapon coated with yew berry hits a target (same profile as
 * eating a {@code YewBerry}).</p>
 */
public class PoisonedEffect implements Status {

    /** Canonical damage for "yewberry poison". */
    public static final int DEFAULT_DAMAGE_FROM_YEWBERRY = 4;

    /** Canonical duration for "yewberry poison". */
    public static final int DEFAULT_DURATION_FROM_YEWBERRY = 5;

    /** Name of the "hurt" method to try first. */
    private static final String METHOD_HURT = "hurt";

    /** Fallback hitpoints field name. */
    private static final String FIELD_HP = "hitPoints";

    /** Damage dealt each tick while the effect is active. */
    private final int damagePerTurn;

    /** Number of ticks remaining before the effect expires. */
    private int turnsRemaining;

    /**
     * Creates a poisoned effect.
     *
     * @param damagePerTurn damage dealt per tick (must be non-negative)
     * @param duration number of ticks the effect should last (must be non-negative)
     */
    public PoisonedEffect(int damagePerTurn, int duration) {
        this.damagePerTurn = Math.max(0, damagePerTurn);
        this.turnsRemaining = Math.max(0, duration);
    }

    /**
     * Applies one tick of poison damage and decreases remaining duration.
     *
     * @param currEntity entity currently being updated
     * @param location the entity's location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining <= 0) {
            return;
        }
        applyDamage(currEntity, damagePerTurn);
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
     * Best-effort damage application without using {@code instanceof}.
     *
     * <p>Order:
     * <ol>
     *     <li>try public/protected {@code hurt(int)}</li>
     *     <li>try declared {@code hurt(int)}</li>
     *     <li>try field {@code hitPoints}</li>
     * </ol>
     * </p>
     */
    private static void applyDamage(GameEntity entity, int dmg) {
        if (dmg <= 0 || entity == null) {
            return;
        }

        // 1) public/protected hurt(int)
        try {
            Method m = entity.getClass().getMethod(METHOD_HURT, int.class);
            m.invoke(entity, dmg);
            return;
        } catch (Throwable ignored) {
            // fall through
        }

        // 2) declared hurt(int)
        try {
            Method m = entity.getClass().getDeclaredMethod(METHOD_HURT, int.class);
            m.setAccessible(true);
            m.invoke(entity, dmg);
            return;
        } catch (Throwable ignored) {
            // fall through
        }

        // 3) field "hitPoints"
        try {
            Field f = entity.getClass().getDeclaredField(FIELD_HP);
            f.setAccessible(true);
            Object v = f.get(entity);
            if (v instanceof Integer) {
                int hp = (Integer) v;
                int nhp = Math.max(0, hp - dmg);
                f.set(entity, nhp);
            }
        } catch (Throwable ignored) {
            // final fallback; silently continue
        }
    }

    @Override
    public String toString() {
        return "Poisoned (" + turnsRemaining + " turns, " + damagePerTurn + " dmg/turn)";
    }
}
