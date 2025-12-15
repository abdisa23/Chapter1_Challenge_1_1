package com.ethiopianbank.services;

import com.ethiopianbank.dao.AccountDAO;
import com.ethiopianbank.dao.CustomerDAO;
import com.ethiopianbank.dao.TransactionDAO;
import com.ethiopianbank.models.Account;
import com.ethiopianbank.models.Customer;
import com.ethiopianbank.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BankService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    
    public BankService() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    // Customer operations
    public boolean createCustomer(Customer customer) {
        return customerDAO.createCustomer(customer);
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public List<Customer> searchCustomers(String searchTerm) {
        return customerDAO.searchCustomers(searchTerm);
    }
    
    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }
    
    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
    
    public int getTotalCustomers() {
        return customerDAO.getTotalCustomers();
    }
    
    // Account operations
    public boolean createAccount(Account account) {
        return accountDAO.createAccount(account);
    }
    
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }
    
    public List<Account> getAccountsByCustomerId(int customerId) {
        return accountDAO.getAccountsByCustomerId(customerId);
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }
    
    public Account getAccountById(int accountId) {
        return accountDAO.getAccountById(accountId);
    }
    
    public List<Account> searchAccounts(String searchTerm) {
        return accountDAO.searchAccounts(searchTerm);
    }
    
    public boolean updateAccountStatus(int accountId, String status) {
        return accountDAO.updateAccountStatus(accountId, status);
    }
    
    public BigDecimal getTotalBalance() {
        return accountDAO.getTotalBalance();
    }
    
    public int getAccountCountByType(String accountType) {
        return accountDAO.getAccountCountByType(accountType);
    }
    
    // Transaction operations
    public boolean performDeposit(Transaction transaction) {
        // Update account balance
        Account account = getAccountById(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        BigDecimal newBalance = account.getBalance().add(transaction.getAmount());
        boolean balanceUpdated = accountDAO.updateAccountBalance(account.getAccountId(), newBalance);
        
        if (balanceUpdated) {
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    public boolean performWithdrawal(Transaction transaction) {
        Account account = getAccountById(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        // Check sufficient balance
        if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
        boolean balanceUpdated = accountDAO.updateAccountBalance(account.getAccountId(), newBalance);
        
        if (balanceUpdated) {
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    public boolean performTransfer(Transaction transaction, String recipientAccountNumber) {
        Account sourceAccount = getAccountById(transaction.getAccountId());
        Account recipientAccount = getAccountByNumber(recipientAccountNumber);
        
        if (sourceAccount == null || recipientAccount == null) {
            throw new IllegalArgumentException("One or both accounts not found");
        }
        
        // Check sufficient balance in source account
        if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        // Update source account balance
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(transaction.getAmount());
        boolean sourceUpdated = accountDAO.updateAccountBalance(sourceAccount.getAccountId(), newSourceBalance);
        
        // Update recipient account balance
        BigDecimal newRecipientBalance = recipientAccount.getBalance().add(transaction.getAmount());
        boolean recipientUpdated = accountDAO.updateAccountBalance(recipientAccount.getAccountId(), newRecipientBalance);
        
        if (sourceUpdated && recipientUpdated) {
            transaction.setRecipientAccount(recipientAccountNumber);
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    public List<Transaction> getAccountTransactions(int accountId) {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }
    
    public BigDecimal getTransactionSummary(String transactionType, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionDAO.getTransactionSummary(transactionType, startDate, endDate);
    }
    
    public int getTodaysTransactionCount() {
        return transactionDAO.getTodaysTransactionCount();
    }
    
    public BigDecimal getTodaysTotalDeposits() {
        return transactionDAO.getTodaysTotalDeposits();
    }
    
    // Dashboard statistics
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalCustomers(getTotalCustomers());
        stats.setTotalBalance(getTotalBalance());
        stats.setTodaysTransactions(getTodaysTransactionCount());
        stats.setTodaysDeposits(getTodaysTotalDeposits());
        stats.setSavingsAccounts(getAccountCountByType("SAVINGS"));
        stats.setCheckingAccounts(getAccountCountByType("CHECKING"));
        return stats;
    }
    
    // Inner class for dashboard statistics
    public static class DashboardStats {
        private int totalCustomers;
        private BigDecimal totalBalance;
        private int todaysTransactions;
        private BigDecimal todaysDeposits;
        private int savingsAccounts;
        private int checkingAccounts;
        
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        
        public BigDecimal getTotalBalance() { return totalBalance; }
        public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
        
        public int getTodaysTransactions() { return todaysTransactions; }
        public void setTodaysTransactions(int todaysTransactions) { this.todaysTransactions = todaysTransactions; }
        
        public BigDecimal getTodaysDeposits() { return todaysDeposits; }
        public void setTodaysDeposits(BigDecimal todaysDeposits) { this.todaysDeposits = todaysDeposits; }
        
        public int getSavingsAccounts() { return savingsAccounts; }
        public void setSavingsAccounts(int savingsAccounts) { this.savingsAccounts = savingsAccounts; }
        
        public int getCheckingAccounts() { return checkingAccounts; }
        public void setCheckingAccounts(int checkingAccounts) { this.checkingAccounts = checkingAccounts; }
    }
}