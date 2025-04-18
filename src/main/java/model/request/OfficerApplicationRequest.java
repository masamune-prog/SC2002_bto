package model.request;

import model.project.RoomType;

import java.util.Map;

public class OfficerApplicationRequest implements Request {
    private final RequestType requestType = RequestType.OFFICER_APPLICATION_REQUEST;
    private String requestID;
    private RequestStatus requestStatus = RequestStatus.PENDING;
    private String projectID;
    private String officerID;
    public OfficerApplicationRequest(String requestID, String projectID, String officerID) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.officerID = officerID;
    }
    public OfficerApplicationRequest(Map<String, String> map) {fromMap(map);}
    @Override
    public String getID() {
        return requestID;
    }

    @Override
    public String getProjectID() {
        return projectID;
    }
    public String getOfficerID() {
        return officerID;
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
                String.format("| %-18s | %-27s |\n", "Officer ID", officerID);
    }
}
