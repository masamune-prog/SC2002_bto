package model.user;

import controller.account.password.PasswordHashManager;
import model.project.RoomType;
import utils.parameters.NotNull;

import java.util.List;

/**
 * A factory class for creating User objects based on the given parameters.
 */
public class UserFactory {
    /**
     * Creates a new User object based on the given parameters.
     *
     * @param userType      The type of user to be created (applicant, officer, or manager).
     * @param nric          The user's NRIC/ID.
     * @param password      The user's password in plaintext.
     * @param name          The user's name.
     * @param age           The user's age.
     * @param maritalStatus The user's marital status.
     * @param project       The user's project.
     * @param projectsInCharge The list of projects (for Officer only).
     * @return              A new User object of the specified type.
     */
    public static User create(UserType userType, String userNRIC, String password, String name,
                              int age, MaritalStatus maritalStatus, String projectID, RoomType roomType) {
        String hashedPassword = PasswordHashManager.hashPassword(password);
        return switch (userType) {
            case APPLICANT -> new Applicant(name, userNRIC, age, maritalStatus, hashedPassword);
            case OFFICER -> new Officer(userNRIC, name, hashedPassword);
            case MANAGER -> new Manager(userNRIC,hashedPassword,name);
        };
    }
}