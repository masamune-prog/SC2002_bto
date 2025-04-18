package boundary.account;

import model.user.UserType;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.PasswordReader;

import java.util.Scanner;

/**
 * This class is used to interact with the user and retrieve various attributes such as domain, password, UserID, and username.
 */
public class AttributeGetter {
    /**
     * Prompts the user to select their domain and returns their chosen UserType.
     *
     * @return The UserType chosen by the user.
     */
    public static UserType getDomain() {
        System.out.println(BoundaryStrings.separator);
        System.out.println("\t1. Applicant");
        System.out.println("\t2. Officer");
        System.out.println("\t3. Manager");
        System.out.println(BoundaryStrings.separator);
        System.out.print("Please select your domain (1-3): ");
        UserType userType = null;
        while (userType == null) {
            Scanner scanner = new Scanner(System.in);
            int domain;
            try {
                domain = IntGetter.readInt();
            } catch (Exception e) {
                System.out.println("Please enter a number.");
                continue;
            }
            userType = switch (domain) {
                case 1 -> UserType.APPLICANT;
                case 2 -> UserType.OFFICER;
                case 3 -> UserType.MANAGER;
                default -> null;
            };
            if (userType == null) {
                System.out.println("Invalid domain. Please try again.");
            }
        }
        return userType;
    }

    /**
     * Prompts the user to enter their password securely without displaying it on the screen.
     *
     * @return The password entered by the user.
     */
    public static String getPassword() {
        System.out.print("Please enter your password: ");
        return PasswordReader.getPassword();
    }



    public static String getUserNRIC() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your NRIC: ");
        return scanner.nextLine();
    }
}