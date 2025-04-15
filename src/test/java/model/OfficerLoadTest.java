package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.io.File;
import model.user.Officer;
import repository.user.OfficerRepository;

public class OfficerLoadTest {

    @Test
    public void testLoadOfficersFromCSV() {
        // Get repository instance
        OfficerRepository officerRepository = OfficerRepository.getInstance();

        // Debug: Print file path and check if it exists
        String filePath = officerRepository.getFilePath();
        System.out.println("CSV file path: " + filePath);
        File csvFile = new File(filePath);
        System.out.println("CSV file exists: " + csvFile.exists());
        System.out.println("CSV file absolute path: " + csvFile.getAbsolutePath());

        if (csvFile.exists()) {
            System.out.println("CSV file size: " + csvFile.length() + " bytes");
        }

        // Force reload to ensure we're testing the latest data
        officerRepository.load();
        //System.out.println("Officer data reloaded from CSV.");
        // Get all officers
        List<Officer> officers = officerRepository.getAll();
        //System.out.println("Loaded officers: " + officers);

        // Debug: Print list size
        System.out.println("Number of officers loaded: " + (officers != null ? officers.size() : "null"));

        // Print details for all officers
        if (officers != null) {
            System.out.println("\n===== DETAILED OFFICER INFORMATION =====");
            for (int i = 0; i < officers.size(); i++) {
                Officer officer = officers.get(i);
                System.out.println("\nOfficer " + (i+1) + ":");
                System.out.println("  ID: " + officer.getID());
                System.out.println("  NRIC: " + officer.getNric());
                System.out.println("  Name: " + officer.getName());
                System.out.println("  Password: " + officer.getHashedPassword());
                System.out.println("  Project: " + officer.getProjectsInCharge());
            }
            System.out.println("\n============= END OF LIST =============");
        }

        // Verify officers were loaded
        assertNotNull(officers, "Officer list should not be null");
        assertTrue(officers.size() > 0, "No officers were loaded from CSV");

        // Verify sequential integer IDs
        for (int i = 0; i < officers.size(); i++) {
            Officer officer = officers.get(i);
            String expectedId = String.valueOf(i + 1); // IDs start from 1
            assertEquals(expectedId, officer.getID(),
                    "Officer at position " + i + " should have ID " + expectedId);
        }

        // Verify some properties of the first officer
        Officer firstOfficer = officers.get(0);
        assertNotNull(firstOfficer.getNric(), "First officer NRIC should not be null");
        assertNotNull(firstOfficer.getName(), "First officer name should not be null");
        assertNotNull(firstOfficer.getHashedPassword(), "First officer password hash should not be null");


        // Check all officers have required fields
        for (int i = 0; i < officers.size(); i++) {
            Officer officer = officers.get(i);
            assertNotNull(officer.getNric(),
                    "Officer " + (i+1) + " should have a non-null NRIC");
            assertNotNull(officer.getName(),
                    "Officer " + (i+1) + " should have a non-null name");
            assertNotNull(officer.getHashedPassword(),
                    "Officer " + (i+1) + " should have a non-null password hash");
        }
    }
}