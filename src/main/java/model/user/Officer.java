package model.user;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

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
        this.nric = map.getOrDefault("nric", "");
        this.hashedPassword = map.getOrDefault("hashedPassword", "");
        this.name = map.getOrDefault("name", "");
        this.projectsInCharge = new ArrayList<>(); // Initialize empty list
        
        // Load projects in charge from CSV
        String projectsInChargeStr = map.get("projectsInCharge");
        if (projectsInChargeStr != null && !projectsInChargeStr.isEmpty()) {
            // Skip processing if it's just the empty array string representation
            if (projectsInChargeStr.equals("[]")) {
                System.out.println("Skipping empty projects array notation for officer: " + this.name);
            } else {
                String[] projectIDs = projectsInChargeStr.split(",");
                for (String projectID : projectIDs) {
                    String trimmedID = projectID.trim();
                    // Skip empty strings and the "[]" placeholder
                    if (!trimmedID.isEmpty() && !trimmedID.equals("[]")) {
                        this.projectsInCharge.add(trimmedID);
                    }
                }
            }
        }
        
        // Debug output
        System.out.println("Creating officer from map: " + 
                         "ID=" + this.officerID + 
                         ", Name=" + this.name + 
                         ", NRIC=" + this.nric + 
                         ", Projects=" + this.projectsInCharge);
        
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

    /**
     * Adds a project to the officer's list of projects in charge
     * @param projectID the ID of the project to add
     * @return true if the project was added, false if it was already in the list
     */
    public boolean addProject(String projectID) {
        if (projectID == null || projectID.isEmpty()) {
            return false;
        }
        
        if (this.projectsInCharge == null) {
            this.projectsInCharge = new ArrayList<>();
        }
        
        // Check if the project is already in the list
        if (!this.projectsInCharge.contains(projectID)) {
            this.projectsInCharge.add(projectID);
            return true;
        }
        
        return false;
    }

    /**
     * Removes a project from the officer's list of projects in charge
     * @param projectID the ID of the project to remove
     * @return true if the project was removed, false if it wasn't in the list
     */
    public boolean removeProject(String projectID) {
        if (projectID == null || projectID.isEmpty() || this.projectsInCharge == null) {
            return false;
        }
        
        return this.projectsInCharge.remove(projectID);
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("officerID", this.officerID);
        map.put("nric", this.nric);
        map.put("hashedPassword", this.hashedPassword);
        map.put("name", this.name);
        
        // Save projects in charge as a comma-separated list
        if (this.projectsInCharge != null && !this.projectsInCharge.isEmpty()) {
            map.put("projectsInCharge", String.join(",", this.projectsInCharge));
        }
        
        return map;
    }
}