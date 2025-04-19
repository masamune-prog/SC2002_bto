package controller.account;

import controller.account.password.PasswordHashManager;
import controller.account.password.PasswordManager;
import controller.account.user.UserAdder;
import controller.account.user.UserFinder;
import controller.account.user.UserUpdater;
import model.project.RoomType;
import model.user.*;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.config.Location;
import utils.exception.PasswordIncorrectException;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;
import utils.iocontrol.CSVReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts, including login, registration, password changes,
 * and loading user data.
 */
public class AccountManager {
    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param userType The type of the user trying to log in.
     * @param userID   The ID of the user.
     * @param password The password provided by the user.
     * @return The authenticated User object.
     * @throws PasswordIncorrectException If the provided password does not match the stored password.
     * @throws ModelNotFoundException     If no user with the given ID and type exists.
     */
    public static User login(UserType userType, String userID, String password)
            throws PasswordIncorrectException, ModelNotFoundException {
        User user = UserFinder.findUser(userID, userType);
        if (PasswordManager.checkPassword(user, password)) {
            return user;
        } else {
            throw new PasswordIncorrectException();
        }
    }

    /**
     * Changes the password for a specified user.
     *
     * @param userType    The type of the user.
     * @param userID      The ID of the user.
     * @param oldPassword The user's current password.
     * @param newPassword The desired new password.
     * @throws PasswordIncorrectException If the provided old password does not match.
     * @throws ModelNotFoundException     If no user with the given ID and type exists.
     */
    public static void changePassword(UserType userType, String userID, String oldPassword, String newPassword)
            throws PasswordIncorrectException, ModelNotFoundException {
        User user = UserFinder.findUser(userID, userType);
        PasswordManager.changePassword(user, oldPassword, newPassword);
        UserUpdater.updateUser(user);
    }

    /**
     * Retrieves a list of all users (Applicants, Managers, Officers) matching the given NRIC.
     *
     * @param userNRIC The NRIC to search for.
     * @return A list of User objects matching the NRIC. Returns an empty list if no matches are found.
     */
    public static List<User> getUsersByNRIC(String userNRIC) {
        List<Applicant> applicantList = ApplicantRepository.getInstance().findByRules(
                applicant -> applicant.checkNRIC(userNRIC)
        );
        List<Manager> managerList = ManagerRepository.getInstance().findByRules(
                manager -> manager.checkNRIC(userNRIC)
        );
        List<Officer> officerList = OfficerRepository.getInstance().findByRules(
                officer -> officer.checkNRIC(userNRIC)
        );
        List<User> userList = new ArrayList<>();
        userList.addAll(applicantList);
        userList.addAll(managerList);
        userList.addAll(officerList);
        return userList;
    }

    /**
     * Registers a new user in the system with full details including password.
     *
     * @param userType      The type of user to register.
     * @param userNRIC      The NRIC of the new user (used as ID).
     * @param password      The password for the new user.
     * @param name          The name of the new user.
     * @param age           The age of the new user.
     * @param maritalStatus The marital status of the new user.
     * @param projectID     The project ID associated with the user (if applicable).
     * @param roomType      The room type associated with the user (if applicable).
     * @return The newly created User object.
     * @throws ModelAlreadyExistsException If a user with the same NRIC (ID) already exists.
     */
    public static User register(UserType userType, String userNRIC, String password, String name,
                                int age, MaritalStatus maritalStatus, String projectID, RoomType roomType)
            throws ModelAlreadyExistsException {
        User user = UserFactory.create(userType, userNRIC, password, name, age, maritalStatus, projectID, roomType);
        UserAdder.addUser(user);
        return user;
    }

    /**
     * Registers a new user in the system with a default password "password".
     * Use this when the specific password is not immediately required or will be set later.
     *
     * @param userType      The type of user to register.
     * @param userNRIC      The NRIC of the new user (used as ID).
     * @param name          The name of the new user.
     * @param age           The age of the new user.
     * @param maritalStatus The marital status of the new user.
     * @param projectID     The project ID associated with the user (if applicable).
     * @param roomType      The room type associated with the user (if applicable).
     * @return The newly created User object.
     * @throws ModelAlreadyExistsException If a user with the same NRIC (ID) already exists.
     */
    public static User register(UserType userType, String userNRIC, String name,
                                Integer age, MaritalStatus maritalStatus, String projectID, RoomType roomType)
            throws ModelAlreadyExistsException {
        return register(userType, userNRIC, "password", name, age, maritalStatus, projectID, roomType);
    }

    private static String getID(String NRIC) {
        return NRIC;
    }

    private static void loadApplicants() {
        List<List<String>> applicantList = CSVReader.read(Location.RESOURCE_LOCATION + "/ApplicantList.csv", true);
        for (List<String> row : applicantList) {
            String name = row.get(0);
            String applicantNRIC = row.get(1);
            Integer Age = Integer.parseInt(row.get(2));
            MaritalStatus maritalStatus = row.get(3).equals("Single") ? MaritalStatus.SINGLE : MaritalStatus.MARRIED;

            String password = row.get(4);
            try {
                register(UserType.APPLICANT, applicantNRIC, password, name, Age, maritalStatus, "", RoomType.NONE);
            } catch (ModelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadManagers() {
        List<List<String>> managerList = CSVReader.read(Location.RESOURCE_LOCATION + "/ManagerList.csv", true);
        for (List<String> row : managerList) {
            String name = row.get(0);
            String managerNRIC = row.get(1);
            Integer Age = Integer.parseInt(row.get(2));
            MaritalStatus maritalStatus = row.get(3).equals("Single") ? MaritalStatus.SINGLE : MaritalStatus.MARRIED;
            String password = row.get(4);
            try {
                register(UserType.MANAGER, managerNRIC, password, name, Age, maritalStatus, "", RoomType.NONE);
            } catch (ModelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadOfficers() {
        List<List<String>> officerList = CSVReader.read(Location.RESOURCE_LOCATION + "/OfficerList.csv", true);
        for (List<String> row : officerList) {
            String name = row.get(0);
            String applicantNRIC = row.get(1);
            Integer Age = Integer.parseInt(row.get(2));
            MaritalStatus maritalStatus = row.get(3).equals("Single") ? MaritalStatus.SINGLE : MaritalStatus.MARRIED;
            String password = row.get(4);
            try {
                register(UserType.OFFICER, applicantNRIC, password, name, Age, maritalStatus, "", RoomType.NONE);
            } catch (ModelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads initial user data from CSV files for Applicants, Managers, and Officers.
     * This is typically called on the first start of the application.
     */
    public static void loadUsers() {
        loadApplicants();
        loadManagers();
        loadOfficers();
    }

    /**
     * Checks if all user repositories (Applicant, Manager, Officer) are empty.
     *
     * @return true if all repositories are empty, false otherwise.
     */
    public static boolean repositoryIsEmpty() {
        return ApplicantRepository.getInstance().isEmpty() &&
                ManagerRepository.getInstance().isEmpty() &&
                OfficerRepository.getInstance().isEmpty();
    }

    /**
     * Retrieves a specific user by their type and ID.
     *
     * @param userType The type of the user to retrieve.
     * @param ID       The ID of the user to retrieve.
     * @return The User object matching the type and ID.
     * @throws ModelNotFoundException If no user with the specified type and ID is found.
     */
    public static User getByDomainAndID(UserType userType, String ID) throws ModelNotFoundException {
        return switch (userType) {
            case APPLICANT -> ApplicantRepository.getInstance().getByID(ID);
            case MANAGER -> ManagerRepository.getInstance().getByID(ID);
            case OFFICER -> OfficerRepository.getInstance().getByID(ID);
        };
    }
}
