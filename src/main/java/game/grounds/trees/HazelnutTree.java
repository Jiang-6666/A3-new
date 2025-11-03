package game.grounds.trees;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Hazelnut;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** A tree that periodically drops {@link game.items.Hazelnut} items around itself (every 10 turns). */
public class HazelnutTree extends Ground {

    private int turnCounter = 0;

    public HazelnutTree() {
        super('A', "Hazelnut Tree");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public void tick(Location location) throws GameEngineException {
        super.tick(location);
        turnCounter++;
        if (turnCounter % 10 != 0) return;
        dropOneAdjacent(location, new Hazelnut());
    }

    private void dropOneAdjacent(Location location, Item item) {
        List<Location> neighbours = new ArrayList<>();
        for (Exit e : location.getExits()) {
            neighbours.add(e.getDestination());
        }
        if (neighbours.isEmpty()) return;
        Location dest = neighbours.get(ThreadLocalRandom.current().nextInt(neighbours.size()));
        dest.addItem(item);
    }
}
