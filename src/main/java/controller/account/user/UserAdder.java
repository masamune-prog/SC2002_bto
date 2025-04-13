package controller.account.user;

import model.user.Applicant;
import model.user.Manager;
import model.user.Officer;
import model.user.User;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
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
        } else if (user instanceof Manager manager) {
            addManager(manager);
        } else if (user instanceof Officer officer) {
            addOfficer(officer);
        }
    }

    /**
     * Adds the specified applicant to the database.
     *
     * @param applicant the applicant to be added
     * @throws ModelAlreadyExistsException if the applicant already exists in the database
     */
    private static void addApplicant(Applicant applicant) throws ModelAlreadyExistsException {
        ApplicantRepository.getInstance().add(applicant);
    }

    /**
     * Adds the specified manager to the database.
     *
     * @param manager the manager to be added
     * @throws ModelAlreadyExistsException if the manager already exists in the database
     */
    private static void addManager(Manager manager) throws ModelAlreadyExistsException {
        ManagerRepository.getInstance().add(manager);
    }

    /**
     * Adds the specified officer to the database.
     *
     * @param officer the officer to be added
     * @throws ModelAlreadyExistsException if the officer already exists in the database
     */
    private static void addOfficer(Officer officer) throws ModelAlreadyExistsException {
        OfficerRepository.getInstance().add(officer);
    }
}