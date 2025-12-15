package com.ethiopianbank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String referenceNumber;
    private LocalDateTime transactionDate;
    private int performedBy;
    private String performedByName;
    private String recipientAccount;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(int transactionId, int accountId, String accountNumber, 
                       String transactionType, BigDecimal amount, String description, 
                       String referenceNumber, LocalDateTime transactionDate, 
                       int performedBy, String performedByName, String recipientAccount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.transactionDate = transactionDate;
        this.performedBy = performedBy;
        this.performedByName = performedByName;
        this.recipientAccount = recipientAccount;
    }
    
    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public int getPerformedBy() { return performedBy; }
    public void setPerformedBy(int performedBy) { this.performedBy = performedBy; }
    
    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
    
    public String getRecipientAccount() { return recipientAccount; }
    public void setRecipientAccount(String recipientAccount) { this.recipientAccount = recipientAccount; }
    
    @Override
    public String toString() {
        return transactionType + " - " + amount + " - " + transactionDate;
    }
}