package game.disease;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A steam hut / sweat lodge. Actors can start steam therapy here
 * using {@link StartSteamTherapyAction}.
 *
 * <p>The tile itself refreshes the therapy status every tick if
 * the actor stays inside.</p>
 */
public class SteamHut extends TreatmentStation {

    /** therapy class name */
    private static final String STEAM_THERAPY_CLASS =
            "game.disease.UnderSteamTherapy";

    public SteamHut() {
        super('S', "Steam Hut");
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        if (!location.containsAnActor()) {
            return;
        }
        Actor a = location.getActor();
        // if already has therapy, let it continue; if not, we do nothing
        // (actor must explicitly start therapy with action)
        // but if we want auto-refresh:
        if (!hasStatus(a, STEAM_THERAPY_CLASS)) {
            return;
        }
        // refresh = nothing to do (status itself counts turns)
    }

    private boolean hasStatus(Actor actor, String className) {
        try {
            java.util.List<?> statuses =
                    (java.util.List<?>) actor.getClass().getMethod("getStatuses").invoke(actor);
            for (Object s : statuses) {
                if (s != null && className.equals(s.getClass().getName())) {
                    return true;
                }
            }
        } catch (Throwable ignored) { }
        return false;
    }
}
