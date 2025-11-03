package game.grounds.spawners;

/**
 * Represents the {@code Tundra} spawner in the game environment.
 * <p>
 * The {@code Tundra} is a type of {@link Spawner} that has a low 5% chance of spawning animals
 * every turn. Animals spawned from the Tundra gain cold resistance and an additional 10 maximum HP.
 * </p>
 *
 * <p><b>Usage:</b> Used to automatically spawn cold-resistant animals such as Bears and Wolves
 * in tundra regions of the map.</p>
 *
 * @author @awan0091
 */

public class Tundra extends Spawner {

  /**
   * Constructs a new {@code Tundra} spawner.
   */
  public Tundra() {
    super('_', "Tundra", 1);
    this.spawnChance = 0.05;
  }
}

