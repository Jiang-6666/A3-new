package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generic melee attack that works across engine variants.
 * - If (damage, hitChance, verb) overrides are provided, they are used (NPC attacks).
 * - Otherwise, it tries the attacker's intrinsic weapon via reflection:
 *     damage()/getDamage(), verb()/getVerb(), chanceToHit()/getHitRate()/hitRate()
 * - Falls back to damage=10, verb="hits", hitChance=100 if not available.
 */

public class AttackAction extends Action {

    protected final Actor target;
    private final String directionText;

    // Optional overrides for NPCs
    private final Integer overrideDamage;
    private final Integer overrideHitChance;
    private final String overrideVerb;

    /** Player-style attack: use attacker's intrinsic weapon (or defaults). */
    public AttackAction(Actor target, String directionText) {
        this(target, directionText, null, null, null);
    }

    /** NPC-style attack: explicit damage/hit/verb regardless of weapon. */
    public AttackAction(Actor target, String directionText, int damage, int hitChance, String verb) {
        this(target, directionText, Integer.valueOf(damage), Integer.valueOf(hitChance), verb);
    }

    private AttackAction(Actor target, String directionText, Integer dmg, Integer hit, String verb) {
        this.target = target;
        this.directionText = directionText == null ? "" : directionText;
        this.overrideDamage = dmg;
        this.overrideHitChance = hit;
        this.overrideVerb = verb;
    }

    @Override
    public String execute(Actor attacker, GameMap map) {
        if (target == null || !map.contains(target)) {
            return attacker + " swings at nothing.";
        }

        int hitChance = (overrideHitChance != null) ? overrideHitChance : readHitChance(attacker, 100);
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll >= hitChance) {
            return attacker + " misses " + target + ".";
        }

        int damage = (overrideDamage != null) ? overrideDamage : readDamage(attacker, 10);
        String verb = (overrideVerb != null) ? overrideVerb : readVerb(attacker, "hits");

        target.hurt(damage);
        String result = attacker + " " + verb + " " + target + " for " + damage + " damage.";
        if (!target.isConscious()) {
            map.removeActor(target);
            result += System.lineSeparator() + target + " collapses.";
        }
        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + (directionText.isEmpty() ? "" : " (" + directionText + ")");
    }

    // ---- Reflection helpers (avoid compile-time dependency on specific IntrinsicWeapon API) ----
    private static int readDamage(Actor attacker, int fallback) {
        try {
            Object w = attacker.getIntrinsicWeapon();
            if (w == null) return fallback;
            Integer v = tryIntNoArg(w, "damage");      if (v != null) return v;
            v = tryIntNoArg(w, "getDamage");           if (v != null) return v;
        } catch (Throwable ignored) {}
        return fallback;
    }

    private static String readVerb(Actor attacker, String fallback) {
        try {
            Object w = attacker.getIntrinsicWeapon();
            if (w == null) return fallback;
            String v = tryStringNoArg(w, "verb");      if (v != null) return v;
            v = tryStringNoArg(w, "getVerb");          if (v != null) return v;
        } catch (Throwable ignored) {}
        return fallback;
    }

    private static int readHitChance(Actor attacker, int fallback) {
        try {
            Object w = attacker.getIntrinsicWeapon();
            if (w == null) return fallback;
            Integer v = tryIntNoArg(w, "chanceToHit"); if (v != null) return v;
            v = tryIntNoArg(w, "getHitRate");          if (v != null) return v;
            v = tryIntNoArg(w, "hitRate");             if (v != null) return v;
        } catch (Throwable ignored) {}
        return fallback;
    }

    private static Integer tryIntNoArg(Object obj, String method) {
        try {
            Method m = obj.getClass().getMethod(method);
            Object val = m.invoke(obj);
            if (val instanceof Integer) return (Integer) val;
            if (val instanceof Number)  return ((Number) val).intValue();
        } catch (Throwable ignored) {}
        return null;
    }

    private static String tryStringNoArg(Object obj, String method) {
        try {
            Method m = obj.getClass().getMethod(method);
            Object val = m.invoke(obj);
            if (val != null) return String.valueOf(val);
        } catch (Throwable ignored) {}
        return null;
    }
}
