import java.util.Random;
import java.util.Scanner;

public class Chapter1_Challenge_1_3 {
    public static void main(String[] args) {
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);
        
        int health = 100;
        boolean defeated = false;
        
        for (int room = 1; room <= 5; room++) {
            System.out.println("Entering room " + room + "...");
            
            int event = rand.nextInt(3) + 1; // Random number 1-3
            
            switch (event) {
                case 1: // Trap
                    health -= 20;
                    System.out.println("A trap sprung! Health is now " + health);
                    break;
                    
                case 2: // Healing potion
                    health += 15;
                    if (health > 100) {
                        health = 100;
                        System.out.println("You found a healing potion! Health is now 100 (capped).");
                    } else {
                        System.out.println("You found a healing potion! Health is now " + health);
                    }
                    break;
                    
                case 3: // Monster
                    System.out.println("A monster appears!");
                    int monsterNumber = rand.nextInt(5) + 1;
                    int guess;
                    
                    do {
                        System.out.print("Guess a number (1-5) to defeat it: ");
                        guess = scanner.nextInt();
                        
                        if (guess != monsterNumber) {
                            System.out.println("Wrong! Try again.");
                        }
                    } while (guess != monsterNumber);
                    
                    System.out.println("You defeated the monster!");
                    break;
            }
            
            // Check if player is defeated
            if (health <= 0) {
                System.out.println("You have been defeated in room " + room + ".");
                defeated = true;
                break;
            }
        }
        
        if (!defeated) {
            System.out.println("You cleared the dungeon! Victorious with " + health + " health!");
        }
        
        scanner.close();
    }
}