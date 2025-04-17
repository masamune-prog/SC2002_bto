package model.user;

import utils.iocontrol.Mappable;
import model.request.RoomType;

import java.util.Map;
import java.util.LinkedHashMap;

public class Applicant implements User, Mappable {
    private String applicantID;
    private String NRIC;
    private String hashedPassword;
    private String name;
    private int age;
    private MaritalStatus maritalStatus; // Use enum instead of String
    private String project;
    private ApplicantStatus status;
    private RoomType roomType;  // booked flat type, null if none

    public Applicant(String applicantID, String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus, String project) {
        this.applicantID = applicantID;
        this.NRIC = nric;
        this.hashedPassword = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.project = project;
        // Set roomType null if no project
        this.roomType = null;
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
    public String getNRIC() {
        return this.NRIC;
    }

   public void setNRIC(String nric) {
        this.NRIC = nric;
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
        this.NRIC = map.get("nric");
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

        // Handle applicant status
        String applicantStatus = map.get("status");
        if (applicantStatus != null) {
            try {
                this.status = ApplicantStatus.valueOf(applicantStatus);
            } catch (IllegalArgumentException e) {
                this.status = ApplicantStatus.UNREGISTERED;
            }
        } else {
            this.status = ApplicantStatus.UNREGISTERED;
        }

        // Determine roomType: null if no project, else parse from map
        String rt = map.get("roomType");
        if (this.project == null || this.project.isEmpty() || rt == null || rt.isEmpty()) {
            this.roomType = null;
        } else {
            try {
                this.roomType = RoomType.valueOf(rt);
            } catch (IllegalArgumentException e) {
                this.roomType = null;
            }
        }
    }

    /**
     * Convert applicant fields to a map for TXT persistence.
     */
    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("applicantID", getID());
        map.put("name", getName());
        map.put("nric", getNRIC());
        map.put("age", String.valueOf(getAge()));
        map.put("maritalStatus", getMaritalStatus().toString());
        map.put("hashedPassword", getHashedPassword());
        map.put("project", getProject() != null ? getProject() : "");
        map.put("status", getStatus() != null ? getStatus().toString() : ApplicantStatus.UNREGISTERED.toString());
        map.put("roomType", getRoomType() != null ? getRoomType().toString() : "");
        return map;
    }

    public ApplicantStatus getStatus() {
        return status;
    }
    public void setStatus(ApplicantStatus status) {
        this.status = status;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}