package boundary;

import boundary.welcome.Welcome;
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

        return !ApplicantFile.exists() || !ProjectFile.exists() || !ManagerFile.exists() || !OfficerFile.exists();
    }

    /**
     * Starts the application.
     */
    public static void start() {
        // Initialize repositories
        ApplicantRepository applicantRepository = ApplicantRepository.getInstance();
        ManagerRepository managerRepository = ManagerRepository.getInstance();
        OfficerRepository officerRepository = OfficerRepository.getInstance();
        ProjectRepository projectRepository = ProjectRepository.getInstance();

        // Create data directory if it doesn't exist
        File dataDir = new File(Location.RESOURCE_LOCATION + "/data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Check if it's first start and load data from CSV
        if (firstStart()) {
            System.out.println("First startup detected. Loading data from CSV files...");

            try {
                // Step 1: Load user data first (like in unit tests);
                applicantRepository.load();
                managerRepository.load();
                officerRepository.load();
                // Step 2: Load project data
                projectRepository.load();

                System.out.println("Data loading complete.");
            } catch (Exception e) {
                System.err.println("Error loading data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Loading existing data...");
        }

        // Start the application UI
        Welcome.welcome();
    }
}