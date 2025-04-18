package boundary;

import boundary.welcome.Welcome;
import controller.account.AccountManager;
import controller.project.ProjectManager;

/**
 * This class is the entry point of the application.
 */
public class UIEntry {
    /**
     * Checks if the application is being run for the first time.
     *
     * @return true if the application is being run for the first time, false otherwise.
     */
    private static boolean firstStart() {
        return AccountManager.repositoryIsEmpty() && ProjectManager.repositoryIsEmpty();
    }

    /**
     * Starts the application.
     * If the application is being run for the afirst time, it loads the default users and projects.
     * Then it displays the welcome page.
     */
    public static void start() {
        if (firstStart()) {
            AccountManager.loadUsers();
            ProjectManager.loadProjects();
        }
        Welcome.welcome();
    }
}
