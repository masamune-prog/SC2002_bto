package boundary.modelviewer;

import controller.project.ProjectManager;
import model.project.Project;
import model.project.ProjectStatus;
import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.MaritalStatus;
import model.user.Officer;
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
     */
    public static void generateDetailsByProjectID() {
        try {
            System.out.println("Please enter the ProjectID to search: ");
            String projectId = new Scanner(System.in).nextLine();
            Project project = null;
            try {
                project = projectManager.getProjectByID(projectId);
            } catch (ModelNotFoundException e) {
                System.out.println("Project Not Found.");
                System.out.println("Press Enter to retry or enter [b] to go back.");
                String input = new Scanner(System.in).nextLine().trim();
                if (input.equalsIgnoreCase("b")) {
                    return; // Return instead of throwing exception
                } else {
                    generateDetailsByProjectID();
                    return;
                }
            }
            if (project != null) {
                ModelViewer.displaySingleDisplayable(project);
            }
            System.out.println("Enter <Enter> to continue");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
        }
    }

    /**
     * Displays projects by manager ID.
     */
    public static void generateDetailsByManagerID() {
        try {
            System.out.println("Please enter the ManagerID to search: ");
            String managerId = new Scanner(System.in).nextLine();
            if (!ManagerRepository.getInstance().contains(managerId)) {
                System.out.println("Manager Not Found.");
                System.out.println("Press enter to retry, or enter [b] to go back");
                String input = new Scanner(System.in).nextLine().trim();
                if (input.equalsIgnoreCase("b")) {
                    return; // Return instead of throwing exception
                } else {
                    generateDetailsByManagerID();
                    return;
                }
            }
            List<Project> projectList = projectManager.getProjectsByManagerID(managerId);
            if (projectList.isEmpty()) {
                System.out.println("No projects found for this manager.");
            } else {
                ModelViewer.displayListOfDisplayable(projectList);
            }
            System.out.println("Enter <Enter> to continue");
            new Scanner(System.in).nextLine();
            return; // Return instead of throwing exception
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
        }
    }

    /**
     * Provides a menu to search for project details.
     */
    public static void generateProjectDetails() {
        try {
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
                return; // Return instead of throwing exception
            }
            
            switch (choice) {
                case 1 -> generateDetailsByProjectID();
                case 2 -> generateDetailsByManagerID();
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    System.out.println("Press Enter to continue...");
                    new Scanner(System.in).nextLine();
                    generateProjectDetails();
                }
            }
        } catch (Exception e) {
            // Handle any exceptions, including PageBackException from sub-methods
            System.out.println("Returning to previous menu...");
        }
    }

    /**
     * Displays available projects for an applicant.
     *
     * @param applicant the applicant to display projects for
     */
    public static void viewAvailableProjectList(Applicant applicant) {
        try {
            ChangePage.changePage();
            if (applicant.getStatus() != ApplicantStatus.UNREGISTERED) {
                System.out.println("You are not allowed to view other projects as you are already registered to a project.");
                // show the project that the applicant is registered to
                Project project = projectManager.getProjectByName(applicant.getProject());
                if (project != null) {
                    System.out.println("You are registered to the following project:");
                    System.out.println("Project Name: " + project.getProjectName());
                    System.out.println("Neighborhood: " + project.getNeighborhood());
                    System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
                } else {
                    System.out.println("No project found for this applicant.");
                }

            } else {
                System.out.println("View Available Project List");
                System.out.println("Your eligibility criteria:");
                System.out.println("- Age: " + applicant.getAge());
                System.out.println("- Marital Status: " + applicant.getMaritalStatus());
                System.out.println("\nAvailable Projects:");

                List<Project> availableProjects = projectManager.getAvailableProjects(applicant);

                // Filter out projects where applicant is an officer
                List<Project> filteredProjects = availableProjects.stream()
                        .filter(project -> {
                            // Check if applicant is not in the assigned officers
                            boolean isOfficer = false;

                            // Check if applicant is manager in charge
                            if (project.getManagerInCharge() != null &&
                                    Objects.equals(project.getManagerInCharge().getID(), applicant.getID())) {
                                isOfficer = true;
                            }

                            // Check if applicant is in assigned officers list
                            if (!isOfficer && project.getAssignedOfficers() != null) {
                                for (Officer officer : project.getAssignedOfficers()) {
                                    if (Objects.equals(officer.getID(), applicant.getID())) {
                                        isOfficer = true;
                                        break;
                                    }
                                }
                            }

                            return !isOfficer;
                        })
                        .toList();

                if (filteredProjects.isEmpty()) {
                    System.out.println("No projects are currently available for your eligibility criteria.");
                } else {
                    for (Project project : filteredProjects) {
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
            return;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
        }
    }

    /**
     * Displays all projects.
     */
    public static void viewAllProject() {
        try {
            ChangePage.changePage();
            System.out.println("View All Projects");
            List<Project> allProjects = projectManager.getAllProjects();
            if (allProjects.isEmpty()) {
                System.out.println("No projects found.");
            } else {
                ModelViewer.displayListOfDisplayable(allProjects);
            }
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
            return; // Return instead of throwing exception
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
        }
    }

    /**
     * Displays the project for a specific applicant.
     *
     * @param applicant the applicant
     */
    public static void viewApplicantProject(Applicant applicant) {
        try {
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
            return; // Return instead of throwing exception
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("Press Enter to go back.");
            new Scanner(System.in).nextLine();
        }
    }
}