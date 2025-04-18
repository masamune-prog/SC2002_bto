package model.request;

import model.project.RoomType;

import java.util.Map;

public class ProjectWithdrawalRequest implements Request {
    private final RequestType requestType = RequestType.PROJECT_WITHDRAWAL_REQUEST;
    private String requestID;
    private RequestStatus requestStatus = RequestStatus.PENDING;
    private String projectID;
    private String applicantID;
    private RoomType roomType;
    private String reason;

    public ProjectWithdrawalRequest(String requestID, String projectID, String applicantID, RoomType roomType, String reason) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.applicantID = applicantID;
        this.roomType = roomType;
        this.reason = reason;
    }
    public ProjectWithdrawalRequest(Map<String, String> map) {fromMap(map);}
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

    public String getApplicantID() {
        return applicantID;
    }
    public RoomType getRoomType() {
        return roomType;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    public void setApplicantID(String applicantID) {
        this.applicantID = applicantID;
    }
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getDisplayableString() {
        return String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", requestType) +
                String.format("| %-18s | %-36s |\n", "Request Status", requestStatus.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                String.format("| %-18s | %-27s |\n", "Applicant ID", applicantID) +
                String.format("| %-18s | %-27s |\n", "Reason", reason);
    }
}
