import controller.project.ProjectManager;
import controller.report.ApplicantReportManager;
import model.project.Project;
import model.project.RoomType;
import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.MaritalStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.project.ProjectRepository;
import repository.user.ApplicantRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicantReportManagerTest {

    private ApplicantRepository applicantRepository;
    private ProjectRepository projectRepository;
    private ApplicantReportManager reportManager;

    private Applicant applicant1, applicant2, applicant3, applicant4;
    private Project project1, project2;

    @BeforeEach
    void setUp() throws ModelAlreadyExistsException, ModelNotFoundException {
        // Use the singleton instances
        applicantRepository = ApplicantRepository.getInstance();
        projectRepository = ProjectRepository.getInstance();
        reportManager = new ApplicantReportManager();

        // Clear repositories before each test to ensure isolation
        applicantRepository.clear();
        projectRepository.clear();

        // --- Create Test Data ---

        // Projects - Arguments rearranged to match the provided constructor
        project1 = new Project("P001",                     // projectID
                "SkyVille",                 // projectTitle
                "Neighbourhood A",          // neighbourhood
                LocalDate.now().minusDays(10), // applicationOpeningDate
                LocalDate.now().plusDays(10),  // applicationClosingDate
                10,                         // twoRoomFlatAvailable
                10,                         // threeRoomFlatAvailable
                200000.0,                   // twoRoomFlatPrice
                300000.0,                   // threeRoomFlatPrice
                "M001",                     // managerNRIC
                new ArrayList<>(),          // officerIDs
                true);                      // visibility

        project2 = new Project("P002",                     // projectID
                "GreenPark",                // projectTitle
                "Neighbourhood B",          // neighbourhood
                LocalDate.now().minusDays(5),  // applicationOpeningDate
                LocalDate.now().plusDays(15), // applicationClosingDate
                5,                          // twoRoomFlatAvailable
                5,                          // threeRoomFlatAvailable
                250000.0,                   // twoRoomFlatPrice
                350000.0,                   // threeRoomFlatPrice
                "M002",                     // managerNRIC
                new ArrayList<>(),          // officerIDs
                true);                      // visibility

        projectRepository.add(project1);
        projectRepository.add(project2);

        // Applicants - Using the new constructor with all fields
        // Booked Applicant 1 (Married, Project 1, 2-Room)
        applicant1 = new Applicant("Alice Tan", "A001", 35, MaritalStatus.MARRIED, "hashedPass1", ApplicantStatus.BOOKED, RoomType.TWO_ROOM_FLAT, "P001");

        // Booked Applicant 2 (Single, Project 1, 2-Room)
        applicant2 = new Applicant("Bob Lim", "A002", 28, MaritalStatus.SINGLE, "hashedPass2", ApplicantStatus.BOOKED, RoomType.TWO_ROOM_FLAT, "P001");

        // Booked Applicant 3 (Married, Project 2, 3-Room)
        applicant3 = new Applicant("Charlie Lee", "A003", 42, MaritalStatus.MARRIED, "hashedPass3", ApplicantStatus.BOOKED, RoomType.THREE_ROOM_FLAT, "P002");

        // Non-Booked Applicant (Should not appear in report)
        applicant4 = new Applicant("Diana Goh", "A004", 30, MaritalStatus.SINGLE, "hashedPass4", ApplicantStatus.SUCCESSFUL, RoomType.TWO_ROOM_FLAT, "P001"); // Status is SUCCESSFUL

        applicantRepository.add(applicant1);
        applicantRepository.add(applicant2);
        applicantRepository.add(applicant3);
        applicantRepository.add(applicant4);

        // Ensure ProjectManager can find the projects by ID during the test
        // This relies on ProjectRepository being populated correctly.
    }

    @AfterEach
    void tearDown() {
        // Clear repositories after each test
        applicantRepository.clear();
        projectRepository.clear();
    }

    @Test
    void generateReport_NoFilters_ReturnsAllBooked() {
        System.out.println("\n--- Running generateReport_NoFilters_ReturnsAllBooked ---"); // Added for clarity
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, null, null, null, null);

        // --- Print the Report Content ---
        System.out.println("Generated Report (No Filters):");
        if (report.isEmpty()) {
            System.out.println("  <No entries found>");
        } else {
            // Header
            System.out.printf("  %-20s %-5s %-15s %-20s %-15s%n", "Applicant Name", "Age", "Marital Status", "Project Name", "Flat Type");
            System.out.println("  -----------------------------------------------------------------------------");
            // Data
            for (ApplicantReportManager.ReportEntry entry : report) {
                System.out.printf("  %-20s %-5d %-15s %-20s %-15s%n",
                        entry.applicantName,
                        entry.age,
                        entry.maritalStatus,
                        entry.projectName,
                        entry.roomType);
            }
            System.out.println("  -----------------------------------------------------------------------------");
        }
        System.out.println("--- Test Assertions ---"); // Added for clarity
        // --- End of Printing ---

        // Original Assertions
        assertEquals(3, report.size(), "Report should contain all 3 booked applicants");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Charlie Lee")), "Charlie should be in the report");
        System.out.println("--- Test Finished ---\n"); // Added for clarity
    }

    @Test
    void generateReport_FilterByMaritalStatus_Married() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(MaritalStatus.MARRIED, null, null, null, null);

        assertEquals(2, report.size(), "Report should contain 2 married booked applicants");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice (Married) should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Charlie Lee")), "Charlie (Married) should be in the report");
        assertFalse(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob (Single) should not be in the report");
    }

    @Test
    void generateReport_FilterByRoomType_TwoRoom() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, RoomType.TWO_ROOM_FLAT, null, null, null);

        assertEquals(2, report.size(), "Report should contain 2 booked applicants with Two-Room flats");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice (2-Room) should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob (2-Room) should be in the report");
        assertFalse(report.stream().anyMatch(e -> e.applicantName.equals("Charlie Lee")), "Charlie (3-Room) should not be in the report");
    }

    @Test
    void generateReport_FilterByProjectName_SkyVille() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, null, "SkyVille", null, null);

        assertEquals(2, report.size(), "Report should contain 2 booked applicants for SkyVille");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice (SkyVille) should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob (SkyVille) should be in the report");
        assertFalse(report.stream().anyMatch(e -> e.applicantName.equals("Charlie Lee")), "Charlie (GreenPark) should not be in the report");
    }
    @Test
    void generateReport_FilterByProjectName_CaseInsensitive() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, null, "skyville", null, null); // Lowercase

        assertEquals(2, report.size(), "Report should contain 2 booked applicants for SkyVille (case-insensitive)");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice (SkyVille) should be in the report");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob (SkyVille) should be in the report");
    }


    @Test
    void generateReport_FilterByAgeRange() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, null, null, 30, 40); // Age 30 to 40 inclusive

        assertEquals(1, report.size(), "Report should contain 1 booked applicant aged 30-40");
        assertTrue(report.stream().anyMatch(e -> e.applicantName.equals("Alice Tan")), "Alice (35) should be in the report");
        assertFalse(report.stream().anyMatch(e -> e.applicantName.equals("Bob Lim")), "Bob (28) should not be in the report");
        assertFalse(report.stream().anyMatch(e -> e.applicantName.equals("Charlie Lee")), "Charlie (42) should not be in the report");
    }

    @Test
    void generateReport_CombinedFilters_Married_TwoRoom_SkyVille() {
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(MaritalStatus.MARRIED, RoomType.TWO_ROOM_FLAT, "SkyVille", null, null);

        assertEquals(1, report.size(), "Report should contain only Alice");
        assertEquals("Alice Tan", report.get(0).applicantName);
        assertEquals(35, report.get(0).age);
        assertEquals(MaritalStatus.MARRIED, report.get(0).maritalStatus);
        assertEquals("SkyVille", report.get(0).projectName);
        assertEquals(RoomType.TWO_ROOM_FLAT, report.get(0).roomType);
    }

    @Test
    void generateReport_NoMatches() {
        // Filter for Single applicants in GreenPark (none exist with BOOKED status)
        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(MaritalStatus.SINGLE, null, "GreenPark", null, null);
        assertTrue(report.isEmpty(), "Report should be empty when no applicants match filters");
    }

    @Test
    void generateReport_ProjectNotFound_ShouldHandleGracefully() throws ModelNotFoundException, ModelAlreadyExistsException {
        // Add a booked applicant whose project doesn't exist in the repo anymore
        Applicant applicant5 = new Applicant("Eve Wong", "A005", 33, MaritalStatus.SINGLE, "hashedPass5", ApplicantStatus.BOOKED, RoomType.TWO_ROOM_FLAT, "P999"); // Non-existent project
        applicantRepository.add(applicant5);

        // Remove the project P001 after adding applicant1 and applicant2 who depend on it
        projectRepository.remove("P001"); // Remove project Alice and Bob are linked to

        List<ApplicantReportManager.ReportEntry> report = reportManager.generateReport(null, null, null, null, null);

        // Expecting Alice, Bob, Charlie, Eve. Alice/Bob project name = N/A. Charlie = GreenPark. Eve = N/A
        assertEquals(4, report.size(), "Should include all booked applicants even if project lookup fails");

        ApplicantReportManager.ReportEntry aliceEntry = report.stream().filter(e -> e.applicantName.equals("Alice Tan")).findFirst().orElse(null);
        ApplicantReportManager.ReportEntry bobEntry = report.stream().filter(e -> e.applicantName.equals("Bob Lim")).findFirst().orElse(null);
        ApplicantReportManager.ReportEntry charlieEntry = report.stream().filter(e -> e.applicantName.equals("Charlie Lee")).findFirst().orElse(null);
        ApplicantReportManager.ReportEntry eveEntry = report.stream().filter(e -> e.applicantName.equals("Eve Wong")).findFirst().orElse(null);


        assertNotNull(aliceEntry, "Alice should be in the report");
        assertEquals("N/A", aliceEntry.projectName, "Alice's project name should be N/A as P001 was removed");

        assertNotNull(bobEntry, "Bob should be in the report");
        assertEquals("N/A", bobEntry.projectName, "Bob's project name should be N/A as P001 was removed");

        assertNotNull(charlieEntry, "Charlie should be in the report");
        assertEquals("GreenPark", charlieEntry.projectName, "Charlie's project name should still be GreenPark");

        assertNotNull(eveEntry, "Eve should be in the report");
        assertEquals("N/A", eveEntry.projectName, "Eve's project name should be N/A as P999 doesn't exist");

        // Restore P001 for other tests if needed, although @AfterEach handles cleanup
        // try { projectRepository.add(project1); } catch (ModelAlreadyExistsException e) { /* Ignore if already exists */ }
    }
}