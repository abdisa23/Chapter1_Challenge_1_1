package com.ethiopianbank.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertUtil {
    
    public static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void showErrorAlert(String title, String content) {
        showAlert(AlertType.ERROR, title, "Error", content);
    }
    
    public static void showInfoAlert(String title, String content) {
        showAlert(AlertType.INFORMATION, title, "Information", content);
    }
    
    public static void showSuccessAlert(String title, String content) {
        showAlert(AlertType.INFORMATION, title, "Success", content);
    }
    
    public static void showWarningAlert(String title, String content) {
        showAlert(AlertType.WARNING, title, "Warning", content);
    }
    
    public static boolean showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmation");
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}