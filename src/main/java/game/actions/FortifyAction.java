package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A special action for the Elemental Drake to heal itself by drawing energy
 * from the earth. This fulfills the "new behavior" requirement of REQ5.
 */
public class FortifyAction extends Action {
    /**
     * The amount of HP to heal when this action is executed.
     */
    private static final int HEAL_AMOUNT = 50;

    /**
     * Executes the healing action on the actor.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string describing that the actor healed.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        return actor + " draws energy from the ground, healing for " + HEAL_AMOUNT + " HP.";
    }

    /**
     * A description of the action for the game menu.
     *
     * @param actor The actor performing the action.
     * @return A string to display in the menu.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " draws energy from the ground.";
    }
}