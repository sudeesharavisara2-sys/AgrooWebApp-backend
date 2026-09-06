package com.agroo.agroo.service;

import com.agroo.agroo.model.WeatherAlert;

import java.util.List;

public interface EmailNotificationService {
    void sendWeatherAlert(WeatherAlert alert);
    void sendDailyForecast(String email, String location);
    void sendBulkWeatherAlerts(List<WeatherAlert> alerts);
}