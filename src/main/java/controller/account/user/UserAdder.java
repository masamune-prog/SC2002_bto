package controller.account.user;

import model.user.*;
import model.user.User;
import repository.user.*;
import utils.exception.ModelAlreadyExistsException;

/**
 * The UserAdder class provides a utility for adding users to the database.
 */
public class UserAdder {
    /**
     * Adds the specified user to the database.
     *
     * @param user the user to be added
     * @throws ModelAlreadyExistsException if the user already exists in the database
     */
    public static void addUser(User user) throws ModelAlreadyExistsException {
        if (user instanceof Applicant applicant) {
            addApplicant(applicant);
        } else if (user instanceof Officer officer) {
            addOfficer(officer);
        } else if (user instanceof Manager manager) {
            addManager(manager);
        }
    }


    private static void addManager(Manager manager) throws ModelAlreadyExistsException {
        ManagerRepository.getInstance().add(manager);
    }
    /**
     * Adds the specified officer to the database.
     * @param officer the officer to be added
     * @throws ModelAlreadyExistsException if the Officer already exists in the database
     */
    private static void addOfficer(Officer officer) throws ModelAlreadyExistsException {
        OfficerRepository.getInstance().add(officer);
    }


    private static void addApplicant(Applicant applicant) throws ModelAlreadyExistsException {
        ApplicantRepository.getInstance().add(applicant);
    }
}
