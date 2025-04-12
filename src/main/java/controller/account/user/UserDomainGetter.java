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
        return switch (user) {
            case Applicant applicant -> UserType.APPLICANT;
            case Manager manager -> UserType.MANAGER;
            case Officer officer -> UserType.OFFICER;
            case null, default -> throw new RuntimeException("No such domain");
        };
    }
}