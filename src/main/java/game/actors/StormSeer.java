// Path: src/main/java/game/actors/StormSeer.java

package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.api.DialogueService;
import game.api.GeminiDialogueService;
import game.weather.ApiWeatherBase;
import game.weather.WeatherController;
import game.weather.WeatherEffect;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import game.actions.TalkToAction;
// 引入 AttackAction 以允许玩家攻击 Storm Seer
import game.actions.AttackAction; //

/**
 * REQ5: The Storm Seer NPC ('§').
 *
 * A mystical NPC who observes the weather. When spoken to by the player,
 * this NPC uses the Gemini API (via DialogueService) to provide a
 * cryptic monologue based on the current API-driven weather.
 */
public class StormSeer extends Actor {

    private final DialogueService dialogueService;
    private final WeatherController weatherController;

    /**
     * Constructor for the StormSeer.
     *
     * @param weatherController A reference to the game's global WeatherController.
     * This is used to read the current weather state.
     */
    public StormSeer(WeatherController weatherController) {
        super("Storm Seer", '§', 99999); // Name, Display Char, Hitpoints
        // DIP: Depend on the interface, not the concrete class
        this.dialogueService = new GeminiDialogueService();
        this.weatherController = weatherController;
    }

    /**
     * The StormSeer does not move or attack. He just observes.
     * Returns a DoNothingAction.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // The Seer is passive and does not take actions on his own.
        return new DoNothingAction();
    }

    /**
     * Defines the actions other actors (like the Player) can perform on the StormSeer.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        // 1. 调用 super.allowableActions() 以保留基础动作
        ActionList actions = super.allowableActions(otherActor, direction, map);

        if (otherActor instanceof Player) {
            // 2. 明确添加 AttackAction，确保玩家能攻击 NPC
            // 这使用 Player 的默认武器（例如 BareFist）
            actions.add(new AttackAction(this, direction));

            // 3. 添加 TalkToAction 的逻辑
            WeatherEffect current = weatherController.getCurrentWeather();

            // 只有当是 API 控制的天气 (ApiWeatherBase) 时才提供交谈选项
            if (current != null && current.isApiControlled() && current instanceof ApiWeatherBase) {
                // TalkToAction 构造函数需要 ApiWeatherBase 类型来获取温度和描述
                actions.add(new TalkToAction(this, (ApiWeatherBase) current));
            }
        }

        return actions;
    }

    /**
     * Called by TalkToAction to get the AI-generated monologue.
     * This method delegates the call to the DialogueService.
     *
     * @param weather The current API weather effect.
     * @return The AI-generated monologue string.
     */
    public String getMonologue(ApiWeatherBase weather) {
        // Get the raw data from the weather effect
        String description = weather.getDescription();
        double temperature = weather.getTemperature();

        // Call the API service
        return dialogueService.getStormSeerMonologue(description, temperature);
    }
}