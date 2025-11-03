package game.effects;

import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Burning status effect that deals damage over time.
 * Stacks with multiple applications.
 */
public class BurningEffect implements Status {
    private int damagePerTurn;
    private int turnsRemaining;

    public BurningEffect(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.turnsRemaining = duration;
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (entity instanceof Actor && turnsRemaining > 0) {
            Actor actor = (Actor) entity;
            actor.hurt(damagePerTurn);
            new Display().println(actor + " is burned, losing " + damagePerTurn + " HP");
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    @Override
    public String toString() {
        return "Burning (" + turnsRemaining + " turns, " + damagePerTurn + " damage/turn)";
    }
}