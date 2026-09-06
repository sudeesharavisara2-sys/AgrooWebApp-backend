package com.agroo.agroo.service.impl;

import com.agroo.agroo.model.User;
import com.agroo.agroo.model.WeatherAlert;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Override
    public void sendWeatherAlert(WeatherAlert alert) {
        // ✅ Get users ONLY in the alert's location
        List<User> usersInLocation = userRepository.findByLocationAndIsActiveTrueAndIsVerifiedTrue(alert.getLocation());

        if (usersInLocation.isEmpty()) {
            System.out.println("⚠️ No active users found in " + alert.getLocation() + ". Sending to test email.");
            sendEmailToUser("farmer1@test.com", alert);
            return;
        }

        // ✅ Send ONLY to users in that location
        for (User user : usersInLocation) {
            sendEmailToUser(user.getEmail(), alert);
        }
    }

    @Override
    public void sendDailyForecast(String email, String location) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("🌤️ Daily Weather Forecast for " + location);
            message.setText(getDailyForecastMessage(location));
            mailSender.send(message);
            System.out.println("✅ Sent daily forecast to: " + email);
        } catch (Exception e) {
            System.err.println("❌ Failed to send forecast: " + e.getMessage());
        }
    }

    @Override
    public void sendBulkWeatherAlerts(List<WeatherAlert> alerts) {
        // ✅ This method sends multiple alerts
        for (WeatherAlert alert : alerts) {
            sendWeatherAlert(alert);
        }
    }

    private void sendEmailToUser(String email, WeatherAlert alert) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(getSubject(alert));
            message.setText(getMessage(alert));
            mailSender.send(message);
            System.out.println("✅ Sent alert to: " + email + " (Location: " + alert.getLocation() + ")");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + email + ": " + e.getMessage());
        }
    }

    private String getSubject(WeatherAlert alert) {
        String emoji = switch (alert.getAlertType()) {
            case "HEAVY_RAIN" -> "🌧️";
            case "HIGH_WIND" -> "💨";
            case "EXTREME_HEAT" -> "🌡️";
            case "DROUGHT" -> "🏜️";
            case "STORM" -> "🌪️";
            default -> "⚠️";
        };
        return emoji + " Weather Alert: " + alert.getAlertType() + " in " + alert.getLocation();
    }

    private String getMessage(WeatherAlert alert) {
        return """
            🌾 Agroo Weather Alert
            =========================
            
            Location: %s
            Alert Type: %s
            Severity: %s
            
            %s
            
            Weather Details:
            🌡️ Temperature: %.1f°C
            💧 Humidity: %.1f%%
            💨 Wind Speed: %.1f km/h
            🌧️ Rainfall: %.1f mm
            
            Expires: %s
            
            Stay safe!
            🌾 Agroo - Empowering Farmers
            """.formatted(
                alert.getLocation(),
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.getTemperature(),
                alert.getHumidity(),
                alert.getWindSpeed(),
                alert.getRainfall(),
                alert.getExpiresAt()
        );
    }

    private String getDailyForecastMessage(String location) {
        return """
            🌤️ Daily Weather Forecast
            =========================
            
            Location: %s
            
            Good morning!
            
            🌡️ Temperature: 28°C - 32°C
            💧 Humidity: 65% - 80%
            💨 Wind: Light to moderate
            🌧️ Rainfall: Low chance
            
            📋 Farming Tips:
            • Water crops early morning or evening
            • Check for pest activity
            • Monitor soil moisture
            
            🌾 Agroo - Empowering Farmers
            """.formatted(location);
    }
}