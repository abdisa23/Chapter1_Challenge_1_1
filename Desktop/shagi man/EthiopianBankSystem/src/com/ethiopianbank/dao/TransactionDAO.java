package com.ethiopianbank.dao;

import com.ethiopianbank.config.DatabaseConnection;
import com.ethiopianbank.models.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDAO {
    
    // Generate unique reference number
    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Create transaction
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                     "description, reference_number, performed_by, recipient_account) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Generate reference number if not provided
            if (transaction.getReferenceNumber() == null || transaction.getReferenceNumber().isEmpty()) {
                transaction.setReferenceNumber(generateReferenceNumber());
            }
            
            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getReferenceNumber());
            
            if (transaction.getPerformedBy() > 0) {
                stmt.setInt(6, transaction.getPerformedBy());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setString(7, transaction.getRecipientAccount());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create transaction error: " + e.getMessage());
        }
        return false;
    }
    
    // Get transactions by account ID
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number, u.full_name as performed_by_name " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "LEFT JOIN users u ON t.performed_by = u.user_id " +
                     "WHERE t.account_id = ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get transactions by account ID error: " + e.getMessage());
        }
        return transactions;
    }
    
    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number, u.full_name as performed_by_name " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "LEFT JOIN users u ON t.performed_by = u.user_id " +
                     "ORDER BY t.transaction_date DESC " +
                     "LIMIT 1000";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all transactions error: " + e.getMessage());
        }
        return transactions;
    }
    
    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number, u.full_name as performed_by_name " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "LEFT JOIN users u ON t.performed_by = u.user_id " +
                     "WHERE t.transaction_date BETWEEN ? AND ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get transactions by date range error: " + e.getMessage());
        }
        return transactions;
    }
    
    // Get transaction summary
    public BigDecimal getTransactionSummary(String transactionType, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = ? " +
                     "AND transaction_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionType);
            stmt.setTimestamp(2, Timestamp.valueOf(startDate));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Get transaction summary error: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    // Get today's transactions count
    public int getTodaysTransactionCount() {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE DATE(transaction_date) = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Get today's transaction count error: " + e.getMessage());
        }
        return 0;
    }
    
    // Get today's total deposits
    public BigDecimal getTodaysTotalDeposits() {
        String sql = "SELECT SUM(amount) as total FROM transactions " +
                     "WHERE transaction_type = 'DEPOSIT' AND DATE(transaction_date) = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Get today's total deposits error: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    // Helper method
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("account_id"),
            rs.getString("account_number"),
            rs.getString("transaction_type"),
            rs.getBigDecimal("amount"),
            rs.getString("description"),
            rs.getString("reference_number"),
            rs.getTimestamp("transaction_date").toLocalDateTime(),
            rs.getInt("performed_by"),
            rs.getString("performed_by_name"),
            rs.getString("recipient_account")
        );
    }
}