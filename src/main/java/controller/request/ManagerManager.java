package controller.request;

import controller.project.ProjectManager; // Added import
import model.project.Project;
import model.request.ProjectApplicationRequest;
import model.request.ProjectBookingRequest;
import model.request.ProjectDeregistrationRequest; // Added import
import model.request.Request;
import model.request.RequestStatus;
import model.user.Manager;
import model.user.Officer;
import repository.request.RequestRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manages all operations that can be performed by a Manager
 */
public class ManagerManager {
    private final ProjectManager projectManager; // Changed from ProjectRepository
    private final RequestRepository requestRepository;

    /**
     * Constructs a ManagerManager with default repositories
     */
    public ManagerManager() {
        this.projectManager = new ProjectManager(); // Changed initialization
        this.requestRepository = RequestRepository.getInstance();
    }

    /**
     * Creates a new project with the given details
     */
    public Project createProject(String projectID, String projectName, Manager manager,
                                 boolean visibility, String neighborhood, Integer twoRoomFlatsAvailable, Integer threeRoomFlatsAvailable, Double twoRoomFlatsPrice, Double threeRoomFlatsPrice, LocalDate applicationOpeningDate, LocalDate applicationClosingDate, Manager managerInCharge) {
        validateProjectData(projectName, manager);
        Project project = new Project(projectID, visibility, projectName,
                neighborhood, twoRoomFlatsAvailable, threeRoomFlatsAvailable,
                twoRoomFlatsPrice, threeRoomFlatsPrice,
                applicationOpeningDate, applicationClosingDate, managerInCharge);

        try {
            projectManager.addProject(project); // Use ProjectManager to add
        } catch (ModelAlreadyExistsException e) {
            throw new IllegalArgumentException("Project with ID " + projectID + " already exists");
        }
        return project;
    }

    /**
     * Gets all projects managed by a specific manager
     */
    public List<Project> getManagerProjects(Manager manager) {
        return projectManager.findProjectsByRules(
                project -> project.getManagerInCharge().getID().equals(manager.getID())
        );
    }

    /**
     * Assigns an officer to a project managed by this manager
     */
    public boolean assignOfficerToProject(Project project, Officer officer, Manager manager) {
        if (project == null || officer == null || !project.getManagerInCharge().getID().equals(manager.getID())) {
            return false;
        }

        if (project.getNumOfficers() >= 10 || project.getAssignedOfficers().contains(officer)) {
            return false;
        }

        boolean result = project.assignOfficer(officer);
        if (result) {
            try {
                projectManager.updateProject(project); // Use ProjectManager to update
            } catch (ModelNotFoundException e) {
                return false;
            }
        }
        return result;
    }

    /**
     * Removes an officer from a project managed by this manager
     */
    public boolean removeOfficerFromProject(Project project, Officer officer, Manager manager) {
        if (project == null || officer == null || !project.getManagerInCharge().getID().equals(manager.getID())) {
            return false;
        }

        boolean result = project.removeOfficer(officer);
        if (result) {
            try {
                projectManager.updateProject(project); // Use ProjectManager to update
            } catch (ModelNotFoundException e) {
                return false;
            }
        }
        return result;
    }

    /**
     * Updates project visibility
     */
    public boolean updateProjectVisibility(Project project, boolean visibility, Manager manager) {
        if (project == null || !project.getManagerInCharge().getID().equals(manager.getID())) {
            return false;
        }

        project.setVisibility(visibility);
        try {
            projectManager.updateProject(project); // Use ProjectManager to update
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Updates project application dates
     */
    public boolean updateProjectDates(Project project, LocalDate openingDate,
                                      LocalDate closingDate, Manager manager) {
        if (project == null || !project.getManagerInCharge().getID().equals(manager.getID())) {
            return false;
        }

        if (openingDate != null) {
            project.setApplicationOpeningDate(openingDate);
        }
        if (closingDate != null) {
            project.setApplicationClosingDate(closingDate);
        }

        try {
            projectManager.updateProject(project); // Use ProjectManager to update
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Updates project flat availability
     */
    public boolean updateFlatAvailability(Project project, int twoRoomFlats,
                                          int threeRoomFlats, Manager manager) {
        if (project == null || !project.getManagerInCharge().getID().equals(manager.getID())) {
            return false;
        }

        project.setTwoRoomFlatsAvailable(twoRoomFlats);
        project.setThreeRoomFlatsAvailable(threeRoomFlats);

        try {
            projectManager.updateProject(project); // Use ProjectManager to update
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Gets all project application requests pending manager review
     */
    public List<ProjectApplicationRequest> getPendingApplicationRequests(Manager manager) {
        // Define the predicates separately to avoid ambiguity
        Predicate<Request> managerPredicate = request ->
                request.getManagerID() != null && request.getManagerID().equals(manager.getID());
        Predicate<Request> statusPredicate = request ->
                request.getStatus() == RequestStatus.PENDING;
        Predicate<Request> typePredicate = request ->
                request instanceof ProjectApplicationRequest;

        List<Request> requests = requestRepository.findByRules(
                managerPredicate, statusPredicate, typePredicate);

        return requests.stream()
                .map(request -> (ProjectApplicationRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Gets all project booking requests pending manager review
     */
    public List<ProjectBookingRequest> getPendingBookingRequests(Manager manager) {
        // Define the predicates separately to avoid ambiguity
        Predicate<Request> managerPredicate = request ->
                request.getManagerID() != null && request.getManagerID().equals(manager.getID());
        Predicate<Request> statusPredicate = request ->
                request.getStatus() == RequestStatus.PENDING;
        Predicate<Request> typePredicate = request ->
                request instanceof ProjectBookingRequest;

        List<Request> requests = requestRepository.findByRules(
                managerPredicate, statusPredicate, typePredicate);

        return requests.stream()
                .map(request -> (ProjectBookingRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Gets all project deregistration requests pending manager review
     */
    public List<ProjectDeregistrationRequest> getPendingDeregistrationRequests(Manager manager) {
        // Define the predicates separately to avoid ambiguity
        Predicate<Request> managerPredicate = request ->
                request.getManagerID() != null && request.getManagerID().equals(manager.getID());
        Predicate<Request> statusPredicate = request ->
                request.getStatus() == RequestStatus.PENDING;
        Predicate<Request> typePredicate = request ->
                request instanceof ProjectDeregistrationRequest;

        List<Request> requests = requestRepository.findByRules(
                managerPredicate, statusPredicate, typePredicate);

        return requests.stream()
                .map(request -> (ProjectDeregistrationRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Approves a pending request
     */
    public boolean approveRequest(Request request, Manager manager) {
        if (request == null || request.getManagerID() == null ||
                !request.getManagerID().equals(manager.getID()) ||
                request.getStatus() != RequestStatus.PENDING) {
            return false;
        }

        request.setStatus(RequestStatus.APPROVED);
        try {
            requestRepository.update(request);
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Rejects a pending request
     */
    public boolean rejectRequest(Request request, Manager manager) {
        if (request == null || request.getManagerID() == null ||
                !request.getManagerID().equals(manager.getID()) ||
                request.getStatus() != RequestStatus.PENDING) {
            return false;
        }

        request.setStatus(RequestStatus.REJECTED);
        try {
            requestRepository.update(request);
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Gets a specific project by ID
     */
    public Project getProjectByID(String projectID) throws ModelNotFoundException {
        return projectManager.getProjectByID(projectID); // Use ProjectManager
    }

    /**
     * Gets a specific request by ID
     */
    public Request getRequestByID(String requestID) throws ModelNotFoundException {
        return requestRepository.getByID(requestID);
    }

    /**
     * Validates project data before creation
     */
    private void validateProjectData(String projectName, Manager manager) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        if (manager == null) {
            throw new IllegalArgumentException("A manager must be assigned to the project");
        }

        if (projectManager.getProjectByName(projectName) != null) { // Use ProjectManager
            throw new IllegalArgumentException("A project with this name already exists");
        }
    }
}