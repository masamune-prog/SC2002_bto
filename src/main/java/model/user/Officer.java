package model.user;
import utils.parameters.EmptyID;
import utils.parameters.NotNull;
import java.util.List;

public class Officer implements User {
    private String nric;
    private String passwordHash;
    private String name;
    private int age;
    private String maritalStatus;
    private String project;
    private List<String> projectsInCharge;
    /**
     * Constructs a new Officer object with the specified NRIC and default password.
     *
     * @param nric the NRIC of the officer.
     * @param name the name of the officer.
     * @param age the age of the officer.
     * @param maritalStatus the marital status of the officer.
     * @param project the project assigned to the officer.
     */
    public Officer(String nric, String passwordHash, String name, int age, String maritalStatus,
                   String project, List<String> projectsInCharge) {
        this.nric = nric;
        this.passwordHash = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.project = project;
        this.projectsInCharge = projectsInCharge;
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
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String getMaritalStatus() {
        return maritalStatus;
    }

    @Override
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public String getProject() {
        return project;
    }

    @Override
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