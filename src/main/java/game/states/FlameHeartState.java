package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.UseWeaponOnActorAction;
import game.actors.Player;
import game.actors.animals.ElementalDrake;
import game.behaviours.WanderBehaviour;

/**
 * Represents the Flame Heart (offensive) state of the Elemental Drake.
 * In this state, the drake is aggressive, seeking out the player to attack
 * and spawning fire on the battlefield.
 */
public class FlameHeartState implements DragonState {
    /**
     * A behavior for random movement when no target is in range.
     */
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    /**
     * Generates an action for the drake's turn. It will prioritize attacking
     * an adjacent player. If no player is adjacent, it will wander randomly.
     *
     * @param drake the ElementalDrake instance.
     * @param map the GameMap the drake is on.
     * @return a UseWeaponOnActorAction if a player is adjacent, or a random move action.
     */
    @Override
    public Action getAction(ElementalDrake drake, GameMap map) {
        // Prioritize attacking an adjacent player
        for (Exit exit : map.locationOf(drake).getExits()) {
            Actor target = exit.getDestination().getActor();
            if (target instanceof Player) {
                // Return an action to attack the target using the drake itself as the weapon
                return new UseWeaponOnActorAction(drake, null, target, exit.getName());
            }
        }
        // If no player is adjacent, wander randomly
        return wanderBehaviour.generateAction(drake, map);
    }

    /**
     * Delegates the attack execution to the drake's fire attack method.
     *
     * @param drake the ElementalDrake instance performing the attack.
     * @param target the Actor being attacked.
     * @param map the GameMap.
     * @return a String describing the result of the fire attack.
     */
    @Override
    public String performAttack(ElementalDrake drake, Actor target, GameMap map) {
        return drake.attackWithFire(target, map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateName() { return "Flame Heart"; }
}