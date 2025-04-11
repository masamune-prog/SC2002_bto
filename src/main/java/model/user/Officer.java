package model.user;
import java.util.List;
import java.util.Map;

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
    public Officer(String officerID, String nric, String hashedPassword, String name,String project,
                   List<String> projectsInCharge) {
        this.nric = nric;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.projectsInCharge = projectsInCharge;
    }
    public Officer(Map<String, String> informationMap) {
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
        this.officerID = map.get("officerID");
        this.nric = map.get("NRIC");
        this.hashedPassword = map.get("hashedPassword");
        this.name = map.get("Name");
        // Initialize projectsInCharge if needed (might be null at first)
        // We'll need to implement project assignment separately
    }

    public List<String> getProjectsInCharge() {
        return projectsInCharge;
    }

    public void setProjectsInCharge(List<String> projectsInCharge) {
        this.projectsInCharge = projectsInCharge;
    }
}