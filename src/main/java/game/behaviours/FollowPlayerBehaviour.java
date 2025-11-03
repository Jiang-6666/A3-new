package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Player;

/** Move one step toward the player (greedy) if possible. */
public class FollowPlayerBehaviour implements Behaviour {

    private final Player owner;

    public FollowPlayerBehaviour(Player owner) {
        this.owner = owner;
    }

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        if (owner == null || !map.contains(owner)) return null;

        Location here = map.locationOf(actor);
        Location there = map.locationOf(owner);
        int bestDist = dist(here, there);
        Exit best = null;

        for (Exit e : here.getExits()) {
            if (!e.getDestination().canActorEnter(actor) || e.getDestination().getActor() != null) continue;
            int d = dist(e.getDestination(), there);
            if (d < bestDist) { bestDist = d; best = e; }
        }
        if (best == null) return null;
        return new MoveActorAction(best.getDestination(), best.getName());
    }

    private int dist(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
