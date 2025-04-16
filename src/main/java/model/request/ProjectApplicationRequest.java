package model.request;

import java.util.HashMap;
import java.util.Map;

public class ProjectApplicationRequest implements Request {
    private String requestID;
    private String projectID;
    private RequestStatus status;
    private String managerID;
    private String applicantID;
    private RoomType roomType;

    public ProjectApplicationRequest(String requestID, String projectID, RequestStatus status,
                                     String managerID, String applicantID, RoomType roomType) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.status = status != null ? status : RequestStatus.PENDING;
        this.managerID = managerID;
        this.applicantID = applicantID;
        this.roomType = roomType;
    }

    public ProjectApplicationRequest(Map<String, String> map) {
        fromMap(map);
    }

    @Override
    public String getID() {
        return requestID;
    }

    @Override
    public String getProjectID() {
        return projectID;
    }

    @Override
    public RequestStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PROJECT_APPLICATION_REQUEST;
    }

    @Override
    public String getManagerID() {
        return managerID;
    }

    public String getApplicantID() {
        return applicantID;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    @Override
    public String getDisplayableString() {
        return getSplitter() + "\n" +
                String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", getRequestType()) +
                String.format("| %-18s | %-36s |\n", "Request Status", status.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Manager ID", managerID) +
                String.format("| %-18s | %-27s |\n", "Applicant ID", applicantID) +
                String.format("| %-18s | %-27s |\n", "Room Type", roomType) +
                getSplitter();
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.requestID = map.get("requestID");
        this.projectID = map.get("projectID");
        this.status = map.get("status") != null ? 
                RequestStatus.valueOf(map.get("status")) : RequestStatus.PENDING;
        this.managerID = map.get("managerID");
        this.applicantID = map.get("applicantID");
        
        if (map.get("roomType") != null) {
            try {
                this.roomType = RoomType.valueOf(map.get("roomType"));
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing room type: " + map.get("roomType"));
                this.roomType = null;
            }
        } else {
            this.roomType = null;
        }
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("requestID", requestID);
        map.put("projectID", projectID);
        map.put("status", status != null ? status.toString() : null);
        map.put("managerID", managerID);
        map.put("applicantID", applicantID);
        map.put("roomType", roomType != null ? roomType.toString() : null);
        map.put("requestType", getRequestType().toString());
        return map;
    }
}