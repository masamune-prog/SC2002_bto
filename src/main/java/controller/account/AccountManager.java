package controller.account;

import controller.account.password.PasswordManager;
import controller.account.user.UserFinder;
import controller.account.user.UserUpdater;
import model.user.*;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.config.Location;
import utils.exception.ModelNotFoundException;
import utils.exception.PasswordIncorrectException;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that manages user accounts and data loading
 */
public class AccountManager {
    public static User login(UserType userType, String userNRIC, String password)
            throws PasswordIncorrectException, ModelNotFoundException {
        //get userID from userNRIC
        User user = UserFinder.findUser(userNRIC, userType);
        System.out.println("User found: " + user.getNRIC() + " " + user.getID());
        // Check if user exists
        if (user == null) {
            throw new ModelNotFoundException("User with NRIC " + userNRIC + " not found");
        }

        //System.err.println("User found: " + user.getNric() + " " + user.getID());
        if (PasswordManager.checkPassword(user, password)) {
            return user;
        } else {
            // Throw the exception instead of returning null
            throw new PasswordIncorrectException();
        }
    }
    //TODO: Add method for registration of new users and updating of user password
    public static void changePassword(UserType userType, String userNRIC, String oldPassword, String newPassword)
            throws PasswordIncorrectException, ModelNotFoundException {
        User user = UserFinder.findUser(userNRIC, userType);
        PasswordManager.changePassword(user, oldPassword, newPassword);
        UserUpdater.updateUser(user);
    }

    /**
     * Loads the applicants from the CSV file
     */
    public static void loadApplicantsFromCSV() {
        ApplicantRepository applicantRepository = ApplicantRepository.getInstance();
        String filePath = applicantRepository.getFilePath();
        System.out.println("Loading applicants from: " + filePath);
        System.out.println("Resource location: " + Location.RESOURCE_LOCATION);

        try {
            applicantRepository.load();
            System.out.println("Applicants loaded successfully. Count: " + applicantRepository.getAll().size());

            // Debug output
            for (Applicant applicant : applicantRepository.getAll()) {
                System.out.println("Loaded applicant: " + applicant.getName() +
                        " (ID: " + applicant.getID() +
                        ", Marital Status: " + applicant.getMaritalStatus() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error loading applicants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the managers from the CSV file
     */
    public static void loadManagersFromCSV() {
        ManagerRepository managerRepository = ManagerRepository.getInstance();
        String filePath = managerRepository.getFilePath();
        System.out.println("Loading managers from: " + filePath);

        try {
            managerRepository.load();
            System.out.println("Managers loaded successfully. Count: " + managerRepository.getAll().size());

            // Debug output
            for (Manager manager : managerRepository.getAll()) {
                System.out.println("Loaded manager: " + manager.getName() + " (ID: " + manager.getID() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error loading managers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the officers from the CSV file
     */
    public static void loadOfficersFromCSV() {
        OfficerRepository officerRepository = OfficerRepository.getInstance();
        String filePath = officerRepository.getFilePath();
        System.out.println("Loading officers from: " + filePath);

        try {
            officerRepository.load();
            System.out.println("Officers loaded successfully. Count: " + officerRepository.getAll().size());

            // Debug output
            for (Officer officer : officerRepository.getAll()) {
                System.out.println("Loaded officer: " + officer.getName() + " (ID: " + officer.getID() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error loading officers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads all users (Applicants, Managers, Officers) from CSV files
     */
    public static void loadAllUsersFromCSV() {
        System.out.println("Loading all users from CSV files...");

        // Load in proper sequence
        loadApplicantsFromCSV();
        loadManagersFromCSV();
        loadOfficersFromCSV();

        System.out.println("All users loaded successfully.");
    }

    /**
     * Checks if all user repositories are empty
     *
     * @return true if all repositories are empty, false otherwise
     */
    public static boolean repositoriesAreEmpty() {
        return ApplicantRepository.getInstance().isEmpty() &&
                ManagerRepository.getInstance().isEmpty() &&
                OfficerRepository.getInstance().isEmpty();
    }
    public static boolean checkUserExists(String userNRIC, UserType userType) throws ModelNotFoundException {
        User user = UserFinder.findUser(userNRIC, userType);
        return user != null;
    }
    public static List<User> getUsersByUserName(String userName) {
        List<User> users = new ArrayList<>();

        // Search in applicants repository
        ApplicantRepository applicantRepo = ApplicantRepository.getInstance();
        for (Applicant applicant : applicantRepo.getAll()) {
            if (applicant.getName() != null && applicant.getName().equals(userName)) {
                users.add(applicant);
            }
        }

        // Search in managers repository
        ManagerRepository managerRepo = ManagerRepository.getInstance();
        for (Manager manager : managerRepo.getAll()) {
            if (manager.getName() != null && manager.getName().equals(userName)) {
                users.add(manager);
            }
        }

        // Search in officers repository
        OfficerRepository officerRepo = OfficerRepository.getInstance();
        for (Officer officer : officerRepo.getAll()) {
            if (officer.getName() != null && officer.getName().equals(userName)) {
                users.add(officer);
            }
        }

        return users;
    }
}