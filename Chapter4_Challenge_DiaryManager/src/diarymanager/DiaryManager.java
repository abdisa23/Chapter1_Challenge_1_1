package diarymanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Main diary management class with all core functionality
 */
public class DiaryManager {
    private DiaryConfig config;
    private Scanner scanner;
    private static final String CONFIG_FILE = "diary_config.ser";
    
    public DiaryManager() {
        this.scanner = new Scanner(System.in);
        loadConfig();
        ensureDirectories();
    }
    
    /**
     * Load configuration from serialized file or create new
     */
    @SuppressWarnings("unchecked")
    private void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(configPath.toFile()))) {
                config = (DiaryConfig) ois.readObject();
                System.out.println("Configuration loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading config, using defaults: " + e.getMessage());
                config = new DiaryConfig();
            }
        } else {
            config = new DiaryConfig();
            System.out.println("No config found, using default settings.");
        }
    }
    
    /**
     * Save configuration to serialized file
     */
    private void saveConfig() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(config);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
    
    /**
     * Ensure required directories exist
     */
    private void ensureDirectories() {
        try {
            Files.createDirectories(config.getEntriesDirectory());
            Files.createDirectories(config.getBackupDirectory());
            System.out.println("Directories are ready.");
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }
    
    /**
     * Main menu system
     */
    public void showMenu() {
        while (true) {
            System.out.println("\n=== DIARY MANAGER ===");
            System.out.println("1. Write new entry");
            System.out.println("2. Read entries");
            System.out.println("3. Search entries");
            System.out.println("4. Backup entries");
            System.out.println("5. Settings");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1:
                        writeEntry();
                        break;
                    case 2:
                        readEntries();
                        break;
                    case 3:
                        searchEntries();
                        break;
                    case 4:
                        backupEntries();
                        break;
                    case 5:
                        showSettings();
                        break;
                    case 6:
                        saveConfig();
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Write a new diary entry
     */
    private void writeEntry() {
        System.out.println("\n=== WRITE NEW ENTRY ===");
        System.out.println("Enter your diary entry (type 'END' on a new line to finish):");
        
        StringBuilder content = new StringBuilder();
        String line;
        
        while (true) {
            line = scanner.nextLine();
            if (line.equals("END")) {
                break;
            }
            content.append(line).append("\n");
        }
        
        if (content.length() == 0) {
            System.out.println("Entry is empty. Not saved.");
            return;
        }
        
        DiaryEntry entry = new DiaryEntry(content.toString().trim());
        Path filePath = config.getEntriesDirectory().resolve(entry.getFilename());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write("Timestamp: " + entry.getFormattedTimestamp() + "\n\n");
            writer.write(entry.getContent());
            System.out.println("Entry saved as: " + entry.getFilename());
            
            if (config.isAutoBackup()) {
                createBackup();
            }
        } catch (IOException e) {
            System.err.println("Error saving entry: " + e.getMessage());
        }
    }
    
    /**
     * Read existing entries
     */
    private void readEntries() {
        System.out.println("\n=== READ ENTRIES ===");
        List<Path> entries = listEntries();
        
        if (entries.isEmpty()) {
            System.out.println("No diary entries found.");
            return;
        }
        
        // Display all entries
        for (int i = 0; i < entries.size(); i++) {
            String filename = entries.get(i).getFileName().toString();
            String displayName = filename.replace("diary_", "").replace(".txt", "");
            displayName = displayName.replace("_", "-").replace("-", ":", 3);
            System.out.printf("%d. %s%n", i + 1, displayName);
        }
        
        System.out.print("Select entry number to read (0 to return): ");
        
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return;
            int choice = Integer.parseInt(input);
            
            if (choice == 0) {
                return;
            }
            
            if (choice > 0 && choice <= entries.size()) {
                readEntry(entries.get(choice - 1));
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    /**
     * Read a specific entry file
     */
    private void readEntry(Path entryPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(entryPath.toFile()))) {
            System.out.println("\n=== ENTRY ===\n");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("\n=== END ===\n");
        } catch (IOException e) {
            System.err.println("Error reading entry: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Search entries by keyword
     */
    private void searchEntries() {
        System.out.println("\n=== SEARCH ENTRIES ===");
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            System.out.println("No keyword entered.");
            return;
        }
        
        List<Path> entries = listEntries();
        List<Path> matches = new ArrayList<>();
        
        System.out.println("Searching for: '" + keyword + "'");
        
        for (Path entryPath : entries) {
            try (BufferedReader reader = new BufferedReader(new FileReader(entryPath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains(keyword)) {
                        matches.add(entryPath);
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + entryPath.getFileName());
            }
        }
        
        if (matches.isEmpty()) {
            System.out.println("No entries found containing: " + keyword);
        } else {
            System.out.println("Found " + matches.size() + " matching entries:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, matches.get(i).getFileName());
            }
            
            System.out.print("Select entry number to read (0 to return): ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) return;
                int choice = Integer.parseInt(input);
                
                if (choice > 0 && choice <= matches.size()) {
                    readEntry(matches.get(choice - 1));
                } else if (choice != 0) {
                    System.out.println("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Backup entries to ZIP file
     */
    private void backupEntries() {
        System.out.println("\n=== BACKUP ENTRIES ===");
        
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backupPath = config.getBackupDirectory().resolve("diary_backup_" + timestamp + ".zip");
        
        List<Path> entries = listEntries();
        
        if (entries.isEmpty()) {
            System.out.println("No entries to backup.");
            return;
        }
        
        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream(backupPath.toFile()))) {
            
            for (Path entry : entries) {
                ZipEntry zipEntry = new ZipEntry(entry.getFileName().toString());
                zos.putNextEntry(zipEntry);
                
                Files.copy(entry, zos);
                zos.closeEntry();
            }
            
            System.out.println("Backup created successfully: " + backupPath.getFileName());
            System.out.println("Entries backed up: " + entries.size());
            
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }
    
    /**
     * Create a single backup (used for auto-backup)
     */
    private void createBackup() {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backupPath = config.getBackupDirectory().resolve("auto_backup_" + timestamp + ".zip");
        
        List<Path> entries = listEntries();
        
        if (entries.isEmpty()) {
            return;
        }
        
        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream(backupPath.toFile()))) {
            
            for (Path entry : entries) {
                ZipEntry zipEntry = new ZipEntry(entry.getFileName().toString());
                zos.putNextEntry(zipEntry);
                Files.copy(entry, zos);
                zos.closeEntry();
            }
            
            System.out.println("Auto-backup created: " + backupPath.getFileName());
            
        } catch (IOException e) {
            System.err.println("Error creating auto-backup: " + e.getMessage());
        }
    }
    
    /**
     * Show and modify settings
     */
    private void showSettings() {
        while (true) {
            System.out.println("\n=== SETTINGS ===");
            System.out.println("1. Auto-backup: " + (config.isAutoBackup() ? "ON" : "OFF"));
            System.out.println("2. Show current paths");
            System.out.println("3. Return to main menu");
            System.out.print("Choose an option: ");
            
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1:
                        config.setAutoBackup(!config.isAutoBackup());
                        System.out.println("Auto-backup is now: " + 
                            (config.isAutoBackup() ? "ON" : "OFF"));
                        saveConfig();
                        break;
                    case 2:
                        System.out.println("Entries directory: " + 
                            config.getEntriesDirectory().toAbsolutePath());
                        System.out.println("Backup directory: " + 
                            config.getBackupDirectory().toAbsolutePath());
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * List all diary entry files
     */
    private List<Path> listEntries() {
        List<Path> entries = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                config.getEntriesDirectory(), "diary_*.txt")) {
            
            for (Path entry : stream) {
                entries.add(entry);
            }
            
            // Sort by filename (which includes timestamp)
            entries.sort((p1, p2) -> p2.getFileName().toString()
                .compareTo(p1.getFileName().toString()));
            
        } catch (IOException e) {
            System.err.println("Error listing entries: " + e.getMessage());
        }
        
        return entries;
    }
    
    /**
     * Close resources
     */
    public void close() {
        scanner.close();
    }
}