package com.agroo.agroo.controller;

import com.agroo.agroo.model.WeatherAlert;
import com.agroo.agroo.repository.WeatherAlertRepository;
import com.agroo.agroo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherAlertRepository weatherAlertRepository;  // ✅ Add this

    @GetMapping("/alerts")
    public ResponseEntity<List<WeatherAlert>> getAllAlerts() {
        return ResponseEntity.ok(weatherService.getAllActiveAlerts());
    }

    @GetMapping("/alerts/location/{location}")
    public ResponseEntity<List<WeatherAlert>> getAlertsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(weatherService.getAlertsByLocation(location));
    }

    @GetMapping("/alerts/{id}")
    public ResponseEntity<WeatherAlert> getAlert(@PathVariable Long id) {
        return ResponseEntity.ok(weatherService.getAlert(id));
    }

    @PostMapping("/check/{location}")
    public ResponseEntity<WeatherAlert> checkWeather(@PathVariable String location) {
        WeatherAlert alert = weatherService.checkWeatherAndCreateAlert(location);
        if (alert == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/check/all")
    public ResponseEntity<String> checkAllLocations() {
        weatherService.checkAllLocations();
        return ResponseEntity.ok("Weather check completed");
    }

    @PatchMapping("/alerts/{id}/deactivate")
    public ResponseEntity<Void> deactivateAlert(@PathVariable Long id) {
        weatherService.deactivateAlert(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // 🧪 TEST ENDPOINTS - For Testing Weather Alert System
    // ============================================================

    /**
     * Generate a mock weather alert for testing
     * Example: POST /api/weather/test/generate-mock-alert?location=Kandy&type=HEAVY_RAIN
     */
    @PostMapping("/test/generate-mock-alert")
    public ResponseEntity<WeatherAlert> generateMockAlert(
            @RequestParam(defaultValue = "Colombo") String location,
            @RequestParam(defaultValue = "HEAVY_RAIN") String alertType) {

        WeatherAlert alert = new WeatherAlert();
        alert.setLocation(location);
        alert.setAlertType(alertType);
        alert.setSeverity(getSeverity(alertType));
        alert.setMessage(getMockMessage(alertType, location));
        alert.setTemperature(25.5);
        alert.setHumidity(85.0);
        alert.setWindSpeed(15.0);
        alert.setRainfall(15.5);
        alert.setIsActive(true);
        alert.setIsSent(false);
        alert.setExpiresAt(LocalDateTime.now().plusHours(24));

        WeatherAlert saved = weatherAlertRepository.save(alert);
        return ResponseEntity.ok(saved);
    }

    /**
     * Generate mock alerts for all locations (for bulk testing)
     * Example: POST /api/weather/test/generate-mock-alerts
     */
    @PostMapping("/test/generate-mock-alerts")
    public ResponseEntity<String> generateMockAlerts() {
        String[] locations = {"Colombo", "Kandy", "Galle", "Jaffna"};
        String[] alertTypes = {"HEAVY_RAIN", "HIGH_WIND", "EXTREME_HEAT", "DROUGHT"};

        int count = 0;
        for (String location : locations) {
            for (String alertType : alertTypes) {
                WeatherAlert alert = new WeatherAlert();
                alert.setLocation(location);
                alert.setAlertType(alertType);
                alert.setSeverity(getSeverity(alertType));
                alert.setMessage(getMockMessage(alertType, location));
                alert.setTemperature(25.5 + Math.random() * 10);
                alert.setHumidity(60.0 + Math.random() * 30);
                alert.setWindSpeed(10.0 + Math.random() * 30);
                alert.setRainfall(5.0 + Math.random() * 15);
                alert.setIsActive(true);
                alert.setIsSent(false);
                alert.setExpiresAt(LocalDateTime.now().plusHours(24));
                weatherAlertRepository.save(alert);
                count++;
            }
        }
        return ResponseEntity.ok("Generated " + count + " mock alerts!");
    }

    /**
     * Delete all test alerts
     * Example: DELETE /api/weather/test/delete-all
     */
    @DeleteMapping("/test/delete-all")
    public ResponseEntity<String> deleteAllAlerts() {
        weatherAlertRepository.deleteAll();
        return ResponseEntity.ok("All alerts deleted!");
    }

    /**
     * Get all alerts (including inactive)
     * Example: GET /api/weather/test/all
     */
    @GetMapping("/test/all")
    public ResponseEntity<List<WeatherAlert>> getAllAlertsIncludingInactive() {
        return ResponseEntity.ok(weatherAlertRepository.findAll());
    }

    // Helper methods
    private String getSeverity(String alertType) {
        return switch (alertType) {
            case "HEAVY_RAIN", "HIGH_WIND", "EXTREME_HEAT" -> "WARNING";
            case "STORM" -> "CRITICAL";
            default -> "INFO";
        };
    }

    private String getMockMessage(String alertType, String location) {
        return switch (alertType) {
            case "HEAVY_RAIN" -> "🧪 TEST: Heavy rain warning for " + location + "! Rainfall: 15.5 mm.";
            case "HIGH_WIND" -> "🧪 TEST: High wind warning for " + location + "! Wind speed: 35.0 km/h.";
            case "EXTREME_HEAT" -> "🧪 TEST: Extreme heat warning for " + location + "! Temperature: 38.5°C.";
            case "DROUGHT" -> "🧪 TEST: Drought conditions in " + location + ". Humidity: 25%.";
            case "STORM" -> "🧪 TEST: Storm warning for " + location + "! Take immediate precautions.";
            default -> "🧪 TEST: Weather alert for " + location;
        };
    }
}