package com.ecolife.weatherwidget;

import com.ecolife.weatherwidget.util.ConfigManager;
import com.ecolife.weatherwidget.util.ThemeManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load configuration
        ConfigManager.printAllProperties();
        
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeatherView.fxml"));
        Parent root = loader.load();
        
        // Create scene with transparent background
        Scene scene = new Scene(root, 420, 650);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Apply theme
        ThemeManager.applyTheme(scene, ThemeManager.Theme.GREEN);
        
        // Configure stage
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(ConfigManager.getProperty("app.name", "EcoLife Weather Widget"));
        primaryStage.setScene(scene);
        
        // Set application icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/leaf_icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }
        
        // Make window draggable
        makeDraggable(root, primaryStage);
        
        // Add shadow effect
        root.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 12);");
        
        // Show the stage
        primaryStage.show();
        
        // Auto-refresh timer (optional)
        startAutoRefresh(loader.getController());
    }
    
    private void makeDraggable(Parent root, Stage stage) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    
    private void startAutoRefresh(Object controller) {
        // This would auto-refresh weather data at intervals
        // Implementation depends on controller structure
        int intervalMinutes = ConfigManager.getIntProperty("update.interval.minutes", 30);
        System.out.println("Auto-refresh interval: " + intervalMinutes + " minutes");
    }
    
    @Override
    public void stop() throws Exception {
        // Cleanup resources
        System.out.println("Application closing...");
        super.stop();
        Platform.exit();
        System.exit(0);
    }
    
    public static void main(String[] args) {
        System.out.println("Starting EcoLife Weather Widget...");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));

        launch(args);
    }
}