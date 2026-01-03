package diarymanager;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for Diary Manager
 */
public class DiaryConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String entriesDirectory;
    private String backupDirectory;
    private boolean autoBackup;
    
    public DiaryConfig() {
        this.entriesDirectory = "entries";
        this.backupDirectory = "backups";
        this.autoBackup = false;
    }
    
    public Path getEntriesDirectory() {
        return Paths.get(entriesDirectory);
    }
    
    public void setEntriesDirectory(Path entriesDirectory) {
        this.entriesDirectory = entriesDirectory.toString();
    }
    
    public Path getBackupDirectory() {
        return Paths.get(backupDirectory);
    }
    
    public void setBackupDirectory(Path backupDirectory) {
        this.backupDirectory = backupDirectory.toString();
    }
    
    public boolean isAutoBackup() {
        return autoBackup;
    }
    
    public void setAutoBackup(boolean autoBackup) {
        this.autoBackup = autoBackup;
    }
}