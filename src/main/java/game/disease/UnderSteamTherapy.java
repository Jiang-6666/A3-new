package game.disease;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Method;

/**
 * Status added when the actor starts steam therapy inside a {@link SteamHut}.
 * After enough ticks, it cures {@link FrostFluDisease}.
 */
public class UnderSteamTherapy implements Status {

    /** Number of ticks required to cure. */
    private static final int REQUIRED_STEAM_TURNS = 4;

    /** Disease to cure. */
    private static final String FLU_CLASS = "game.disease.FrostFluDisease";

    private int steamTicks = 0;
    private boolean active = true;

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (!active) {
            return;
        }
        steamTicks++;

        if (steamTicks >= REQUIRED_STEAM_TURNS) {
            // look for frost flu and cure it
            try {
                java.util.List<?> statuses =
                        (java.util.List<?>) entity.getClass().getMethod("getStatuses").invoke(entity);
                Object flu = null;
                for (Object s : statuses) {
                    if (s != null && FLU_CLASS.equals(s.getClass().getName())) {
                        flu = s;
                        break;
                    }
                }
                if (flu != null) {
                    Method cure = flu.getClass().getMethod("cure");
                    cure.invoke(flu);
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
