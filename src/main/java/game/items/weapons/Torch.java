package game.items.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.UseWeaponOnActorAction;
import game.effects.BurningEffect;
import game.grounds.Fire;

/**
 * Torch weapon ({@code 'y'}).
 *
 * <p>Deals 10 damage with a 50% chance to hit. On hit the target gains {@link BurningEffect}
 * for 7 turns dealing 3 damage/turn (stacks). Each swing also spawns {@link Fire} on the 8 tiles
 * adjacent to the attacker for 5 turns.</p>
 *
 * <p><b>REQ3</b>: Implements the Torch combat and area ignition behavior. Torches cannot be
 * coated.</p>
 */
public class Torch extends Item implements Weapon {
    /** Constructs a new Torch item. */
    public Torch() {
        super("Torch", 'y', true);
    }

    /**
     * Performs the attack. On miss, only the surrounding fire spawns; on hit, burning is applied to
     * the target in addition to damage.
     *
     * @param attacker the attacking actor
     * @param target the target actor
     * @param map the game map
     * @return a human-readable result string
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) >= 50) {
            spawnSurroundingFire(map.locationOf(attacker), map, 5);
            return attacker + " swings Torch at " + target + " but misses. Flames flare around!";
        }
        int dmg = 10;
        target.hurt(dmg);
        target.addStatus(new BurningEffect(3, 7));
        spawnSurroundingFire(map.locationOf(attacker), map, 5);
        return attacker + " scorches " + target + " with Torch for " + dmg + " damage. The target catches fire!";
    }

    /**
     * Spawns temporary {@link Fire} terrain in the 8 neighboring tiles around {@code center}.
     *
     * @param center the central location (attacker's tile)
     * @param map the game map
     * @param duration number of ticks the fire should last
     */
    private void spawnSurroundingFire(Location center, GameMap map, int duration) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int nx = center.x() + dx;
                int ny = center.y() + dy;
                if (map.getXRange().contains(nx) && map.getYRange().contains(ny)) {
                    map.at(nx, ny).setGround(new Fire(duration));
                }
            }
        }
    }

    /**
     * Offers adjacent melee attack actions using the torch.
     *
     * @param owner the torch owner
     * @param map the game map
     * @return an {@link ActionList} with attack actions for adjacent targets
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = new ActionList();
        Location here = map.locationOf(owner);
        addIfTarget(list, map, here.x(), here.y() - 1, owner, "N");
        addIfTarget(list, map, here.x(), here.y() + 1, owner, "S");
        addIfTarget(list, map, here.x() - 1, here.y(), owner, "W");
        addIfTarget(list, map, here.x() + 1, here.y(), owner, "E");
        return list;
    }

    private void addIfTarget(
            ActionList list, GameMap map, int x, int y, Actor owner, String dirTxt) {
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
}
