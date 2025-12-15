package com.ethiopianbank.dao;

import com.ethiopianbank.config.DatabaseConnection;
import com.ethiopianbank.models.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    // Create customer
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, middle_name, email, phone, " +
                     "address, date_of_birth, gender, id_number, id_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getMiddleName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getAddress());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(7, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            
            stmt.setString(8, customer.getGender());
            stmt.setString(9, customer.getIdNumber());
            stmt.setString(10, customer.getIdType());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create customer error: " + e.getMessage());
        }
        return false;
    }
    
    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all customers error: " + e.getMessage());
        }
        return customers;
    }
    
    // Get customer by ID
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get customer by ID error: " + e.getMessage());
        }
        return null;
    }
    
    // Search customers
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE " +
                     "first_name LIKE ? OR " +
                     "last_name LIKE ? OR " +
                     "phone LIKE ? OR " +
                     "email LIKE ? OR " +
                     "id_number LIKE ? " +
                     "ORDER BY customer_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, likeTerm);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search customers error: " + e.getMessage());
        }
        return customers;
    }
    
    // Update customer
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, middle_name = ?, " +
                     "email = ?, phone = ?, address = ?, date_of_birth = ?, gender = ?, " +
                     "id_number = ?, id_type = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getMiddleName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getAddress());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(7, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            
            stmt.setString(8, customer.getGender());
            stmt.setString(9, customer.getIdNumber());
            stmt.setString(10, customer.getIdType());
            stmt.setInt(11, customer.getCustomerId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update customer error: " + e.getMessage());
            return false;
        }
    }
    
    // Delete customer
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete customer error: " + e.getMessage());
            return false;
        }
    }
    
    // Get total customer count
    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) as total FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total customers error: " + e.getMessage());
        }
        return 0;
    }
    
    // Helper method
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Date dob = rs.getDate("date_of_birth");
        LocalDate dateOfBirth = null;
        if (dob != null) {
            dateOfBirth = dob.toLocalDate();
        }
        
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("middle_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            dateOfBirth,
            rs.getString("gender"),
            rs.getString("id_number"),
            rs.getString("id_type"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}