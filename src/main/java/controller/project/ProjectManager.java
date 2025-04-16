package controller.project;

import model.project.Project;
import model.project.ProjectStatus;
import model.user.Applicant;
import model.user.Manager;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import model.user.MaritalStatus;
import utils.exception.ModelNotFoundException;
import utils.exception.ModelAlreadyExistsException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Manages project creation and modification operations
 */
public class ProjectManager {
    private final ProjectRepository projectRepository;
    private final ManagerRepository managerRepository;
    private final OfficerRepository officerRepository;

    /**
     * Constructs a ProjectManager with default repositories
     */
    public ProjectManager() {
        this.projectRepository = ProjectRepository.getInstance();
        this.managerRepository = ManagerRepository.getInstance();
        this.officerRepository = OfficerRepository.getInstance();
    }
    public static void loadProjectsFromCSV() {
        ProjectRepository projectRepository = ProjectRepository.getInstance();
        String filePath = projectRepository.getFilePath();
        System.out.println("Loading projects from: " + filePath);

        try {
            projectRepository.load();
            System.out.println("Projects loaded successfully. Count: " + projectRepository.getAll().size());

            // Debug output
            for (Project project : projectRepository.getAll()) {
                System.out.println("Loaded project: " + project.getProjectName() +
                        " (ID: " + project.getID() +
                        ", Flats: 2R=" + project.getTwoRoomFlatsAvailable() +
                        ", 3R=" + project.getThreeRoomFlatsAvailable() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error loading projects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Creates a new project with the given details
     */
    public Project createProject(boolean visibility,
                                 String projectName, String neighborhood,
                                 int twoRoomFlatsAvailable, int threeRoomFlatsAvailable,
                                 double twoRoomFlatsPrice, double threeRoomFlatsPrice,
                                 LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                                 Manager managerInCharge) {

        validateProjectData(projectName, managerInCharge);
        // Generate a unique project ID
        String projectID = projectRepository.getNewProjectID();
        Project project = new Project(projectID, visibility, projectName,
                neighborhood, twoRoomFlatsAvailable, threeRoomFlatsAvailable,
                twoRoomFlatsPrice, threeRoomFlatsPrice,
                applicationOpeningDate, applicationClosingDate, managerInCharge);

        try {
            projectRepository.add(project);
        } catch (ModelAlreadyExistsException e) {
            throw new IllegalStateException("Generated project ID already exists: " + projectID, e);
        }

        return project;
    }

    /**
     * Validates project data before creation
     */
    private void validateProjectData(String projectName, Manager managerInCharge) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        if (managerInCharge == null) {
            throw new IllegalArgumentException("A manager must be assigned to the project");
        }

        // Check for duplicate project names
        for (Project existingProject : projectRepository.getAll()) {
            if (existingProject.getProjectName().equals(projectName)) {
                throw new IllegalArgumentException("A project with this name already exists");
            }
        }
    }

    /**
     * Assigns an officer to a project
     */
    public boolean assignOfficerToProject(Project project, Officer officer) {
        if (project == null || officer == null) {
            return false;
        }
        // Check if maximum number of officers is reached
        if (project.getNumOfficers() >= 10) {
            return false;
        }

        // Check if already assigned
        if (project.getAssignedOfficers().contains(officer)) {
            return false;
        }

        return project.assignOfficer(officer);
    }

    /**
     * Removes an officer from a project
     */
    public boolean removeOfficerFromProject(Project project, Officer officer) {
        if (project == null || officer == null) {
            return false;
        }

        return project.removeOfficer(officer);
    }

    /**
     * Gets all projects an officer is assigned to
     */
    public List<Project> getOfficerProjects(Officer officer) {
        List<Project> officerProjects = new ArrayList<>();

        for (Project project : projectRepository.getAll()) {
            if (project.getAssignedOfficers().contains(officer)) {
                officerProjects.add(project);
            }
        }

        return officerProjects;
    }

    /**
     * Updates project visibility
     */
    public void updateProjectVisibility(Project project, boolean visibility) {
        project.setVisibility(visibility);
    }

    /**
     * Updates project application dates
     */
    public void updateProjectDates(Project project, LocalDate openingDate, LocalDate closingDate) {
        if (openingDate != null) {
            project.setApplicationOpeningDate(openingDate);
        }
        if (closingDate != null) {
            project.setApplicationClosingDate(closingDate);
        }
    }

    /**
     * Updates project flat availability
     */
    public void updateFlatAvailability(Project project, int twoRoomFlats, int threeRoomFlats) {
        project.setTwoRoomFlatsAvailable(twoRoomFlats);
        project.setThreeRoomFlatsAvailable(threeRoomFlats);
    }

    /**
     * Gets a project by its name
     */
    public Project getProjectByName(String projectName) {
        for (Project project : projectRepository.getAll()) {
            if (project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }
    //method to see simply all projects
    public Project viewAvailableProjects(Applicant applicant) {
        return projectRepository.getByProjectName(applicant.getProject());
    }
    //method to see all filtered available projects
    public List<Project> getAvailableProjects(Applicant applicant) {
        List<Project> availableProjects = new ArrayList<>();

        // First, import the correct enum
        MaritalStatus marriedStatus = MaritalStatus.MARRIED;
        MaritalStatus singleStatus = MaritalStatus.SINGLE;

        for (Project project : projectRepository.getAll()) {
            // Check if project is visible and within application period
            if (project.isVisible() &&
                    project.getApplicationOpeningDate() != null &&
                    project.getApplicationClosingDate() != null &&
                    project.getApplicationOpeningDate().isBefore(LocalDate.now()) &&
                    project.getApplicationClosingDate().isAfter(LocalDate.now())) {

                boolean isEligible = false;

                // Check if 2-room flats are available and applicant is eligible
                if (project.getTwoRoomFlatsAvailable() > 0) {
                    // 2-room flats: 35+ and single OR any age and married
                    if ((applicant.getAge() >= 35 && applicant.getMaritalStatus() == singleStatus) ||
                            applicant.getMaritalStatus() == marriedStatus) {
                        isEligible = true;
                    }
                }

                // Check if 3-room flats are available and applicant is eligible
                if (project.getThreeRoomFlatsAvailable() > 0) {
                    // 3-room flats: only for married couples
                    if (applicant.getMaritalStatus() == marriedStatus) {
                        isEligible = true;
                    }
                }

                if (isEligible) {
                    availableProjects.add(project);
                }
            }
        }
        return availableProjects;
    }

    public List<Project> getProjectsByOfficer(String officerID) {
        List<Project> projects = new ArrayList<>();
        for (Project project : projectRepository.getAll()) {
            if (project.hasOfficer(officerID)) {
                projects.add(project);
            }
        }
        return projects;
    }

    public List<Project> getAvailableProjects() {
        List<Project> availableProjects = new ArrayList<>();
        for (Project project : projectRepository.getAll()) {
            if (project.getStatus() == ProjectStatus.AVAILABLE) {
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }

    public void addOfficerToProject(String projectID, String officerID) throws ModelNotFoundException {
        Project project = projectRepository.getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project not found");
        }

        Officer officer = officerRepository.getByID(officerID);
        if (officer == null) {
            throw new ModelNotFoundException("Officer not found");
        }

        project.addOfficer(officerID);
        projectRepository.update(project);

        officer.getProjectsInCharge().add(projectID);
        officerRepository.update(officer);
    }

    public void removeOfficerFromProject(String projectID, String officerID) throws ModelNotFoundException {
        Project project = projectRepository.getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project not found");
        }

        Officer officer = officerRepository.getByID(officerID);
        if (officer == null) {
            throw new ModelNotFoundException("Officer not found");
        }

        project.removeOfficer(officerID);
        projectRepository.update(project);

        officer.getProjectsInCharge().remove(projectID);
        officerRepository.update(officer);
    }

    /**
     * Gets all projects.
     */
    public List<Project> getAllProjects() {
        return projectRepository.getAll();
    }

    /**
     * Gets projects managed by a specific manager ID.
     */
    public List<Project> getProjectsByManagerID(String managerID) {
        //loop through all projects and find the ones with the same manager ID
        List<Project> projects = new ArrayList<>();
        for (Project project : projectRepository.getAll()) {
            if (project.getManagerInCharge().getID().equals(managerID)) {
                projects.add(project);
            }
        }
        return projects;
    }

    /**
     * Updates an existing project in the repository.
     */
    public void updateProject(Project project) throws ModelNotFoundException {
        projectRepository.update(project);
    }

    /**
     * Deletes a project by its ID.
     */
    public void deleteProject(String projectID) throws ModelNotFoundException {
        //Project project = projectRepository.getByID(projectID);
        projectRepository.remove(projectID);
    }

    /**
     * Finds projects based on a set of rules (predicates).
     */
    public List<Project> findProjectsByRules(Predicate<Project>... rules) {
        return projectRepository.findByRules(rules);
    }

    /**
     * Adds a project to the repository. 
     * Generally, createProject should be used, but this allows adding pre-constructed objects if needed.
     */
    public void addProject(Project project) throws ModelAlreadyExistsException {
        projectRepository.add(project);
    }

    /**
     * Gets a project by its ID.
     */
    public Project getProjectByID(String projectID) throws ModelNotFoundException {
        return projectRepository.getByID(projectID);
    }
}