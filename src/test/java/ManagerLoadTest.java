import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.io.File;
import model.user.Manager;
import repository.user.ManagerRepository;

public class ManagerLoadTest {

    @Test
    public void testLoadManagersFromCSV() {
        // Get repository instance
        ManagerRepository managerRepository = ManagerRepository.getInstance();

        // Debug: Print file path and check if it exists
        String filePath = managerRepository.getFilePath();
        System.out.println("CSV file path: " + filePath);
        File csvFile = new File(filePath);
        System.out.println("CSV file exists: " + csvFile.exists());
        System.out.println("CSV file absolute path: " + csvFile.getAbsolutePath());

        if (csvFile.exists()) {
            System.out.println("CSV file size: " + csvFile.length() + " bytes");
        }

        // Force reload to ensure we're testing the latest data
        managerRepository.load();

        // Get all managers
        List<Manager> managers = managerRepository.getAll();

        // Debug: Print list size
        System.out.println("Number of managers loaded: " + (managers != null ? managers.size() : "null"));

        // Print details for all managers
        if (managers != null) {
            System.out.println("\n===== DETAILED MANAGER INFORMATION =====");
            for (int i = 0; i < managers.size(); i++) {
                Manager manager = managers.get(i);
                System.out.println("\nManager " + (i+1) + ":");
                System.out.println("  ID: " + manager.getID());
                System.out.println("  NRIC: " + manager.getNric());
                System.out.println("  Name: " + manager.getName());
                System.out.println("  Project In Charge: " + manager.getProjectInCharge());
                System.out.println("  Password: " + manager.getHashedPassword());
            }
            System.out.println("\n============= END OF LIST =============");
        }

        // Verify managers were loaded
        assertNotNull(managers, "Manager list should not be null");
        assertTrue(managers.size() > 0, "No managers were loaded from CSV");

        // Verify sequential integer IDs
        for (int i = 0; i < managers.size(); i++) {
            Manager manager = managers.get(i);
            String expectedId = String.valueOf(i + 1); // IDs start from 1
            assertEquals(expectedId, manager.getID(),
                    "Manager at position " + i + " should have ID " + expectedId);
        }

        // Verify some properties of the first manager
        Manager firstManager = managers.get(0);
        assertNotNull(firstManager.getNric(), "First manager NRIC should not be null");
        assertNotNull(firstManager.getName(), "First manager name should not be null");
        assertNotNull(firstManager.getHashedPassword(), "First manager password hash should not be null");

        // Check all managers have required fields
        for (int i = 0; i < managers.size(); i++) {
            Manager manager = managers.get(i);
            assertNotNull(manager.getNric(),
                    "Manager " + (i+1) + " should have a non-null NRIC");
            assertNotNull(manager.getName(),
                    "Manager " + (i+1) + " should have a non-null name");
            assertNotNull(manager.getHashedPassword(),
                    "Manager " + (i+1) + " should have a non-null password hash");
        }
    }
}