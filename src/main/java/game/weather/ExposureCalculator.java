package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Computes how much weather actually hits an actor, based on:
 * <ul>
 *     <li>the ground they are standing on (may act as shelter)</li>
 *     <li>the items they carry/wear (coats, parkas) that expose {@link WeatherShield}</li>
 * </ul>
 *
 * <p>Important: this class does <b>not</b> use {@code instanceof}. It uses method presence
 * checks to determine whether an object behaves like {@link WeatherShield}.</p>
 */
public class ExposureCalculator {

    /** Fallback factor when we cannot find any shielding. */
    private static final double DEFAULT_EXPOSURE = 1.0;

    /** Per-tick location (injected by controller). */
    private Location currentLocation;

    /**
     * @param loc location to be used when computing exposure
     * @return this calculator (for fluent use)
     */
    public ExposureCalculator withLocation(Location loc) {
        this.currentLocation = loc;
        return this;
    }

    /**
     * Compute how exposed the given actor is to the given weather.
     *
     * <p>Final factor is:
     * <pre>
     *   base 1.0
     *   × min(actorShield, groundShield)
     * </pre>
     * </p>
     *
     * @param actor actor to check
     * @param weather current weather
     * @return factor in [0, 1], where 1 = fully exposed, 0 = fully protected
     */
    public double computeExposureFor(Actor actor, WeatherEffect weather) {
        if (actor == null || weather == null) {
            return DEFAULT_EXPOSURE;
        }
        String wid = weather.getId();

        double actorShield = readActorShield(actor, wid);
        double groundShield = readGroundShield(wid);

        // choose the better protection (smaller)
        double result = Math.min(actorShield, groundShield);
        if (result < 0.0) {
            result = 0.0;
        } else if (result > 1.0) {
            result = 1.0;
        }
        return result;
    }

    private double readActorShield(Actor actor, String weatherId) {
        // try actor.getInventory()
        try {
            Method m = actor.getClass().getMethod("getInventory");
            Object invObj = m.invoke(actor);
            if (invObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Item> items = (List<Item>) invObj;
                double best = DEFAULT_EXPOSURE;
                for (Item item : items) {
                    double p = tryReadShield(item, weatherId);
                    if (p < best) {
                        best = p;
                    }
                }
                return best;
            }
        } catch (Throwable ignored) {
            // fall through
        }
        return DEFAULT_EXPOSURE;
    }

    private double readGroundShield(String weatherId) {
        if (currentLocation == null) {
            return DEFAULT_EXPOSURE;
        }
        Object ground = currentLocation.getGround();
        return tryReadShield(ground, weatherId);
    }

    /**
     * Try to call getWeatherProtection(String) on the given object.
     *
     * @param source     object that might support weather shielding
     * @param weatherId  weather we're protecting from
     * @return shielding factor or 1.0 if none
     */
    private double tryReadShield(Object source, String weatherId) {
        if (source == null) {
            return DEFAULT_EXPOSURE;
        }
        try {
            Method m = source.getClass().getMethod("getWeatherProtection", String.class);
            Object v = m.invoke(source, weatherId);
            if (v instanceof Number) {
                return ((Number) v).doubleValue();
            }
        } catch (Throwable ignored) {
            // not a shield
        }
        return DEFAULT_EXPOSURE;
    }
}
