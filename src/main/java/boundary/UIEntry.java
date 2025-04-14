package boundary;

import boundary.welcome.Welcome;
import controller.account.AccountManager;
import controller.project.ProjectManager;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import repository.user.ApplicantRepository;
import utils.config.Location;

import java.io.File;

/**
 * This class is the entry point of the application.
 */
public class UIEntry {
    /**
     * Checks if the application has data files on disk.
     *
     * @return true if data files don't exist, false otherwise.
     */
    private static boolean firstStart() {
        File ApplicantFile = new File(Location.RESOURCE_LOCATION + "\\ApplicantList.csv");
        File ManagerFile = new File(Location.RESOURCE_LOCATION + "\\ManagerList.csv");
        File OfficerFile = new File(Location.RESOURCE_LOCATION + "\\OfficerList.csv");
        File ProjectFile = new File(Location.RESOURCE_LOCATION + "\\ProjectList.csv");

        // Check if files exist and are readable
        boolean filesExist = ApplicantFile.exists() && ApplicantFile.canRead() &&
                ManagerFile.exists() && ManagerFile.canRead() &&
                OfficerFile.exists() && OfficerFile.canRead() &&
                ProjectFile.exists() && ProjectFile.canRead();

        System.out.println("Checking for data files at: " + Location.RESOURCE_LOCATION);
        System.out.println("Files exist: " + filesExist);
        return !filesExist;
    }

    /**
     * Starts the application.
     */
    public static void start() {
        // Create data directory if it doesn't exist
        File dataDir = new File(Location.RESOURCE_LOCATION);
        if (!dataDir.exists()) {
            System.out.println("Creating data directory: " + dataDir.getAbsolutePath());
            boolean created = dataDir.mkdirs();
            System.out.println("Directory created: " + created);
        }

        // Initialize repositories
        ApplicantRepository applicantRepository = ApplicantRepository.getInstance();
        ManagerRepository managerRepository = ManagerRepository.getInstance();
        OfficerRepository officerRepository = OfficerRepository.getInstance();
        ProjectRepository projectRepository = ProjectRepository.getInstance();
        RequestRepository requestRepository = RequestRepository.getInstance();

        // Check if it's first start
        if (firstStart()) {
            System.out.println("First startup detected. Loading initial data...");
            // For first startup, load from predefined data sources or create defaults
            // This is where you'd generate or import initial data sets
            AccountManager.loadAllUsersFromCSV();
            ProjectManager.loadProjectsFromCSV();
        } else {
            System.out.println("Loading existing data...");
            try {
                // Load user data first
                AccountManager.loadAllUsersFromCSV();
                // Then load project data
                ProjectManager.loadProjectsFromCSV();
                System.out.println("Data loading complete.");
            } catch (Exception e) {
                System.err.println("Error loading data: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Start the application UI
        Welcome.welcome();
    }
}