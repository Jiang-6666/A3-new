package game.disease;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Simple fever: damages HP every turn until the actor has spent
 * enough turns in campfire therapy.
 *
 * <p>Healed by {@code game.disease.UnderCampfireTherapy}.</p>
 */
public class FeverDisease extends DiseaseEffect {

    /** Damage per turn from fever. */
    private static final int FEVER_DAMAGE_PER_TURN = 2;

    /** Name of the therapy status class. */
    private static final String CAMPFIRE_THERAPY_CLASS =
            "game.disease.UnderCampfireTherapy";

    /** After we are healed we become inactive. */
    private boolean active = true;

    @Override
    protected void doTick(GameEntity entity, Location location) {
        if (!active) {
            return;
        }

        // if actor is currently under campfire therapy, let therapy handle removal
        if (hasStatusByName(entity, CAMPFIRE_THERAPY_CLASS)) {
            // therapy status will call remove on us later
            return;
        }

        applyDamage(entity, FEVER_DAMAGE_PER_TURN);
    }

    @Override
    public boolean isStatusActive() {
        return active;
    }

    /**
     * Called by therapy (via reflection) when fully healed.
     */
    public void cure() {
        this.active = false;
    }
}
