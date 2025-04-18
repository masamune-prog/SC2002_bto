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

public class ProjectManager {
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
    public static List<Project> viewAllProject() {
        return ProjectRepository.getInstance().getList();
    }
    public static List<Project> viewVisibleProjects() {
        return ProjectRepository.getInstance().findByRules(p -> p.getVisibility() == Boolean.TRUE);
    }
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
    //TODO: implement the method to view all projects that are open for application for the applicant
    public static List<Project> viewApplicantEligibleProjects(String applicantNRIC) {
        //get applicant details

        return ProjectRepository.getInstance().findByRules(p -> p.getVisibility() == Boolean.TRUE && p.getApplicationOpeningDate().isBefore(LocalDate.now()) && p.getApplicationClosingDate().isAfter(LocalDate.now()));
    }
    public static void removeFlat(String projectID, RoomType flatType) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (flatType == RoomType.TWO_ROOM_FLAT) {
            project.setTwoRoomFlatAvailable(project.getTwoRoomFlatAvailable() - 1);
        } else if (flatType == RoomType.THREE_ROOM_FLAT) {
            project.setThreeRoomFlatAvailable(project.getThreeRoomFlatAvailable() - 1);
        }
        ProjectRepository.getInstance().update(project);

    }
    public static void addFlat(String projectID, RoomType flatType) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (flatType == RoomType.TWO_ROOM_FLAT) {
            project.setTwoRoomFlatAvailable(project.getTwoRoomFlatAvailable() + 1);
        } else if (flatType == RoomType.THREE_ROOM_FLAT) {
            project.setThreeRoomFlatAvailable(project.getThreeRoomFlatAvailable() + 1);
        }
        ProjectRepository.getInstance().update(project);
    }
    public static Project getByID(String projectID) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project with ID " + projectID + " not found");
        }
        return project;
    }
    //remember to check outcome when running
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
    public static List<Project> getAvailableProject(String applicantNRIC) throws ModelNotFoundException {
        //get all projects
        List<Project> allProjects = ProjectRepository.getInstance().getList();
        Applicant applicant = ApplicantManager.getByNRIC(applicantNRIC);
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

    public static boolean repositoryIsEmpty() {
        return ProjectRepository.getInstance().isEmpty();
    }
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
    public static List<Project> getAllProjects() {
        return ProjectRepository.getInstance().getList();
    }
    public static void deleteProject(String projectID) throws ModelNotFoundException {
        Project project = ProjectRepository.getInstance().getByID(projectID);
        if (project == null) {
            throw new ModelNotFoundException("Project with ID " + projectID + " not found");
        }
        ProjectRepository.getInstance().remove(projectID);
    }


}
