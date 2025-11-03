package game.grounds.spawners;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents the {@code Meadow} spawner in the game environment.
 * <p>
 * The {@code Meadow} has a 50% chance of spawning animals every 7 turns.
 * Animals spawned from this terrain can consume edible ground items such as
 * {@code Apple}, {@code Hazelnut}, and {@code YewBerry}, and experience the same effects
 * as the player when consuming them.
 *
 * @author @awan0091
 */

public class Meadow extends Spawner {

  /**
   * Constructs a new {@code Meadow} spawner.
   */
  public Meadow() {
    super('w', "Meadow", 7);
    this.spawnChance = 0.5;
  }

  /**
   * Configures spawned animals to consume ground items such as fruits and nuts.
   * <p>This extends {@link Spawner#spawnCondition(Actor)} by invoking the
   * animal’s {@code setCanConsumeGroundItems(true)} method via reflection.</p>
   *
   * The try&catch sets the flag to allow ground consumption
   *
   * @param animal The actor being spawned
   */
  @Override
  protected void spawnCondition(Actor animal) {
    super.spawnCondition(animal); // calls onSpawnedFrom() method

    try {
      var consumeFlag = animal.getClass().getMethod("setCanConsumeGroundItems", boolean.class);
      consumeFlag.invoke(animal, true);
    } catch (Exception ignored) {}
  }
}

