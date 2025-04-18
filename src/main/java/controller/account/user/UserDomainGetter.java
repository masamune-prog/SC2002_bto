package controller.account.user;

import model.user.*;

/**
 * A class that provides a utility for getting the domain of a user.
 */
public class UserDomainGetter {
    /**
     * Gets the domain of the specified user.
     *
     * @param user the user whose domain is to be retrieved
     * @return the domain of the user
     */
    public static UserType getUserDomain(User user) {
        if (user instanceof Applicant) {
            return UserType.APPLICANT;
        } else if (user instanceof Officer) {
            return UserType.OFFICER;
        } else if (user instanceof Manager) {
            return UserType.MANAGER;
        } else {
            throw new RuntimeException("No such domain");
        }
    }
}
