package game.api;

import game.weather.WeatherEffect;
import game.weather.ApiClearWeather;
import game.weather.ApiRainWeather;
import game.weather.ApiSnowWeather;

// Import the required OpenWeatherMap JAPIS libraries
// Ensure these are in your pom.xml
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;

/**
 * REQ5: Implementation of WeatherService using the OpenWeatherMap (OWM) API.
 * Adheres to SRP: This class's only job is to contact OWM and map the result.
 */
public class OwmWeatherService implements WeatherService {

    private final OWM owm;
    private final String locationCity;
    private final String locationCountry;

    // A simple cache to avoid spamming the API
    private WeatherEffect lastWeather = null;
    private long lastCheckTime = 0;
    private static final long CACHE_DURATION_MS = 10 * 60 * 1000; // 10 minutes

    /**
     * Constructor. Loads configuration from the Config class.
     */
    public OwmWeatherService() {
        String apiKey = Config.getOwmApiKey();
        if (apiKey == null) {
            System.err.println("REQ5 WARNING: OWM_API_KEY environment variable is not set. Weather service will use fallbacks.");
            this.owm = null;
        } else {
            this.owm = new OWM(apiKey);
            this.owm.setUnit(OWM.Unit.METRIC);
        }
        this.locationCity = Config.WEATHER_LOCATION_CITY;
        this.locationCountry = Config.WEATHER_LOCATION_COUNTRY;
    }

    @Override
    public WeatherEffect getCurrentWeather() {
        long now = System.currentTimeMillis();
        // Return cached result if still valid
        if (lastWeather != null && (now - lastCheckTime < CACHE_DURATION_MS)) {
            return lastWeather;
        }

        // Fallback if API is not configured
        if (owm == null) {
            return getFallbackWeather();
        }

        try {
            CurrentWeather cwd = owm.currentWeatherByCityName(locationCity, OWM.Country.valueOf(locationCountry));

            if (!cwd.hasRespCode() || cwd.getRespCode() != 200) {
                System.err.println("REQ5 WARNING: OWM API returned code " + cwd.getRespCode());
                return getFallbackWeather(); // API response error
            }

            // Get key data
            double temp = cwd.getMainData().getTemp();
            String weatherName = cwd.getWeatherList().get(0).getMainInfo();
            String weatherDesc = cwd.getWeatherList().get(0).getDescription();

            // REQ5 Logic: Map API result to a game WeatherEffect
            // We pass the temperature and description to the effect
            WeatherEffect effect;
            switch (weatherName.toLowerCase()) {
                case "rain":
                case "drizzle":
                case "thunderstorm":
                    effect = new ApiRainWeather(temp, weatherDesc);
                    break;

                case "snow":
                    effect = new ApiSnowWeather(temp, weatherDesc);
                    break;

                case "clear":
                case "clouds":
                default:
                    effect = new ApiClearWeather(temp, weatherDesc);
                    break;
            }

            // Update cache
            this.lastWeather = effect;
            this.lastCheckTime = now;
            return effect;

        } catch (APIException e) {
            System.err.println("REQ5 ERROR: Failed to call OpenWeatherMap API: " + e.getMessage());
            return getFallbackWeather(); // API call failed
        }
    }

    /**
     * Provides a default, hard-coded weather if the API call fails or is not configured.
     * @return A fallback ApiClearWeather effect.
     */
    private WeatherEffect getFallbackWeather() {
        return new ApiClearWeather(15.0, "Temperate"); // 15°C as a neutral fallback
    }
}