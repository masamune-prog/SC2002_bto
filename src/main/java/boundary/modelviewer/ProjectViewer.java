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
        // Determine the maximum width for each column dynamically
        int idWidth = Math.max(8, projects.stream().mapToInt(p -> p.getID().length()).max().orElse(8));
        int titleWidth = Math.max(28, projects.stream().mapToInt(p -> p.getProjectTitle().length()).max().orElse(28));
        int dateWidth = 12; // Fixed width for dates
        int roomWidth = Math.max(26, projects.stream()
                .mapToInt(p -> String.format("$%.2f, %d", p.getTwoRoomFlatPrice(), p.getTwoRoomFlatAvailable()).length())
                .max().orElse(26));

        // Print table header
        String headerFormat = String.format("┌─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┬─%%-%ds─┐",
                idWidth, titleWidth, dateWidth, dateWidth, roomWidth, roomWidth);
        System.out.println(String.format(headerFormat, "", "", "", "", "", "").replace(" ", "─"));

        String columnFormat = String.format("│ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │ %%-%ds │",
                idWidth, titleWidth, dateWidth, dateWidth, roomWidth, roomWidth);
        System.out.printf(columnFormat + "\n",
                "ID", "Title", "Open Date", "Close Date", "2-Room ($, Avail)", "3-Room ($, Avail)");

        System.out.println(String.format(headerFormat, "", "", "", "", "", "").replace(" ", "─"));

        // Print each project
        for (Project project : projects) {
            System.out.printf(columnFormat + "\n",
                    project.getID(),
                    project.getProjectTitle(),
                    project.getApplicationOpeningDate().format(DATE_FORMATTER),
                    project.getApplicationClosingDate().format(DATE_FORMATTER),
                    String.format("$%.2f, %d", project.getTwoRoomFlatPrice(), project.getTwoRoomFlatAvailable()),
                    String.format("$%.2f, %d", project.getThreeRoomFlatPrice(), project.getThreeRoomFlatAvailable()));
        }

        // Print table footer
        System.out.println(String.format(headerFormat, "", "", "", "", "", "").replace(" ", "─"));
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