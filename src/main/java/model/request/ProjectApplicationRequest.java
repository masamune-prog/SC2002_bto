package model.request;

import model.project.RoomType;

import java.util.Map;

public class ProjectApplicationRequest implements Request {
    private final RequestType requestType = RequestType.PROJECT_APPLICATION_REQUEST;
    private String requestID;
    private RequestStatus requestStatus = RequestStatus.PENDING;
    private String projectID;
    private String applicantID;
    private RoomType roomType;

    public ProjectApplicationRequest(String requestID, String projectID, String applicantID, RoomType roomType) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.applicantID = applicantID;
        this.roomType = roomType;
    }
    public ProjectApplicationRequest(Map<String, String> map) {fromMap(map);}
    @Override
    public String getID() {
        return requestID;
    }
    public String getApplicantID() {
        return applicantID;
    }
    public RoomType getRoomType() {
        return roomType;
    }
    @Override
    public String getProjectID() {
        return projectID;
    }

    @Override
    public RequestStatus getStatus() {
        return requestStatus;
    }

    @Override
    public void setStatus(RequestStatus status) {
        this.requestStatus = status;
    }

    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    public String getDisplayableString() {
        return String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", requestType) +
                String.format("| %-18s | %-36s |\n", "Request Status", requestStatus.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Applicant ID", applicantID) +
                String.format("| %-18s | %-27s |\n", "Room Type", roomType);
    }


}
