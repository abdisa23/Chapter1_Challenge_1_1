package diarymanager;

/**
 * Main class to run the Diary Manager application
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Diary Manager!");
        System.out.println("=========================");
        
        DiaryManager manager = null;
        try {
            manager = new DiaryManager();
            manager.showMenu();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
    }
}