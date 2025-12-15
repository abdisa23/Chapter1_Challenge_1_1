package com.ethiopianbank.controllers;

import com.ethiopianbank.models.User;
import com.ethiopianbank.services.BankService;
import com.ethiopianbank.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label currentTimeLabel;
    @FXML private BorderPane mainContentPane;
    @FXML private Button customersButton;
    @FXML private Button accountsButton;
    @FXML private Button transactionsButton;
    @FXML private Button dashboardButton;
    @FXML private Button logoutButton;
    
    // Dashboard statistics labels
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label todaysTransactionsLabel;
    @FXML private Label todaysDepositsLabel;
    @FXML private Label savingsAccountsLabel;
    @FXML private Label checkingAccountsLabel;
    
    private User currentUser;
    private BankService bankService;
    
    public DashboardController() {
        this.bankService = new BankService();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
        loadDashboardStatistics();
    }
    
    @FXML
    private void initialize() {
        // Setup initial UI
        updateDateTime();
        loadDashboardView();
    }
    
    private void updateUI() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName());
            userRoleLabel.setText("Role: " + currentUser.getRole());
        }
    }
    
    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Thread updateTimeThread = new Thread(() -> {
            while (true) {
                try {
                    String currentTime = LocalDateTime.now().format(formatter);
                    javafx.application.Platform.runLater(() -> 
                        currentTimeLabel.setText("Current Time: " + currentTime));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
    }
    
    private void loadDashboardStatistics() {
        BankService.DashboardStats stats = bankService.getDashboardStats();
        
        totalCustomersLabel.setText(String.valueOf(stats.getTotalCustomers()));
        totalBalanceLabel.setText("ETB " + stats.getTotalBalance());
        todaysTransactionsLabel.setText(String.valueOf(stats.getTodaysTransactions()));
        todaysDepositsLabel.setText("ETB " + stats.getTodaysDeposits());
        savingsAccountsLabel.setText(String.valueOf(stats.getSavingsAccounts()));
        checkingAccountsLabel.setText(String.valueOf(stats.getCheckingAccounts()));
    }
    
    @FXML
    private void handleDashboard() {
        loadDashboardView();
        updateButtonStyles(dashboardButton);
        loadDashboardStatistics();
    }
    
    @FXML
    private void handleCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer.fxml"));
            Parent view = loader.load();
            mainContentPane.setCenter(view);
            updateButtonStyles(customersButton);
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load customers view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account.fxml"));
            Parent view = loader.load();
            mainContentPane.setCenter(view);
            updateButtonStyles(accountsButton);
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load accounts view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction.fxml"));
            Parent view = loader.load();
            mainContentPane.setCenter(view);
            updateButtonStyles(transactionsButton);
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load transactions view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleNewCustomer() {
        showCustomerForm(null);
    }
    
    @FXML
    private void handleNewAccount() {
        showAccountForm(null);
    }
    
    @FXML
    private void handleNewTransaction() {
        showTransactionForm();
    }
    
    @FXML
    private void handleReports() {
        AlertUtil.showInfoAlert("Reports", "Reports feature coming soon!");
    }
    
    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmationAlert("Logout", "Are you sure you want to logout?")) {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            
            // Show login window again
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Ethiopian Bank System - Login");
                loginStage.setScene(new Scene(root, 600, 400));
                loginStage.show();
            } catch (IOException e) {
                AlertUtil.showErrorAlert("Error", "Failed to load login screen: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAbout() {
        AlertUtil.showInfoAlert("About Ethiopian Bank System", 
            "Version 1.0.0\n\n" +
            "Developed for Ethiopian Banking Operations\n" +
            "Features:\n" +
            "- Customer Management\n" +
            "- Account Management\n" +
            "- Transaction Processing\n" +
            "- Reports and Analytics");
    }
    
    private void loadDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_content.fxml"));
            Parent view = loader.load();
            mainContentPane.setCenter(view);
            updateButtonStyles(dashboardButton);
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load dashboard view: " + e.getMessage());
        }
    }
    
    private void showCustomerForm(com.ethiopianbank.models.Customer customer) {
        try {
            // Create a custom dialog for customer form
            Stage dialog = new Stage();
            dialog.setTitle(customer == null ? "Add New Customer" : "Edit Customer");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer_form.fxml"));
            Parent root = loader.load();
            
            CustomerController controller = loader.getController();
            if (customer != null) {
                controller.setCustomerForEdit(customer);
            }
            controller.setDialogStage(dialog);
            
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            
            // Refresh customer view if needed
            if (mainContentPane.getCenter().getId() != null && 
                mainContentPane.getCenter().getId().equals("customerView")) {
                handleCustomers();
            }
            
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load customer form: " + e.getMessage());
        }
    }
    
    private void showAccountForm(com.ethiopianbank.models.Account account) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle(account == null ? "Create New Account" : "Edit Account");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account_form.fxml"));
            Parent root = loader.load();
            
            AccountController controller = loader.getController();
            if (account != null) {
                controller.setAccountForEdit(account);
            }
            controller.setDialogStage(dialog);
            
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            
            // Refresh account view if needed
            if (mainContentPane.getCenter().getId() != null && 
                mainContentPane.getCenter().getId().equals("accountView")) {
                handleAccounts();
            }
            
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load account form: " + e.getMessage());
        }
    }
    
    private void showTransactionForm() {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Perform Transaction");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction_form.fxml"));
            Parent root = loader.load();
            
            TransactionController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setDialogStage(dialog);
            
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            
            // Refresh transaction view if needed
            if (mainContentPane.getCenter().getId() != null && 
                mainContentPane.getCenter().getId().equals("transactionView")) {
                handleTransactions();
            }
            
        } catch (IOException e) {
            AlertUtil.showErrorAlert("Error", "Failed to load transaction form: " + e.getMessage());
        }
    }
    
    private void updateButtonStyles(Button activeButton) {
        // Reset all buttons
        dashboardButton.getStyleClass().remove("active-button");
        customersButton.getStyleClass().remove("active-button");
        accountsButton.getStyleClass().remove("active-button");
        transactionsButton.getStyleClass().remove("active-button");
        
        // Set active button
        activeButton.getStyleClass().add("active-button");
    }
}