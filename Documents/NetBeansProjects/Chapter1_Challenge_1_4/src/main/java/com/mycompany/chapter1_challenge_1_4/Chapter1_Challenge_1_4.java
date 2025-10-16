/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chapter1_challenge_1_4;

import java.io.*;
import java.util.Scanner;

public class Chapter1_Challenge_1_4 {
    public static void main(String[] args) {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader("config.txt"));
            
            // Read first line (config version)
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Config file is empty!");
            }
            
            // Parse config version
            int configVersion = Integer.parseInt(firstLine);
            
            // Check config version
            if (configVersion < 2) {
                throw new Exception("Config version too old!");
            }
            
            // Read second line (file path)
            String filePath = reader.readLine();
            if (filePath == null) {
                throw new IOException("Missing file path in config!");
            }
            
            // Check if file exists
            File configFile = new File(filePath);
            if (!configFile.exists()) {
                throw new IOException("File at path '" + filePath + "' does not exist!");
            }
            
            System.out.println("Config successfully loaded!");
            System.out.println("Version: " + configVersion);
            System.out.println("File path: " + filePath);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: Config file 'config.txt' not found!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format in config file!");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // This always executes
            System.out.println("Config read attempt finished.");
            
            // Close reader if it was opened
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Warning: Could not close file reader.");
                }
            }
        }
    }
}