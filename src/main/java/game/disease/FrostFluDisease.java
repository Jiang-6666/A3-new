package game.disease;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Frost flu: mainly reduces warmth every turn; healed in a steam hut.
 *
 * <p>Healed by {@code game.disease.UnderSteamTherapy}.</p>
 */
public class FrostFluDisease extends DiseaseEffect {

    /** Warmth lost per turn. */
    private static final int FLU_WARMTH_LOSS = 1;

    /** Name of the therapy status class. */
    private static final String STEAM_THERAPY_CLASS =
            "game.disease.UnderSteamTherapy";

    private boolean active = true;

    @Override
    protected void doTick(GameEntity entity, Location location) {
        if (!active) {
            return;
        }

        // if under correct therapy, do not worsen
        if (hasStatusByName(entity, STEAM_THERAPY_CLASS)) {
            return;
        }

        reduceWarmth(entity, FLU_WARMTH_LOSS);
    }

    @Override
    public boolean isStatusActive() {
        return active;
    }

    /** Called by therapy when finished. */
    public void cure() {
        this.active = false;
    }
}
