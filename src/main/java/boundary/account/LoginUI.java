package boundary.account;

import boundary.mainpage.*;
import controller.account.AccountManager;
import model.user.Applicant;
import model.user.User;
import model.user.UserType;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.exception.PasswordIncorrectException;
import utils.ui.ChangePage;
import boundary.account.ForgetDomain;
import java.util.Scanner;

/**
 * This class provides a user interface (UI) for the user to login.
 */
public class LoginUI {
    /**
     * Displays a login page.
     *
     * @throws PageBackException if the user chooses to go back to the previous page.
     */
    public static void login() throws PageBackException {
        ChangePage.changePage();
        UserType domain = AttributeGetter.getDomain();
        String userNRIC = AttributeGetter.getUserNRIC();
        if (userNRIC.equals("")) {
            try {
                ForgetDomain.forgotUserDomain();
            } catch (PageBackException e) {
                login();
            }
        }
        String password = AttributeGetter.getPassword();
//        System.err.println("Logging in...");
//        System.err.println("Domain: " + domain);
//        System.err.println("User ID: " + userID);
//        System.err.println("Password: " + password);
//        new Scanner(System.in).nextLine();
        try {
            User user = AccountManager.login(domain, userNRIC, password);
            switch (domain) {
                case APPLICANT -> ApplicantMainPage.applicantMainPage(user);
                case OFFICER -> OfficerMainPage.officerMainPage(user);
                case MANAGER -> ManagerMainPage.managerMainPage(user);
                default -> throw new IllegalStateException("Unexpected value: " + domain);
            }
            return;
        } catch (PasswordIncorrectException e) {
            System.out.println("Password incorrect.");
        } catch (ModelNotFoundException e) {
            System.out.println("User not found.");
        }
        System.out.println("Enter [b] to go back, or any other key to try again.");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if (choice.equals("b")) {
            throw new PageBackException();
        } else {
            System.out.println("Please try again.");
            login();
        }
    }
}
