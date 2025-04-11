package model.request;

import java.util.Map;

public class ProjectApplicationRequest implements Request {
    private String requestID;
    private String projectID;
    private RequestStatus status;
    private String managerID;
    private String applicantID;
    private String roomType;

    public ProjectApplicationRequest(String requestID, String projectID, RequestStatus status,
                                     String managerID, String applicantID, String roomType) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.status = status;
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

    public String getRoomType() {
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
        this.status = RequestStatus.valueOf(map.get("status"));
        this.managerID = map.get("managerID");
        this.applicantID = map.get("applicantID");
        this.roomType = map.get("roomType");
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = Map.of(
                "requestID", requestID,
                "projectID", projectID,
                "status", status.toString(),
                "managerID", managerID,
                "applicantID", applicantID,
                "roomType", roomType
        );
        return map;
    }
}