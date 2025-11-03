package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Choose a random adjacent enterable tile and move there. */
public class WanderBehaviour implements Behaviour {

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        List<Exit> options = new ArrayList<>();
        for (Exit e : here.getExits()) {
            Location dest = e.getDestination();
            if (dest.canActorEnter(actor) && dest.getActor() == null) {
                options.add(e);
            }
        }
        if (options.isEmpty()) return null;
        Exit choice = options.get(ThreadLocalRandom.current().nextInt(options.size()));
        return new MoveActorAction(choice.getDestination(), choice.getName());
    }
}
