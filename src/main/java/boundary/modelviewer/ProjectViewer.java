package boundary.modelviewer;

import controller.project.ProjectManager;
import model.project.Project;
import model.user.Applicant;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

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

    /**
     * Displays a list of projects in tabular format.
     *
     * @param projects the list of projects to display
     */
    public static void displayProjects(List<Project> projects) {
        // Print table header
        System.out.println("┌──────────┬───────────────────────┬──────────────┬──────────────┬─────────────────┬─────────────────┐");
        System.out.printf("│ %-8s │ %-21s │ %-12s │ %-12s │ %-15s │ %-15s │\n",
                "ID", "Title", "Open Date", "Close Date", "2-Room ($, Avail)", "3-Room ($, Avail)");
        System.out.println("├──────────┼───────────────────────┼──────────────┼──────────────┼─────────────────┼─────────────────┤");

        // Print each project
        for (Project project : projects) {
            System.out.printf("│ %-8s │ %-21s │ %-12s │ %-12s │ $%-6.2f, %-5d │ $%-6.2f, %-5d │\n",
                    project.getID(),
                    truncateString(project.getProjectTitle(), 21),
                    project.getApplicationOpeningDate().format(DATE_FORMATTER),
                    project.getApplicationClosingDate().format(DATE_FORMATTER),
                    project.getTwoRoomFlatPrice(),
                    project.getTwoRoomFlatAvailable(),
                    project.getThreeRoomFlatPrice(),
                    project.getThreeRoomFlatAvailable());
        }

        // Print table footer
        System.out.println("└──────────┴───────────────────────┴──────────────┴──────────────┴─────────────────┴─────────────────┘");
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