package controller.account.user;

import model.user.*;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

/**
 * The UserUpdater class provides a utility for updating users in the database.
 */
public class UserUpdater {
    /**
     * Updates the specified applicant in the database.
     *
     * @param applicant the applicant to be updated
     * @throws ModelNotFoundException if the applicant is not found in the database
     */
    private static void updateApplicant(Applicant applicant) throws ModelNotFoundException {
        ApplicantRepository.getInstance().update(applicant);
    }

    /**
     * Updates the specified manager in the database.
     *
     * @param manager the manager to be updated
     * @throws ModelNotFoundException if the manager is not found in the database
     */
    private static void updateManager(Manager manager) throws ModelNotFoundException {
        ManagerRepository.getInstance().update(manager);
    }

    /**
     * Updates the specified officer in the database.
     *
     * @param officer the officer to be updated
     * @throws ModelNotFoundException if the officer is not found in the database
     */
    private static void updateOfficer(Officer officer) throws ModelNotFoundException {
        OfficerRepository.getInstance().update(officer);
    }

    /**
     * Updates the specified user in the database.
     *
     * @param user the user to be updated
     * @throws ModelNotFoundException if the user is not found in the database
     */
    public static void updateUser(User user) throws ModelNotFoundException {
        if (user instanceof Applicant applicant) {
            updateApplicant(applicant);
        } else if (user instanceof Manager manager) {
            updateManager(manager);
        } else if (user instanceof Officer officer) {
            updateOfficer(officer);
        }
    }
}