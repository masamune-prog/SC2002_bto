package boundary.modelviewer;

import controller.project.ProjectManager;
import model.project.Project;
import model.project.ProjectStatus;
import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.MaritalStatus;
import repository.project.ProjectRepository;
import repository.user.ManagerRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.ChangePage;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Displays the project details.
 */
public class ProjectViewer {
    private static final ProjectManager projectManager = new ProjectManager();

    /**
     * Displays a menu to select project status and returns the selected value.
     *
     * @return the selected ProjectStatus enum value
     * @throws PageBackException if the user chooses to go back
     */
    public static ProjectStatus getProjectStatus() throws PageBackException {
        System.out.println("\t1. Available");
        System.out.println("\t2. Unavailable");
        System.out.print("Please enter your choice: ");
        int option = IntGetter.readInt();
        return switch (option) {
            case 1 -> ProjectStatus.AVAILABLE;
            case 2 -> ProjectStatus.UNAVAILABLE;
            default -> {
                System.out.println("Please enter a number between 1-2.");
                System.out.println("Press Enter to retry or enter 0 and press Enter to exit.");
                String input = new Scanner(System.in).nextLine().trim();
                if (input.equals("0")) {
                    throw new PageBackException();
                } else {
                    yield getProjectStatus();
                }
            }
        };
    }

    /**
     * Displays project details by project ID.
     *
     * @throws PageBackException if the user chooses to go back
     */
    public static void generateDetailsByProjectID() throws PageBackException {
        System.out.println("Please Enter the ProjectID to search: ");
        String projectId = new Scanner(System.in).nextLine();
        Project project = ProjectRepository.getInstance().getProjectByID(projectId);
        ModelViewer.displaySingleDisplayable(project);
        System.out.println("Enter <Enter> to continue");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }

    /**
     * Displays projects by manager ID.
     *
     * @throws PageBackException if the user chooses to go back
     */
    public static void generateDetailsByManagerID() throws PageBackException {
        System.out.println("Please enter the ManagerID to search: ");
        String managerId = new Scanner(System.in).nextLine();
        if (!ManagerRepository.getInstance().contains(managerId)) {
            System.out.println("Manager Not Found.");
            System.out.println("Press enter to retry, or enter [b] to go back");
            String input = new Scanner(System.in).nextLine().trim();
            if (input.equalsIgnoreCase("b")) {
                throw new PageBackException();
            } else {
                generateDetailsByManagerID();
                return;
            }
        }
        List<Project> projectList = ProjectRepository.getInstance().findByRules(p ->
                p.getManagerInCharge() != null &&
                        p.getManagerInCharge().getID().equalsIgnoreCase(managerId));
        ModelViewer.displayListOfDisplayable(projectList);
        System.out.println("Enter <Enter> to continue");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }




    /**
     * Provides a menu to search for project details.
     *
     * @throws PageBackException if the user chooses to go back
     */
    public static void generateProjectDetails() throws PageBackException {
        ChangePage.changePage();
        System.out.println(BoundaryStrings.separator);
        System.out.println("Please select the way to search:");
        System.out.println("\t 1. By ProjectID");
        System.out.println("\t 2. By ManagerID");
        System.out.println("\t 0. Go Back");
        System.out.println(BoundaryStrings.separator);
        System.out.print("Please enter your choice: ");
        int choice = IntGetter.readInt();
        if (choice == 0) {
            throw new PageBackException();
        }
        try {
            switch (choice) {
                case 1 -> generateDetailsByProjectID();
                case 2 -> generateDetailsByManagerID();
                default -> {
                    System.out.println("Invalid choice. Please enter again. ");
                    new Scanner(System.in).nextLine();
                    throw new PageBackException();
                }
            }
        } catch (PageBackException e) {
            generateProjectDetails();
        }
    }




    /**
     * Displays available projects for an applicant.
     *
     * @param applicant the applicant to display projects for
     * @throws PageBackException if the user chooses to go back
     */
    public static void viewAvailableProjectList(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        if (applicant.getStatus() != ApplicantStatus.UNREGISTERED) {
            System.out.println("You are not allowed to view available projects as you are already registered to a project.");
        } else {
            System.out.println("View Available Project List");
            System.out.println("Your eligibility criteria:");
            System.out.println("- Age: " + applicant.getAge());
            System.out.println("- Marital Status: " + applicant.getMaritalStatus());
            System.out.println("\nAvailable Projects:");
            
            List<Project> availableProjects = projectManager.getAvailableProjects(applicant);
            if (availableProjects.isEmpty()) {
                System.out.println("No projects are currently available for your eligibility criteria.");
            } else {
                for (Project project : availableProjects) {
                    System.out.println("\nProject Details:");
                    System.out.println("---------------");
                    System.out.println("Project Name: " + project.getProjectName());
                    System.out.println("Neighborhood: " + project.getNeighborhood());
                    System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
                    
                    // Display flat availability
                    System.out.println("\nFlat Availability:");
                    if (project.getTwoRoomFlatsAvailable() > 0) {
                        System.out.println("- 2-Room Flats: " + project.getTwoRoomFlatsAvailable() + " available");
                        System.out.println("  Price: $" + project.getTwoRoomFlatsPrice());
                    }
                    if (project.getThreeRoomFlatsAvailable() > 0) {
                        System.out.println("- 3-Room Flats: " + project.getThreeRoomFlatsAvailable() + " available");
                        System.out.println("  Price: $" + project.getThreeRoomFlatsPrice());
                    }
                    
                    // Display eligibility information
                    System.out.println("\nEligibility for this project:");
                    if (project.getTwoRoomFlatsAvailable() > 0) {
                        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
                            System.out.println("- Eligible for 2-Room Flats: Yes (Married)");
                        } else if (applicant.getAge() >= 35) {
                            System.out.println("- Eligible for 2-Room Flats: Yes (Age 35+)");
                        } else {
                            System.out.println("- Eligible for 2-Room Flats: No (Must be married or 35+)");
                        }
                    }
                    if (project.getThreeRoomFlatsAvailable() > 0) {
                        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
                            System.out.println("- Eligible for 3-Room Flats: Yes (Married)");
                        } else {
                            System.out.println("- Eligible for 3-Room Flats: No (Must be married)");
                        }
                    }
                    System.out.println("---------------");
                }
            }
        }
        System.out.println("\nPress Enter to go back.");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }

    /**
     * Displays all projects.
     *
     * @throws PageBackException if the user chooses to go back
     */
    public static void viewAllProject() throws PageBackException {
        ChangePage.changePage();
        System.out.println("View All Project List");
        List<Project> allProjects = ProjectRepository.getInstance().getAll();
        ModelViewer.displayListOfDisplayable(allProjects);
        System.out.println("Press Enter to go back.");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }

    /**
     * Displays the project for a specific applicant.
     *
     * @param applicant the applicant
     * @throws PageBackException if the user chooses to go back
     */
    public static void viewApplicantProject(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        System.out.println("View Applicant Project");
        Project project = projectManager.viewAvailableProjects(applicant);
        if (project == null) {
            System.out.println("Applicant has no project yet.");
        } else {
            ModelViewer.displaySingleDisplayable(project);
        }
        System.out.println("Press Enter to go back.");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }
}