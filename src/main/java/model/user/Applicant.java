package model.user;

import controller.account.password.PasswordHashManager;
import model.project.RoomType;
import utils.iocontrol.Mappable;
import utils.parameters.EmptyID;
import utils.parameters.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class Applicant implements User {
    private String applicantNRIC;
    private String hashedPassword;
    private String applicantName;
    private Integer applicantAge;
    private MaritalStatus maritalStatus; // Use enum instead of String
    private String applicantProjectID;
    private ApplicantStatus applicantStatus;
    private RoomType roomType;  // booked flat type, null if none

    //for the default password
    public Applicant(String name, String NRIC, Integer age, MaritalStatus maritalStatus) {
        this.applicantName = name;
        this.applicantNRIC = NRIC;
        this.applicantAge = age;
        this.maritalStatus = maritalStatus;// Assuming NRIC is unique and used as ID
        this.applicantStatus = ApplicantStatus.NO_REGISTRATION; // Default status
        this.roomType = RoomType.NONE; // Default status
    }

    //Applicant with specified ID and Password
    public Applicant(String name, String NRIC, Integer age, MaritalStatus maritalStatus, @NotNull String hashedPassword) {
        this.applicantName = name;
        this.applicantNRIC = NRIC;
        this.applicantAge = age;
        this.maritalStatus = maritalStatus;
        this.applicantStatus = ApplicantStatus.NO_REGISTRATION;
        this.roomType = RoomType.NONE;
        this.hashedPassword = hashedPassword;// Default status
    }
    // Applicant with predefined Status for unit testing
    public Applicant(String name, String NRIC, Integer age, MaritalStatus maritalStatus, @NotNull String hashedPassword, ApplicantStatus applicantStatus,RoomType roomType, String projectID) {
        this.applicantName = name;
        this.applicantNRIC = NRIC;
        this.applicantAge = age;
        this.maritalStatus = maritalStatus;
        this.applicantStatus = applicantStatus;
        this.roomType = RoomType.NONE;
        this.hashedPassword = hashedPassword;// Default status
        this.applicantProjectID = projectID;

    }
    //Default constructor
    public Applicant() {
        super();
        this.applicantNRIC = EmptyID.EMPTY_ID;
        this.applicantName = EmptyID.EMPTY_ID;
        this.applicantAge = 0;
        this.maritalStatus = MaritalStatus.SINGLE;
        this.applicantStatus = ApplicantStatus.NO_REGISTRATION;
        this.applicantProjectID = EmptyID.EMPTY_ID;// Default status
        this.roomType = RoomType.NONE;

    }

    public Applicant(Map<String, String> informationMap) {
        fromMap(informationMap);
    }

    public static User getUser(Map<String, String> informationMap) {
        return new Applicant(informationMap);
    }

    @Override
    public String getID() {
        return this.applicantNRIC;
    }

    @Override
    public void setID(String id) {
        this.applicantNRIC = id;
    }

    @Override
    public String getNRIC() {
        return this.applicantNRIC;
    }

    public void setNRIC(String nric) {
        this.applicantNRIC = nric;
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
        return applicantName;
    }

    @Override
    public void setName(String name) {
        this.applicantName = name;
    }

    @Override
    public Object getUserType() {
        return UserType.APPLICANT;
    }

    public RoomType getRoomType() {
        return roomType;
    }
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    public int getAge() {
        return applicantAge;
    }

    public void setAge(int age) {
        this.applicantAge = age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public ApplicantStatus getApplicantStatus() {
        return applicantStatus;
    }
    public void setApplicantStatus(ApplicantStatus applicantStatus) {
        this.applicantStatus = applicantStatus;
    }

    public String getProjectID() {
        return applicantProjectID;
    }

    public void setProjectID(String projectID) {
        this.applicantProjectID = projectID;
    }
}
