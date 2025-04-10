import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.io.File;
import model.user.Applicant;
import model.user.MaritalStatus;
import repository.user.ApplicantRepository;

public class ApplicantLoadTest {

    @Test
    public void testLoadApplicantsFromCSV() {
        // Get repository instance
        ApplicantRepository applicantRepository = ApplicantRepository.getInstance();

        // Debug: Print file path and check if it exists
        String filePath = applicantRepository.getFilePath();
        System.out.println("CSV file path: " + filePath);
        File csvFile = new File(filePath);
        System.out.println("CSV file exists: " + csvFile.exists());
        System.out.println("CSV file absolute path: " + csvFile.getAbsolutePath());

        if (csvFile.exists()) {
            System.out.println("CSV file size: " + csvFile.length() + " bytes");
        }

        // Force reload to ensure we're testing the latest data
        applicantRepository.load();

        // Get all applicants
        List<Applicant> applicants = applicantRepository.getAll();

        // Debug: Print list size
        System.out.println("Number of applicants loaded: " + (applicants != null ? applicants.size() : "null"));

        // Print details for all applicants
        if (applicants != null) {
            System.out.println("\n===== DETAILED APPLICANT INFORMATION =====");
            for (int i = 0; i < applicants.size(); i++) {
                Applicant applicant = applicants.get(i);
                System.out.println("\nApplicant " + (i+1) + ":");
                System.out.println("  ID: " + applicant.getID());
                System.out.println("  NRIC: " + applicant.getNric());
                System.out.println("  Name: " + applicant.getName());
                System.out.println("  Age: " + applicant.getAge());
                System.out.println("  Marital Status: " + applicant.getMaritalStatus() +
                        " (enum value: " + (applicant.getMaritalStatus() != null ?
                        applicant.getMaritalStatus().name() : "null") + ")");
                System.out.println("  Project: " + applicant.getProject());
                System.out.println("  Password: " + applicant.getHashedPassword());
            }
            System.out.println("\n============= END OF LIST =============");
        }

        // Verify applicants were loaded
        assertNotNull(applicants, "Applicant list should not be null");
        assertTrue(applicants.size() > 0, "No applicants were loaded from CSV");

        // Verify sequential integer IDs
        for (int i = 0; i < applicants.size(); i++) {
            Applicant applicant = applicants.get(i);
            String expectedId = String.valueOf(i + 1); // IDs start from 1
            assertEquals(expectedId, applicant.getID(),
                    "Applicant at position " + i + " should have ID " + expectedId);
        }

        // Verify some properties of the first applicant
        Applicant firstApplicant = applicants.get(0);
        assertNotNull(firstApplicant.getNric(), "First applicant NRIC should not be null");
        assertNotNull(firstApplicant.getName(), "First applicant name should not be null");
        assertNotNull(firstApplicant.getMaritalStatus(), "First applicant marital status should not be null");

        // Check all applicants have valid marital status
        for (int i = 0; i < applicants.size(); i++) {
            Applicant applicant = applicants.get(i);
            assertNotNull(applicant.getMaritalStatus(),
                    "Applicant " + (i+1) + " should have a non-null marital status");

            // Verify the marital status is one of the expected enum values
            MaritalStatus status = applicant.getMaritalStatus();
            assertTrue(
                    status == MaritalStatus.SINGLE ||
                            status == MaritalStatus.MARRIED ||
                            status == MaritalStatus.DIVORCED ||
                            status == MaritalStatus.WIDOWED,
                    "Applicant " + (i+1) + " has invalid marital status: " + status
            );
        }
    }
}