import model.user.Manager;
import repository.user.ManagerRepository;
import utils.exception.ModelAlreadyExistsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class tests the persistence and loading mechanism of Manager objects.
 * It simulates saving data to text files and then loading it back.
 */
public class ManagerSaveTest {
    /**
     * The array of managers.
     */
    private static Manager[] managers;

    /**
     * This method is used to set up the test managers.
     */
    public static void setUp() {
        managers = new Manager[3];
        // Using the full constructor for clarity
        managers[0] = new Manager(
                "M1234567A", // managerNRIC
                "hashed_password_1", // hashedPassword (placeholder)
                "Alice Wonderland", // managerName
                new ArrayList<>(Arrays.asList("P101", "P102")) // projectIDsInCharge
        );
        managers[1] = new Manager(
                "M7654321B", // managerNRIC
                "hashed_password_2", // hashedPassword (placeholder)
                "Bob The Builder", // managerName
                new ArrayList<>() // Empty project list
        );
        managers[2] = new Manager(
                "M9876543C", // managerNRIC
                "hashed_password_3", // hashedPassword (placeholder)
                "Charlie Chaplin", // managerName
                new ArrayList<>(Arrays.asList("P201")) // projectIDsInCharge
        );
    }

    /**
     * This method tests saving managers to a file and then loading them back.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            // Phase 1: Setup and save managers to repository
            setUp();
            ManagerRepository repository = ManagerRepository.getInstance();
            System.out.println("=== PHASE 1: SAVING DATA ===");
            System.out.println("Clearing repository...");
            repository.clear();
            System.out.println("Repository cleared.");

            System.out.println("\nAdding managers...");
            for (Manager manager : managers) {
                repository.add(manager);
                System.out.println("Added: " + manager.getName() + " (" + manager.getID() + ")");
            }
            System.out.println("All managers added successfully.");
            printAllManagers(repository);

            // Phase 2: Simulate application restart by creating a new repository instance
            // In a real scenario, this would be a new JVM instance loading data from files
            System.out.println("\n=== PHASE 2: SIMULATING APPLICATION RESTART ===");
            System.out.println("Creating new repository instance to simulate application restart...");

            // Create a new repository instance (forcing a reload from file)
            ManagerRepository newRepository = new ManagerRepository();

            System.out.println("New repository created. Loading data from file...");

            // Phase 3: Verify loaded data
            System.out.println("\n=== PHASE 3: VERIFYING LOADED DATA ===");
            List<Manager> loadedManagers = newRepository.getAll();
            System.out.println("Number of managers loaded: " + loadedManagers.size());

            if (loadedManagers.size() != managers.length) {
                System.out.println("ERROR: Expected " + managers.length + " managers, but found " + loadedManagers.size());
            } else {
                System.out.println("Manager count matches expected value.");
            }

            printAllManagers(newRepository);

            // Verify each manager's data
            verifyManagers(managers, loadedManagers);

            System.out.println("\nTest completed successfully!");

        } catch (ModelAlreadyExistsException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Print all managers in the repository.
     *
     * @param repository The manager repository
     */
    private static void printAllManagers(ManagerRepository repository) {
        System.out.println("\nManagers in repository:");
        List<Manager> allManagers = repository.getAll();
        for (Manager m : allManagers) {
            System.out.println("- " + m.getID() + ": " + m.getName() +
                    ", Projects: " + m.getProjectIDsInCharge());
        }
    }

    /**
     * Verify that loaded managers match the original managers.
     *
     * @param originalManagers The original managers array
     * @param loadedManagers The list of loaded managers
     */
    private static void verifyManagers(Manager[] originalManagers, List<Manager> loadedManagers) {
        System.out.println("\nVerifying individual manager data:");
        boolean allMatch = true;

        // For each original manager, find the corresponding loaded manager and compare
        for (Manager original : originalManagers) {
            boolean found = false;
            for (Manager loaded : loadedManagers) {
                if (original.getID().equals(loaded.getID())) {
                    found = true;
                    boolean nameMatch = original.getName().equals(loaded.getName());
                    boolean projectsMatch = original.getProjectIDsInCharge().equals(loaded.getProjectIDsInCharge());

                    System.out.println("Manager " + original.getID() + ":");
                    System.out.println("  - Name match: " + nameMatch +
                            (nameMatch ? "" : " (Expected: " + original.getName() +
                                    ", Found: " + loaded.getName() + ")"));
                    System.out.println("  - Projects match: " + projectsMatch +
                            (projectsMatch ? "" : " (Expected: " + original.getProjectIDsInCharge() +
                                    ", Found: " + loaded.getProjectIDsInCharge() + ")"));

                    if (!nameMatch || !projectsMatch) {
                        allMatch = false;
                    }
                    break;
                }
            }

            if (!found) {
                System.out.println("ERROR: Manager " + original.getID() + " not found in loaded data!");
                allMatch = false;
            }
        }

        if (allMatch) {
            System.out.println("\nAll manager data correctly loaded from file!");
        } else {
            System.out.println("\nWARNING: Some manager data doesn't match the original!");
        }
    }
}