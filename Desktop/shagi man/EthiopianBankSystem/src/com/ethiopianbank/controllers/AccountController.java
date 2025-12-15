package com.ethiopianbank.controllers;

import com.ethiopianbank.models.Account;
import com.ethiopianbank.models.Customer;
import com.ethiopianbank.services.BankService;
import com.ethiopianbank.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button newAccountButton;
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> accountNumberColumn;
    @FXML private TableColumn<Account, String> customerNameColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, BigDecimal> balanceColumn;
    @FXML private TableColumn<Account, String> currencyColumn;
    @FXML private TableColumn<Account, String> statusColumn;
    @FXML private TableColumn<Account, LocalDate> openedDateColumn;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;
    @FXML private Button transferButton;
    @FXML private Button deactivateButton;
    @FXML private Button viewTransactionsButton;
    
    private BankService bankService;
    private ObservableList<Account> accounts;
    private Stage dialogStage;
    private Account accountForEdit;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bankService = new BankService();
        this.accounts = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadAccounts();
        setupEventHandlers();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setAccountForEdit(Account account) {
        this.accountForEdit = account;
    }
    
    private void setupTableColumns() {
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        openedDateColumn.setCellValueFactory(new PropertyValueFactory<>("openedDate"));
        
        // Format balance column
        balanceColumn.setCellFactory(column -> new TableCell<Account, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("ETB " + item.toString());
                }
            }
        });
        
        // Format date column
        openedDateColumn.setCellFactory(column -> new TableCell<Account, LocalDate>() {
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
        
        accountTable.setItems(accounts);
        
        // Update button states based on selection
        accountTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateButtonStates(newSelection != null));
    }
    
    private void loadAccounts() {
        accounts.clear();
        accounts.addAll(bankService.getAllAccounts());
    }
    
    private void setupEventHandlers() {
        searchButton.setOnAction(e -> handleSearch());
        refreshButton.setOnAction(e -> handleRefresh());
        newAccountButton.setOnAction(e -> handleNewAccount());
        depositButton.setOnAction(e -> handleDeposit());
        withdrawButton.setOnAction(e -> handleWithdraw());
        transferButton.setOnAction(e -> handleTransfer());
        deactivateButton.setOnAction(e -> handleDeactivate());
        viewTransactionsButton.setOnAction(e -> handleViewTransactions());
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAccounts();
        } else {
            accounts.clear();
            accounts.addAll(bankService.searchAccounts(searchTerm));
        }
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadAccounts();
    }
    
    @FXML
    private void handleNewAccount() {
        showAccountForm(null);
    }
    
    @FXML
    private void handleDeposit() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            showDepositForm(selectedAccount);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select an account to deposit");
        }
    }
    
    @FXML
    private void handleWithdraw() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            showWithdrawForm(selectedAccount);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select an account to withdraw");
        }
    }
    
    @FXML
    private void handleTransfer() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            showTransferForm(selectedAccount);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select an account to transfer from");
        }
    }
    
    @FXML
    private void handleDeactivate() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            if (AlertUtil.showConfirmationAlert("Deactivate Account", 
                "Are you sure you want to deactivate account: " + selectedAccount.getAccountNumber() + "?")) {
                
                boolean success = bankService.updateAccountStatus(selectedAccount.getAccountId(), "INACTIVE");
                if (success) {
                    AlertUtil.showSuccessAlert("Success", "Account deactivated successfully");
                    loadAccounts();
                } else {
                    AlertUtil.showErrorAlert("Error", "Failed to deactivate account");
                }
            }
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select an account to deactivate");
        }
    }
    
    @FXML
    private void handleViewTransactions() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            showAccountTransactions(selectedAccount);
        } else {
            AlertUtil.showWarningAlert("No Selection", "Please select an account to view transactions");
        }
    }
    
    private void showAccountForm(Account account) {
        // Create a dialog for account form
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle(account == null ? "Create New Account" : "Edit Account");
        
        // Set up buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Create form fields
        ComboBox<Customer> customerCombo = new ComboBox<>();
        TextField accountNumberField = new TextField();
        ComboBox<String> accountTypeCombo = new ComboBox<>(FXCollections.observableArrayList(
            "SAVINGS", "CHECKING", "BUSINESS", "FIXED_DEPOSIT"
        ));
        TextField balanceField = new TextField();
        ComboBox<String> currencyCombo = new ComboBox<>(FXCollections.observableArrayList("ETB", "USD"));
        TextField interestRateField = new TextField();
        DatePicker openedDatePicker = new DatePicker();
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList(
            "ACTIVE", "INACTIVE", "SUSPENDED"
        ));
        
        // Load customers for combo box
        bankService.getAllCustomers().forEach(customerCombo.getItems()::add);
        customerCombo.setConverter(new javafx.util.StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFullName() + " (" + customer.getPhone() + ")" : "";
            }
            
            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        
        // Set defaults
        balanceField.setText("0.00");
        interestRateField.setText("0.00");
        currencyCombo.setValue("ETB");
        openedDatePicker.setValue(LocalDate.now());
        statusCombo.setValue("ACTIVE");
        
        // Populate fields if editing
        if (account != null) {
            Customer customer = bankService.getCustomerById(account.getCustomerId());
            if (customer != null) {
                customerCombo.setValue(customer);
            }
            accountNumberField.setText(account.getAccountNumber());
            accountTypeCombo.setValue(account.getAccountType());
            balanceField.setText(account.getBalance().toString());
            currencyCombo.setValue(account.getCurrency());
            interestRateField.setText(account.getInterestRate().toString());
            openedDatePicker.setValue(account.getOpenedDate());
            statusCombo.setValue(account.getStatus());
        }
        
        // Add fields to grid
        grid.add(new Label("Customer:"), 0, 0);
        grid.add(customerCombo, 1, 0);
        grid.add(new Label("Account Number:"), 0, 1);
        grid.add(accountNumberField, 1, 1);
        grid.add(new Label("Account Type:"), 0, 2);
        grid.add(accountTypeCombo, 1, 2);
        grid.add(new Label("Initial Balance:"), 0, 3);
        grid.add(balanceField, 1, 3);
        grid.add(new Label("Currency:"), 0, 4);
        grid.add(currencyCombo, 1, 4);
        grid.add(new Label("Interest Rate (%):"), 0, 5);
        grid.add(interestRateField, 1, 5);
        grid.add(new Label("Opened Date:"), 0, 6);
        grid.add(openedDatePicker, 1, 6);
        grid.add(new Label("Status:"), 0, 7);
        grid.add(statusCombo, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Add validation listeners
        customerCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        accountTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        Runnable validateForm = () -> {
            boolean valid = customerCombo.getValue() != null && 
                          accountTypeCombo.getValue() != null;
            saveButton.setDisable(!valid);
        };
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Account updatedAccount = new Account();
                if (account != null) {
                    updatedAccount.setAccountId(account.getAccountId());
                }
                
                Customer selectedCustomer = customerCombo.getValue();
                if (selectedCustomer != null) {
                    updatedAccount.setCustomerId(selectedCustomer.getCustomerId());
                    updatedAccount.setCustomerName(selectedCustomer.getFullName());
                }
                
                updatedAccount.setAccountNumber(accountNumberField.getText().trim());
                updatedAccount.setAccountType(accountTypeCombo.getValue());
                updatedAccount.setBalance(new BigDecimal(balanceField.getText().trim()));
                updatedAccount.setCurrency(currencyCombo.getValue());
                updatedAccount.setInterestRate(new BigDecimal(interestRateField.getText().trim()));
                updatedAccount.setOpenedDate(openedDatePicker.getValue());
                updatedAccount.setStatus(statusCombo.getValue());
                
                return updatedAccount;
            }
            return null;
        });
        
        Optional<Account> result = dialog.showAndWait();
        result.ifPresent(updatedAccount -> {
            try {
                boolean success;
                if (account != null) {
                    // For editing, we need to update the account
                    // Note: In real implementation, you'd need an updateAccount method
                    AlertUtil.showInfoAlert("Info", "Account editing feature coming soon!");
                } else {
                    success = bankService.createAccount(updatedAccount);
                    if (success) {
                        AlertUtil.showSuccessAlert("Success", "Account created successfully");
                        loadAccounts();
                    } else {
                        AlertUtil.showErrorAlert("Error", "Failed to create account");
                    }
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("Error", e.getMessage());
            }
        });
    }
    
    private void showDepositForm(Account account) {
        Dialog<BigDecimal> dialog = new Dialog<>();
        dialog.setTitle("Deposit to Account: " + account.getAccountNumber());
        
        // Set up buttons
        ButtonType depositButtonType = new ButtonType("Deposit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(depositButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        Label currentBalanceLabel = new Label("Current Balance: ETB " + account.getBalance());
        Label newBalanceLabel = new Label();
        
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal amount = new BigDecimal(newVal);
                BigDecimal newBalance = account.getBalance().add(amount);
                newBalanceLabel.setText("New Balance: ETB " + newBalance);
            } catch (NumberFormatException e) {
                newBalanceLabel.setText("Invalid amount");
            }
        });
        
        grid.add(new Label("Amount to Deposit:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(currentBalanceLabel, 0, 1);
        grid.add(newBalanceLabel, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == depositButtonType) {
                try {
                    return new BigDecimal(amountField.getText().trim());
                } catch (NumberFormatException e) {
                    AlertUtil.showErrorAlert("Error", "Invalid amount");
                    return null;
                }
            }
            return null;
        });
        
        Optional<BigDecimal> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                return;
            }
            
            // Create transaction
            com.ethiopianbank.models.Transaction transaction = new com.ethiopianbank.models.Transaction();
            transaction.setAccountId(account.getAccountId());
            transaction.setTransactionType("DEPOSIT");
            transaction.setAmount(amount);
            transaction.setDescription("Cash deposit");
            
            try {
                boolean success = bankService.performDeposit(transaction);
                if (success) {
                    AlertUtil.showSuccessAlert("Success", "Deposit successful");
                    loadAccounts();
                } else {
                    AlertUtil.showErrorAlert("Error", "Failed to process deposit");
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("Error", e.getMessage());
            }
        });
    }
    
    private void showWithdrawForm(Account account) {
        Dialog<BigDecimal> dialog = new Dialog<>();
        dialog.setTitle("Withdraw from Account: " + account.getAccountNumber());
        
        // Set up buttons
        ButtonType withdrawButtonType = new ButtonType("Withdraw", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(withdrawButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        Label currentBalanceLabel = new Label("Current Balance: ETB " + account.getBalance());
        Label newBalanceLabel = new Label();
        
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal amount = new BigDecimal(newVal);
                BigDecimal newBalance = account.getBalance().subtract(amount);
                newBalanceLabel.setText("New Balance: ETB " + newBalance);
                
                // Highlight insufficient funds
                if (amount.compareTo(account.getBalance()) > 0) {
                    newBalanceLabel.setStyle("-fx-text-fill: red;");
                } else {
                    newBalanceLabel.setStyle("");
                }
            } catch (NumberFormatException e) {
                newBalanceLabel.setText("Invalid amount");
            }
        });
        
        grid.add(new Label("Amount to Withdraw:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(currentBalanceLabel, 0, 1);
        grid.add(newBalanceLabel, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == withdrawButtonType) {
                try {
                    return new BigDecimal(amountField.getText().trim());
                } catch (NumberFormatException e) {
                    AlertUtil.showErrorAlert("Error", "Invalid amount");
                    return null;
                }
            }
            return null;
        });
        
        Optional<BigDecimal> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                return;
            }
            
            if (amount.compareTo(account.getBalance()) > 0) {
                AlertUtil.showErrorAlert("Error", "Insufficient funds");
                return;
            }
            
            // Create transaction
            com.ethiopianbank.models.Transaction transaction = new com.ethiopianbank.models.Transaction();
            transaction.setAccountId(account.getAccountId());
            transaction.setTransactionType("WITHDRAWAL");
            transaction.setAmount(amount);
            transaction.setDescription("Cash withdrawal");
            
            try {
                boolean success = bankService.performWithdrawal(transaction);
                if (success) {
                    AlertUtil.showSuccessAlert("Success", "Withdrawal successful");
                    loadAccounts();
                } else {
                    AlertUtil.showErrorAlert("Error", "Failed to process withdrawal");
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("Error", e.getMessage());
            }
        });
    }
    
    private void showTransferForm(Account sourceAccount) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Transfer from Account: " + sourceAccount.getAccountNumber());
        
        // Set up buttons
        ButtonType transferButtonType = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(transferButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField recipientAccountField = new TextField();
        recipientAccountField.setPromptText("Enter recipient account number");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Transfer description");
        descriptionArea.setPrefRowCount(2);
        
        Label currentBalanceLabel = new Label("Current Balance: ETB " + sourceAccount.getBalance());
        Label statusLabel = new Label();
        
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal amount = new BigDecimal(newVal);
                if (amount.compareTo(sourceAccount.getBalance()) > 0) {
                    statusLabel.setText("Insufficient funds");
                    statusLabel.setStyle("-fx-text-fill: red;");
                } else {
                    statusLabel.setText("Transfer will proceed");
                    statusLabel.setStyle("");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid amount");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        grid.add(new Label("Recipient Account:"), 0, 0);
        grid.add(recipientAccountField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        grid.add(currentBalanceLabel, 0, 3);
        grid.add(statusLabel, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == transferButtonType) {
                try {
                    String recipientAccount = recipientAccountField.getText().trim();
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    String description = descriptionArea.getText().trim();
                    
                    if (recipientAccount.isEmpty()) {
                        AlertUtil.showErrorAlert("Error", "Please enter recipient account number");
                        return null;
                    }
                    
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                        return null;
                    }
                    
                    if (amount.compareTo(sourceAccount.getBalance()) > 0) {
                        AlertUtil.showErrorAlert("Error", "Insufficient funds");
                        return null;
                    }
                    
                    // Check if recipient account exists
                    Account recipient = bankService.getAccountByNumber(recipientAccount);
                    if (recipient == null) {
                        AlertUtil.showErrorAlert("Error", "Recipient account not found");
                        return null;
                    }
                    
                    // Create transaction
                    com.ethiopianbank.models.Transaction transaction = new com.ethiopianbank.models.Transaction();
                    transaction.setAccountId(sourceAccount.getAccountId());
                    transaction.setTransactionType("TRANSFER");
                    transaction.setAmount(amount);
                    transaction.setDescription(description.isEmpty() ? "Fund transfer" : description);
                    
                    boolean success = bankService.performTransfer(transaction, recipientAccount);
                    if (success) {
                        AlertUtil.showSuccessAlert("Success", "Transfer successful");
                        loadAccounts();
                    } else {
                        AlertUtil.showErrorAlert("Error", "Failed to process transfer");
                    }
                    
                } catch (NumberFormatException e) {
                    AlertUtil.showErrorAlert("Error", "Invalid amount format");
                } catch (Exception e) {
                    AlertUtil.showErrorAlert("Error", e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showAccountTransactions(Account account) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Transactions for Account: " + account.getAccountNumber());
            
            // Create table for transactions
            TableView<com.ethiopianbank.models.Transaction> transactionTable = new TableView<>();
            
            TableColumn<com.ethiopianbank.models.Transaction, String> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
            
            TableColumn<com.ethiopianbank.models.Transaction, String> typeColumn = new TableColumn<>("Type");
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
            
            TableColumn<com.ethiopianbank.models.Transaction, BigDecimal> amountColumn = new TableColumn<>("Amount");
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            
            TableColumn<com.ethiopianbank.models.Transaction, String> descColumn = new TableColumn<>("Description");
            descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            
            TableColumn<com.ethiopianbank.models.Transaction, String> refColumn = new TableColumn<>("Reference");
            refColumn.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));
            
            transactionTable.getColumns().addAll(dateColumn, typeColumn, amountColumn, descColumn, refColumn);
            
            // Load transactions
            ObservableList<com.ethiopianbank.models.Transaction> transactions = 
                FXCollections.observableArrayList(bankService.getAccountTransactions(account.getAccountId()));
            transactionTable.setItems(transactions);
            
            // Create layout
            VBox vbox = new VBox(10);
            vbox.setPadding(new javafx.geometry.Insets(20));
            
            Label titleLabel = new Label("Transaction History for " + account.getAccountNumber());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Label balanceLabel = new Label("Current Balance: ETB " + account.getBalance());
            
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> dialog.close());
            
            vbox.getChildren().addAll(titleLabel, balanceLabel, transactionTable, closeButton);
            
            Scene scene = new Scene(vbox, 800, 500);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
            
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Error", "Failed to show transactions: " + e.getMessage());
        }
    }
    
    private void updateButtonStates(boolean hasSelection) {
        depositButton.setDisable(!hasSelection);
        withdrawButton.setDisable(!hasSelection);
        transferButton.setDisable(!hasSelection);
        deactivateButton.setDisable(!hasSelection);
        viewTransactionsButton.setDisable(!hasSelection);
    }
}