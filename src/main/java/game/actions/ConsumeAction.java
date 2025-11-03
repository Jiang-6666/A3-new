package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Player;
import game.items.Bottle;
import game.items.Edible;

/** ConsumeAction that handles both EatAction and DrinkAction*/
public class ConsumeAction extends Action {

  private final Item item;
  private final Edible edible;
  private final Bottle bottle;
  private final boolean fromGround;

  public ConsumeAction(Item item, Edible edible, boolean fromGround) {
    this.item = item;
    this.edible = edible;
    this.bottle = null;
    this.fromGround = fromGround;
  }

  // From DrinkAction
  public ConsumeAction(Bottle bottle) {
    this.item = null;
    this.edible = null;
    this.bottle = bottle;
    this.fromGround = false;
  }

  @Override
  public String execute(Actor actor, GameMap map) {
    // DrinkAction
    if (bottle != null) {
      if (!(actor instanceof Player)) {
        return actor + " cannot drink from the bottle.";
      }
      if (bottle.getSips() <= 0) {
        return "The bottle is empty.";
      }
      Player p = (Player) actor;
      bottle.consumeSip();
      p.increaseHydration(4);
      return actor + " drinks from the bottle (" + bottle.getSips() + "/5 sips left).";
    }

    // EatAction
    if (edible == null) return actor + " can't eat " + item + ".";
    String result = edible.consume(actor, map);

    if (fromGround) {
      var location = map.locationOf(actor);
      if (location != null && location.getItems().contains(item)) {
        location.removeItem(item);
      }
    } else {
      actor.removeItemFromInventory(item);
    }

    return result;
  }

  @Override
  public String menuDescription(Actor actor) {
    if (bottle != null)
      return "Drink from bottle (" + bottle.getSips() + "/5)";
    return actor + " eats " + item;
  }
}
