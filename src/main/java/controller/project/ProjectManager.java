package controller.project;

import controller.request.ApplicantManager;
import controller.request.ManagerManager;
import controller.request.OfficerManager;
import model.project.Project;
import model.project.RoomType;
import model.user.Applicant;
import model.user.MaritalStatus;
import repository.project.ProjectRepository;
import utils.config.Location;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;
import utils.iocontrol.CSVReader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages BTO projects, including creation, retrieval, updates, and deletion.
 * Handles project eligibility checks for applicants, flat allocation, and loading initial project data.
 * Interacts with {@link ProjectRepository}, {@link ApplicantManager}, {@link ManagerManager}, and {@link OfficerManager}.
 */
public class ProjectManager {
    /**
     * Creates a new project, automatically generating a unique project ID.
     * Checks for overlapping project timelines for the assigned manager before creation.
     *
     * @param projectTitle           The title of the project.
     * @param neighbourhood          The neighbourhood where the project is located.
     * @param applicationOpeningDate The date when applications open.
     * @param applicationClosingDate The date when applications close.
     * @param twoRoomFlatAvailable   The number of available 2-room flats.
     * @param threeRoomFlatAvailable The number of available 3-room flats.
     * @param twoRoomFlatPrice       The price of a 2-room flat.
     * @param threeRoomFlatPrice     The price of a 3-room flat.
     * @param managerNRIC            The NRIC of the manager responsible for the project.
     * @param officerIDs             A list of NRICs of officers assigned to the project.
     * @param visibility             Whether the project is visible to applicants.
     * @return The generated unique ID for the newly created project.
     * @throws ModelAlreadyExistsException If the manager has another project with overlapping application dates.
     */
    public static String createProject(String projectTitle, String neighbourhood,
                                       LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                                       Integer twoRoomFlatAvailable, Integer threeRoomFlatAvailable,
                                       Double twoRoomFlatPrice, Double threeRoomFlatPrice,
                                       String managerNRIC, List<String> officerIDs, Boolean visibility) throws ModelAlreadyExistsException {

        // Check for overlapping projects by the same manager
        List<Project> overlap = ProjectRepository.getInstance().findByRules(p ->
                p.getManagerNRIC().equals(managerNRIC) &&
                        !p.getApplicationClosingDate().isBefore(applicationOpeningDate) &&
                        !p.getApplicationOpeningDate().isAfter(applicationClosingDate)
        );

        if (!overlap.isEmpty()) {
            throw new ModelAlreadyExistsException("Manager has overlapping projects.");
        }

        // Generate new project ID and create project
        String projectID = ProjectRepository.getInstance().getNewProjectID();
        Project newProject = new Project(projectID, projectTitle, neighbourhood,
                applicationOpeningDate, applicationClosingDate,
                twoRoomFlatAvailable, threeRoomFlatAvailable,
                twoRoomFlatPrice, threeRoomFlatPrice,
                managerNRIC, officerIDs, visibility);

        ProjectRepository.getInstance().add(newProject);
        return projectID;
    }

    /**
     * Creates a new project with a specified project ID.
     * Checks for overlapping project timelines for the assigned manager before creation.
     *
     * @param projectID              The unique ID for the project.
     * @param projectTitle           The title of the project.
     * @param neighbourhood          The neighbourhood where the project is located.
     * @param applicationOpeningDate The date when applications open.
     * @param applicationClosingDate The date when applications close.
     * @param twoRoomFlatAvailable   The number of available 2-room flats.
     * @param threeRoomFlatAvailable The number of available 3-room flats.
     * @param twoRoomFlatPrice       The price of a 2-room flat.
     * @param threeRoomFlatPrice     The price of a 3-room flat.
     * @param managerNRIC            The NRIC of the manager responsible for the project.
     * @param officerIDs             A list of NRICs of officers assigned to the project.
     * @param visibility             Whether the project is visible to applicants.
     * @throws ModelAlreadyExistsException If a project with the same ID already exists or if the manager has overlapping projects.
     */
    public static void createProject(String projectID, String projectTitle, String neighbourhood,
                                     LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                                     Integer twoRoomFlatAvailable, Integer threeRoomFlatAvailable,
                                     Double twoRoomFlatPrice, Double threeRoomFlatPrice, String managerNRIC, List<String> officerIDs, Boolean visibility) throws ModelAlreadyExistsException {
        //check if the Manager has overlapping projects
        //check if the Manager has overlapping projects where the application closing date of any projct is after the application opening date of the new project
        // Check for overlapping projects by the same manager
        List<Project> overlap = ProjectRepository.getInstance().findByRules(p ->
                p.getManagerNRIC().equals(managerNRIC) &&
                        !p.getApplicationClosingDate().isBefore(applicationOpeningDate) &&
                        !p.getApplicationOpeningDate().isAfter(applicationClosingDate)
        );

        if (!overlap.isEmpty()) {
            throw new ModelAlreadyExistsException("Manager has overlapping projects.");
        }
        Project p1 = new Project(projectID, projectTitle, neighbourhood, applicationOpeningDate, applicationClosingDate, twoRoomFlatAvailable, threeRoomFlatAvailable, twoRoomFlatPrice, threeRoomFlatPrice, managerNRIC, officerIDs, visibility);
        ProjectRepository.getInstance().add(p1);
    }

    /**
     * Retrieves a list of all projects from the repository, regardless of visibility or dates.
     *
     * @return A list of all {@link Project} objects.
     */
    public static List<Project> viewAllProject() {
        return ProjectRepository.getInstance().getList();
    }

    /**
     * Retrieves a list of all projects that are currently marked as visible.
     *
     * @return A list of visible {@link Project} objects.
     */
    public static List<Project> viewVisibleProjects() {
        return ProjectRepository.getInstance().findByRules(p -> p.getVisibility() == Boolean.TRUE);
    }

    /**
     * Updates the details of an existing project.
     *
     * @param projectID              The ID of the project to update.
     * @param projectTitle           The new title.
     * @param neighbourhood          The new neighbourhood.
     * @param applicationOpeningDate The new application opening date.
     * @param applicationClosingDate The new application closing date.
     * @param twoRoomFlatAvailable   The new count of available 2-room flats.
     * @param threeRoomFlatAvailable The new count of available 3-room flats.
     * @param twoRoomFlatPrice       The new price for 2-room flats.
     * @param threeRoomFlatPrice     The new price for 3-room flats.
     * @param managerNRIC            The new manager's NRIC.
     * @param officerIDs             The new list of officer NRICs.
     * @param visibility             The new visibility status.
     * @throws ModelNotFoundException If the project with the specified ID is not found.
     * @throws ModelAlreadyExistsException Potentially thrown by the underlying repository update mechanism, though less common on update.
     */
    public static void updateProject(String projectID,
                                     String projectTitle,
                                     String neighbourhood,
                                     LocalDate applicationOpeningDate,
                                     LocalDate applicationClosingDate,
                                     Integer twoRoomFlatAvailable,
                                     Integer threeRoomFlatAvailable,
                                     Double twoRoomFlatPrice,
                                     Double threeRoomFlatPrice,
                                     String managerNRIC,
                                     List<String> officerIDs,
                                     Boolean visibility)
            throws ModelAlreadyExistsException, ModelNotFoundException {

        ProjectRepository repo = ProjectRepository.getInstance();
        Project project = repo.getByID(projectID);

        // update fields
        project.setProjectTitle(projectTitle);
        project.setNeighbourhood(neighbourhood);
        project.setApplicationOpeningDate(applicationOpeningDate);
        project.setApplicationClosingDate(applicationClosingDate);
        project.setTwoRoomFlatAvailable(twoRoomFlatAvailable);
        project.setThreeRoomFlatAvailable(threeRoomFlatAvailable);
        project.setTwoRoomFlatPrice(twoRoomFlatPrice);
        project.setThreeRoomFlatPrice(threeRoomFlatPrice);
        project.setManagerNRIC(managerNRIC);
        project.setOfficerIDs(officerIDs);
        project.setVisibility(visibility);

        // persist update
        repo.update(project);
    }

    /**
     * Retrieves a list of projects that are currently open for application and visible.
     * Does not filter based on applicant eligibility criteria (like marital status).
     * TODO: Enhance this method or create a new one to fully check applicant eligibility.
     *
     * @param applicantNRIC The NRIC of the applicant (currently unused in filtering logic but kept for potential future use).
     * @return A list of {@link Project} objects open for application.
     */
    public static List<Project> viewApplicantEligibleProjects(String applicantNRIC) {
        //get applicant details

        return ProjectRepository.getInstance().findByRules(p -> p.getVisibility() == Boolean.TRUE && p.getApplicationOpeningDate().isBefore(LocalDate.now()) && p.getApplicationClosingDate().isAfter(LocalDate.now()));
    }

    /**
     * Decrements the count of available flats of a specific type for a given project.
     * Used when a flat is allocated or booked.
     *
     * @param projectID The ID of the project.
     * @param flatType  The type of flat ({@link RoomType#TWO_ROOM_FLAT} or {@link RoomType#THREE_ROOM_FLAT}).
     * @throws ModelNotFoundException If the project with the specified ID is not found.
     */
    public static void removeFlat(String projectID, RoomType flatType) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (flatType == RoomType.TWO_ROOM_FLAT) {
            project.setTwoRoomFlatAvailable(project.getTwoRoomFlatAvailable() - 1);
        } else if (flatType == RoomType.THREE_ROOM_FLAT) {
            project.setThreeRoomFlatAvailable(project.getThreeRoomFlatAvailable() - 1);
        }
        ProjectRepository.getInstance().update(project);

    }

    /**
     * Increments the count of available flats of a specific type for a given project.
     * Used when a booking is rejected or withdrawn.
     *
     * @param projectID The ID of the project.
     * @param flatType  The type of flat ({@link RoomType#TWO_ROOM_FLAT} or {@link RoomType#THREE_ROOM_FLAT}).
     * @throws ModelNotFoundException If the project with the specified ID is not found.
     */
    public static void addFlat(String projectID, RoomType flatType) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (flatType == RoomType.TWO_ROOM_FLAT) {
            project.setTwoRoomFlatAvailable(project.getTwoRoomFlatAvailable() + 1);
        } else if (flatType == RoomType.THREE_ROOM_FLAT) {
            project.setThreeRoomFlatAvailable(project.getThreeRoomFlatAvailable() + 1);
        }
        ProjectRepository.getInstance().update(project);
    }

    /**
     * Retrieves a project by its unique ID.
     *
     * @param projectID The ID of the project to retrieve.
     * @return The {@link Project} object.
     * @throws ModelNotFoundException If no project with the specified ID is found.
     */
    public static Project getByID(String projectID) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project with ID " + projectID + " not found");
        }
        return project;
    }

    /**
     * Adds an officer to a project's list of assigned officers.
     * Checks if the officer is already assigned before adding.
     *
     * @param projectID The ID of the project.
     * @param officerID The NRIC of the officer to add.
     * @return true if the officer was successfully added, false if the officer was already assigned or if project/officer ID is null.
     * @throws ModelNotFoundException If the project with the specified ID is not found.
     */
    public static Boolean addOfficerToProject(String projectID, String officerID) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (project == null || officerID == null) {
            return false;
        }
        Boolean status = project.addOfficerID(officerID);
        if (status) {
            ProjectRepository.getInstance().update(project);
        }
        return status;

    }

    /**
     * Retrieves a list of projects available for application by a specific applicant.
     * Filters based on project visibility, application dates, applicant's marital status (for 2-room flats),
     * and ensures the applicant is not the manager or an officer for the project.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @return A list of {@link Project} objects the applicant is eligible to apply for.
     * @throws ModelNotFoundException If the applicant with the specified NRIC is not found.
     */
    public static List<Project> getAvailableProject(String applicantNRIC) throws ModelNotFoundException {
        //get all projects
        List<Project> allProjects = ProjectRepository.getInstance().getList();
        Applicant applicant = ApplicantManager.getByNRIC(applicantNRIC);
        //if applicant is SINGLE and Under 35, return empty list
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && applicant.getAge() < 35) {
            return new ArrayList<Project>();
        }
        //filter projects that are available
        List<Project> availableProjects = allProjects.stream()
                .filter(project -> project.getVisibility() == Boolean.TRUE && project.getApplicationOpeningDate().isBefore(LocalDate.now()) && project.getApplicationClosingDate().isAfter(LocalDate.now()))
                .toList();
        //check if the applicantNRIC is equal to the project managerNRIC or in the officerID list, return empty list if yes
        availableProjects = availableProjects.stream()
                .filter(project -> !project.getManagerNRIC().equals(applicantNRIC) && !project.getOfficerIDs().contains(applicantNRIC))
                .toList();
        //if project has no 2 room flat available, remove from list if applicant not married
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
            availableProjects = availableProjects.stream()
                    .filter(project -> project.getTwoRoomFlatAvailable() > 0)
                    .toList();
        }
        return availableProjects;
    }

    /**
     * Checks if the project repository is empty.
     *
     * @return true if no projects are stored, false otherwise.
     */
    public static boolean repositoryIsEmpty() {
        return ProjectRepository.getInstance().isEmpty();
    }

    /**
     * Loads initial project data from a CSV file (ProjectList.csv).
     * Parses project details, dates, manager, and officers, then creates project objects.
     * Handles potential parsing errors.
     */
    public static void loadProjects() {
        List<List<String>> projects = CSVReader.read(Location.RESOURCE_LOCATION + "/ProjectList.csv", true);
        for (List<String> project : projects) {
            try {
                // Extract project information from CSV columns
                String projectName = project.get(0);
                String projectID = "P0";
                String neighbourhood = project.get(1);
                // Parse room information
                int twoRoomUnits = Integer.parseInt(project.get(3));
                double twoRoomPrice = Double.parseDouble(project.get(4));
                int threeRoomUnits = Integer.parseInt(project.get(6));
                double threeRoomPrice = Double.parseDouble(project.get(7));

                // Parse dates using DD/MM/YYYY format
                String[] openDateParts = project.get(8).split("/");
                LocalDate openingDate = LocalDate.of(
                        Integer.parseInt(openDateParts[2]),
                        Integer.parseInt(openDateParts[1]),
                        Integer.parseInt(openDateParts[0])
                );

                String[] closeDateParts = project.get(9).split("/");
                LocalDate closingDate = LocalDate.of(
                        Integer.parseInt(closeDateParts[2]),
                        Integer.parseInt(closeDateParts[1]),
                        Integer.parseInt(closeDateParts[0])
                );

                // Get manager NRIC
                String managerName = project.get(10);
                String managerNRIC = ManagerManager.getIDByManagerName(managerName);

                // Parse officers
                List<String> officerIDs = new ArrayList<>();
                if (!project.get(12).isEmpty()) {
                    String[] officerNames = project.get(12).split(",");
                    for (String officerName : officerNames) {
                        String officerNRIC = OfficerManager.getIDByOfficerName(officerName.trim());
                        if (officerNRIC != null) {
                            officerIDs.add(officerNRIC);
                        }
                    }
                }

                // Create the project with default visibility set to true
                createProject(
                        projectID,
                        projectName,
                        neighbourhood,
                        openingDate,
                        closingDate,
                        twoRoomUnits,
                        threeRoomUnits,
                        twoRoomPrice,
                        threeRoomPrice,
                        managerNRIC,
                        officerIDs,
                        true
                );

                //System.out.println("Project " + projectName + " loaded successfully");

            } catch (Exception e) {
                System.out.println("Error loading project: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves a list of all projects currently in the repository.
     * Alias for {@link #viewAllProject()}.
     *
     * @return A list of all {@link Project} objects.
     */
    public static List<Project> getAllProjects() {
        return ProjectRepository.getInstance().getList();
    }

    /**
     * Deletes a project from the repository.
     *
     * @param projectID The ID of the project to delete.
     * @throws ModelNotFoundException If the project with the specified ID is not found.
     */
    public static void deleteProject(String projectID) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project with ID " + projectID + " not found");
        }
        ProjectRepository.getInstance().remove(projectID);
    }
}
