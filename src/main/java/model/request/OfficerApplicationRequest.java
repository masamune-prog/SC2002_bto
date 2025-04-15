package model.request;

import java.util.HashMap;
import java.util.Map;

import model.project.Project;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

public class OfficerApplicationRequest implements Request {
    private String requestID;
    private RequestStatus status;

    // Object references
    private Project project;
    private Officer officer;

    // String identifiers for mapping
    private String projectID;
    private String userName;
    private String nric;

    public OfficerApplicationRequest(String requestID, Project project, Officer officer) {
        this.requestID = requestID;
        this.project = project;
        this.officer = officer;
        this.projectID = project != null ? project.getID() : null;
        this.userName = officer != null ? officer.getName() : null;
        this.nric = officer != null ? officer.getNric() : null;
        this.status = RequestStatus.PENDING;
    }
    public OfficerApplicationRequest(String requestID, String userName, String nric, String projectID, RequestStatus status) {
        this.requestID = requestID;
        this.userName = userName;
        this.nric = nric;
        this.projectID = projectID;
        this.status = status != null ? status : RequestStatus.PENDING;

        // Try to load the objects if possible
        try {
            if (projectID != null) {
                this.project = ProjectRepository.getInstance().getByID(projectID);
            }
            if (nric != null) {
                this.officer = OfficerRepository.getInstance().getByNRIC(nric);
            }
        } catch (ModelNotFoundException e) {
            // Objects will remain null if not found
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
        this.projectID = map.get("projectID");
        this.status = map.get("status") != null ?
                RequestStatus.valueOf(map.get("status")) :
                RequestStatus.PENDING;

        // Try to load the objects if possible
        try {
            if (projectID != null) {
                this.project = ProjectRepository.getInstance().getByID(projectID);
            }
            if (nric != null) {
                this.officer = OfficerRepository.getInstance().getByNRIC(nric);
            }
        } catch (ModelNotFoundException e) {
            // Objects will remain null if not found
        }
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("requestID", requestID);
        map.put("userName", userName);
        map.put("nric", nric);
        map.put("projectID", projectID);
        map.put("status", status.toString());
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