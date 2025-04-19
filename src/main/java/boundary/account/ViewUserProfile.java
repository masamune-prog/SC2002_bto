package boundary.account;

import model.user.User;
import model.user.Applicant;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.UserTypeGetter;

import java.util.Scanner;

/**
 * This class provides a UI for the user to view his/her profile.
 */
public class ViewUserProfile {
    /**
     * Displays the user's profile. For Applicants, shows detailed applicant fields.
     *
     * @param user the user whose profile is to be displayed.
     */
    public static void viewUserProfile(User user) {
        String userType = UserTypeGetter.getUserTypeInCamelCase(user);
        System.out.println("Welcome to View " + userType + " Profile");

        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            System.out.println("┌─────────────────┬──────────────────────┬─────┬─────────────────┬──────────────────────┬────────────────┬────────────┐");
            System.out.printf("| %-15s | %-20s | %-3s | %-15s | %-20s | %-15s | %-10s |%n",
                    "Applicant NRIC", "Name", "Age", "Marital Status", "Project ID", "Status", "RoomType");
            System.out.println("├─────────────────┼──────────────────────┼─────┼─────────────────┼──────────────────────┼────────────────┼────────────┤");
            System.out.printf("| %-15s | %-20s | %-3d | %-15s | %-20s | %-15s | %-10s |%n",
                    applicant.getNRIC(),
                    applicant.getName(),
                    applicant.getAge(),
                    applicant.getMaritalStatus().name(),
                    applicant.getProjectID(),
                    applicant.getApplicantStatus().name(),
                    applicant.getRoomType().name());
            System.out.println("└─────────────────┴──────────────────────┴─────┴─────────────────┴──────────────────────┴────────────────┴────────────┘");
        } else {
            // Generic user view: only Name and NRIC
            System.out.println("┌─────────────────┬──────────────────────┐");
            System.out.printf("| %-15s | %-20s |%n", "Name", "NRIC");
            System.out.println("├─────────────────┼──────────────────────┤");
            System.out.printf("| %-15s | %-20s |%n",
                    user.getName(),
                    user.getNRIC());
            System.out.println("└─────────────────┴──────────────────────┘");
        }
    }

    /**
     * Displays the user's profile page and waits for user to go back.
     *
     * @param user the user whose profile is to be displayed.
     * @throws PageBackException if the user chooses to go back.
     */
    public static void viewUserProfilePage(User user) throws PageBackException {
        ChangePage.changePage();
        viewUserProfile(user);
        System.out.println("Press enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }
}
