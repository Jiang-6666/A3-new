package game.actors.animals;

import edu.monash.fit2099.engine.actions.*;
import edu.monash.fit2099.engine.actors.*;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.*;

import game.items.weapons.BareFist;
import game.grounds.spawners.Spawner;
import game.actions.*;
import game.actors.Player;
import game.items.Edible;
import game.taming.Tameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all animal actors in the game.
 * <p>
 * The {@code Animal} class defines shared attributes and behaviors among all animal types,
 * including warmth, taming, and interaction logic with the environment.
 * Animals lose warmth each turn and can become unconscious when it reaches zero.
 * </p>
 *
 * <p>
 * Each subclass (e.g. {@code Bear}, {@code Wolf}, {@code Deer}) extends this class and defines
 * specific taming requirements, behaviors, and interactions based on their traits and environment.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Manage warmth and environmental resistance (e.g. cold resistance)</li>
 *   <li>Handle taming and behavior assignment for different animals</li>
 *   <li>Integrate with {@link Spawner} for location-based spawning effects</li>
 * </ul>
 *
 * @author @awan0091
 */
public abstract class Animal extends Actor implements Tameable {

  protected final List<Behaviour> behaviours = new ArrayList<>();
  protected boolean tamed = false;
  protected Player owner;
  protected int warmth;
  protected boolean coldResistant = false;
  protected boolean canConsumeGroundItems = false;

  /**
   * Constructs a new {@code Animal} instance with base attributes.
   *
   * @param name the animal's name
   * @param displayChar the display character for the animal on the map
   * @param hitPoints the initial hit points (health)
   * @param warmthLevel the starting warmth level of the animal
   */
  protected Animal(String name, char displayChar, int hitPoints, int warmthLevel) {
    super(name, displayChar, hitPoints);
    this.warmth = warmthLevel; // A2
    this.setIntrinsicWeapon(new BareFist());
  }

  /** Reduces the animal’s warmth level by one per turn. (A2)*/
  protected void decreaseWarmth() {
    warmth = Math.max(0, warmth - 1);
  }

  /** Sets whether the animal is cold resistant (used for tundra spawns). (A2) */
  public void setColdResistant(boolean isColdResistant) {
    this.coldResistant = isColdResistant;
  }

  /** Sets whether the animal can consume edible items from the ground (used for meadow spawns). (A2) */
  public void setCanConsumeGroundItems(boolean value) {
    this.canConsumeGroundItems = value;
  }

  /**
   * Reduces warmth due to frostbite unless cold resistant.
   *
   * @param amount the warmth reduction value
   *
   * @author @Jiang-6666
   */
  public void sufferFrostbite(int amount) {
    if (!coldResistant) {
      warmth = Math.max(0, warmth - amount);
    }
  }

  @Override
  public boolean isTamed() {
    return tamed;
  }

  /** Defines the taming item type required for this animal (implemented by subclasses). */
  protected abstract Class<?extends Item> getTamingItemType();

  /** Defines the message shown when the animal is successfully tamed. */
  protected abstract String getTamedMessage();

  /** Handles behaviour updates when the animal is tamed. */
  protected abstract void onTamed(Player owner);

  /**
   * Attempts to tame the animal based on an item dropped at the player’s location.
   *
   * @param newOwner the player attempting to tame
   * @param playerLocation the player’s current location
   * @return message indicating the result of the taming attempt
   */
  @Override
  public String attemptTame(Player newOwner, Location playerLocation) {
    if (tamed) return "The " + this.getClass().getSimpleName() + " is already tamed.";

    Item required = findOnGround(playerLocation, getTamingItemType());
    if (required == null)
      return "Drop a " + getTamingItemType().getSimpleName() + " on your tile, then attempt to tame.";

    playerLocation.removeItem(required);
    this.owner = newOwner;
    this.tamed = true;
    behaviours.clear();
    onTamed(owner);
    return getTamedMessage();
  }

  /**
   * Defines the animal’s behavior each turn, including warmth management,
   * environmental reactions, and decision-making through behaviors.
   */
  @Override
  public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {

    // A2: Animal unconscious
    if (!this.isConscious()) {
      this.unconscious(map);
      return new DoNothingAction();
    }

    // A2: Warmth decay
    if (!coldResistant) {
      decreaseWarmth();
    }

    // A2: Feels cold feedback
    if (warmth > 0 && warmth <= 10) {
      display.println(this + " feels cold!");
    }

    // A2: Removed from the game
    if (this.isConscious() && warmth <= 0) {
      if (map.contains(this)) {
        this.hurt(this.getAttribute(BaseAttributes.HEALTH));
        map.removeActor(this);
        display.println(this + " is unconscious without warmth!");
      }
      return new DoNothingAction();
    }

    // A2 (Meadow behaviour)
    if (canConsumeGroundItems) {
      Location here = map.locationOf(this);
      for (Item item : here.getItems()) {
        if (Edible.class.isAssignableFrom(item.getClass())) {
          Edible edible = (Edible) item;
          return new ConsumeAction(item, edible, true);
        }
      }
    }

    for (Behaviour b : behaviours) {
      Action a = b.generateAction(this, map);
      if (a != null) return a;
    }
    return new DoNothingAction();
  }

  /**
   * Called when the animal is spawned by a {@link Spawner}.
   * <p>
   * Applies environmental bonuses such as cold resistance (Tundra)
   * or edible item consumption ability (Meadow).
   * </p>
   *
   * @param spawner the spawning source
   */
  public void onSpawnedFrom(Spawner spawner) {
    // Only apply tundra bonuses
    if (spawner.getClass().getSimpleName().equals("Tundra")) {
      this.setColdResistant(true);
      this.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, 10); // Increases the maximum HP by 10
      this.heal(10);
    }

    // Meadow effects
    if (spawner.getClass().getSimpleName().equals("Meadow")) {
      this.setCanConsumeGroundItems(true);
    }
  }

  @Override
  public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
    ActionList list = super.allowableActions(otherActor, direction, map);
    if (otherActor instanceof Player) {
      list.add(new AttackAction(this, direction));
      if (!tamed) list.add(new TameAction(this, direction));
    }
    return list;
  }

  /** Finds an item on the ground of a given type. */
  private Item findOnGround(Location loc, Class<? extends Item> cls) {
    for (Item i : loc.getItems()) {
      if (cls.isInstance(i)) return i;
    }
    return null;
  }

  @Override
  public String toString() {
    return super.toString() + " [Warmth: " + warmth + "]";
  }
}
