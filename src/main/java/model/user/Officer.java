package model.user;
import java.util.List;
import model.project.Project;
public class Officer implements User {
    private String officerID;
    private String nric;
    private String hashedPassword;
    private String name;
    private String project;
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
        this.project = project;
        this.projectsInCharge = projectsInCharge;
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



    public String getProject() {
        return project;
    }


    public void setProject(String project) {
        this.project = project;
    }

    public List<String> getProjectsInCharge() {
        return projectsInCharge;
    }

    public void setProjectsInCharge(List<String> projectsInCharge) {
        this.projectsInCharge = projectsInCharge;
    }
}