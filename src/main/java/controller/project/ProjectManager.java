package controller.project;

import model.project.Project;
import model.user.Applicant;
import model.user.Manager;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.user.ManagerRepository;
import model.user.MaritalStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages project creation and modification operations
 */
public class ProjectManager {
    private final ProjectRepository projectRepository;
    private final ManagerRepository managerRepository;

    /**
     * Constructs a ProjectManager with default repositories
     */
    public ProjectManager() {
        this.projectRepository = ProjectRepository.getInstance();
        this.managerRepository = ManagerRepository.getInstance();
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



        // Add to repository
        projectRepository.getAll().add(project);

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
}