package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.actions.FortifyAction;
import game.actors.animals.ElementalDrake;
import game.behaviours.WanderBehaviour;
import game.grounds.Dirt;

/**
 * Represents the Earthen Scale (defensive) state of the Elemental Drake.
 * In this state, the drake is passive, focusing on healing itself when on
 * Dirt tiles and wandering otherwise. Its attacks are weaker.
 */
public class EarthenScaleState implements DragonState {
    /**
     * A behavior for random movement when not healing.
     */
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    /**
     * Generates an action for the drake's turn. It prioritizes healing by
     * performing a FortifyAction if it is standing on Dirt. Otherwise, it will
     * wander randomly. It will not proactively attack in this state.
     *
     * @param drake the ElementalDrake instance.
     * @param map the GameMap the drake is on.
     * @return a FortifyAction if on Dirt, otherwise a random move action.
     */
    @Override
    public Action getAction(ElementalDrake drake, GameMap map) {
        Ground currentGround = map.locationOf(drake).getGround();
        if (currentGround instanceof Dirt) {
            return new FortifyAction();
        }
        // If not on Dirt, wander randomly.
        return wanderBehaviour.generateAction(drake, map);
    }

    /**
     * Delegates the attack execution to the drake's weak earth attack method.
     *
     * @param drake the ElementalDrake instance performing the attack.
     * @param target the Actor being attacked.
     * @param map the GameMap.
     * @return a String describing the result of the earth attack.
     */
    @Override
    public String performAttack(ElementalDrake drake, Actor target, GameMap map) {
        return drake.attackWithEarth(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateName() { return "Earthen Scale"; }
}