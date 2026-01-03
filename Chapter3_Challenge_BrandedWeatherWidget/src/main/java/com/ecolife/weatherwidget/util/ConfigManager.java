package com.ecolife.weatherwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static boolean loaded = false;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Config file not found, using defaults");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            loaded = true;
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    private static void setDefaultProperties() {
        properties.setProperty("app.name", "EcoLife Weather Widget");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("default.city", "EcoCity");
        properties.setProperty("temperature.unit", "celsius");
        properties.setProperty("update.interval.minutes", "30");
        properties.setProperty("animation.enabled", "true");
        properties.setProperty("feature.search", "true");
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    public static double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean isLoaded() {
        return loaded;
    }
    
    public static void printAllProperties() {
        System.out.println("=== Configuration Properties ===");
        properties.forEach((key, value) -> 
            System.out.println(key + ": " + value));
    }
}