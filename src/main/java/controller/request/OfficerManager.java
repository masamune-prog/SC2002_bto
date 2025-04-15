package controller.request;

import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.RequestStatus;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

public class OfficerManager {
    private final RequestManager requestManager;
    private final RequestRepository requestRepository;
    private final ProjectRepository projectRepository;
    private final OfficerRepository officerRepository;

    public OfficerManager() {
        this.requestManager = new RequestManager();
        this.requestRepository = RequestRepository.getInstance();
        this.projectRepository = ProjectRepository.getInstance();
        this.officerRepository = OfficerRepository.getInstance();
    }

    public String createOfficerApplication(String officerID, String projectID) throws ModelNotFoundException {
        // Check if officer exists
        Officer officer = officerRepository.getByID(officerID);
        if (officer == null) {
            throw new ModelNotFoundException("Officer not found");
        }

        // Check if project exists
        Project project = projectRepository.getByID(projectID);
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

    public void approveOfficerApplication(String requestID) throws ModelNotFoundException {
        OfficerApplicationRequest request = (OfficerApplicationRequest) requestRepository.getByID(requestID);
        if (request == null) {
            throw new ModelNotFoundException("Request not found");
        }

        // Get the officer and project
        Officer officer = officerRepository.getByID(request.getID());
        Project project = projectRepository.getByID(request.getProjectID());

        if (officer == null || project == null) {
            throw new ModelNotFoundException("Officer or Project not found");
        }

        // Add officer to project
        project.addOfficer(officer.getID());
        projectRepository.update(project);

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
} 