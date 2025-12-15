package com.ethiopianbank.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {
    private int accountId;
    private String accountNumber;
    private int customerId;
    private String customerName;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private BigDecimal interestRate;
    private LocalDate openedDate;
    private String status;
    
    // Constructors
    public Account() {}
    
    public Account(int accountId, String accountNumber, int customerId, String customerName,
                   String accountType, BigDecimal balance, String currency, 
                   BigDecimal interestRate, LocalDate openedDate, String status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.interestRate = interestRate;
        this.openedDate = openedDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public LocalDate getOpenedDate() { return openedDate; }
    public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return accountNumber + " - " + accountType + " - Balance: " + currency + " " + balance;
    }
}