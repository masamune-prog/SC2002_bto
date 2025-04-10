package model.user;
import model.Model;
public interface User extends Model {
    String getID();
    void setID(String id);

    String getNric();
    void setNric(String nric);
    /**
     * Gets the hashed password of the user
     *
     * @return the hashed password of the user
     */
    String getHashedPassword();

    /**
     * Sets the hashed password of the user
     *
     * @param hashedPassword the hashed password of the user
     */
    void setHashedPassword(String hashedPassword);

    String getName();
    void setName(String name);

}