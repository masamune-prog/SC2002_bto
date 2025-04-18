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
 * A class that manages the account of a user
 */
public class AccountManager {
    public static User login(UserType userType, String userID, String password)
            throws PasswordIncorrectException, ModelNotFoundException {
        User user = UserFinder.findUser(userID, userType);
        if (PasswordManager.checkPassword(user, password)) {
            return user;
        } else {
            throw new PasswordIncorrectException();
        }
    }

    public static void changePassword(UserType userType, String userID, String oldPassword, String newPassword)
            throws PasswordIncorrectException, ModelNotFoundException {
        User user = UserFinder.findUser(userID, userType);
        PasswordManager.changePassword(user, oldPassword, newPassword);
        UserUpdater.updateUser(user);
    }

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
    //Use the userFactory to create a user and add it to the repository
    public static User register(UserType userType, String userNRIC, String password, String name,
                                int age, MaritalStatus maritalStatus, String projectID, RoomType roomType)
            throws ModelAlreadyExistsException {
        User user = UserFactory.create(userType, userNRIC, password, name, age, maritalStatus, projectID, roomType);
        UserAdder.addUser(user);
        return user;
    }
    //when the password is not needed, use this method
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
            //System.out.println("Password: " + password);
            //String hashedPassword = PasswordHashManager.hashPassword(password);
            //System.out.println("Password: " + hashedPassword);
            try {
                register(UserType.APPLICANT,applicantNRIC,password,name,Age,maritalStatus, "", RoomType.NONE);
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
            //String hashedPassword = PasswordHashManager.hashPassword(password);
            try {
                register(UserType.MANAGER,managerNRIC,password,name,Age,maritalStatus, "", RoomType.NONE);
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
                register(UserType.OFFICER,applicantNRIC,password,name,Age,maritalStatus, "", RoomType.NONE);
            } catch (ModelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadUsers() {
        loadApplicants();
        loadManagers();
        loadOfficers();
    }

    public static boolean repositoryIsEmpty() {
        return ApplicantRepository.getInstance().isEmpty() &&
                ManagerRepository.getInstance().isEmpty() &&
                OfficerRepository.getInstance().isEmpty();
    }

    public static User getByDomainAndID(UserType userType, String ID) throws ModelNotFoundException {
        return switch (userType) {
            case APPLICANT -> ApplicantRepository.getInstance().getByID(ID);
            case MANAGER -> ManagerRepository.getInstance().getByID(ID);
            case OFFICER -> OfficerRepository.getInstance().getByID(ID);
        };
    }
}
