public class Chapter1_Challenge_1_2 {
    public static void main(String[] args) {
        String[] winningNumbers = {"12-34-56-78-90", "33-44-11-66-22", "01-02-03-04-05"};
        
        double highestAverage = -1;
        String bestNumber = "";
        
        // Using for-each loop to analyze each ticket
        for (String ticket : winningNumbers) {
            System.out.println("Analyzing: " + ticket);
            
            // Remove dashes
            String cleanNumber = ticket.replace("-", "");
            
            // Convert to char array and then to int array
            char[] charDigits = cleanNumber.toCharArray();
            int[] digits = new int[charDigits.length];
            
            int sum = 0;
            
            // Using for loop to process each digit
            for (int i = 0; i < charDigits.length; i++) {
                digits[i] = Character.getNumericValue(charDigits[i]);
                sum += digits[i];
            }
            
            double average = (double) sum / digits.length;
            System.out.println("Digit Sum: " + sum + ", Digit Average: " + average);
            
            // Check if this is the highest average so far
            if (average > highestAverage) {
                highestAverage = average;
                bestNumber = ticket;
            }
            System.out.println(); // Empty line for readability
        }
        
        System.out.println("The winning number with the highest average is: " + 
                          bestNumber + " with an average of " + highestAverage);
    }
}