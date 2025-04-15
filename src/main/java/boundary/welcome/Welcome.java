package boundary.welcome;


import boundary.account.ForgetUserID;
import boundary.account.LoginUI;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.ChangePage;

/**
 * This class provides a UI for the user to enter the system.
 */
public class Welcome {
    /**
     * Displays a welcome page.
     */
    public static void welcome() {
        ChangePage.changePage();
        System.out.println(BoundaryStrings.WELCOME_LOGO);
        System.out.println("Welcome to the BTO Management System!\n");
        System.out.println(BoundaryStrings.separator);
        System.out.println("Please enter your choice to continue.");
        System.out.println("\t1. Login");
        System.out.println("\t2. Forget UserID");
        System.out.println("\t3. Exit");
        System.out.print("Your choice (1-3): ");
        try {
            while (true) {
                int choice = IntGetter.readInt();
                switch (choice) {
                    case 1 -> LoginUI.login();
                    case 2 -> ForgetUserID.forgotUserID();
                    case 3 -> ExitPage.exitPage();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (PageBackException e) {
            welcome();
        } catch (ModelNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}