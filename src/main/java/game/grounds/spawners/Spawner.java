package game.grounds.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all spawners in the game.
 * <p>
 * The {@code Spawner} class defines the shared logic for automatically spawning animals
 * onto the map under specific environmental and timing conditions.
 * Each subclass (e.g. {@link Tundra}, {@link Meadow}, {@link Cave}) customizes the
 * spawn rate, interval, and additional environmental effects on the spawned animals.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Manage turn-based spawning intervals and random spawn probabilities</li>
 *   <li>Control when and where new {@link Actor} instances (animals) appear</li>
 *   <li>Invoke subclass-specific {@code spawnCondition()} effects (e.g., cold resistance or ground consumption)</li>
 * </ul>
 *
 * @author @awan0091
 */
public abstract class Spawner extends Ground {
  protected final Random random = new Random();
  private int tickCounter = 0;
  private final int spawnInterval;
  protected final List<Actor> spawnOptions = new ArrayList<>(); // Any animal to be spawned
  protected double spawnChance = 1.0;// 0-1 probability
  protected boolean active = false;
  protected static Player playerRef;

  /**
   * Constructs a new {@code Spawner}.
   *
   * @param displayChar the map display character
   * @param name the name of the spawner (Tundra, Cave, Meadow)
   * @param spawnInterval the number of turns between spawn attempts
   */
  protected Spawner(char displayChar, String name, int spawnInterval) {
    super(displayChar, name);
    this.spawnInterval = spawnInterval;
    this.tickCounter = spawnInterval - 1;
  }

  /**
   * Adds an animal type to the spawner’s possible spawn list.
   *
   * @param animal the actor (animal) to be added as a spawn option
   */
  public void addSpawn(Actor animal) {
    spawnOptions.add(animal);
  }

  /**
   * Sets a reference to the player, used to ensure that spawns
   * only occur in the player’s current map.
   *
   * @param player the player reference
   */
  public static void setPlayer(Player player) { playerRef = player; }

  /**
   * Handles the spawner’s tick logic once per game turn.
   * <p>
   * Controls activation, countdown, and triggering of spawn attempts.
   * </p>
   *
   * @param location the spawner’s map location
   * @throws GameEngineException if an engine-related error occurs during spawning
   */
  @Override
  public void tick(Location location) throws GameEngineException {
    super.tick(location);

    // To ensure animal is not in the same map
    if (playerRef == null || !location.map().contains(playerRef)) {
      return;
    }

    if (!active) {
      active = true;
      return;
    }

    // Countdown spawn timer
    tickCounter--;

    if (tickCounter <= 0) {
      tickCounter = spawnInterval; // Reset and check if spawnable when it reaches zero
      spawnablePosition(location);
    }
  }

  /**
   * Determines whether the spawner can create an animal at the given location,
   * and if conditions are met, adds it to the map.
   *
   * @param location the target spawn location
   * @throws GameEngineException if an error occurs while spawning
   */
  private void spawnablePosition(Location location) throws GameEngineException {
    if (location.containsAnActor())
      return;
    if (spawnOptions.isEmpty())
      return;
    if (random.nextDouble() > spawnChance)
      return;

    Actor animal = spawnAnimal();
    if (animal == null)
      return;

    spawnCondition(animal);
//    System.out.println("[SPAWN DEBUG] " + animal.getClass().getSimpleName() +
//        " spawned at (" + location.x() + "," + location.y() + ") on " + location.map());
    location.addActor(animal);
  }

  /**
   * Randomly selects an animal type from the spawn list and creates a new instance.
   * Ensure chosen at random for equal probability/chances.
   *
   * @return the newly created animal instance, or null if instantiation fails
   */
  private Actor spawnAnimal() {
    int index = random.nextInt(spawnOptions.size());

    // New instance per spawn to avoid sharing state
    Actor base = spawnOptions.get(index);
    try {
      return base.getClass().getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Applies subclass-specific spawn effects to an animal.
   * <p>
   * This method uses reflection to call {@code onSpawnedFrom(Spawner)} in the
   * spawned animal class if defined.
   * </p>
   *
   * @param animal the newly spawned animal
   */
  protected void spawnCondition(Actor animal) {
    try {
      // Use reflection to call onSpawnedFrom if it exists
      var method = animal.getClass().getMethod("onSpawnedFrom", Spawner.class);
      method.invoke(animal, this);
    } catch (NoSuchMethodException ignored) {
      // Not all animals implement this method
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

