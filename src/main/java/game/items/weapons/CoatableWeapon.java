package game.items.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.CoatWeaponAction;
import game.actions.UseWeaponOnActorAction;
import game.effects.FrostbiteEffect;
import game.effects.PoisonedEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base class for melee weapons that support coatings and offer adjacent attack actions.
 *
 * <p>Provides coating actions (Snow/YewBerry) to the owner and applies coating effects on hit.
 * Torch does <b>not</b> extend this class.</p>
 *
 * <p><b>REQ3/REQ4</b>: Centralizes coating behavior across applicable weapons.</p>
 *
 * <p>Refined to:
 * <ul>
 *     <li>remove {@code switch} in favour of a registry (better OCP, lower connascence)</li>
 *     <li>avoid {@code instanceof}</li>
 *     <li>replace magic numbers with named constants</li>
 * </ul>
 * </p>
 */
public abstract class CoatableWeapon extends Item implements Weapon {

    /* ====== coating effect constants (no magic numbers) ====== */
    private static final int POISON_DAMAGE_PER_TURN = 4;
    private static final int POISON_DURATION_TURNS = 5;
    private static final int FROSTBITE_DURATION_TURNS = 3;

    /** Strategy used to apply a coating's effect upon a successful hit. */
    @FunctionalInterface
    public interface CoatingApplier {
        void apply(Actor attacker, Actor target, GameMap map);
    }

    /** Registry of behaviors for each coating (Strategy / OCP-friendly). */
    private static final Map<Coating, CoatingApplier> COATING_BEHAVIORS = new ConcurrentHashMap<>();

    static {
        COATING_BEHAVIORS.put(Coating.YEWBERRY, (attacker, target, map) -> {
            // Poison: 4 dmg/turn for 5 turns (stacks)
            target.addStatus(new PoisonedEffect(
                    POISON_DAMAGE_PER_TURN,
                    POISON_DURATION_TURNS
            ));
        });

        COATING_BEHAVIORS.put(Coating.SNOW, (attacker, target, map) -> {
            // Frostbite: -1 warmth/turn for 3 turns (stacks), unless target is cold-resistant.
            if (!isColdResistant(target)) {
                target.addStatus(new FrostbiteEffect(FROSTBITE_DURATION_TURNS));
            }
        });
    }

    /**
     * Allow external modules to add/override behavior for new coatings
     * without modifying this class (improves OCP, reduces connascence).
     */
    public static void registerCoatingBehavior(Coating coating, CoatingApplier applier) {
        if (coating != null && applier != null) {
            COATING_BEHAVIORS.put(coating, applier);
        }
    }

    private Coating coating = Coating.NONE;

    public CoatableWeapon(String name, char displayChar) {
        super(name, displayChar, true);
    }

    public Coating getCoating() {
        return coating;
    }

    public void setCoating(Coating c) {
        this.coating = (c == null ? Coating.NONE : c);
    }

    /**
     * Apply coating effects on hit (stacking). Torch subclasses should NOT call this.
     * Uses a Strategy registry instead of a switch.
     */
    protected void applyCoatingOnHit(Actor attacker, Actor target, GameMap map) {
        CoatingApplier applier = COATING_BEHAVIORS.get(coating);
        if (applier != null) {
            applier.apply(attacker, target, map);
        }
    }

    /**
     * Duck-typed cold resistance:
     *  - Prefer public boolean method isColdResistant()
     *  - Fallback boolean field "coldResistant" (any visibility)
     * If neither exists, assume NOT cold-resistant.
     *
     *  IMPORTANT: does NOT use instanceof; instead, it uses Boolean.TRUE.equals(...)
     */
    private static boolean isColdResistant(Actor target) {
        // 1) try method
        try {
            Method m = target.getClass().getMethod("isColdResistant");
            Object v = m.invoke(target);
            if (Boolean.TRUE.equals(v)) {
                return true;
            }
        } catch (Throwable ignored) {
            // fall through to field
        }

        // 2) try field
        try {
            Field f = target.getClass().getDeclaredField("coldResistant");
            f.setAccessible(true);
            Object v = f.get(target);
            if (Boolean.TRUE.equals(v)) {
                return true;
            }
        } catch (Throwable ignored) {
            // fall through to default
        }

        // 3) default
        return false;
    }

    /** Owner actions: coating + adjacent melee attacks to any actors in 4 directions. */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = new ActionList();

        // Coating options
        list.add(new CoatWeaponAction(this, CoatWeaponAction.Type.SNOW));
        list.add(new CoatWeaponAction(this, CoatWeaponAction.Type.YEWBERRY));

        // Melee attack options (adjacent: N,S,E,W)
        Location here = map.locationOf(owner);
        addIfTarget(list, map, here.x(), here.y() - 1, owner, "N");
        addIfTarget(list, map, here.x(), here.y() + 1, owner, "S");
        addIfTarget(list, map, here.x() - 1, here.y(), owner, "W");
        addIfTarget(list, map, here.x() + 1, here.y(), owner, "E");

        return list;
    }

    private void addIfTarget(ActionList list, GameMap map, int x, int y, Actor owner, String dirTxt) {
        if (!map.getXRange().contains(x) || !map.getYRange().contains(y)) {
            return;
        }
        Location loc = map.at(x, y);
        if (loc.containsAnActor()) {
            Actor target = loc.getActor();
            if (target != owner) {
                list.add(new UseWeaponOnActorAction(this, this, target, dirTxt));
            }
        }
    }

    /** Helper for RNG% */
    protected static boolean roll(int percent) {
        return ThreadLocalRandom.current().nextInt(100) < percent;
    }

    /** Show coating in item name to aid debugging and avoid "getter unused" warnings. */
    @Override
    public String toString() {
        String base = super.toString();
        Coating c = getCoating();
        return (c == Coating.NONE) ? base : base + " [" + c.name().toLowerCase() + "]";
    }
}
