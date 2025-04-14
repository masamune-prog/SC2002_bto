package utils.iocontrol;

import java.util.Scanner;

/**

 The IntGetter class provides a static method for reading integer input from the user through the console.
 */
public class IntGetter {
    /**
     * Reads an integer value from the console input.
     *
     * @return the integer value read from the console input.
     */
    public static int readInt() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Please enter a valid integer.");
                scanner.nextLine(); // Clear the scanner buffer
            }
        }
    }
}
