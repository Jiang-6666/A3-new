package game.api;

import game.weather.WeatherEffect;

/**
 * REQ5: Interface (ISP, DIP) for a systemic weather API service.
 * Responsible for fetching real-world weather and translating it into a
 * game-understandable WeatherEffect.
 */
public interface WeatherService {
    /**
     * Fetches the current real-world weather and maps it to a game WeatherEffect.
     *
     * @return A valid WeatherEffect (e.g., ApiRainWeather).
     * Returns a non-null fallback (e.g., ApiClearWeather) on failure.
     */
    WeatherEffect getCurrentWeather();
}
