package com.ethiopianbank.utils;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 8) {
            return accountNumber;
        }
        return accountNumber.substring(0, 4) + "****" + accountNumber.substring(accountNumber.length() - 4);
    }
    
    public static String generateOTP() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
}