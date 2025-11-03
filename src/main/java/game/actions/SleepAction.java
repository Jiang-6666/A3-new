package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Player;

/** Skips 6–10 turns while the player sleeps on a dropped {@link game.items.Bedroll}.
 * <p>During sleep, hydration and warmth do not decrease.</p>
 */

public class SleepAction extends Action {

    private final int turns;

    /**
     * @param turns number of turns to sleep (e.g., 6–10 per spec)
     */
    public SleepAction(int turns) {
        this.turns = Math.max(1, turns);
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor instanceof Player) {
            Player p = (Player) actor;
            p.startSleeping(turns);
            return actor + " sleeps for " + turns + " turns. Time passes quietly...";
        }
        return actor + " tries to sleep, but nothing happens.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Sleep on bedroll (" + turns + " turns)";
    }
}
