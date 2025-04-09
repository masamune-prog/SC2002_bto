package model.user;

import utils.parameters.EmptyID;
import utils.parameters.NotNull;

import java.util.Map;

public class Applicant implements User {
    private String nric;
    private String passwordHash;
    private String name;
    private int age;
    private String maritalStatus;
    private String project;
    public Applicant(String nric, String passwordHash, String name, int age, String maritalStatus, String project) {
        this.nric = nric;
        this.passwordHash = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.project = project;
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
}