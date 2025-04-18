package model.request;

import model.project.RoomType;

import java.util.Map;

public class ProjectBookingRequest implements Request {
    private final RequestType requestType = RequestType.PROJECT_BOOKING_REQUEST;
    private String requestID;
    private RequestStatus requestStatus = RequestStatus.PENDING;
    private String projectID;
    private String applicantID;
    private String projectApplicationRequestID;
    private RoomType roomType;

    public ProjectBookingRequest(String requestID, String projectID, String applicantID, String projectApplicationRequestID, RoomType roomType) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.applicantID = applicantID;
        this.projectApplicationRequestID = projectApplicationRequestID;
        this.roomType = roomType;
    }
    public ProjectBookingRequest(Map<String, String> map) {fromMap(map);}


    @Override
    public String getID() {
        return requestID;
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
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    @Override
    public void setStatus(RequestStatus status) {
        this.requestStatus = status;
    }

    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    public String getApplicantID() {
        return applicantID;
    }
    public String getProjectApplicationRequestID() {
        return projectApplicationRequestID;
    }
    public String getDisplayableString() {
        return String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", requestType) +
                String.format("| %-18s | %-36s |\n", "Request Status", requestStatus.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Applicant ID", applicantID) +
                String.format("| %-18s | %-27s |\n", "projectApplicationRequestID", projectApplicationRequestID);
    }
}
