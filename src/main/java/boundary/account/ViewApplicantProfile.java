package boundary.account;

import boundary.mainpage.ApplicantMainPage;
import model.user.Applicant;
import model.user.User;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.UserTypeGetter;

import java.util.Scanner;

/**
 * This class provides a UI for displaying an applicant's profile.
 */
public class ViewApplicantProfile {

    /**
     * Displays the applicant's profile in a table format.
     *
     * @param user the user whose profile will be displayed
     */
    public static void viewApplicantProfile(Applicant user) {
        String userType = UserTypeGetter.getUserTypeInCamelCase(user);
        System.out.println("Welcome to View " + userType + " Profile");
        System.out.println("┌--------------------------------------------------------------------┐");
        System.out.printf("| %-15s | %-30s | %-15s |\n", "Name", "Status", userType + " ID");
        System.out.println("|-----------------|--------------------------------|-----------------|");
        System.out.printf("| %-15s | %-30s | %-15s |\n", user.getName(), user.getStatus(), user.getID());
        System.out.println("└--------------------------------------------------------------------┘");
        System.out.println("Enter any other key to continue.");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        //we load ApplicantMainPage here
        ApplicantMainPage.applicantMainPage(user);

    }




}