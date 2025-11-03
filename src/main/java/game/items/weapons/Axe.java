package game.items.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.BleedingEffect;

/**
 * Axe weapon ({@code 'p'}).
 *
 * <p>Deals 15 damage with a 75% chance to hit. On hit there is a 50% chance to apply
 * {@link BleedingEffect} (10 damage/turn for 2 turns). Coating effects are also applied on hit.</p>
 *
 * <p><b>REQ3</b>: Implements the Axe combat behavior.</p>
 */
public class Axe extends CoatableWeapon {
    /** Constructs a new Axe item. */
    public Axe() {
        super("Axe", 'p');
    }

    /**
     * Performs the melee attack logic for Axe.
     *
     * @param attacker the attacking actor
     * @param target the target actor
     * @param map the game map
     * @return a human-readable result string
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        if (!roll(75)) {
            return attacker + " swings Axe at " + target + " but misses.";
        }
        int dmg = 15;
        target.hurt(dmg);
        StringBuilder sb =
                new StringBuilder(attacker + " chops " + target + " with Axe for " + dmg + " damage.");
        if (roll(50)) {
            target.addStatus(new BleedingEffect(10, 2));
            sb.append(" The target starts bleeding!");
        }
        applyCoatingOnHit(attacker, target, map);
        return sb.toString();
    }
}
