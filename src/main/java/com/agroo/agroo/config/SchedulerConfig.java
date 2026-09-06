package com.agroo.agroo.config;

import com.agroo.agroo.service.EmailNotificationService;
import com.agroo.agroo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final WeatherService weatherService;
    private final EmailNotificationService emailService;

    @Scheduled(cron = "0 0 */6 * * ?")
    public void checkWeather() {
        System.out.println("🌤️ Checking weather...");
        weatherService.checkAllLocations();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void sendAlerts() {
        var alerts = weatherService.getAllActiveAlerts();
        if (!alerts.isEmpty()) {
            System.out.println("📧 Sending " + alerts.size() + " alerts...");
            for (var alert : alerts) {
                emailService.sendWeatherAlert(alert);
                weatherService.markAsSent(alert.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void dailyForecast() {
        System.out.println("🌅 Daily forecast...");
    }
}