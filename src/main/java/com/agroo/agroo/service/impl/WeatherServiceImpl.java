package com.agroo.agroo.service.impl;

import com.agroo.agroo.model.WeatherAlert;
import com.agroo.agroo.repository.WeatherAlertRepository;
import com.agroo.agroo.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherAlertRepository weatherAlertRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    private static final String[] LOCATIONS = {
            "Colombo", "Kandy", "Galle", "Jaffna", "Trincomalee",
            "Anuradhapura", "Polonnaruwa", "Badulla", "Ratnapura",
            "Kurunegala", "Matara", "Negombo", "Dambulla"
    };

    @Override
    @Transactional
    public WeatherAlert checkWeatherAndCreateAlert(String location) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("⚠️ Weather API key not configured.");
            return null;
        }

        try {
            WeatherData weather = fetchWeatherData(location);
            return createAlertIfNeeded(location, weather);
        } catch (Exception e) {
            System.err.println("❌ Error checking weather for " + location + ": " + e.getMessage());
            return null;
        }
    }

    // ✅ NEW: Get weather data only (no alert creation)
    public WeatherData getWeatherDataOnly(String location) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("⚠️ Weather API key not configured.");
            return null;
        }
        try {
            return fetchWeatherData(location);
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather for " + location + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<WeatherAlert> getAllActiveAlerts() {
        return weatherAlertRepository.findByIsActiveTrueAndIsSentFalse();
    }

    @Override
    public List<WeatherAlert> getAlertsByLocation(String location) {
        return weatherAlertRepository.findByLocationContainingIgnoreCase(location);
    }

    @Override
    public WeatherAlert getAlert(Long id) {
        return weatherAlertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
    }

    @Override
    @Transactional
    public void deactivateAlert(Long id) {
        WeatherAlert alert = getAlert(id);
        alert.setIsActive(false);
        weatherAlertRepository.save(alert);
    }

    @Override
    @Transactional
    public void checkAllLocations() {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("⚠️ Weather API key not configured.");
            return;
        }

        System.out.println("🌤️ Checking weather for all locations...");
        for (String location : LOCATIONS) {
            try {
                checkWeatherAndCreateAlert(location);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("❌ Error checking " + location + ": " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void markAsSent(Long id) {
        weatherAlertRepository.markAsSent(id, LocalDateTime.now());
    }

    private WeatherData fetchWeatherData(String location) {
        String encodedLocation = location.replace(" ", "%20");
        String[] suffixes = {"Sri%20Lanka", "LK", ""};

        for (String suffix : suffixes) {
            try {
                String url;
                if (suffix.isEmpty()) {
                    url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, encodedLocation, apiKey);
                } else {
                    url = String.format("%s?q=%s,%s&appid=%s&units=metric", apiUrl, encodedLocation, suffix, apiKey);
                }

                System.out.println("🌤️ Trying: " + url);
                String response = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(response);

                if (root.has("cod") && root.get("cod").asInt() == 200) {
                    WeatherData weather = new WeatherData();
                    weather.temperature = root.path("main").path("temp").asDouble();
                    weather.humidity = root.path("main").path("humidity").asDouble();
                    weather.windSpeed = root.path("wind").path("speed").asDouble();
                    weather.rainfall = root.path("rain").path("1h").asDouble(0.0);

                    System.out.println("✅ Weather data received for: " + location +
                            " (Temp: " + String.format("%.1f", weather.temperature) + "°C)");
                    return weather;
                }
            } catch (Exception e) {
                System.err.println("❌ Failed with suffix '" + suffix + "': " + e.getMessage());
            }
        }

        throw new RuntimeException("Location not found: " + location);
    }

    private WeatherAlert createAlertIfNeeded(String location, WeatherData weather) {
        String alertType = null;
        String severity = "INFO";
        String message = null;

        if (weather.rainfall > 10.0) {
            alertType = "HEAVY_RAIN";
            severity = "WARNING";
            message = "🌧️ Heavy rain warning for " + location + "! Rainfall: " + String.format("%.1f", weather.rainfall) + " mm.";
        } else if (weather.windSpeed > 30.0) {
            alertType = "HIGH_WIND";
            severity = "WARNING";
            message = "💨 High wind warning for " + location + "! Wind speed: " + String.format("%.1f", weather.windSpeed) + " km/h.";
        } else if (weather.temperature > 35.0) {
            alertType = "EXTREME_HEAT";
            severity = "WARNING";
            message = "🌡️ Extreme heat warning for " + location + "! Temperature: " + String.format("%.1f", weather.temperature) + "°C.";
        } else if (weather.rainfall < 0.5 && weather.humidity < 30) {
            alertType = "DROUGHT";
            severity = "INFO";
            message = "🏜️ Drought conditions in " + location + ". Humidity: " + String.format("%.1f", weather.humidity) + "%. Consider irrigation.";
        } else if (weather.rainfall > 5.0 && weather.windSpeed > 20.0) {
            alertType = "STORM";
            severity = "CRITICAL";
            message = "🌪️ Storm warning for " + location + "! Rainfall: " + String.format("%.1f", weather.rainfall) + "mm, Wind: " + String.format("%.1f", weather.windSpeed) + " km/h.";
        }

        if (alertType == null) {
            System.out.println("✅ No alert needed for " + location + " - Weather is normal");
            return null;
        }

        // ✅ Check if similar active alert already exists
        List<WeatherAlert> existingAlerts = weatherAlertRepository.findByLocationContainingIgnoreCase(location);
        for (WeatherAlert existing : existingAlerts) {
            if (existing.getAlertType().equals(alertType) && existing.getIsActive()) {
                System.out.println("⚠️ Alert already exists for " + location + ": " + alertType);
                return null;
            }
        }

        WeatherAlert alert = new WeatherAlert();
        alert.setLocation(location);
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setTemperature(weather.temperature);
        alert.setHumidity(weather.humidity);
        alert.setWindSpeed(weather.windSpeed);
        alert.setRainfall(weather.rainfall);
        alert.setIsActive(true);
        alert.setIsSent(false);
        alert.setExpiresAt(LocalDateTime.now().plusHours(24));

        System.out.println("✅ New alert created for " + location + ": " + alertType);
        return weatherAlertRepository.save(alert);
    }

    // ✅ Make WeatherData public so it can be used by controller
    public static class WeatherData {
        private double temperature;
        private double humidity;
        private double windSpeed;
        private double rainfall;

        // ✅ Getters and setters for serialization
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getHumidity() { return humidity; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
        public double getRainfall() { return rainfall; }
        public void setRainfall(double rainfall) { this.rainfall = rainfall; }
    }
}