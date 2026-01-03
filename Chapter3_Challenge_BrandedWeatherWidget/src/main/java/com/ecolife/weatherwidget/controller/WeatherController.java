package com.ecolife.weatherwidget.controller;

import com.ecolife.weatherwidget.model.Weather;
import com.ecolife.weatherwidget.model.Forecast;
import com.ecolife.weatherwidget.service.WeatherService;
import com.ecolife.weatherwidget.util.AnimationUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class WeatherController implements Initializable {
    
    @FXML private Label locationLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label conditionLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private ImageView weatherIcon;
    @FXML private HBox forecastContainer;
    @FXML private Button refreshButton;
    @FXML private Button closeButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private Circle humidityCircle;
    @FXML private Circle windCircle;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private VBox mainContent;
    
    private WeatherService weatherService;
    private Weather currentWeather;
    private List<Forecast> forecastList;
    private String currentCity = "EcoCity";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        weatherService = new WeatherService();
        
        // Initially hide loading indicator
        loadingIndicator.setVisible(false);
        
        // Set up UI
        setupUI();
        
        // Load initial weather data
        loadWeatherData();
        
        // Start time updates
        startTimeUpdates();
    }
    
    private void setupUI() {
        // Setup animations
        AnimationUtil.fadeIn(mainContent, 800);
        
        // Setup event handlers
        refreshButton.setOnAction(e -> loadWeatherData());
        closeButton.setOnAction(e -> Platform.exit());
        searchButton.setOnAction(e -> searchWeather());
        
        // Enter key for search
        searchField.setOnAction(e -> searchWeather());
        
        // Add hover effects
        AnimationUtil.addHoverAnimation(refreshButton, 1.1, 200);
        AnimationUtil.addHoverAnimation(searchButton, 1.1, 200);
        
        // Pulsing animation for refresh button
        AnimationUtil.addPulseAnimation(refreshButton, 1.05, 1500);
    }
    
    private void loadWeatherData() {
        showLoading(true);
        
        Task<Void> weatherTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // For real API, use: currentWeather = weatherService.getCurrentWeather(currentCity);
                    // For now, use mock data
                    currentWeather = weatherService.getMockWeather(currentCity);
                    forecastList = weatherService.getMockForecast(currentCity);
                    
                    Platform.runLater(() -> {
                        updateCurrentWeather();
                        updateForecast();
                        showLoading(false);
                        
                        // Success animation
                        AnimationUtil.rotateAnimation(refreshButton, 360, 500);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("Failed to load weather data");
                        AnimationUtil.shakeAnimation(refreshButton);
                    });
                }
                return null;
            }
        };
        
        new Thread(weatherTask).start();
    }
    
    private void searchWeather() {
        String city = searchField.getText().trim();
        if (!city.isEmpty()) {
            currentCity = city;
            loadWeatherData();
            searchField.clear();
        }
    }
    
    private void updateCurrentWeather() {
        if (currentWeather == null) return;
        
        locationLabel.setText(currentWeather.getLocation());
        temperatureLabel.setText(String.format("%.1f°C", currentWeather.getTemperature()));
        conditionLabel.setText(currentWeather.getCondition());
        humidityLabel.setText(String.format("%d%%", currentWeather.getHumidity()));
        windLabel.setText(String.format("%.1f km/h", currentWeather.getWindSpeed()));
        
        // Calculate feels like temperature (simple approximation)
        double feelsLike = currentWeather.getTemperature();
        if (currentWeather.getHumidity() > 70) feelsLike += 2;
        if (currentWeather.getWindSpeed() > 20) feelsLike -= 3;
        feelsLikeLabel.setText(String.format("Feels like: %.1f°C", feelsLike));
        
        // Update circle indicators
        humidityCircle.setRotate(currentWeather.getHumidity() * 3.6);
        windCircle.setRotate(Math.min(currentWeather.getWindSpeed() * 10, 360));
        
        // Animate temperature change
        AnimationUtil.fadeIn(temperatureLabel, 500);
    }
    
    private void updateForecast() {
        forecastContainer.getChildren().clear();
        
        if (forecastList == null || forecastList.isEmpty()) return;
        
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d");
        
        for (Forecast forecast : forecastList) {
            VBox forecastCard = createForecastCard(forecast, dayFormatter, dateFormatter);
            forecastContainer.getChildren().add(forecastCard);
            
            // Staggered animation
            AnimationUtil.fadeIn(forecastCard, 300);
        }
    }
    
    private VBox createForecastCard(Forecast forecast, DateTimeFormatter dayFormatter, DateTimeFormatter dateFormatter) {
        VBox card = new VBox(8);
        card.getStyleClass().add("forecast-card");
        
        Label dayLabel = new Label(forecast.getDate().format(dayFormatter));
        dayLabel.getStyleClass().add("forecast-day");
        
        Label dateLabel = new Label(forecast.getDate().format(dateFormatter));
        dateLabel.getStyleClass().add("forecast-date");
        dateLabel.setStyle("-fx-text-fill: #A5D6A7; -fx-font-size: 10px;");
        
        Label iconLabel = new Label(forecast.getIcon());
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label tempLabel = new Label(String.format("%.0f°", forecast.getTemperature()));
        tempLabel.getStyleClass().add("forecast-temp");
        
        Label conditionLabel = new Label(forecast.getCondition());
        conditionLabel.getStyleClass().add("forecast-condition");
        
        card.getChildren().addAll(dayLabel, dateLabel, iconLabel, tempLabel, conditionLabel);
        
        // Add hover animation
        AnimationUtil.addHoverAnimation(card, 1.08, 200);
        
        // Add click handler for details
        card.setOnMouseClicked(e -> showForecastDetails(forecast));
        
        return card;
    }
    
    private void showForecastDetails(Forecast forecast) {
        // Create a simple detail popup
        String details = String.format(
            "📅 %s\n🌡️ Temperature: %.1f°C\n🌈 Condition: %s\n%s",
            forecast.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
            forecast.getTemperature(),
            forecast.getCondition(),
            forecast.getIcon()
        );
        
        // In a real app, you might show a dialog or tooltip
        System.out.println("Forecast Details: " + details);
        
        // Visual feedback
        AnimationUtil.rotateAnimation(forecastContainer, 5, 100);
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm")));
        dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
    }
    
    private void startTimeUpdates() {
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            e -> updateDateTime()
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        mainContent.setOpacity(show ? 0.5 : 1.0);
        refreshButton.setDisable(show);
        
        if (show) {
            loadingIndicator.setProgress(-1); // Indeterminate progress
        }
    }
    
    private void showError(String message) {
        // Simple error display - could be enhanced with a proper dialog
        conditionLabel.setText("❌ " + message);
        conditionLabel.setStyle("-fx-text-fill: #FFCDD2; -fx-font-size: 14px;");
        
        // Auto-clear error after 5 seconds
        Timeline clearError = new Timeline(new KeyFrame(
            Duration.seconds(5),
            e -> {
                if (currentWeather != null) {
                    conditionLabel.setText(currentWeather.getCondition());
                    conditionLabel.setStyle("-fx-text-fill: #C8E6C9; -fx-font-size: 18px;");
                }
            }
        ));
        clearError.play();
    }
}