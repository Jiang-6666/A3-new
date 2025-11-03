package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import game.items.weapons.CoatableWeapon;
import game.items.weapons.Coating;
import game.grounds.Snow;
/**
 * Action that coats a {@link CoatableWeapon} with either Snow or Yew Berry.
 *
 * <p>Coating with Snow requires the actor to stand on a {@link Snow} tile. Coating with Yew Berry
 * consumes one berry from the actor's inventory. Re-coating replaces the previous coating.</p>
 *
 * <p><b>REQ4</b>: Adds the weapon coating system without modifying the engine.</p>
 */

public class CoatWeaponAction extends Action {
    public enum Type { YEWBERRY, SNOW }

    private final CoatableWeapon weapon;
    private final Type type;

    public CoatWeaponAction(CoatableWeapon weapon, Type type) {
        this.weapon = weapon;
        this.type = type;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        switch (type) {
            case YEWBERRY: {
                Item found = null;
                for (Item it : actor.getItemInventory()) {
                    if (it instanceof YewBerry) { found = it; break; }
                }
                if (found == null) return "No Yew Berry to use for coating.";
                actor.removeItemFromInventory(found); // consume
                weapon.setCoating(Coating.YEWBERRY);
                return actor + " coats " + weapon + " with Yew Berry (poison).";
            }
            case SNOW: {
                Location here = map.locationOf(actor);
                if (!(here.getGround() instanceof Snow)) {
                    return "You must be standing on snow to coat with snow.";
                }
                weapon.setCoating(Coating.SNOW);
                return actor + " coats " + weapon + " with Snow (frostbite).";
            }
        }
        return actor + " does nothing.";
    }

    @Override
    public String menuDescription(Actor actor) {
        String what = type == Type.YEWBERRY ? "Yew Berry" : "Snow";
        return "Coat " + weapon + " with " + what;
    }
}
