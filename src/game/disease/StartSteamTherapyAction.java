package game.disease;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Action to start steam therapy while standing on a {@link SteamHut}.
 */
public class StartSteamTherapyAction extends Action {

    private static final String STEAM_THERAPY_CLASS =
            "game.disease.UnderSteamTherapy";

    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        if (!(here.getGround() instanceof SteamHut)) {
            return "You must stand in a Steam Hut to start therapy.";
        }
        try {
            Class<?> c = Class.forName(STEAM_THERAPY_CLASS);
            Object status = c.getDeclaredConstructor().newInstance();
            actor.getClass().getMethod("addStatus",
                            edu.monash.fit2099.engine.capabilities.Status.class)
                    .invoke(actor, status);
            return actor + " starts steam therapy.";
        } catch (Throwable e) {
            return "Failed to start steam therapy.";
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Start steam therapy";
    }

    @Override
    public String hotkey() {
        return "";
    }
}
