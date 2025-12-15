package com.ethiopianbank;

import com.ethiopianbank.config.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            DatabaseConnection.testConnection();
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            
            // Add CSS
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            
            // Set stage properties
            primaryStage.setTitle("Ethiopian Bank System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            // Set application icon
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            } catch (Exception e) {
                System.out.println("Logo not found, using default icon");
            }
            
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start application: " + e.getMessage());
            System.exit(1);
        }
    }
    
    @Override
    public void stop() {
        // Clean up database connection when application closes
        DatabaseConnection.closeConnection();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}