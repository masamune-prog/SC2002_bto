package boundary.account;

import controller.account.AccountManager;
import model.user.User;
import utils.exception.PageBackException;
import utils.ui.ChangePage;

import java.io.IOException;
import java.util.List;

import static controller.account.user.UserDomainGetter.getUserDomain;

/**
 * This class provides a user interface (UI) for retrieving the user domains associated with a given NRIC
 * in case the user forgot which domains they have access to.
 */
public class ForgetDomain {
    /**
     * Displays a list of user domains associated with the given NRIC.
     *
     * @throws PageBackException if the user chooses to go back to the previous page.
     */
    public static void forgotUserDomain() throws PageBackException {
        ChangePage.changePage();
        String nric = AttributeGetter.getUserNRIC();
        List<User> users = AccountManager.getUsersByNRIC(nric);

        if (users.isEmpty()) {
            System.out.println("No user found with NRIC " + nric + ".");
        } else {
            System.out.println("Found " + users.size() + " user(s) with NRIC " + nric + ".");
            System.out.println("The list of user domains associated with NRIC " + nric + " is:");
            System.out.println("┌--------------------------------------┐");
            System.out.println("| NRIC             | User Domain       |");
            System.out.println("|--------------------------------------|");
            for (User user : users) {
                System.out.printf("| %-17s| %-18s|\n", nric, getUserDomain(user));
            }
            System.out.println("└--------------------------------------┘");
        }

        System.out.println("Press Enter key to go back.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PageBackException();
    }
}
