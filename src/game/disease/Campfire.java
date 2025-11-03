package game.disease;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A ground tile representing an active campfire.
 *
 * <p>While fuel lasts, any actor standing on it is given
 * {@link UnderCampfireTherapy}. When fuel runs out, it becomes dirt-like
 * (here we just keep it as burnt campfire but inactive).</p>
 */
public class Campfire extends TreatmentStation {

    /** How many turns the fire lasts. */
    private static final int INITIAL_FUEL_TURNS = 8;

    /** Class name of the therapy status we apply. */
    private static final String CAMPFIRE_THERAPY_CLASS =
            "game.disease.UnderCampfireTherapy";

    private int remainingFuel = INITIAL_FUEL_TURNS;

    public Campfire() {
        super('^', "Campfire");
    }

    @Override
    public void tick(Location location) {
        super.tick(location);

        if (remainingFuel <= 0) {
            return;
        }

        // if someone is standing here, give them therapy
        if (location.containsAnActor()) {
            Actor a = location.getActor();
            // add status by reflection so we do not depend on constructor in here
            try {
                // create new UnderCampfireTherapy()
                Class<?> clazz = Class.forName(CAMPFIRE_THERAPY_CLASS);
                Object therapy = clazz.getDeclaredConstructor().newInstance();
                // actor.addStatus(...)
                a.getClass().getMethod("addStatus", edu.monash.fit2099.engine.capabilities.Status.class)
                        .invoke(a, therapy);
            } catch (Throwable ignored) { }
        }

        remainingFuel--;
    }

    /**
     * @return true if fire is still burning
     */
    public boolean isActive() {
        return remainingFuel > 0;
    }
}
