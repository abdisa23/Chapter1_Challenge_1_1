/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter1_challenge_1_;
import java.util.Scanner;

public class Chapter1_Challenge_1_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Take input
        System.out.print("Enter a positive integer: ");
        int number = scanner.nextInt();
        
        // Extract last digit using modulus
        int lastDigit = number % 10;
        
        // Calculate number of digits using log10
        int numDigits = (int) Math.log10(number) + 1;
        
        // Extract first digit using division
        int firstDigit = (int) (number / Math.pow(10, numDigits - 1));
        
        // Calculate product of first and last digit
        int product = firstDigit * lastDigit;
        
        // Extract second digit
        int secondDigit = (number / (int) Math.pow(10, numDigits - 2)) % 10;
        
        // Extract second-last digit
        int secondLastDigit = (number / 10) % 10;
        
        // Calculate sum of second and second-last digit
        int sum = secondDigit + secondLastDigit;
        
        // Create final code by concatenating product and sum
        String finalCode = String.valueOf(product) + String.valueOf(sum);
        
        System.out.println("The decrypted code is: " + finalCode);
        
        scanner.close();
    }
}