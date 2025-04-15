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
     * @param userNRIC nric of the applicant to be found
     * @return the applicant with the specified ID
     * @throws ModelNotFoundException if the applicant is not found
     */
    private static User findApplicant(String userNRIC) throws ModelNotFoundException {
        return ApplicantRepository.getInstance().getByNRIC(userNRIC);
    }
    private static User findApplicantByID(String ID) throws ModelNotFoundException {
        return ApplicantRepository.getInstance().getByID(ID);
    }

    /**
     * Finds the officer with the specified ID.
     *
     * @param userID the ID of the officer to be found
     * @return the officer with the specified ID
     * @throws ModelNotFoundException if the officer is not found
     */
    private static User findOfficer(String userNRIC) throws ModelNotFoundException {
        return OfficerRepository.getInstance().getByNRIC(userNRIC);
    }
    private static User findOfficerByID(String ID) throws ModelNotFoundException {
        return OfficerRepository.getInstance().getByID(ID);
    }

    /**
     * Finds the manager with the specified ID.
     *
     * @param userID the ID of the manager to be found
     * @return the manager with the specified ID
     * @throws ModelNotFoundException if the manager is not found
     */
    private static User findManager(String userNRIC) throws ModelNotFoundException {
        return ManagerRepository.getInstance().getByNRIC(userNRIC);
    }
    private static User findManagerByID(String ID) throws ModelNotFoundException {
        return ManagerRepository.getInstance().getByID(ID);
    }
    /**
     * Finds the user with the specified ID.
     *
     * @param userID   the ID of the user to be found
     * @param userType the type of the user to be found
     * @return the user with the specified ID
     * @throws ModelNotFoundException if the user is not found
     */
    public static User findUser(String userNRIC, UserType userType) throws ModelNotFoundException {
        return switch (userType) {
            case APPLICANT -> findApplicant(userNRIC);
            case OFFICER -> findOfficer(userNRIC);
            case MANAGER -> findManager(userNRIC);
        };
    }
    public static User findUserByID(String userID, UserType userType) throws ModelNotFoundException {
        return switch (userType) {
            case APPLICANT -> findApplicantByID(userID);
            case OFFICER -> findOfficerByID(userID);
            case MANAGER -> findManagerByID(userID);
        };
    }
}