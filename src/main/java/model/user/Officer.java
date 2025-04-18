package model.user;

import utils.parameters.EmptyID;
import utils.parameters.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Officer implements User {
    private String officerNRIC;
    private String hashedPassword;
    private String officerName;
    //private List<String> projectsInCharge;

    //
    public Officer(String officerNRIC, String officerName) {
        this.officerNRIC = officerNRIC;
        this.officerName = officerName;

    }

    //with password
    public Officer(String officerNRIC, String officerName, @NotNull String hashedPassword) {
        this.officerNRIC = officerNRIC;
        this.officerName = officerName;
        this.hashedPassword = hashedPassword;

    }

    public Officer(Map<String, String> map) {
        this.fromMap(map);
    }

    /**
     * default constructor for Supervisor class
     */
    public Officer() {
        this.officerNRIC = EmptyID.EMPTY_ID;
        this.officerName = EmptyID.EMPTY_ID;
    }

    public static User getUser(Map<String, String> map) {
        return new Officer(map);
    }


    @Override
    public String getID() {
        return officerNRIC;
    }

    @Override
    public void setID(String id) {
        this.officerNRIC = id;
    }

    @Override
    public String getNRIC() {
        return officerNRIC;
    }

    @Override
    public void setNRIC(String nric) {
        this.officerNRIC = nric;
    }

    @Override
    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public String getName() {
        return this.officerName;
    }

    @Override
    public void setName(String name) {
        this.officerName = name;
    }

    @Override
    public Object getUserType() {
        return UserType.OFFICER;
    }
}