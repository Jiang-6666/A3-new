package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.animals.ElementalDrake;

/**
 * An interface for a state in the State design pattern.
 * Each concrete state class will define a specific set of behaviors for the ElementalDrake.
 */
public interface DragonState {
    /**
     * Determines the action the drake should perform on its turn, such as attacking,
     * healing, or moving.
     *
     * @param drake the ElementalDrake instance.
     * @param map the GameMap the drake is on.
     * @return an Action to be executed.
     */
    Action getAction(ElementalDrake drake, GameMap map);

    /**
     * Executes the specific attack logic for this state.
     *
     * @param drake the ElementalDrake instance performing the attack.
     * @param target the Actor being attacked.
     * @param map the GameMap.
     * @return a String describing the result of the attack.
     */
    String performAttack(ElementalDrake drake, Actor target, GameMap map);

    /**
     * Gets the name of the state for display purposes.
     *
     * @return The name of the state as a String.
     */
    String getStateName();
}