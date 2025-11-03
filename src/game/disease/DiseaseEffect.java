package game.disease;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Base class for disease-like status effects.
 *
 * <p>Provides common reflection helpers to:
 * <ul>
 *     <li>damage an actor (via {@code hurt(int)} or {@code hitPoints} field)</li>
 *     <li>reduce warmth (via {@code decreaseWarmth()}, {@code increaseWarmth(int)}, or {@code warmth} field)</li>
 *     <li>check/remove another status by class name (no {@code instanceof})</li>
 * </ul>
 * </p>
 */
public abstract class DiseaseEffect implements Status {

    /**
     * Called once per turn on the entity that has this disease.
     */
    @Override
    public final void tickStatus(GameEntity entity, Location location) {
        doTick(entity, location);
    }

    /**
     * Subclasses implement their own per-turn behaviour here.
     *
     * @param entity   diseased entity
     * @param location current location
     */
    protected abstract void doTick(GameEntity entity, Location location);

    /**
     * Try to deal damage to the entity without using {@code instanceof}.
     *
     * @param entity target
     * @param amount damage (non-negative)
     */
    protected void applyDamage(GameEntity entity, int amount) {
        if (entity == null || amount <= 0) {
            return;
        }
        // 1) try public/protected hurt(int)
        try {
            Method m = entity.getClass().getMethod("hurt", int.class);
            m.invoke(entity, amount);
            return;
        } catch (Throwable ignored) { }

        // 2) try declared hurt(int)
        try {
            Method m = entity.getClass().getDeclaredMethod("hurt", int.class);
            m.setAccessible(true);
            m.invoke(entity, amount);
            return;
        } catch (Throwable ignored) { }

        // 3) try hitPoints field
        try {
            Field f = entity.getClass().getDeclaredField("hitPoints");
            f.setAccessible(true);
            Object v = f.get(entity);
            if (v instanceof Integer) {
                int hp = (Integer) v;
                int nhp = Math.max(0, hp - amount);
                f.set(entity, nhp);
            }
        } catch (Throwable ignored) { }
    }

    /**
     * Try to reduce warmth on the entity.
     *
     * @param entity target
     * @param amount amount (non-negative)
     */
    protected void reduceWarmth(GameEntity entity, int amount) {
        if (entity == null || amount <= 0) {
            return;
        }
        // 1) decreaseWarmth()
        try {
            Method m = entity.getClass().getMethod("decreaseWarmth");
            for (int i = 0; i < amount; i++) {
                m.invoke(entity);
            }
            return;
        } catch (Throwable ignored) { }

        // 2) increaseWarmth(int) with negative
        try {
            Method m = entity.getClass().getMethod("increaseWarmth", int.class);
            m.invoke(entity, -amount);
            return;
        } catch (Throwable ignored) { }

        // 3) warmth field
        try {
            Field f = entity.getClass().getDeclaredField("warmth");
            f.setAccessible(true);
            Object v = f.get(entity);
            if (v instanceof Integer) {
                int w = (Integer) v;
                int nw = Math.max(0, w - amount);
                f.set(entity, nw);
            }
        } catch (Throwable ignored) { }
    }

    /**
     * Check if the entity currently has a status with the given class name.
     *
     * @param entity    entity to check
     * @param className fully qualified class name of the status
     * @return true if found
     */
    protected boolean hasStatusByName(GameEntity entity, String className) {
        if (entity == null || className == null) {
            return false;
        }
        try {
            Method m = entity.getClass().getMethod("hasStatus", Status.class);
            // we do not have the instance, so we cannot call that directly
            // fall through to list version
        } catch (Throwable ignored) { }

        // try to get all statuses
        try {
            Method m = entity.getClass().getMethod("getStatuses");
            Object listObj = m.invoke(entity);
            if (listObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<?> list = (java.util.List<?>) listObj;
                for (Object s : list) {
                    if (s != null && className.equals(s.getClass().getName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) { }
        return false;
    }

    /**
     * Remove a status from entity by class name.
     *
     * @param entity    entity
     * @param className status class name
     */
    protected void removeStatusByName(GameEntity entity, String className) {
        if (entity == null || className == null) {
            return;
        }
        // try removeStatus(Status) pattern
        try {
            Method get = entity.getClass().getMethod("getStatuses");
            Object listObj = get.invoke(entity);
            if (listObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> list = (java.util.List<Object>) listObj;
                Object target = null;
                for (Object s : list) {
                    if (s != null && className.equals(s.getClass().getName())) {
                        target = s;
                        break;
                    }
                }
                if (target != null) {
                    try {
                        Method rem = entity.getClass().getMethod("removeStatus", Status.class);
                        rem.invoke(entity, target);
                    } catch (Throwable ignored) {
                        list.remove(target);
                    }
                }
            }
        } catch (Throwable ignored) { }
    }
}
