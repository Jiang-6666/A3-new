package game.disease;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Method;

/**
 * Status attached when actor stands on a burning {@link Campfire}.
 * Counts consecutive turns of heat; when enough turns collected,
 * it tries to cure {@link FeverDisease}.
 */
public class UnderCampfireTherapy implements Status {

    /** Number of turns of heat required to cure fever. */
    private static final int REQUIRED_HEAT_TURNS = 3;

    /** Fever class name to cure. */
    private static final String FEVER_CLASS = "game.disease.FeverDisease";

    private int heatTicks = 0;
    private boolean active = true;

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (!active) {
            return;
        }
        heatTicks++;

        if (heatTicks >= REQUIRED_HEAT_TURNS) {
            // try to find FeverDisease on the entity and call cure()
            try {
                java.util.List<?> statuses =
                        (java.util.List<?>) entity.getClass().getMethod("getStatuses").invoke(entity);
                Object fever = null;
                for (Object s : statuses) {
                    if (s != null && FEVER_CLASS.equals(s.getClass().getName())) {
                        fever = s;
                        break;
                    }
                }
                if (fever != null) {
                    // call cure()
                    Method cure = fever.getClass().getMethod("cure");
                    cure.invoke(fever);
                    // remove ourselves
                    entity.getClass().getMethod("removeStatus", Status.class).invoke(entity, this);
                    active = false;
                }
            } catch (Throwable ignored) { }
        }
    }

    @Override
    public boolean isStatusActive() {
        return active;
    }
}
