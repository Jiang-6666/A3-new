package game.disease;

import edu.monash.fit2099.engine.items.Item;

/**
 * A simple bundle of wood used to build a campfire.
 *
 * <p>Player can carry it and later use {@link game.disease.BuildCampfireAction}
 * to turn it into a {@link game.disease.Campfire} ground.</p>
 */
public class WoodBundle extends Item {

    private static final char DISPLAY_CHAR = 'w';

    public WoodBundle() {
        super("Wood Bundle", DISPLAY_CHAR, true);
    }
}
