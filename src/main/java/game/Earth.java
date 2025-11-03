package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.actors.Actor; // REQ5: Added import
import edu.monash.fit2099.engine.actions.DoNothingAction; // REQ5: Added import
import game.actors.Player;
import game.actors.animals.Bear;
import game.actors.animals.Deer;
import game.actors.animals.Wolf;
import game.actors.animals.ElementalDrake;
import game.grounds.*;
import game.grounds.spawners.Cave;
import game.grounds.spawners.Meadow;
import game.grounds.spawners.Spawner;
import game.grounds.spawners.Tundra;
import game.grounds.trees.HazelnutTree;
import game.grounds.trees.WildAppleTree;
import game.grounds.trees.YewBerryTree;
import game.items.*;
import game.teleport.TeleportDestination;

// --- REQ5 IMPORTS ---
import game.weather.WeatherController;
import game.api.WeatherService;
import game.api.OwmWeatherService;
import game.weather.WeatherEffect;
import game.actors.StormSeer;
// --- END REQ5 IMPORTS ---

import java.util.Arrays;
import java.util.List;

/**
 * World assembler for the Earth scenario.
 *
 * <p>Builds maps, registers ground display characters, places actors/items, and starts the game
 * loop. For REQ3/REQ4 testing, the Player may be equipped with Axe, Torch, and Bow at world init
 * (no engine changes required).</p>
 *
 * <p><b>REQ5 Modification:</b>
 * This class now initializes the {@link WeatherController} and {@link WeatherService}.
 * It overrides the {@link #run()} method to inject the API weather logic into the
 * main game loop.
 * </p>
 */
public class Earth extends World {

    private Player player;

    // --- REQ5 FIELDS ---
    /** REQ3/4: Manages applying weather effects to actors/ground. */
    private WeatherController weatherController;
    /** REQ5: Manages fetching weather data from the OWM API. */
    private WeatherService apiWeatherService;
    // --- END REQ5 FIELDS ---

    public Earth(Display display) {
        super(display);
    }

    public void constructWorld() throws Exception {
        // === FIXED: Define groundCreator at the beginning of the method ===
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();

        // Register all ground types
        groundCreator.registerGround('.', Snow::new);
        groundCreator.registerGround('T', WildAppleTree::new);
        groundCreator.registerGround('A', HazelnutTree::new);
        groundCreator.registerGround('Y', YewBerryTree::new);
        groundCreator.registerGround('#', TeleDoor::new);
        groundCreator.registerGround('O', TeleportationCircle::new);
        groundCreator.registerGround('+', Dirt::new);

        // REQ5 Note: We see 'Fire.java' and 'Campfire.java' both use '^'.
        // 'BuildCampfireAction' creates 'Campfire', so we register 'Campfire'
        // for any '^' that might be pre-placed on a map.
        groundCreator.registerGround('^', game.disease.Campfire::new);

        // === Forest map (original) ===
        List<String> forestMapLayout = Arrays.asList(
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................"
        );

        // Now groundCreator is defined and can be used here
        GameMap forestMap = new GameMap("Forest", groundCreator, forestMapLayout);
        this.addGameMap(forestMap);

        // === Plains map for REQ1 ===
        List<String> plainsMapLayout = Arrays.asList(
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                "........................."
        );

        // groundCreator is in scope and can be used here too
        GameMap plainsMap = new GameMap("Plains", groundCreator, plainsMapLayout);
        this.addGameMap(plainsMap);

        // === Place Player ===
        this.player = new Player();
        this.addPlayer(player, forestMap.at(8, 4));
        Spawner.setPlayer(player);

        // === Give player a Teleport Cube ===
        TeleportCube cube = new TeleportCube();
        cube.addDestination(new TeleportDestination(plainsMap, 10, 4, "Plains Center"));
        cube.addDestination(new TeleportDestination(forestMap, 20, 5, "Forest East Side"));
        player.addItemToInventory(cube);

        // ... (Teleport door and circle setup code remains unchanged) ...
        // === Create and configure Tele-doors ===
        TeleDoor forestDoor = new TeleDoor();
        forestDoor.addDestination(new TeleportDestination(plainsMap, 5, 3, "Plains West Side"));
        forestDoor.addDestination(new TeleportDestination(forestMap, 30, 5, "Forest Far East"));
        forestMap.at(12, 5).setGround(forestDoor);

        // Plains Tele-door
        TeleDoor plainsDoor = new TeleDoor();
        plainsDoor.addDestination(new TeleportDestination(forestMap, 12, 5, "Forest Tele-door"));
        plainsDoor.addDestination(new TeleportDestination(plainsMap, 20, 4, "Plains East Side"));
        plainsMap.at(12, 4).setGround(plainsDoor);

        // === Create and configure Teleportation Circles ===
        TeleportationCircle forestCircle = new TeleportationCircle();
        forestCircle.addDestination(new TeleportDestination(plainsMap, 15, 6, "Plains South Area"));
        forestCircle.addDestination(new TeleportDestination(forestMap, 5, 8, "Forest Southwest"));
        forestMap.at(15, 3).setGround(forestCircle);

        // Plains Teleportation Circle
        TeleportationCircle plainsCircle = new TeleportationCircle();
        plainsCircle.addDestination(new TeleportDestination(forestMap, 15, 3, "Forest Circle"));
        plainsCircle.addDestination(new TeleportDestination(plainsMap, 3, 2, "Plains Northwest"));
        plainsMap.at(8, 5).setGround(plainsCircle);


        // ... (Animals, Trees, Items setup code remains unchanged) ...
        // === Animals ===
        forestMap.addActor(new Deer(), forestMap.at(4, 1));
        forestMap.addActor(new Wolf(), forestMap.at(1, 4));
        forestMap.addActor(new Bear(), forestMap.at(4, 4));
        forestMap.addActor(new ElementalDrake(), forestMap.at(4, 3));

        // === Trees ===
        forestMap.at(6, 2).setGround(new WildAppleTree());
        forestMap.at(8, 5).setGround(new HazelnutTree());
        forestMap.at(14, 7).setGround(new YewBerryTree());

        // === Items ===
        forestMap.at(2, 1).addItem(new Meat());
        forestMap.at(2, 2).addItem(new Apple());

        // === Items in the Inventory ===
        player.addItemToInventory(new Apple());
        player.addItemToInventory(new Hazelnut());
        player.addItemToInventory(new YewBerry());

        // ... (Spawners setup code remains unchanged) ...
        // === REQ2: Automatic Animal Spawners ===
        // Forest
        Tundra forestTundra = new Tundra();
        forestTundra.addSpawn(new Bear());
        forestMap.at(3, 3).setGround(forestTundra);

        Cave forestCave = new Cave();
        forestCave.addSpawn(new Bear());
        forestCave.addSpawn(new Wolf());
        forestCave.addSpawn(new Deer());
        forestMap.at(4, 4).setGround(forestCave);

        Meadow forestMeadow = new Meadow();
        forestMeadow.addSpawn(new Deer());
        forestMap.at(8, 8).setGround(forestMeadow);

        // Drop test items to check consumption behaviour
        forestMap.at(8, 8).addItem(new Apple());
        forestMap.at(8, 8).addItem(new Hazelnut());
        forestMap.at(8, 8).addItem(new YewBerry());

        // Plains
        Tundra plainsTundra = new Tundra();
        plainsTundra.addSpawn(new Wolf());
        plainsMap.at(3, 3).setGround(plainsTundra);

        Cave plainsCave = new Cave();
        plainsCave.addSpawn(new Bear());
        plainsCave.addSpawn(new Wolf());
        plainsMap.at(4, 4).setGround(plainsCave);

        Meadow plainsMeadow = new Meadow();
        plainsMeadow.addSpawn(new Deer());
        plainsMeadow.addSpawn(new Bear());
        plainsMap.at(6, 6).setGround(plainsMeadow);

        // Weapons
        player.addItemToInventory(new game.items.weapons.Axe());
        player.addItemToInventory(new game.items.weapons.Torch());
        player.addItemToInventory(new game.items.weapons.Bow());

        // ... (System.out.println sections remain unchanged) ...


        // === REQ3/4 and REQ5 INITIALIZATION ===

        // 1. (REQ3/4) Initialize the Weather Controller
        this.weatherController = new WeatherController();

        // 2. (REQ5) Initialize the API Weather Service
        this.apiWeatherService = new OwmWeatherService();

        // 3. (REQ5) Create and place the StormSeer NPC
        StormSeer stormSeer = new StormSeer(this.weatherController);
        // Place him somewhere in the Forest
        forestMap.at(10, 2).addActor(stormSeer);
    }

    /**
     * REQ5: Override the World's run method.
     * This allows us to inject the API weather logic into the
     * main game loop before each turn is processed.
     */
    @Override
    public void run() {
        // Based on World.java, the base run() method contains the loop
        // that calls gameLoop(). We must replicate that loop here.

        try {
            if (player == null)
                throw new IllegalStateException("Player not set.");

            // initialize the last action map to nothing actions;
            // This loop is copied from World.java's run() method
            for (Actor actor : actorLocations) {
                lastActionMap.put(actor, new DoNothingAction());
            }

            // This loop is basically the whole game
            while (stillRunning()) {

                // --- REQ5 API WEATHER LOGIC (INJECTED) ---
                // 1. Fetch current weather from the API
                WeatherEffect apiWeather = apiWeatherService.getCurrentWeather();

                // 2. Push this weather state to the controller
                if (apiWeather != null) {
                    // FIX: Use the 'gameMaps' field from World.java
                    weatherController.setExternalWeather(apiWeather, gameMaps.toArray(new GameMap[0]));
                }

                // 3. Tick the controller
                // This applies the API weather's effects (actor + ground)
                // FIX: Use the 'gameMaps' field from World.java
                weatherController.tick(gameMaps.toArray(new GameMap[0]));
                // --- END REQ5 LOGIC ---

                // Now, run the original game turn logic from the base World class
                // FIX: Call gameLoop() instead of gameTurn()
                super.gameLoop();
            }

            // After the loop (when stillRunning() is false), print the end game message
            display.println(endGameMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean stillRunning() {
        // player is a field in Earth, so we check it directly
        return super.stillRunning() && player.isConscious();
    }
}