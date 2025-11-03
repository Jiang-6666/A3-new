package game.weather;

import edu.monash.fit2099.engine.actors.Actor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection helpers for applying warmth/hydration deltas to actors
 * without modifying the engine and without using instanceof.
 */
public final class WeatherReflection {

    private static final String METHOD_DECREASE_WARMTH = "decreaseWarmth";
    private static final String METHOD_INCREASE_WARMTH = "increaseWarmth";
    private static final String FIELD_WARMTH = "warmth";

    private static final String METHOD_DECREASE_HYDRATION = "decreaseHydration";
    private static final String METHOD_INCREASE_HYDRATION = "increaseHydration";
    private static final String FIELD_HYDRATION = "hydration";

    private WeatherReflection() { }

    public static void applyWarmthDelta(Actor actor, int delta) {
        if (actor == null || delta == 0) {
            return;
        }
        if (delta < 0) {
            int amt = -delta;
            // try decreaseWarmth()
            try {
                Method m = actor.getClass().getMethod(METHOD_DECREASE_WARMTH);
                for (int i = 0; i < amt; i++) {
                    m.invoke(actor);
                }
                return;
            } catch (Throwable ignored) { }
            // try increaseWarmth(int) with negative
            try {
                Method m = actor.getClass().getMethod(METHOD_INCREASE_WARMTH, int.class);
                m.invoke(actor, -amt);
                return;
            } catch (Throwable ignored) { }
            // try warmth field
            try {
                Field f = actor.getClass().getDeclaredField(FIELD_WARMTH);
                f.setAccessible(true);
                Object v = f.get(actor);
                if (v instanceof Integer) {
                    int w = (Integer) v;
                    int nw = Math.max(0, w - amt);
                    f.set(actor, nw);
                }
            } catch (Throwable ignored) { }
        } else {
            // delta > 0 : increaseWarmth
            try {
                Method m = actor.getClass().getMethod(METHOD_INCREASE_WARMTH, int.class);
                m.invoke(actor, delta);
                return;
            } catch (Throwable ignored) { }
            try {
                Field f = actor.getClass().getDeclaredField(FIELD_WARMTH);
                f.setAccessible(true);
                Object v = f.get(actor);
                if (v instanceof Integer) {
                    int w = (Integer) v;
                    int nw = w + delta;
                    f.set(actor, nw);
                }
            } catch (Throwable ignored) { }
        }
    }

    public static void applyHydrationDelta(Actor actor, int delta) {
        if (actor == null || delta == 0) {
            return;
        }
        if (delta < 0) {
            int amt = -delta;
            // try decreaseHydration()
            try {
                Method m = actor.getClass().getMethod(METHOD_DECREASE_HYDRATION);
                for (int i = 0; i < amt; i++) {
                    m.invoke(actor);
                }
                return;
            } catch (Throwable ignored) { }
            // try increaseHydration(int) with negative
            try {
                Method m = actor.getClass().getMethod(METHOD_INCREASE_HYDRATION, int.class);
                m.invoke(actor, -amt);
                return;
            } catch (Throwable ignored) { }
            // try hydration field
            try {
                Field f = actor.getClass().getDeclaredField(FIELD_HYDRATION);
                f.setAccessible(true);
                Object v = f.get(actor);
                if (v instanceof Integer) {
                    int h = (Integer) v;
                    int nh = Math.max(0, h - amt);
                    f.set(actor, nh);
                }
            } catch (Throwable ignored) { }
        } else {
            // delta > 0 : increaseHydration
            try {
                Method m = actor.getClass().getMethod(METHOD_INCREASE_HYDRATION, int.class);
                m.invoke(actor, delta);
                return;
            } catch (Throwable ignored) { }
            try {
                Field f = actor.getClass().getDeclaredField(FIELD_HYDRATION);
                f.setAccessible(true);
                Object v = f.get(actor);
                if (v instanceof Integer) {
                    int h = (Integer) v;
                    int nh = h + delta;
                    f.set(actor, nh);
                }
            } catch (Throwable ignored) { }
        }
    }
}
