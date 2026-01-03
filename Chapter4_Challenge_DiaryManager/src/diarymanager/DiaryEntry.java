package diarymanager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single diary entry with timestamp and content
 */
public class DiaryEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime timestamp;
    private String content;
    private String filename;
    
    // Formatter for filename
    private static final DateTimeFormatter FILE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    
    // Formatter for display
    private static final DateTimeFormatter DISPLAY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public DiaryEntry(String content) {
        this.timestamp = LocalDateTime.now();
        this.content = content;
        this.filename = "diary_" + timestamp.format(FILE_FORMATTER) + ".txt";
    }
    
    public DiaryEntry(LocalDateTime timestamp, String content, String filename) {
        this.timestamp = timestamp;
        this.content = content;
        this.filename = filename;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getFormattedTimestamp() {
        return timestamp.format(DISPLAY_FORMATTER);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", getFormattedTimestamp(), 
                           content.substring(0, Math.min(content.length(), 50)) + "...");
    }
}