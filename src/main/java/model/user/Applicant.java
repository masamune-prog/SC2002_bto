package model.user;

import utils.iocontrol.Mappable;
import utils.parameters.EmptyID;
import utils.parameters.NotNull;

import java.util.Map;

public class Applicant implements User, Mappable {
    private String applicantID;
    private String nric;
    private String hashedPassword;
    private String name;
    private int age;
    private MaritalStatus maritalStatus; // Use enum instead of String
    private String project;
    private ApplicantStatus status;
    public Applicant(String applicantID, String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus, String project) {
        this.applicantID = applicantID;
        this.nric = nric;
        this.hashedPassword = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.project = project;
    }

    public Applicant(Map<String, String> informationMap) {
        fromMap(informationMap);
    }

    @Override
    public String getID() {
        return this.applicantID;
    }

    @Override
    public void setID(String id) {
        this.applicantID = id;
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

    public String getPassword() {
        return hashedPassword;
    }
    public void setPassword(String password) {
        this.hashedPassword = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.applicantID = map.get("applicantID");
        this.nric = map.get("nric");
        this.hashedPassword = map.get("hashedPassword");
        this.name = map.get("name");

        try {
            this.age = Integer.parseInt(map.get("age"));
        } catch (NumberFormatException e) {
            this.age = 0;
        }

        // Handle the marital status specifically
        String status = map.get("maritalStatus");
        this.maritalStatus = MaritalStatus.fromString(status);

        this.project = map.get("project");
    }

    public ApplicantStatus getStatus() {
        return status;
    }
    public void setStatus(ApplicantStatus status) {
        this.status = status;
    }
}