package com.ecolife.weatherwidget.service;

import com.ecolife.weatherwidget.model.Weather;
import com.ecolife.weatherwidget.model.Forecast;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {
    private static final String API_KEY = "your_api_key_here"; // Replace with actual API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public WeatherService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    public Weather getCurrentWeather(String city) throws Exception {
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", 
                                  BASE_URL, city, API_KEY);
        
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch weather data: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            
            return new Weather(
                root.path("name").asText(),
                root.path("main").path("temp").asDouble(),
                root.path("main").path("humidity").asInt(),
                root.path("wind").path("speed").asDouble(),
                root.path("weather").get(0).path("description").asText(),
                getWeatherIcon(root.path("weather").get(0).path("main").asText()),
                LocalDateTime.now()
            );
        }
    }
    
    public List<Forecast> get5DayForecast(String city) throws Exception {
        String url = String.format("%s/forecast?q=%s&appid=%s&units=metric&cnt=5", 
                                  BASE_URL, city, API_KEY);
        
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch forecast data: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode list = root.path("list");
            
            List<Forecast> forecasts = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (JsonNode item : list) {
                forecasts.add(new Forecast(
                    LocalDateTime.parse(item.path("dt_txt").asText(), formatter),
                    item.path("main").path("temp").asDouble(),
                    item.path("weather").get(0).path("description").asText(),
                    getWeatherIcon(item.path("weather").get(0).path("main").asText())
                ));
            }
            
            return forecasts;
        }
    }
    
    private String getWeatherIcon(String condition) {
        return switch (condition.toLowerCase()) {
            case "clear" -> "☀️";
            case "clouds" -> "☁️";
            case "rain" -> "🌧️";
            case "drizzle" -> "🌦️";
            case "thunderstorm" -> "⛈️";
            case "snow" -> "❄️";
            case "mist", "fog" -> "🌫️";
            default -> "🌈";
        };
    }
    
    // Mock data generator for testing without API
    public Weather getMockWeather(String city) {
        double baseTemp = 20.0 + (Math.random() * 10 - 5);
        return new Weather(
            city,
            baseTemp,
            50 + (int)(Math.random() * 30),
            5 + Math.random() * 15,
            getRandomCondition(),
            getRandomIcon(),
            LocalDateTime.now()
        );
    }
    
    public List<Forecast> getMockForecast(String city) {
        List<Forecast> forecasts = new ArrayList<>();
        String[] conditions = {"Sunny", "Partly Cloudy", "Rainy", "Cloudy", "Clear"};
        String[] icons = {"☀️", "⛅", "🌧️", "☁️", "🌙"};
        
        for (int i = 0; i < 5; i++) {
            forecasts.add(new Forecast(
                LocalDateTime.now().plusDays(i),
                18 + Math.random() * 10,
                conditions[i],
                icons[i]
            ));
        }
        
        return forecasts;
    }
    
    private String getRandomCondition() {
        String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Clear"};
        return conditions[(int)(Math.random() * conditions.length)];
    }
    
    private String getRandomIcon() {
        String[] icons = {"☀️", "⛅", "☁️", "🌧️", "🌙"};
        return icons[(int)(Math.random() * icons.length)];
    }
}