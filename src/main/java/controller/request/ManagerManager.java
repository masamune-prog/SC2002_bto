package controller.request;

import model.project.Project;
import model.request.ProjectApplicationRequest;
import model.request.ProjectBookingRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.user.Manager;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all operations that can be performed by a Manager
 */
public class ManagerManager {
    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;

    /**
     * Constructs a ManagerManager with default repositories
     */
    public ManagerManager() {
        this.projectRepository = ProjectRepository.getInstance();
        this.requestRepository = RequestRepository.getInstance();
    }

    /**
     * Creates a new project with the given details
     */
    public Project createProject(String projectID, String projectName, Manager manager,
                                 boolean visibility, String neighborhood, Integer twoRoomFlatsAvailable, Integer threeRoomFlatsAvailable,Double twoRoomFlatsPrice,Double threeRoomFlatsPrice, LocalDate applicationOpeningDate, LocalDate applicationClosingDate,Manager managerInCharge) {
        validateProjectData(projectName, manager);
        Project project = new Project(projectID, visibility, projectName,
                neighborhood, twoRoomFlatsAvailable, threeRoomFlatsAvailable,
                twoRoomFlatsPrice, threeRoomFlatsPrice,
                applicationOpeningDate, applicationClosingDate, managerInCharge);

        try {
            projectRepository.add(project);
        } catch (ModelAlreadyExistsException e) {
            throw new IllegalArgumentException("Project with ID " + projectID + " already exists");
        }
        return project;
    }

    /**
     * Gets all projects managed by a specific manager
     */
    public List<Project> getManagerProjects(Manager manager) {
        return projectRepository.findByRules(
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
                projectRepository.update(project);
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
                projectRepository.update(project);
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
            projectRepository.update(project);
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
            projectRepository.update(project);
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
            projectRepository.update(project);
            return true;
        } catch (ModelNotFoundException e) {
            return false;
        }
    }

    /**
     * Gets all project application requests pending manager review
     */
    public List<ProjectApplicationRequest> getPendingApplicationRequests(Manager manager) {
        return requestRepository.findByRules(
                        request -> request.getManagerID().equals(manager.getID()),
                        request -> request.getStatus() == RequestStatus.PENDING,
                        request -> request instanceof ProjectApplicationRequest
                ).stream()
                .map(request -> (ProjectApplicationRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Gets all project booking requests pending manager review
     */
    public List<ProjectBookingRequest> getPendingBookingRequests(Manager manager) {
        return requestRepository.findByRules(
                        request -> request.getManagerID().equals(manager.getID()),
                        request -> request.getStatus() == RequestStatus.PENDING,
                        request -> request instanceof ProjectBookingRequest
                ).stream()
                .map(request -> (ProjectBookingRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Approves a pending request
     */
    public boolean approveRequest(Request request, Manager manager) {
        if (request == null || !request.getManagerID().equals(manager.getID()) ||
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
        if (request == null || !request.getManagerID().equals(manager.getID()) ||
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
        return projectRepository.getByID(projectID);
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

        for (Project existingProject : projectRepository.getAll()) {
            if (existingProject.getProjectName().equals(projectName)) {
                throw new IllegalArgumentException("A project with this name already exists");
            }
        }
    }
}