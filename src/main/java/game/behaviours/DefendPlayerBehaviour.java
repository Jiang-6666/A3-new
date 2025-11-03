package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.taming.Tameable;
import game.traits.Predator;

/** Makes a tamed animal attack hostile predators adjacent to it to defend its owner.
 * <p>“Hostile” means the target implements {@link game.traits.Predator} and is not already
 * allied via {@link game.taming.Tameable}.</p>
 */
public class DefendPlayerBehaviour implements Behaviour {

    private final int damage;
    private final int hitChance;
    private final String verb;

    public DefendPlayerBehaviour(int damage, int hitChance, String verb) {
        this.damage = damage;
        this.hitChance = hitChance;
        this.verb = verb;
    }

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        for (Exit e : here.getExits()) {
            Location dst = e.getDestination();
            Actor target = dst.getActor();
            if (target == null) continue;

            boolean predator = target instanceof Predator;
            boolean allied = (target instanceof Tameable) && ((Tameable) target).isTamed();

            if (predator && !allied) {
                return new AttackAction(target, e.getName(), damage, hitChance, verb);
            }
        }
        return null;
    }
}
