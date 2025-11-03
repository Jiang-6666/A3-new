package game.grounds.spawners;

/**
 * Represents the {@code Cave} spawner in the game environment.
 * <p>
 * The {@code Cave} spawns animals every 5 turns with a 100% success rate.
 * Caves are versatile spawners that can produce multiple animal types such as
 * Bears, Wolves, and Deer in a single environment.
 * </p>
 *
 * @author @awan0091
 */
public class Cave extends Spawner {
  /**
   * Constructs a new {@code Cave} spawner.
   */
  public Cave() {
    super('C', "Cave", 5);
    this.spawnChance = 1.0;
  }
}