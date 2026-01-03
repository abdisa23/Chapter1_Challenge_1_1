package com.ecolife.weatherwidget.model;

import java.time.LocalDateTime;

public class Forecast {
    private LocalDateTime date;
    private double temperature;
    private String condition;
    private String icon;
    
    public Forecast(LocalDateTime date, double temperature, String condition, String icon) {
        this.date = date;
        this.temperature = temperature;
        this.condition = condition;
        this.icon = icon;
    }
    
    // Getters and Setters
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    @Override
    public String toString() {
        return String.format("Forecast: %s - %.1f°C, %s",
            date.toLocalDate(), temperature, condition);
    }
}