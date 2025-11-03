package game.effects;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A stacking damage-over-time (DoT) effect that represents bleeding.
 *
 * <p>Each active instance hurts the affected {@link Actor} once per tick. Multiple instances may be
 * applied at the same time and their damage stacks linearly.</p>
 *
 * <p><b>REQ3</b>: Applied by Axe on a successful bleed proc.</p>
 */
public class BleedingEffect implements Status {
    /** Damage dealt each tick while the effect is active. */
    private int damagePerTurn;

    /** Number of ticks remaining before the effect expires. */
    private int turnsRemaining;

    /**
     * Creates a bleeding effect.
     *
     * @param damagePerTurn damage dealt per tick (must be non-negative)
     * @param duration number of ticks the effect should last (must be non-negative)
     */
    public BleedingEffect(int damagePerTurn, int duration) {
        this.damagePerTurn = Math.max(0, damagePerTurn);
        this.turnsRemaining = Math.max(0, duration);
    }

    /**
     * Applies one tick of bleeding damage and decreases remaining duration.
     *
     * @param currEntity entity currently being updated (expected to be an {@link Actor})
     * @param location the entity's location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining <= 0) {
            return;
        }
        if (currEntity instanceof Actor) {
            ((Actor) currEntity).hurt(damagePerTurn);
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

    @Override
    public String toString() {
        return "Bleeding (" + turnsRemaining + " turns, " + damagePerTurn + " dmg/turn)";
    }
}
