package com.agroo.agroo.service;

import com.agroo.agroo.model.WeatherAlert;

import java.util.List;

public interface WeatherService {
    WeatherAlert checkWeatherAndCreateAlert(String location);
    List<WeatherAlert> getAllActiveAlerts();
    List<WeatherAlert> getAlertsByLocation(String location);
    WeatherAlert getAlert(Long id);
    void deactivateAlert(Long id);
    void checkAllLocations();
    void markAsSent(Long id);
}