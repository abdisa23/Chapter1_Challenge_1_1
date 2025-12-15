package com.ethiopianbank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnection {
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(DatabaseConfig.DRIVER);
                connection = DriverManager.getConnection(
                    DatabaseConfig.URL, 
                    DatabaseConfig.USERNAME, 
                    DatabaseConfig.PASSWORD
                );
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Database test query executed successfully.");
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Test query failed: " + e.getMessage());
            }
        }
    }
}