package controller.request;

import controller.project.ProjectManager;
import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.user.Officer;
import repository.request.RequestRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

import java.util.List;

public class OfficerManager {
    private final RequestManager requestManager;
    private final ProjectManager projectManager;
    private final RequestRepository requestRepository;
    private final OfficerRepository officerRepository;

    public OfficerManager() {
        this.requestManager = new RequestManager();
        this.projectManager = new ProjectManager();
        this.requestRepository = RequestRepository.getInstance();
        this.officerRepository = OfficerRepository.getInstance();
    }

    public String createOfficerApplication(String officerID, String projectID) throws ModelNotFoundException {
        // Check if officer exists
        Officer officer = officerRepository.getByID(officerID);
        if (officer == null) {
            throw new ModelNotFoundException("Officer not found");
        }

        // Check if project exists
        Project project = projectManager.getProjectByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project not found");
        }

        // Create new request
        String requestID = requestManager.getNewRequestID();
        OfficerApplicationRequest request = new OfficerApplicationRequest(requestID, project, officer);
        request.setStatus(RequestStatus.PENDING);
        requestRepository.add(request);

        return requestID;
    }
    public List<Request> getPendingOfficerApplicationsRequest(String OfficerID) throws ModelNotFoundException {
        List<Request> requests = requestRepository.getBookingRequestsByOfficer(OfficerID);
        return requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .toList();


    }
    public List<Request> getAllRequestHistory(String officerID) throws ModelNotFoundException {
        return requestRepository.getBookingRequestsByOfficer(officerID);
    }
    /*
    public void approveOfficerApplication(String requestID) throws ModelNotFoundException {
        OfficerApplicationRequest request = (OfficerApplicationRequest) requestRepository.getByID(requestID);
        if (request == null) {
            throw new ModelNotFoundException("Request not found");
        }

        // Get the officer and project
        Officer officer = officerRepository.getByID(request.getID());
        Project project = projectManager.getProjectByID(request.getProjectID());

        if (officer == null || project == null) {
            throw new ModelNotFoundException("Officer or Project not found");
        }

        // Add officer to project
        project.addOfficer(officer.getID());
        projectManager.updateProject(project);

        // Add project to officer's list
        officer.getProjectsInCharge().add(project.getID());
        officerRepository.update(officer);

        // Update request status
        request.approve();
        requestRepository.update(request);
    }

    public void rejectOfficerApplication(String requestID) throws ModelNotFoundException {
        OfficerApplicationRequest request = (OfficerApplicationRequest) requestRepository.getByID(requestID);
        if (request == null) {
            throw new ModelNotFoundException("Request not found");
        }

        request.reject();
        requestRepository.update(request);
    }
     */
}