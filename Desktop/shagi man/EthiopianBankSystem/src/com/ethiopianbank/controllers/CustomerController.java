package com.ethiopianbank.controllers;

import com.ethiopianbank.models.Customer;
import com.ethiopianbank.services.BankService;
import com.ethiopianbank.utils.AlertUtil;
import com.ethiopianbank.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button newCustomerButton;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> idNumberColumn;
    @FXML private TableColumn<Customer, LocalDate> dobColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button viewAccountsButton;
    
    private BankService bankService;
    private ObservableList<Customer> customers;
    private Stage dialogStage;
    private Customer customerForEdit;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bankService = new BankService();
        this.customers = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadCustomers();
        setupEventHandlers();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setCustomerForEdit(Customer customer) {
        this.customerForEdit = customer;
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        idNumberColumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        
        // Format date column
        dobColumn.setCellFactory(column -> new TableCell<Customer, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        
        customerTable.setItems(customers);
        
        // Update button states based on selection
        customerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateButtonStates(newSelection != null));
    }
    
    private void loadCustomers() {
        customers.clear();
        customers.addAll(bankService.getAllCustomers());
    }
    
    private void setupEventHandlers() {
        searchButton.setOnAction(e -> handleSearch());
        refreshButton.setOnAction(e -> handleRefresh());
        newCustomerButton.setOnAction(e -> handleNewCustomer());
        editButton.setOnAction(e -> handleEditCustomer());
        deleteButton.setOnAction(e -> handleDeleteCustomer());
        viewAccountsButton.setOnAction(e -> handleViewAccounts());
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCustomers();
        } else {
            customers.clear();
            customers.addAll(bankService.searchCustomers(searchTerm));
        }
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadCustomers();
    }
    
    @FXML
    private void handleNewCustomer() {
        showCustomerForm(null);
    }
    
    @FXML
    private void handleEditCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            showCustomerForm(selectedCustomer);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select a customer to edit");
        }
    }
    
    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            if (AlertUtil.showConfirmationAlert("Delete Customer", 
                "Are you sure you want to delete customer: " + selectedCustomer.getFullName() + "?")) {
                
                boolean success = bankService.deleteCustomer(selectedCustomer.getCustomerId());
                if (success) {
                    AlertUtil.showSuccessAlert("Success", "Customer deleted successfully");
                    loadCustomers();
                } else {
                    AlertUtil.showErrorAlert("Error", "Failed to delete customer. Customer may have active accounts.");
                }
            }
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select a customer to delete");
        }
    }
    
    @FXML
    private void handleViewAccounts() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            showCustomerAccounts(selectedCustomer);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select a customer to view accounts");
        }
    }
    
    private void showCustomerForm(Customer customer) {
        // Create a dialog for customer form
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Add New Customer" : "Edit Customer");
        
        // Set up buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList(
            "MALE", "FEMALE", "OTHER"
        ));
        TextField idNumberField = new TextField();
        ComboBox<String> idTypeCombo = new ComboBox<>(FXCollections.observableArrayList(
            "NATIONAL_ID", "PASSPORT", "DRIVER_LICENSE"
        ));
        
        // Populate fields if editing
        if (customer != null) {
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            middleNameField.setText(customer.getMiddleName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            addressArea.setText(customer.getAddress());
            dobPicker.setValue(customer.getDateOfBirth());
            genderCombo.setValue(customer.getGender());
            idNumberField.setText(customer.getIdNumber());
            idTypeCombo.setValue(customer.getIdType());
        }
        
        // Add fields to grid
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Middle Name:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Date of Birth:"), 0, 5);
        grid.add(dobPicker, 1, 5);
        grid.add(new Label("Gender:"), 0, 6);
        grid.add(genderCombo, 1, 6);
        grid.add(new Label("ID Number:"), 0, 7);
        grid.add(idNumberField, 1, 7);
        grid.add(new Label("ID Type:"), 0, 8);
        grid.add(idTypeCombo, 1, 8);
        grid.add(new Label("Address:"), 0, 9);
        grid.add(addressArea, 1, 9);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Add validation listeners
        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        Runnable validateForm = () -> {
            boolean valid = !firstNameField.getText().trim().isEmpty() &&
                          !lastNameField.getText().trim().isEmpty() &&
                          ValidationUtil.isValidPhone(phoneField.getText().trim());
            saveButton.setDisable(!valid);
        };
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Customer updatedCustomer = new Customer();
                if (customer != null) {
                    updatedCustomer.setCustomerId(customer.getCustomerId());
                }
                updatedCustomer.setFirstName(firstNameField.getText().trim());
                updatedCustomer.setLastName(lastNameField.getText().trim());
                updatedCustomer.setMiddleName(middleNameField.getText().trim());
                updatedCustomer.setEmail(emailField.getText().trim());
                updatedCustomer.setPhone(phoneField.getText().trim());
                updatedCustomer.setAddress(addressArea.getText().trim());
                updatedCustomer.setDateOfBirth(dobPicker.getValue());
                updatedCustomer.setGender(genderCombo.getValue());
                updatedCustomer.setIdNumber(idNumberField.getText().trim());
                updatedCustomer.setIdType(idTypeCombo.getValue());
                return updatedCustomer;
            }
            return null;
        });
        
        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(updatedCustomer -> {
            try {
                boolean success;
                if (customer != null) {
                    success = bankService.updateCustomer(updatedCustomer);
                } else {
                    success = bankService.createCustomer(updatedCustomer);
                }
                
                if (success) {
                    AlertUtil.showSuccessAlert("Success", 
                        "Customer " + (customer != null ? "updated" : "created") + " successfully");
                    loadCustomers();
                } else {
                    AlertUtil.showErrorAlert("Error", "Failed to save customer");
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("Error", e.getMessage());
            }
        });
    }
    
    private void showCustomerAccounts(Customer customer) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Accounts for " + customer.getFullName());
            
            // Create a simple dialog showing customer accounts
            VBox vbox = new VBox(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
            
            Label titleLabel = new Label("Accounts for " + customer.getFullName());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            ListView<String> accountList = new ListView<>();
            ObservableList<String> accounts = FXCollections.observableArrayList();
            
            // Get customer accounts
            bankService.getAccountsByCustomerId(customer.getCustomerId())
                .forEach(account -> accounts.add(account.toString()));
            
            if (accounts.isEmpty()) {
                accounts.add("No accounts found for this customer.");
            }
            
            accountList.setItems(accounts);
            
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> dialog.close());
            
            vbox.getChildren().addAll(titleLabel, accountList, closeButton);
            
            Scene scene = new Scene(vbox, 400, 300);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
            
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Error", "Failed to show customer accounts: " + e.getMessage());
        }
    }
    
    private void updateButtonStates(boolean hasSelection) {
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
        viewAccountsButton.setDisable(!hasSelection);
    }
}