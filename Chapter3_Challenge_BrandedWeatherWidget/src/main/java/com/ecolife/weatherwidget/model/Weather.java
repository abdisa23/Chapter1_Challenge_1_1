package com.ecolife.weatherwidget.model;

import java.time.LocalDateTime;

public class Weather {
    private String location;
    private double temperature;
    private int humidity;
    private double windSpeed;
    private String condition;
    private String icon;
    private LocalDateTime lastUpdated;
    
    public Weather(String location, double temperature, int humidity, 
                  double windSpeed, String condition, String icon, 
                  LocalDateTime lastUpdated) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.icon = icon;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return String.format("Weather in %s: %.1f°C, %s, Humidity: %d%%, Wind: %.1f km/h",
            location, temperature, condition, humidity, windSpeed);
    }
}