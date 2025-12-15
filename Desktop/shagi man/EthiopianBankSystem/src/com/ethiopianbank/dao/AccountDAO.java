package com.ethiopianbank.dao;

import com.ethiopianbank.config.DatabaseConnection;
import com.ethiopianbank.models.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    
    // Generate unique account number
    private String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType) {
            case "SAVINGS": prefix = "SV"; break;
            case "CHECKING": prefix = "CH"; break;
            case "BUSINESS": prefix = "BS"; break;
            case "FIXED_DEPOSIT": prefix = "FD"; break;
            default: prefix = "AC";
        }
        
        // Generate timestamp-based number
        long timestamp = System.currentTimeMillis() % 1000000000L;
        return prefix + String.format("%010d", timestamp);
    }
    
    // Create account
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, " +
                     "balance, currency, interest_rate, opened_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Generate account number if not provided
            if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
                account.setAccountNumber(generateAccountNumber(account.getAccountType()));
            }
            
            stmt.setString(1, account.getAccountNumber());
            stmt.setInt(2, account.getCustomerId());
            stmt.setString(3, account.getAccountType());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setString(5, account.getCurrency());
            stmt.setBigDecimal(6, account.getInterestRate());
            stmt.setDate(7, Date.valueOf(account.getOpenedDate()));
            stmt.setString(8, account.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setAccountId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create account error: " + e.getMessage());
        }
        return false;
    }
    
    // Get all accounts with customer name
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "ORDER BY a.account_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all accounts error: " + e.getMessage());
        }
        return accounts;
    }
    
    // Get accounts by customer ID
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.customer_id = ? " +
                     "ORDER BY a.opened_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get accounts by customer ID error: " + e.getMessage());
        }
        return accounts;
    }
    
    // Get account by account number
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT a.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get account by number error: " + e.getMessage());
        }
        return null;
    }
    
    // Get account by account ID
    public Account getAccountById(int accountId) {
        String sql = "SELECT a.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.account_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get account by ID error: " + e.getMessage());
        }
        return null;
    }
    
    // Update account balance
    public boolean updateAccountBalance(int accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update account balance error: " + e.getMessage());
            return false;
        }
    }
    
    // Update account status
    public boolean updateAccountStatus(int accountId, String status) {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, accountId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update account status error: " + e.getMessage());
            return false;
        }
    }
    
    // Search accounts
    public List<Account> searchAccounts(String searchTerm) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.account_number LIKE ? OR " +
                     "c.first_name LIKE ? OR " +
                     "c.last_name LIKE ? OR " +
                     "c.phone LIKE ? " +
                     "ORDER BY a.account_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search accounts error: " + e.getMessage());
        }
        return accounts;
    }
    
    // Get total balance sum
    public BigDecimal getTotalBalance() {
        String sql = "SELECT SUM(balance) as total FROM accounts WHERE status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Get total balance error: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    // Get account count by type
    public int getAccountCountByType(String accountType) {
        String sql = "SELECT COUNT(*) as count FROM accounts WHERE account_type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Get account count by type error: " + e.getMessage());
        }
        return 0;
    }
    
    // Helper method
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("account_id"),
            rs.getString("account_number"),
            rs.getInt("customer_id"),
            rs.getString("customer_name"),
            rs.getString("account_type"),
            rs.getBigDecimal("balance"),
            rs.getString("currency"),
            rs.getBigDecimal("interest_rate"),
            rs.getDate("opened_date").toLocalDate(),
            rs.getString("status")
        );
    }
}