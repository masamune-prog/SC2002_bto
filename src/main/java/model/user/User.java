package model.user;
import model.Model;

/**
 * Represents a user in the system.
 * This interface defines the common properties and methods for all user types.
 */
public interface User extends Model {
    /**
     * Gets the unique identifier for the user.
     * Typically derived from the NRIC.
     * @return The user's ID.
     */
    String getID();

    /**
     * Sets the unique identifier for the user.
     * @param id The user's ID.
     */
    void setID(String id);

    /**
     * Gets the National Registration Identity Card (NRIC) number of the user.
     * @return The user's NRIC.
     */
    String getNRIC();

    /**
     * Sets the National Registration Identity Card (NRIC) number of the user.
     * @param nric The user's NRIC.
     */
    void setNRIC(String nric);

    /**
     * Gets the hashed password of the user.
     *
     * @return the hashed password of the user
     */
    String getHashedPassword();

    /**
     * Sets the hashed password of the user.
     *
     * @param hashedPassword the hashed password of the user
     */
    void setHashedPassword(String hashedPassword);

    /**
     * Gets the name of the user.
     * @return The user's name.
     */
    String getName();

    /**
     * Sets the name of the user.
     * @param name The user's name.
     */
    void setName(String name);

    /**
     * Checks if the provided NRIC matches the user's NRIC (case-insensitive).
     * @param NRIC The NRIC to check.
     * @return true if the NRIC matches, false otherwise.
     */
    default boolean checkNRIC(String NRIC) {
        return this.getNRIC().equalsIgnoreCase(NRIC);
    }

    /**
     * Gets the type of the user (e.g., Applicant, Manager, Officer).
     * @return An object representing the user type, typically an enum.
     */
    Object getUserType();
}