package game.actors.animals;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.UseWeaponOnActorAction;
import game.actors.Player;
import game.effects.FrostbiteEffect;
import game.grounds.Fire;
import game.states.DragonState;
import game.states.EarthenScaleState;
import game.states.FlameHeartState;
import game.states.StormsEyeState;

import java.util.Random;

/**
 * A powerful creature that can switch between three elemental states.
 * This class represents the High Distinction (HD) requirement for a stateful creature.
 * It uses the State design pattern to delegate its behavior to a state object.
 * It also implements the Weapon interface to contain its own attack logic,
 * ensuring consistency with the project's design.
 */
public class ElementalDrake extends Animal implements Weapon {

    // --- Constants for REQ Feedback (Magic Numbers) ---
    private static final int STATE_CHANGE_CHANCE = 25; // %

    private static final int FLAME_HIT_RATE = 85; // %
    private static final int FLAME_DAMAGE = 80;
    private static final int FLAME_DURATION = 3; // turns

    private static final int FROST_HIT_RATE = 70; // %
    private static final int FROST_DAMAGE = 40;
    private static final int FROST_DURATION = 3; // turns

    private static final int EARTH_HIT_RATE = 60; // %
    private static final int EARTH_DAMAGE = 25;
    // --- End Constants ---

    /**
     * The current state object that dictates the drake's behavior.
     */
    private DragonState currentState;

    /**
     * Random number generator for state transitions and attack rolls.
     */
    private final Random random = new Random();

    /**
     * Constructor.
     * Initializes the Elemental Drake with high health and warmth, and sets its
     * initial state to Flame Heart. It is also innately cold-resistant.
     */
    public ElementalDrake() {
        super("Elemental Drake", 'D', 500, 999);
        this.currentState = new FlameHeartState();
        this.setColdResistant(true); // Immune to Frostbite
    }

    /**
     * Called once per turn, this method handles state transitions and delegates
     * action generation to the current state object.
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn.
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        handleStateTransition();
        Action action = currentState.getAction(this, map);
        return (action != null) ? action : new DoNothingAction();
    }

    /**
     * Manages the probabilistic transition between states in a fixed cycle.
     * There is a 25% chance each turn for the drake to shift to the next state.
     * The cycle is: Flame -> Earth -> Storm -> Flame.
     */
    private void handleStateTransition() {
        if (random.nextInt(100) < STATE_CHANGE_CHANCE) { // 25% chance to change state
            if (currentState instanceof FlameHeartState) {
                currentState = new EarthenScaleState();
            } else if (currentState instanceof EarthenScaleState) {
                currentState = new StormsEyeState();
            } else {
                currentState = new FlameHeartState();
            }
        }
    }

    /**
     * Defines the actions that other actors can perform on this Elemental Drake.
     * Primarily allows the Player to attack it.
     *
     * @param otherActor the Actor that might be performing an action.
     * @param direction  String representing the direction of the other Actor.
     * @param map        current GameMap.
     * @return A list of allowable actions.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList list = new ActionList();
        // Allow the player to attack the drake, using the drake itself as the weapon.
        if (this.isConscious() && otherActor instanceof Player) {
            list.add(new UseWeaponOnActorAction(this, null, otherActor, direction));
        }
        return list;
    }

    /**
     * The core attack logic, delegated from a UseWeaponOnActorAction.
     * The actual attack performed is determined by the current state object.
     *
     * @param attacker the actor performing the attack (always this drake).
     * @param target the actor being attacked.
     * @param map the GameMap containing the actors.
     * @return a String describing the result of the attack.
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        return currentState.performAttack(this, target, map);
    }

    /**
     * Executes the fire-based attack for the Flame Heart state.
     * Deals high damage and spawns Fire ground in the surrounding area.
     * @param target the actor to attack.
     * @param map the GameMap.
     * @return a String describing the result.
     */
    public String attackWithFire(Actor target, GameMap map) {
        if (random.nextInt(100) >= FLAME_HIT_RATE) { // 85% hit rate
            return this + " misses " + target + ".";
        }
        target.hurt(FLAME_DAMAGE);
        String result = this + " claws " + target + " for " + FLAME_DAMAGE + " damage.";
        // Spawn surrounding fire
        Location here = map.locationOf(this);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Location groundLocation = map.at(here.x() + dx, here.y() + dy);
                if (map.getXRange().contains(groundLocation.x()) && map.getYRange().contains(groundLocation.y())) {
                    groundLocation.setGround(new Fire(FLAME_DURATION)); // Fire lasts 3 turns
                }
            }
        }
        result += System.lineSeparator() + this + " breathes a torrent of fire!";
        return result;
    }

    /**
     * Executes the frost-based attack for the Storm's Eye state.
     * Deals moderate damage and applies the Frostbite status effect.
     * @param target the actor to attack.
     * @return a String describing the result.
     */
    public String attackWithFrost(Actor target) {
        if (random.nextInt(100) >= FROST_HIT_RATE) { // 70% hit rate
            return this + " misses " + target + ".";
        }
        target.hurt(FROST_DAMAGE);
        target.addStatus(new FrostbiteEffect(FROST_DURATION)); // Official FrostbiteEffect
        return this + " chillingly strikes " + target + " for " + FROST_DAMAGE + " damage and applies Frostbite!";
    }

    /**
     * Executes the physical attack for the Earthen Scale state.
     * Deals low damage with no special effects.
     * @param target the actor to attack.
     * @return a String describing the result.
     */
    public String attackWithEarth(Actor target) {
        if (random.nextInt(100) >= EARTH_HIT_RATE) { // 60% hit rate
            return this + " misses " + target + ".";
        }
        target.hurt(EARTH_DAMAGE);
        return this + " tail whips " + target + " for " + EARTH_DAMAGE + " damage.";
    }

    // --- WEAPON INTERFACE METHODS (Unused for this implementation) ---
    // These methods are required by the Weapon interface but are not directly
    // used because the attack logic is handled by the state-specific methods.
    public int damage() { return 0; }
    public String verb() { return "attacks"; }
    public int chanceToHit() { return 0; }

    // --- UNUSED TAMING METHODS ---
    // The Elemental Drake cannot be tamed.
    @Override
    protected Class<? extends Item> getTamingItemType() { return null; }
    @Override
    protected String getTamedMessage() { return ""; }
    @Override
    protected void onTamed(Player owner) {}

    /**
     * Appends the current state's name to the drake's display string.
     * @return A string representation of the drake, e.g., "Elemental Drake (Flame Heart)".
     */
    @Override
    public String toString() {
        return super.toString() + " (" + currentState.getStateName() + ")";
    }
}