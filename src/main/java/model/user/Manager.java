package model.user;

import java.util.Map;
import java.util.HashMap;

public class Manager implements User {
    private String managerID;
    private String nric;
    private String hashedPassword;
    private String name;
    private String projectInCharge;
    /**
     * Constructs a new Manager object with the specified NRIC and default password.
     *
     * @param nric          the NRIC of the manager.
     * @param name          the name of the manager.
     * @param project       the project assigned to the manager.
     */
    public Manager(String managerID, String nric, String passwordHash, String name,
                   String project, String projectInCharge) {
        this.managerID = managerID;
        this.nric = nric;
        this.hashedPassword = passwordHash;
        this.name = name;
        this.projectInCharge = projectInCharge;
    }
    public Manager(Map<String, String> informationMap) {
        fromMap(informationMap);
    }
    @Override
    public String getID() {
        return this.managerID;
    }

    @Override
    public void setID(String id) {

    }

    @Override
    public String getNric() {
        return nric;
    }

    @Override
    public void setNric(String nric) {
        this.nric = nric;
    }

    @Override
    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public void setHashedPassword(String passwordHash) {
        this.hashedPassword = passwordHash;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getProjectInCharge() {
        return projectInCharge;
    }
    public void setProjectInCharge(String projectInCharge) {
        this.projectInCharge = projectInCharge;
    }
    @Override
    public void fromMap(Map<String, String> map) {
        this.managerID = map.get("managerID");
        this.nric = map.get("nric");
        this.hashedPassword = map.get("hashedPassword");
        this.name = map.get("name");
        // Default project value if not specified
        this.projectInCharge = map.get("projectInCharge") != null ? map.get("projectInCharge") : "No Project";
        
        // Log warnings if essential data is missing
        if (this.managerID == null || this.name == null || this.nric == null) {
            System.err.println("Warning: Manager missing required fields - ID: " + this.managerID + 
                             ", NRIC: " + this.nric + ", Name: " + this.name);
        }
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("managerID", this.managerID);
        map.put("nric", this.nric);
        map.put("hashedPassword", this.hashedPassword);
        map.put("name", this.name);
        map.put("projectInCharge", this.projectInCharge != null ? this.projectInCharge : "No Project");
        return map;
    }
}
