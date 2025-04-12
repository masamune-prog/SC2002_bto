package controller.account.user;

import model.user.User;
import model.user.UserType;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

/**
 * A class that provides a utility for finding users in the database.
 */
public class UserFinder {
    /**
     * Finds the applicant with the specified ID.
     *
     * @param userID the ID of the applicant to be found
     * @return the applicant with the specified ID
     * @throws ModelNotFoundException if the applicant is not found
     */
    private static User findApplicant(String userID) throws ModelNotFoundException {
        return ApplicantRepository.getInstance().getByID(userID);
    }

    /**
     * Finds the officer with the specified ID.
     *
     * @param userID the ID of the officer to be found
     * @return the officer with the specified ID
     * @throws ModelNotFoundException if the officer is not found
     */
    private static User findOfficer(String userID) throws ModelNotFoundException {
        return OfficerRepository.getInstance().getByID(userID);
    }

    /**
     * Finds the manager with the specified ID.
     *
     * @param userID the ID of the manager to be found
     * @return the manager with the specified ID
     * @throws ModelNotFoundException if the manager is not found
     */
    private static User findManager(String userID) throws ModelNotFoundException {
        return ManagerRepository.getInstance().getByID(userID);
    }

    /**
     * Finds the user with the specified ID.
     *
     * @param userID   the ID of the user to be found
     * @param userType the type of the user to be found
     * @return the user with the specified ID
     * @throws ModelNotFoundException if the user is not found
     */
    public static User findUser(String userID, UserType userType) throws ModelNotFoundException {
        return switch (userType) {
            case APPLICANT -> findApplicant(userID);
            case OFFICER -> findOfficer(userID);
            case MANAGER -> findManager(userID);
        };
    }
}