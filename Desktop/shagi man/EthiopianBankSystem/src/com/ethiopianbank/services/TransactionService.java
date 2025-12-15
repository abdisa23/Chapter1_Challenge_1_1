package com.ethiopianbank.services;

import com.ethiopianbank.dao.TransactionDAO;
import com.ethiopianbank.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private TransactionDAO transactionDAO;
    
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }
    
    public boolean recordTransaction(Transaction transaction) {
        return transactionDAO.createTransaction(transaction);
    }
    
    public List<Transaction> getTransactionsByAccount(int accountId) {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionDAO.getTransactionsByDateRange(startDate, endDate);
    }
    
    public BigDecimal getDailyTransactionTotal(String transactionType) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return transactionDAO.getTransactionSummary(transactionType, startOfDay, endOfDay);
    }
    
    public int getTransactionCountToday() {
        return transactionDAO.getTodaysTransactionCount();
    }
}