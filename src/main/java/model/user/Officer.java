package model.user;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import model.project.Project;
public class Officer implements User {
    private String officerID;
    private String nric;
    private String hashedPassword;
    private String name;
    private List<String> projectsInCharge;

    /**
     * Constructs a new Officer object with the specified NRIC and default password.
     *
     * @param nric          the NRIC of the officer.
     * @param name          the name of the officer.
     */
    public Officer(String officerID, String nric, String hashedPassword, String name, String project,
                   List<String> projectsInCharge) {
        this.officerID = officerID;
        this.nric = nric;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.projectsInCharge = projectsInCharge != null ? projectsInCharge : new ArrayList<>();
    }

    public Officer(Map<String, String> informationMap) {
        this.projectsInCharge = new ArrayList<>(); // Initialize empty list
        fromMap(informationMap);
    }

    @Override
    public String getID() {
        return this.officerID;
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
        return this.hashedPassword;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.officerID = map.getOrDefault("officerID", "");
        this.nric = map.getOrDefault("NRIC", "");
        this.hashedPassword = map.getOrDefault("hashedPassword", "");
        this.name = map.getOrDefault("Name", "");
        this.projectsInCharge = new ArrayList<>(); // Initialize empty list
        
        // Debug output
        System.out.println("Creating officer from map: " + 
                         "ID=" + this.officerID + 
                         ", Name=" + this.name + 
                         ", NRIC=" + this.nric);
        
        // Validate required fields
        if (this.officerID.isEmpty() || this.nric.isEmpty() || this.name.isEmpty()) {
            System.err.println("Warning: Officer missing required fields - ID: " + this.officerID + 
                             ", NRIC: " + this.nric + ", Name: " + this.name);
        }
    }

    public List<String> getProjectsInCharge() {
        return projectsInCharge;
    }

    public void setProjectsInCharge(List<String> projectsInCharge) {
        this.projectsInCharge = projectsInCharge;
    }
}