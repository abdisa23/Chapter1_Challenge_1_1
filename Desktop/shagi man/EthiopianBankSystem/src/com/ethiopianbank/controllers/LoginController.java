package com.ethiopianbank.controllers;

import com.ethiopianbank.services.AuthService;
import com.ethiopianbank.utils.AlertUtil;
import com.ethiopianbank.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    
    private AuthService authService;
    
    public LoginController() {
        this.authService = new AuthService();
    }
    
    @FXML
    private void initialize() {
        // Set default values for testing (remove in production)
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showErrorAlert("Login Error", "Please enter both username and password");
            return;
        }
        
        try {
            User user = authService.login(username, password);
            if (user != null) {
                // Login successful
                AlertUtil.showSuccessAlert("Login Successful", "Welcome " + user.getFullName());
                
                // Load dashboard
                loadDashboard(user);
                
                // Close login window
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
            } else {
                AlertUtil.showErrorAlert("Login Failed", "Invalid username or password");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Login Error", e.getMessage());
        }
    }
    
    private void loadDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            // Pass user to dashboard controller
            DashboardController controller = loader.getController();
            controller.setCurrentUser(user);
            
            Stage stage = new Stage();
            stage.setTitle("Ethiopian Bank System - Dashboard");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}