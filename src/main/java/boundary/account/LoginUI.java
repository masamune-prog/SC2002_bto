package boundary.account;

import boundary.mainpage.ApplicantMainPage;
import boundary.mainpage.ManagerMainPage;
import boundary.mainpage.OfficerMainPage;
import controller.account.AccountManager;
import model.user.User;
import model.user.UserType;
import utils.exception.PasswordIncorrectException;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;

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
    public static void login() throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        UserType userType = AttributeGetter.getDomain();
        String userNRIC = AttributeGetter.getUserNRIC();

        if (userNRIC.isEmpty()) {
            try {
                ForgetUserID.forgotUserID();
            } catch (PageBackException e) {
                login();
            }
        }

        String password = AttributeGetter.getPassword();
        //System.out.println(userType);
        //System.out.println(userNRIC);
        //System.out.println(password);
        boolean accountExist = AccountManager.checkUserExists(userNRIC, userType);
        //check if the userNRIC is valid
        //if not, throw ModelNotFoundException
        if (!accountExist) {
            System.out.println("Account does not exist.");
            System.out.println("Please try again.");
            login();
        }
        try {
            //check if the userNRIC is valid
            //if not, throw ModelNotFoundException

            User user = AccountManager.login(userType, userNRIC, password);
            //print out the user passed into AccountManager
            switch (userType) {
                case APPLICANT -> ApplicantMainPage.applicantMainPage(user);
                //TODO Create the other 2 frontend
                case MANAGER -> ManagerMainPage.managerMainPage(user);
                //case OFFICER -> OfficerMainPage.officerMainPage(user);
                default -> throw new IllegalStateException("Unexpected user type: " + userType);
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