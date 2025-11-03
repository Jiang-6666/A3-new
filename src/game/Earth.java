package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
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
import java.util.Arrays;
import java.util.List;

public class Earth extends World {
    private Player player;
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
        groundCreator.registerGround('^', Fire::new);

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

        // === Create and configure Tele-doors ===
        // Forest Tele-door at (12, 5)
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
        // Forest Teleportation Circle
        TeleportationCircle forestCircle = new TeleportationCircle();
        forestCircle.addDestination(new TeleportDestination(plainsMap, 15, 6, "Plains South Area"));
        forestCircle.addDestination(new TeleportDestination(forestMap, 5, 8, "Forest Southwest"));
        forestMap.at(15, 3).setGround(forestCircle);

        // Plains Teleportation Circle
        TeleportationCircle plainsCircle = new TeleportationCircle();
        plainsCircle.addDestination(new TeleportDestination(forestMap, 15, 3, "Forest Circle"));
        plainsCircle.addDestination(new TeleportDestination(plainsMap, 3, 2, "Plains Northwest"));
        plainsMap.at(8, 5).setGround(plainsCircle);

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
    }

    @Override
    protected boolean stillRunning() {
        return super.stillRunning() && player.isConscious();
    }

}