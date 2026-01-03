# Diary Manager - Personal Diary Application

A command-line diary management application built in Java that allows users to write, read, search, and backup diary entries.

## Features

### Core Features:
- **Write Entries**: Create new diary entries with automatic timestamping
- **Read Entries**: Browse and read previous entries with timestamps
- **Search Functionality**: Search for entries containing specific keywords
- **Entry Management**: Each entry saved as a separate text file

### Technical Features:
- Uses `LocalDateTime` and `DateTimeFormatter` for accurate timestamps
- Implements `BufferedReader`/`BufferedWriter` for efficient text operations
- Utilizes `Files` API from `java.nio.file` for file management
- Graceful exception handling for all I/O operations
- Simple and intuitive menu system

### Advanced Features (Bonus):
- **Object Serialization**: Saves application configuration state
- **Backup System**: Creates ZIP archives of all diary entries
- **Auto-backup**: Optional automatic backup after each new entry

## Project Structure

```
DiaryManager/
├── entries/                    # Diary entry files
│   ├── diary_2023_10_25_14_30_45.txt
│   ├── diary_2023_10_26_09_15_22.txt
│   └── ...
├── backups/                   # Backup ZIP files
│   ├── diary_backup_20231025_143045.zip
│   └── ...
├── diary_config.ser          # Serialized configuration
└── src/diarymanager/         # Source code
    ├── DiaryEntry.java       # Entry data model
    ├── DiaryConfig.java      # Configuration class
    ├── DiaryManager.java     # Main application logic
    └── Main.java            # Entry point
```

## How to Use

### Prerequisites:
- Java 8 or higher
- Command line/terminal access

### Compilation:
1. Navigate to the project directory
2. Run the compilation script:
   - Windows: `compile.bat`
   - Linux/Mac: `chmod +x compile.sh && ./compile.sh`

### Running the Application:
```bash
java -cp bin diarymanager.Main
```

### Using the Application:

1. **Write New Entry** (Option 1):
   - Type your diary entry
   - Type `END` on a new line to finish
   - Entry is automatically saved with timestamp

2. **Read Entries** (Option 2):
   - View list of all entries
   - Select an entry by number to read
   - Entries are sorted by date (newest first)

3. **Search Entries** (Option 3):
   - Enter a keyword to search
   - View matching entries
   - Select an entry to read

4. **Backup Entries** (Option 4):
   - Create a ZIP backup of all entries
   - Backup files are timestamped

5. **Settings** (Option 5):
   - Toggle auto-backup feature
   - View file paths

6. **Exit** (Option 6):
   - Safely exit the application
   - Configuration is automatically saved

## Design Choices

### 1. File Naming Convention:
- Entries use format: `diary_YYYY_MM_DD_HH_mm_ss.txt`
- Ensures unique filenames and chronological sorting
- Easy to parse timestamps from filenames

### 2. Separation of Concerns:
- `DiaryEntry`: Pure data model with formatting logic
- `DiaryManager`: Core business logic and file operations
- `DiaryConfig`: Configuration management with serialization
- `Main`: Simple entry point with error handling

### 3. Error Handling:
- All I/O operations include try-catch blocks
- User-friendly error messages
- Application continues running after recoverable errors

### 4. Resource Management:
- Uses try-with-resources for automatic resource closing
- Buffered I/O for better performance
- Proper cleanup in finally blocks

### 5. User Experience:
- Clear menu system with numbered options
- Context-sensitive help and prompts
- Ability to cancel operations with option 0

## Technical Implementation Details

### Timestamp Management:
```java
// For filenames
DateTimeFormatter FILE_FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

// For display
DateTimeFormatter DISPLAY_FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
```

### File Operations:
- Uses `Files.createDirectories()` to ensure directory structure
- Implements `DirectoryStream` for efficient file listing with pattern matching
- Employs `BufferedReader`/`BufferedWriter` for text file operations

### Serialization:
- `DiaryConfig` implements `Serializable` for state persistence
- Configuration saved to `diary_config.ser`
- Automatic loading on application start

### Backup System:
- Creates ZIP files using `ZipOutputStream`
- Each entry file added as a separate `ZipEntry`
- Backup filenames include timestamp for versioning

## Testing

The application has been tested for:
- Creating and reading entries
- Searching with various keywords
- Backup creation and restoration
- Edge cases (empty entries, missing directories)
- Invalid user input handling

## Future Enhancements

Potential improvements:
1. Encryption for diary entries
2. Cloud backup integration
3. Export to PDF functionality
4. Tagging system for entries
5. Graphical user interface (GUI)
6. Mobile app companion

## License

This project is created for educational purposes as part of Chapter 4 Challenge.

## Author

Created as a demonstration of Java I/O capabilities including:
- File operations
- Serialization
- NIO.2 APIs
- Exception handling
- Command-line application design