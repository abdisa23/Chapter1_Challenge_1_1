package com.ethiopianbank.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Phone validation pattern (Ethiopian format)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+251|0)[79]\\d{8}$");
    
    // Account number validation
    private static final Pattern ACCOUNT_NUMBER_PATTERN = 
        Pattern.compile("^[A-Z]{2}\\d{10}$");
    
    // ID number validation (alphanumeric, 6-20 chars)
    private static final Pattern ID_NUMBER_PATTERN = 
        Pattern.compile("^[A-Za-z0-9]{6,20}$");
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email can be optional
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }
    
    public static boolean isValidIdNumber(String idNumber) {
        return idNumber != null && ID_NUMBER_PATTERN.matcher(idNumber).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2;
    }
    
    public static boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isDateOfBirthValid(LocalDate date) {
        if (date == null) {
            return true; // Optional field
        }
        LocalDate minDate = LocalDate.now().minusYears(120);
        LocalDate maxDate = LocalDate.now().minusYears(16);
        return date.isAfter(minDate) && date.isBefore(maxDate);
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }
    
    public static String validateCustomerData(String firstName, String lastName, String phone, 
                                            String email, LocalDate dob, String idNumber) {
        if (!isValidName(firstName)) {
            return "First name must be at least 2 characters";
        }
        if (!isValidName(lastName)) {
            return "Last name must be at least 2 characters";
        }
        if (!isValidPhone(phone)) {
            return "Invalid phone number format. Use +251 or 0 followed by 9 digits";
        }
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            return "Invalid email format";
        }
        if (dob != null && !isDateOfBirthValid(dob)) {
            return "Customer must be at least 16 years old";
        }
        if (idNumber != null && !idNumber.trim().isEmpty() && !isValidIdNumber(idNumber)) {
            return "Invalid ID number format";
        }
        return null; // No errors
    }
}