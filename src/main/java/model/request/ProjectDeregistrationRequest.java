package model.request;

import java.util.Map;

public class ProjectDeregistrationRequest implements Request {
    private String requestID;
    private String projectID;
    private RequestStatus status;
    private String managerID;
    private String applicantID;
    private String originalRequestID;
    private String withdrawalReason;

    public ProjectDeregistrationRequest(String requestID, String projectID, RequestStatus status,
                                        String managerID, String applicantID, String originalRequestID,
                                        String withdrawalReason) {
        this.requestID = requestID;
        this.projectID = projectID;
        this.status = status;
        this.managerID = managerID;
        this.applicantID = applicantID;
        this.originalRequestID = originalRequestID;
        this.withdrawalReason = withdrawalReason;
    }

    public ProjectDeregistrationRequest(Map<String, String> map) {
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
        return RequestType.PROJECT_DEREGISTRATION_REQUEST;
    }

    @Override
    public String getManagerID() {
        return managerID;
    }

    public String getApplicantID() {
        return applicantID;
    }

    public String getOriginalRequestID() {
        return originalRequestID;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
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
                String.format("| %-18s | %-27s |\n", "Original Request", originalRequestID) +
                String.format("| %-18s | %-27s |\n", "Withdrawal Reason", withdrawalReason) +
                getSplitter();
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.requestID = map.get("requestID");
        this.projectID = map.get("projectID");
        this.status = RequestStatus.valueOf(map.get("status"));
        this.managerID = map.get("managerID");
        this.applicantID = map.get("applicantID");
        this.originalRequestID = map.get("originalRequestID");
        this.withdrawalReason = map.get("withdrawalReason");
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "requestID", requestID,
                "projectID", projectID,
                "status", status.toString(),
                "managerID", managerID,
                "applicantID", applicantID,
                "originalRequestID", originalRequestID,
                "withdrawalReason", withdrawalReason
        );
    }
}