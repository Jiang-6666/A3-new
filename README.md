# FIT2099 Assignment (Semester 2, 2025)
```                                                                             
`7MMF'     A     `7MF'`7MMF'`7MN.   `7MF'MMP""MM""YMM `7MM"""YMM  `7MM"""Mq.  
  `MA     ,MA     ,V    MM    MMN.    M  P'   MM   `7   MM    `7    MM   `MM. 
   VM:   ,VVM:   ,V     MM    M YMb   M       MM        MM   d      MM   ,M9  
    MM.  M' MM.  M'     MM    M  `MN. M       MM        MMmmMM      MMmmdM9   
    `MM A'  `MM A'      MM    M   `MM.M       MM        MM   Y  ,   MM  YM.   
     :MM;    :MM;       MM    M     YMM       MM        MM     ,M   MM   `Mb. 
      VF      VF      .JMML..JML.    YM     .JMML.    .JMMmmmmMMM .JMML. .JMM.
```

### Contribution Log
https://docs.google.com/spreadsheets/d/1ez0N13NdYaHnNk5-XAbfXox9lezkiv1yeXDEAiv4xmk/edit?usp=sharing


REQ3 and REQ4: CREATIVE MODE
Weather System & Disease Infection

Goal: Add a global dynamic weather system and a transmissible disease system that directly affect Hydration and Warmth for both player and animals—without modifying the engine.
Originality: Environment-driven survival and contagion (not more weapons/teleport). Clear numbers, clear runtime flow, easy to test.

(1) Scenario (Player Perspective)

-A polar wasteland cycles between Rainstorm, Windstorm, and Blizzard.
-Weather accelerates dehydration or hypothermia.
-Diseases spread between animals and the player; infected actors lose HP each turn until cured by the correct fruit.
-Survival demands reading the weather, planning routes, and managing supplies.

(2) Feature Overview (What & Why)

-Dynamic Weather (global)
World cycles across Rainstorm / Windstorm / Blizzard.
Each weather applies per-turn adjustments to Hydration/Warmth for all actors.
Each spell lasts 8–12 turns (uniform random); after that, weather switches by probability (Rain 40% / Wind 30% / Blizzard 30%, tunable).
Single Weather Controller broadcasts effects to avoid engine edits.

-Disease Infection (actor-level)
On infection, actors lose HP per turn; some diseases also drain Warmth.
Adjacency transmission (N/S/E/W). Weather exposure can trigger certain diseases.
Curative Fruits remove the matching disease immediately on consumption.

-Implementation stance
Use duck typing / reflection to call increase/decreaseHydration/Warmth (or fallback to fields if present).
Keep the engine untouched; integrate via Actions, Items, and status/effect ticking.

(3) Rules & Numbers (default; tunable)

-Weather duration & switching
Each weather lasts 8–12 turns.
Switch probabilities (on expiry): Rain 40%, Wind 30%, Blizzard 30%.

-Rainstorm (per turn)
Hydration: mitigates dehydration by +1 vs. baseline drain (min clamp at 0).
Warmth: −1 (damp chill).
Optional flavor: every 3 turns, spawn a decorative puddle near the player.

-Windstorm (per turn)
Hydration: extra −1 (exertion / wind).
Warmth: −1 every 2 turns (wind chill).
Optional: ranged hit chance −10% (omit if you prefer no combat impact).

-Blizzard (per turn)
Hydration: choose 0 or extra −1 (dry cold; pick one for balance).
Warmth: −2 (severe hypothermia).
Optional flavor: snowy footprints/trace.

-Diseases
Fever
Effect: −3 HP per turn.
Transmission: 30% per turn to each orthogonally adjacent non-infected actor.
Cure: Sunberry (consume → Fever removed immediately).
FrostFlu
Effect: −2 HP per turn and −1 Warmth per turn.
Transmission: 20% per turn to orthogonal neighbors.
Blizzard trigger: while in Blizzard, if avg Warmth ≤ 2 over the last 3 turns, 15% chance to gain FrostFlu.
Cure: Snowcap Berry (consume → FrostFlu removed immediately).

-Cure semantics
Consuming the correct fruit removes its disease this turn.
Consuming the wrong fruit does not cure (you decide if it still consumes the item; state your rule clearly).

(4) New Classes (meets Creative Mode structure)

-Manager (1)
WeatherController (global; holds current weather, remaining turns, switch probabilities; applies per-turn adjustments to all actors; optional decorative ground spawns).

-Interface / Abstract (1–2)
WeatherEffect (interface): applyTick(actor), optional onEnter() / onExit().
DiseaseEffect (abstract; recommended): centralizes per-turn HP drain + cureTag.

-Concrete (≥3)
Weather effects: RainstormWeather, WindstormWeather, BlizzardWeather (implement WeatherEffect).
Diseases: FeverDisease, FrostFluDisease (extend DiseaseEffect).
Fruits: Sunberry (cures Fever), SnowcapBerry (cures FrostFlu).
Action: EatCureFruitAction (adds “Consume & Cure” in the menu).

-Engine components used (≥2 satisfied)
Action (consume fruit), Item (fruits), Actor (player/animals).
Optional Ground if you add puddles/snowdrifts.

(5) Turn Flow (runtime execution)

-Weather stage
WeatherController decrements remaining turns.
If it reaches 0, switch weather by probability; call new weather’s onEnter().

-Apply weather
Iterate all actors; call current weather’s applyTick(actor) to adjust Hydration/Warmth.

-Disease spread / trigger
For each infected actor, test adjacency transmission to each orthogonal neighbor (Fever 30%, FrostFlu 20%).
If Blizzard and avg Warmth ≤ 2 (last 3 turns), roll 15% to gain FrostFlu.

-Actor actions
If inventory has a matching cure fruit, show “Consume & Cure”; executing it removes the disease immediately.

-Status resolution
Diseases/effects tick (HP and/or Warmth loss) until cured or expired.

(6) QA / Testing Checklist

Weather switching works (8–12-turn spans; probabilities honored; onEnter/onExit fire correctly).
Hydration/Warmth adjustments match the bullet rules each turn (respect min/max bounds).
Adjacency transmission occurs at approximately configured rates when standing next to infected actors; no infection when not adjacent.
Blizzard trigger fires only under Blizzard + avg Warmth ≤ 2 (last 3 turns) at ~15%; otherwise does not.
Cures remove the disease immediately when the correct fruit is consumed; wrong fruit does not cure (and follows your stated consume/not-consume rule).
Compatible with movement, combat, and inventory; no engine modifications.

REQ5 Proposal: Project Chimera - The Storm Seer's Prophecy ⛈️
Core Concept
This feature integrates two APIs to create a dynamic weather system personified by a new, mystical NPC: The Storm Seer. The game will fetch real-world weather data to alter the environment and survival mechanics. The Storm Seer, a rare wandering NPC, will interpret this weather, providing the player with unique, AI-generated monologues that serve as both lore and a gameplay hint about current and upcoming conditions.

APIs of Choice
This project will utilize two distinct APIs for a layered integration:

OpenWeatherMap API (Systemic API):

Role: To provide the raw, real-world weather data (e.g., "Rain," "Snow," current temperature). This API drives the systemic environmental changes.

Justification: It has a reliable free tier and provides the exact data points needed to influence the game's core mechanics.

Google Gemini API (Narrative API):

Role: To generate creative, thematic dialogue for the Storm Seer NPC. This API provides the narrative layer.


Justification: It directly follows the example given in the requirement  and allows for highly dynamic, non-repetitive NPC interactions.

Key Features & Gameplay Integration
Dynamic Weather System (Driven by OpenWeatherMap):

Dynamic Warmth: The warmth decay rate will be tied to the real-world temperature. A cold day in Melbourne means faster warmth drain in-game.

Environmental Effects: "Rain" will extinguish fires faster, while "Snow" will cause Snow ground tiles to appear and significantly increase warmth drain.

The Storm Seer (New NPC, Driven by Gemini):

Unique Interaction: A new NPC, the Storm Seer, will occasionally appear on the map. When the player interacts with him, the game will trigger a call to the Gemini API.

AI-Generated Monologues: The prompt sent to Gemini will include the current weather data fetched from OpenWeatherMap. For example: "Generate a short, cryptic monologue from the perspective of a mystical seer about the current weather, which is 'Light Rain, 10°C'."

Gameplay Hint: The Storm Seer's response will not just be flavor text. It will be a cryptic hint about the current conditions and what might come next (e.g., "The sky weeps today, but these are but gentle tears before the coming icy breath..."), giving the player a valuable strategic advantage.

Technical Implementation & SOLID Principles
This design will be highly modular and adhere to SOLID principles:

Single Responsibility Principle (SRP):

A WeatherService class will be solely responsible for the OpenWeatherMap API call.

A separate DialogueService class will be solely responsible for the Gemini API call.

The StormSeer NPC class will handle its own attributes and actions, not API logic.

Open/Closed Principle (OCP): We will use a WeatherEffect interface with concrete implementations (RainEffect, SnowEffect). This allows new weather types to be added without modifying existing code.

New Classes to be Implemented
This design will introduce more than the required five new classes:

WeatherService (Higher-level class: for OpenWeatherMap API)

DialogueService (Higher-level class: for Gemini API)

StormSeer (The NPC Actor class itself)

WeatherEffect (Interface)

RainEffect (Lower-level class)

SnowEffect (Lower-level class)

TalkToAction (A new Action to interact with the NPC)

Conclusion
This proposal, "The Storm Seer's Prophecy," creates a deeply integrated HD feature. It uses one API (OpenWeatherMap) to create systemic gameplay challenges and a second API (Gemini) to provide a dynamic, narrative-rich way for the player to engage with that system. This two-layered API approach is original, technically robust, and creates meaningful, emergent gameplay that directly fulfills and exceeds the requirements of REQ5.