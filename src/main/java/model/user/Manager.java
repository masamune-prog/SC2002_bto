package model.user;
import utils.parameters.EmptyID;
import utils.parameters.NotNull;
public class Manager implements User {
    private String nric;
    private String passwordHash;
    private String name;
    private int age;
    private String maritalStatus;
    private String project;
    private String projectInCharge;
    /**
     * Constructs a new Manager object with the specified NRIC and default password.
     *
     * @param nric the NRIC of the manager.
     * @param name the name of the manager.
     * @param age the age of the manager.
     * @param maritalStatus the marital status of the manager.
     * @param project the project assigned to the manager.
     */
    public Manager(String nric, String passwordHash, String name, int age, String maritalStatus,
                   String project, String projectInCharge) {
        this.nric = nric;
        this.passwordHash = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.project = project;
        this.projectInCharge = projectInCharge;
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

    public String getProjectInCharge() {
        return projectInCharge;
    }

    public void setProjectInCharge(String projectInCharge) {
        this.projectInCharge = projectInCharge;
    }
}