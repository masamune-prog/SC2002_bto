package model.user;
import controller.account.password.PasswordHashManager;
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
    public static User create(UserType userType, String nric, String password, String name,
                              int age, String maritalStatus, String project,
                              List<String> projectsInCharge) {
        String hashedPassword = PasswordHashManager.hashPassword(password);
        return switch (userType) {
            case APPLICANT -> new Applicant(nric, hashedPassword, name, age, maritalStatus, project);
            case OFFICER -> new Officer(nric, hashedPassword, name, age, maritalStatus, project, projectsInCharge);
            case MANAGER -> new Manager(nric, hashedPassword, name, age, maritalStatus, project, project);
        };
    }
}