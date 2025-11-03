package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.actors.Player;

/** If an adjacent target is available, returns an {@link game.actions.AttackAction}.
 * <p>Configured with damage, hit chance and verb so species can share the behaviour.</p>
 */
public class AttackBehaviour implements Behaviour {

    private final int damage;
    private final int hitChance;
    private final String verb;

    public AttackBehaviour(int damage, int hitChance, String verb) {
        this.damage = damage;
        this.hitChance = hitChance;
        this.verb = verb;
    }

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        for (Exit e : here.getExits()) {
            Actor target = e.getDestination().getActor();
            if (target instanceof Player) {
                return new AttackAction(target, e.getName(), damage, hitChance, verb);
            }
        }
        return null;
    }
}
