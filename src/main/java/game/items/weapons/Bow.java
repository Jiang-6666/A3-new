package game.items.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ShootBowAction;

/**
 * Bow weapon ({@code 'c'}).
 *
 * <p>Deals 5 damage with a 25% chance to hit and can target actors within Manhattan distance
 * ≤ 3. Coating effects apply on hit.</p>
 *
 * <p><b>REQ3</b>: Implements ranged combat without changing engine code.</p>
 */
public class Bow extends CoatableWeapon {
    /** Constructs a new Bow item. */
    public Bow() {
        super("Bow", 'c');
    }

    /**
     * Performs the ranged attack logic for the bow (also used for adjacent shots).
     *
     * @param attacker the attacking actor
     * @param target the target actor
     * @param map the game map
     * @return a human-readable result string
     */
    public String rangedAttack(Actor attacker, Actor target, GameMap map) {
        if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) >= 25) {
            return attacker + " shoots at " + target + " with Bow but misses.";
        }
        int dmg = 5;
        target.hurt(dmg);
        applyCoatingOnHit(attacker, target, map);
        return attacker + " shoots " + target + " with Bow for " + dmg + " damage.";
    }

    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        return rangedAttack(attacker, target, map);
    }

    /**
     * Provides coating actions and, for convenience, ranged shot actions to all targets within
     * Manhattan distance ≤ 3.
     *
     * @param owner the bow owner
     * @param map the game map
     * @return an {@link ActionList} containing coating and shot actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = super.allowableActions(owner, map);
        Location here = map.locationOf(owner);
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (Math.abs(dx) + Math.abs(dy) == 0) {
                    continue;
                }
                if (Math.abs(dx) + Math.abs(dy) > 3) {
                    continue;
                }
                int nx = here.x() + dx;
                int ny = here.y() + dy;
                if (!map.getXRange().contains(nx) || !map.getYRange().contains(ny)) {
                    continue;
                }
                Location loc = map.at(nx, ny);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target != owner) {
                        list.add(new ShootBowAction(this, target));
                    }
                }
            }
        }
        return list;
    }
}
