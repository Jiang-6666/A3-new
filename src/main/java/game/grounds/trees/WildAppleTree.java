package game.grounds.trees;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** A tree that periodically drops {@link game.items.Apple} items around itself (every 3 turns). */

public class WildAppleTree extends Ground {

    private int turnCounter = 0;

    public WildAppleTree() {
        super('T', "Wild Apple Tree");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false; // trees are obstacles
    }

    @Override
    public void tick(Location location) throws GameEngineException {
        super.tick(location);
        turnCounter++;
        if (turnCounter % 3 != 0) return;
        dropOneAdjacent(location, new Apple());
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
