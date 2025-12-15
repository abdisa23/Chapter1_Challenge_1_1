package com.ethiopianbank.controllers;

import com.ethiopianbank.models.Transaction;
import com.ethiopianbank.models.User;
import com.ethiopianbank.services.BankService;
import com.ethiopianbank.services.TransactionService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField accountField;
    @FXML private Button filterButton;
    @FXML private Button clearButton;
    @FXML private Button exportButton;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, LocalDateTime> dateColumn;
    @FXML private TableColumn<Transaction, String> referenceColumn;
    @FXML private TableColumn<Transaction, String> accountColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> performedByColumn;
    @FXML private Label totalDepositsLabel;
    @FXML private Label totalWithdrawalsLabel;
    @FXML private Label totalTransfersLabel;
    
    private BankService bankService;
    private TransactionService transactionService;
    private ObservableList<Transaction> transactions;
    private User currentUser;
    private Stage dialogStage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bankService = new BankService();
        this.transactionService = new TransactionService();
        this.transactions = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupComboBoxes();
        loadAllTransactions();
        updateTransactionTotals();
        setupEventHandlers();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        performedByColumn.setCellValueFactory(new PropertyValueFactory<>("performedByName"));
        
        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<Transaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }
        });
        
        // Format amount column
        amountColumn.setCellFactory(column -> new TableCell<Transaction, BigDecimal>() {
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
        
        transactionTable.setItems(transactions);
    }
    
    private void setupComboBoxes() {
        typeCombo.setItems(FXCollections.observableArrayList(
            "ALL", "DEPOSIT", "WITHDRAWAL", "TRANSFER", "INTEREST"
        ));
        typeCombo.setValue("ALL");
        
        // Set default date range (last 30 days)
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
        toDatePicker.setValue(LocalDate.now());
    }
    
    private void setupEventHandlers() {
        filterButton.setOnAction(e -> handleFilter());
        clearButton.setOnAction(e -> handleClear());
        exportButton.setOnAction(e -> handleExport());
    }
    
    private void loadAllTransactions() {
        transactions.clear();
        transactions.addAll(bankService.getAllTransactions());
    }
    
    private void updateTransactionTotals() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        BigDecimal totalDeposits = bankService.getTransactionSummary("DEPOSIT", startOfDay, endOfDay);
        BigDecimal totalWithdrawals = bankService.getTransactionSummary("WITHDRAWAL", startOfDay, endOfDay);
        BigDecimal totalTransfers = bankService.getTransactionSummary("TRANSFER", startOfDay, endOfDay);
        
        totalDepositsLabel.setText("ETB " + totalDeposits);
        totalWithdrawalsLabel.setText("ETB " + totalWithdrawals);
        totalTransfersLabel.setText("ETB " + totalTransfers);
    }
    
    @FXML
    private void handleFilter() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String transactionType = typeCombo.getValue();
        String accountNumber = accountField.getText().trim();
        
        if (fromDate == null || toDate == null) {
            AlertUtil.showErrorAlert("Error", "Please select both from and to dates");
            return;
        }
        
        if (fromDate.isAfter(toDate)) {
            AlertUtil.showErrorAlert("Error", "From date cannot be after to date");
            return;
        }
        
        transactions.clear();
        
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(23, 59, 59);
        
        if (accountNumber.isEmpty()) {
            // Filter by date only
            transactions.addAll(transactionService.getTransactionsByDateRange(startDateTime, endDateTime));
        } else {
            // Filter by account and date
            com.ethiopianbank.models.Account account = bankService.getAccountByNumber(accountNumber);
            if (account != null) {
                transactions.addAll(bankService.getAccountTransactions(account.getAccountId()));
            } else {
                AlertUtil.showErrorAlert("Error", "Account not found");
                return;
            }
        }
        
        // Apply transaction type filter if not "ALL"
        if (!transactionType.equals("ALL")) {
            transactions.removeIf(t -> !t.getTransactionType().equals(transactionType));
        }
        
        AlertUtil.showInfoAlert("Filter Applied", 
            String.format("Showing %d transactions for the selected criteria", transactions.size()));
    }
    
    @FXML
    private void handleClear() {
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
        toDatePicker.setValue(LocalDate.now());
        typeCombo.setValue("ALL");
        accountField.clear();
        loadAllTransactions();
        updateTransactionTotals();
    }
    
    @FXML
    private void handleExport() {
        AlertUtil.showInfoAlert("Export Feature", 
            "Export functionality will be implemented in the next version.\n" +
            "Transactions will be exported to CSV format.");
    }
    
    @FXML
    private void handleNewDeposit() {
        showDepositForm();
    }
    
    @FXML
    private void handleNewWithdrawal() {
        showWithdrawalForm();
    }
    
    @FXML
    private void handleNewTransfer() {
        showTransferForm();
    }
    
    private void showDepositForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("New Deposit");
        
        // Set up buttons
        ButtonType depositButtonType = new ButtonType("Deposit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(depositButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Deposit description");
        descriptionArea.setPrefRowCount(2);
        
        Label accountInfoLabel = new Label();
        
        accountNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                com.ethiopianbank.models.Account account = bankService.getAccountByNumber(newVal.trim());
                if (account != null) {
                    accountInfoLabel.setText("Account Holder: " + account.getCustomerName() + 
                                           " | Balance: ETB " + account.getBalance());
                } else {
                    accountInfoLabel.setText("Account not found");
                    accountInfoLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                accountInfoLabel.setText("");
            }
        });
        
        grid.add(new Label("Account Number:"), 0, 0);
        grid.add(accountNumberField, 1, 0);
        grid.add(accountInfoLabel, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == depositButtonType) {
                try {
                    String accountNumber = accountNumberField.getText().trim();
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    String description = descriptionArea.getText().trim();
                    
                    if (accountNumber.isEmpty()) {
                        AlertUtil.showErrorAlert("Error", "Please enter account number");
                        return null;
                    }
                    
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                        return null;
                    }
                    
                    // Get account
                    com.ethiopianbank.models.Account account = bankService.getAccountByNumber(accountNumber);
                    if (account == null) {
                        AlertUtil.showErrorAlert("Error", "Account not found");
                        return null;
                    }
                    
                    // Create transaction
                    Transaction transaction = new Transaction();
                    transaction.setAccountId(account.getAccountId());
                    transaction.setTransactionType("DEPOSIT");
                    transaction.setAmount(amount);
                    transaction.setDescription(description.isEmpty() ? "Cash deposit" : description);
                    transaction.setPerformedBy(currentUser != null ? currentUser.getUserId() : 0);
                    
                    boolean success = bankService.performDeposit(transaction);
                    if (success) {
                        AlertUtil.showSuccessAlert("Success", "Deposit successful");
                        loadAllTransactions();
                        updateTransactionTotals();
                    } else {
                        AlertUtil.showErrorAlert("Error", "Failed to process deposit");
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
    
    private void showWithdrawalForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("New Withdrawal");
        
        // Set up buttons
        ButtonType withdrawButtonType = new ButtonType("Withdraw", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(withdrawButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Withdrawal description");
        descriptionArea.setPrefRowCount(2);
        
        Label accountInfoLabel = new Label();
        
        accountNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                com.ethiopianbank.models.Account account = bankService.getAccountByNumber(newVal.trim());
                if (account != null) {
                    accountInfoLabel.setText("Account Holder: " + account.getCustomerName() + 
                                           " | Balance: ETB " + account.getBalance());
                } else {
                    accountInfoLabel.setText("Account not found");
                    accountInfoLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                accountInfoLabel.setText("");
            }
        });
        
        grid.add(new Label("Account Number:"), 0, 0);
        grid.add(accountNumberField, 1, 0);
        grid.add(accountInfoLabel, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == withdrawButtonType) {
                try {
                    String accountNumber = accountNumberField.getText().trim();
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    String description = descriptionArea.getText().trim();
                    
                    if (accountNumber.isEmpty()) {
                        AlertUtil.showErrorAlert("Error", "Please enter account number");
                        return null;
                    }
                    
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                        return null;
                    }
                    
                    // Get account
                    com.ethiopianbank.models.Account account = bankService.getAccountByNumber(accountNumber);
                    if (account == null) {
                        AlertUtil.showErrorAlert("Error", "Account not found");
                        return null;
                    }
                    
                    // Check sufficient balance
                    if (amount.compareTo(account.getBalance()) > 0) {
                        AlertUtil.showErrorAlert("Error", "Insufficient funds");
                        return null;
                    }
                    
                    // Create transaction
                    Transaction transaction = new Transaction();
                    transaction.setAccountId(account.getAccountId());
                    transaction.setTransactionType("WITHDRAWAL");
                    transaction.setAmount(amount);
                    transaction.setDescription(description.isEmpty() ? "Cash withdrawal" : description);
                    transaction.setPerformedBy(currentUser != null ? currentUser.getUserId() : 0);
                    
                    boolean success = bankService.performWithdrawal(transaction);
                    if (success) {
                        AlertUtil.showSuccessAlert("Success", "Withdrawal successful");
                        loadAllTransactions();
                        updateTransactionTotals();
                    } else {
                        AlertUtil.showErrorAlert("Error", "Failed to process withdrawal");
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
    
    private void showTransferForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("New Transfer");
        
        // Set up buttons
        ButtonType transferButtonType = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(transferButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField sourceAccountField = new TextField();
        sourceAccountField.setPromptText("Enter source account number");
        
        TextField recipientAccountField = new TextField();
        recipientAccountField.setPromptText("Enter recipient account number");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Transfer description");
        descriptionArea.setPrefRowCount(2);
        
        Label sourceAccountInfo = new Label();
        Label recipientAccountInfo = new Label();
        
        sourceAccountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                com.ethiopianbank.models.Account account = bankService.getAccountByNumber(newVal.trim());
                if (account != null) {
                    sourceAccountInfo.setText("Holder: " + account.getCustomerName() + 
                                            " | Balance: ETB " + account.getBalance());
                } else {
                    sourceAccountInfo.setText("Account not found");
                    sourceAccountInfo.setStyle("-fx-text-fill: red;");
                }
            } else {
                sourceAccountInfo.setText("");
            }
        });
        
        recipientAccountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                com.ethiopianbank.models.Account account = bankService.getAccountByNumber(newVal.trim());
                if (account != null) {
                    recipientAccountInfo.setText("Holder: " + account.getCustomerName() + 
                                               " | Balance: ETB " + account.getBalance());
                } else {
                    recipientAccountInfo.setText("Account not found");
                    recipientAccountInfo.setStyle("-fx-text-fill: red;");
                }
            } else {
                recipientAccountInfo.setText("");
            }
        });
        
        grid.add(new Label("Source Account:"), 0, 0);
        grid.add(sourceAccountField, 1, 0);
        grid.add(sourceAccountInfo, 1, 1);
        grid.add(new Label("Recipient Account:"), 0, 2);
        grid.add(recipientAccountField, 1, 2);
        grid.add(recipientAccountInfo, 1, 3);
        grid.add(new Label("Amount:"), 0, 4);
        grid.add(amountField, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(descriptionArea, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == transferButtonType) {
                try {
                    String sourceAccount = sourceAccountField.getText().trim();
                    String recipientAccount = recipientAccountField.getText().trim();
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    String description = descriptionArea.getText().trim();
                    
                    if (sourceAccount.isEmpty() || recipientAccount.isEmpty()) {
                        AlertUtil.showErrorAlert("Error", "Please enter both account numbers");
                        return null;
                    }
                    
                    if (sourceAccount.equals(recipientAccount)) {
                        AlertUtil.showErrorAlert("Error", "Source and recipient accounts cannot be the same");
                        return null;
                    }
                    
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        AlertUtil.showErrorAlert("Error", "Amount must be greater than zero");
                        return null;
                    }
                    
                    // Get accounts
                    com.ethiopianbank.models.Account source = bankService.getAccountByNumber(sourceAccount);
                    com.ethiopianbank.models.Account recipient = bankService.getAccountByNumber(recipientAccount);
                    
                    if (source == null || recipient == null) {
                        AlertUtil.showErrorAlert("Error", "One or both accounts not found");
                        return null;
                    }
                    
                    // Check sufficient balance
                    if (amount.compareTo(source.getBalance()) > 0) {
                        AlertUtil.showErrorAlert("Error", "Insufficient funds in source account");
                        return null;
                    }
                    
                    // Create transaction
                    Transaction transaction = new Transaction();
                    transaction.setAccountId(source.getAccountId());
                    transaction.setTransactionType("TRANSFER");
                    transaction.setAmount(amount);
                    transaction.setDescription(description.isEmpty() ? "Fund transfer" : description);
                    transaction.setPerformedBy(currentUser != null ? currentUser.getUserId() : 0);
                    
                    boolean success = bankService.performTransfer(transaction, recipientAccount);
                    if (success) {
                        AlertUtil.showSuccessAlert("Success", "Transfer successful");
                        loadAllTransactions();
                        updateTransactionTotals();
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
}