package model.request;

import java.util.HashMap;
import java.util.Map;

import model.project.Project;
import model.user.Officer;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

public class OfficerApplicationRequest implements Request {
    private String requestID;
    private RequestStatus status;

    // Object references
    private Project project; // Keep project object if needed, but don't fetch it here.
    private Officer officer;

    // String identifiers for mapping
    private String projectID; // Ensure projectID is stored
    private String userName;
    private String nric;

    public OfficerApplicationRequest(String requestID, Project project, Officer officer) {
        this.requestID = requestID;
        this.project = project; // Store the passed project object
        this.projectID = project != null ? project.getID() : null; // Store the ID
        this.officer = officer;
        this.userName = officer != null ? officer.getName() : null;
        this.nric = officer != null ? officer.getNRIC() : null;
        this.status = RequestStatus.PENDING;
    }

    public OfficerApplicationRequest(String requestID, String userName, String nric, String projectID, RequestStatus status) {
        this.requestID = requestID;
        this.userName = userName;
        this.nric = nric;
        this.projectID = projectID;
        this.status = status != null ? status : RequestStatus.PENDING;

        // Try to load the officer object if possible
        if (nric != null) {
            this.officer = OfficerRepository.getInstance().getByNRIC(nric);
        }
    }

    public OfficerApplicationRequest(Map<String, String> map) {
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

    public String getUserName() {
        return userName;
    }

    public String getNric() {
        return nric;
    }

    public Officer getOfficer() {
        return officer;
    }

    public String getOfficerID() {
        return officer != null ? officer.getID() : null;
    }

    public Project getProject() {
        return project;
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
        return RequestType.OFFICER_REQUEST;
    }

    @Override
    public String getManagerID() {
        // Officer applications don't have a manager ID initially
        return null;
    }

    @Override
    public String getDisplayableString() {
        return getSplitter() + "\n" +
                String.format("| %-18s | %-27s |\n", "Request ID", requestID) +
                String.format("| %-18s | %-27s |\n", "Request Type", getRequestType()) +
                String.format("| %-18s | %-36s |\n", "Request Status", status.showColorfulStatus()) +
                String.format("| %-18s | %-27s |\n", "Applicant", userName) +
                String.format("| %-18s | %-27s |\n", "NRIC", nric) +
                String.format("| %-18s | %-27s |\n", "Project ID", projectID) +
                getSplitter();
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.requestID = map.get("requestID");
        this.userName = map.get("userName");
        this.nric = map.get("nric");
        this.projectID = map.get("projectID"); // Store projectID from map
        this.status = map.get("status") != null ?
                RequestStatus.valueOf(map.get("status")) :
                RequestStatus.PENDING;

        // Try to load the officer object if possible
        if (nric != null) {
            this.officer = OfficerRepository.getInstance().getByNRIC(nric);
        }
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("requestID", requestID);
        map.put("userName", userName);
        map.put("nric", nric);
        map.put("projectID", projectID); // Use stored projectID
        map.put("status", status != null ? status.toString() : null);
        map.put("requestType", getRequestType().toString());
        return map;
    }

    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }

    public void approve() {
        this.status = RequestStatus.APPROVED;
    }

    public void reject() {
        this.status = RequestStatus.REJECTED;
    }
}