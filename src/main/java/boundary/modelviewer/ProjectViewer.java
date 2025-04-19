package boundary.modelviewer;

import controller.project.ProjectManager;
import model.project.Project;
import model.user.Applicant;
import model.user.Manager;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class provides methods to display project information in a formatted way.
 */
public class ProjectViewer {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Displays available projects for an applicant.
     *
     * @param applicant the applicant viewing the projects
     * @throws PageBackException if the user chooses to go back
     * @throws ModelNotFoundException if the applicant is not found
     */
    public static void viewAvailableProjects(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("Available Projects for " + applicant.getName());

        List<Project> projects = ProjectManager.getAvailableProject(applicant.getNRIC());

        if (projects.isEmpty()) {
            System.out.println("No available projects found.");
        } else {
            displayProjects(projects);
        }

        System.out.println("\nPress Enter to go back to main menu...");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }
    public static void viewAvailableProjects(Applicant applicant, Integer filterNumber) throws PageBackException, ModelNotFoundException {
        // 1 is filter 2 room flat, 2 is filter 3 room flat
        ChangePage.changePage();
        System.out.println("Available Projects for " + applicant.getName());
        if(filterNumber == 0) {
            System.out.println("No filter applied.");
        } else if(filterNumber == 1) {
            System.out.println("Filter: 2 Room Flat");
        } else if(filterNumber == 2) {
            System.out.println("Filter: 3 Room Flat");
        } else {
            System.out.println("Invalid filter number. No filter applied.");

        }
        List<Project> projects = ProjectManager.getAvailableProject(applicant.getNRIC());
        List<Project> filteredProjects = new ArrayList<>();
        if (projects.isEmpty()) {
            System.out.println("No available projects found.");
        } else if(filterNumber != 0) {
            // Filter projects based on the filter number
            for (Project project : projects) {
                if(filterNumber == 1 && project.getTwoRoomFlatAvailable() > 0) {
                    filteredProjects.add(project);
                } else if (filterNumber == 2 && project.getThreeRoomFlatAvailable() > 0) {
                    filteredProjects.add(project);
                }
            }
            //System.out.println("Check if the project is there" + projects);
            //if filter is 0 sort the project by the ProjectNames
            if (projects.isEmpty()) {
                System.out.println("No available projects found.");
            } else {
                displayProjects(filteredProjects);
            }
        } else {
            // Sort projects by project title
            //get the project title and sort it
            List<String> projectTitles = new ArrayList<>();
            for (Project project : projects) {
                projectTitles.add(project.getProjectTitle());
            }
            Collections.sort(projectTitles);
            List<Project> sortedProjects = new ArrayList<>();
            for (String projectTitle : projectTitles) {
                for (Project project : projects) {
                    if (project.getProjectTitle().equals(projectTitle)) {
                        sortedProjects.add(project);
                    }
                }
            }
            displayProjects(sortedProjects);
        }

        System.out.println("\nPress Enter to go back to main menu...");
        new Scanner(System.in).nextLine();
        throw new PageBackException();
    }


    public static void viewAllProjects(Integer filterNumber) throws PageBackException, ModelNotFoundException {
        // 1 is filter 2 room flat, 2 is filter 3 room flat
        ChangePage.changePage();
        if(filterNumber == 0) {
            System.out.println("No filter applied.");
        } else if(filterNumber == 1) {
            System.out.println("Filter: 2 Room Flat");
        } else if(filterNumber == 2) {
            System.out.println("Filter: 3 Room Flat");
        } else {
            System.out.println("Invalid filter number. No filter applied.");

        }
        List<Project> projects = ProjectManager.getAllProjects();
        List<Project> filteredProjects = new ArrayList<>();
        if (projects.isEmpty()) {
            System.out.println("No available projects found.");
        } else if(filterNumber != 0) {
            // Filter projects based on the filter number
            for (Project project : projects) {
                if(filterNumber == 1 && project.getTwoRoomFlatAvailable() > 0) {
                    filteredProjects.add(project);
                } else if (filterNumber == 2 && project.getThreeRoomFlatAvailable() > 0) {
                    filteredProjects.add(project);
                }
            }
            //System.out.println("Check if the project is there" + projects);
            //if filter is 0 sort the project by the ProjectNames
            if (projects.isEmpty()) {
                System.out.println("No available projects found.");
            } else {
                displayProjects(filteredProjects);
            }
        } else {
            // Sort projects by project title
            //get the project title and sort it
            List<String> projectTitles = new ArrayList<>();
            for (Project project : projects) {
                projectTitles.add(project.getProjectTitle());
            }
            Collections.sort(projectTitles);
            List<Project> sortedProjects = new ArrayList<>();
            for (String projectTitle : projectTitles) {
                for (Project project : projects) {
                    if (project.getProjectTitle().equals(projectTitle)) {
                        sortedProjects.add(project);
                    }
                }
            }
            displayProjects(sortedProjects);
        }


    }
    public static void displayProjects(List<Project> projects) {
        // Determine the maximum width for each column dynamically
        if(projects.isEmpty()) {
            System.out.println("No projects to display.");
            return;
        }
        int idWidth = Math.max(8, projects.stream().mapToInt(p -> p.getID().length()).max().orElse(8));
        int titleWidth = Math.max(28, projects.stream().mapToInt(p -> p.getProjectTitle().length()).max().orElse(28));
        int dateWidth = 12; // Fixed width for dates
        int roomWidth = Math.max(26, projects.stream()
                .mapToInt(p -> String.format("$%.2f, %d", p.getTwoRoomFlatPrice(), p.getTwoRoomFlatAvailable()).length())
                .max().orElse(26));
        int officerWidth = 95; // Width for officer IDs (9 chars per ID + commas)

        // Print table header
        String headerFormat = String.format("┌─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┐",
                idWidth, titleWidth, dateWidth, dateWidth, roomWidth, roomWidth, officerWidth);
        System.out.println(String.format(headerFormat, "", "", "", "", "", "", "").replace(" ", "─"));

        String columnFormat = String.format("│ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │",
                idWidth, titleWidth, dateWidth, dateWidth, roomWidth, roomWidth, officerWidth);
        System.out.printf(columnFormat + "\n",
                "ID", "Title", "Open Date", "Close Date", "2-Room ($, Avail)", "3-Room ($, Avail)", "Officers");

        System.out.println(String.format(headerFormat, "", "", "", "", "", "", "").replace(" ", "─"));

        // Print each project
        for (Project project : projects) {
            // Format all officer IDs without truncation
            String officersDisplay = formatAllOfficerIDs(project.getOfficerIDs());

            System.out.printf(columnFormat + "\n",
                    project.getID(),
                    project.getProjectTitle(),
                    project.getApplicationOpeningDate().format(DATE_FORMATTER),
                    project.getApplicationClosingDate().format(DATE_FORMATTER),
                    String.format("$%.2f, %d", project.getTwoRoomFlatPrice(), project.getTwoRoomFlatAvailable()),
                    String.format("$%.2f, %d", project.getThreeRoomFlatPrice(), project.getThreeRoomFlatAvailable()),
                    officersDisplay);
        }

        // Print table footer
        System.out.println(String.format(headerFormat, "", "", "", "", "", "", "").replace(" ", "─"));
    }

    /**
     * Format all officer IDs without truncation
     * @param officerIDs List of officer IDs
     * @return Formatted string of all officer IDs
     */
    private static String formatAllOfficerIDs(List<String> officerIDs) {
        if (officerIDs == null || officerIDs.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < officerIDs.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(officerIDs.get(i));
        }

        return sb.toString();
    }

    /**
     * Truncates a string to a specified length, adding ellipsis if needed.
     *
     * @param str the string to truncate
     * @param length the maximum length
     * @return the truncated string
     */
    private static String truncateString(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length - 3) + "...";
    }
}