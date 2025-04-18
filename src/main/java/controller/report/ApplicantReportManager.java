package controller.report;

import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.MaritalStatus;
import model.project.RoomType;
import model.project.Project;
import repository.user.ApplicantRepository;
import controller.project.ProjectManager;

import java.util.ArrayList;
import java.util.List;

public class ApplicantReportManager {

    /**
     * Inner class to hold the data for each entry in the report.
     */
    public static class ReportEntry {
        public final String applicantName;
        public final int age;
        public final MaritalStatus maritalStatus;
        public final String projectName;
        public final RoomType roomType;

        public ReportEntry(String applicantName, int age, MaritalStatus maritalStatus, String projectName, RoomType roomType) {
            this.applicantName = applicantName;
            this.age = age;
            this.maritalStatus = maritalStatus;
            this.projectName = projectName;
            this.roomType = roomType;
        }
    }

    /**
     * Generates a report of applicants who have booked flats, applying optional filters.
     * Filters: marital status, room type, project name, age range.
     *
     * @param maritalFilter     Optional filter for marital status. Null means no filter.
     * @param roomFilter        Optional filter for room type. Null means no filter.
     * @param filterProjectName Optional filter for project name. Null or empty means no filter.
     * @param minAge            Optional minimum age filter. Null means no filter.
     * @param maxAge            Optional maximum age filter. Null means no filter.
     * @return A list of ReportEntry objects matching the criteria.
     */
    public List<ReportEntry> generateReport(MaritalStatus maritalFilter,
                                            RoomType roomFilter,
                                            String filterProjectName,
                                            Integer minAge,
                                            Integer maxAge) {
        List<ReportEntry> report = new ArrayList<>();
        // Iterate through all applicants in the repository
        for (Applicant app : ApplicantRepository.getInstance().getAll()) {
            // Only include applicants with BOOKED status
            if (app.getApplicantStatus() != ApplicantStatus.BOOKED) continue;

            // Apply Marital status filter
            if (maritalFilter != null && app.getMaritalStatus() != maritalFilter) continue;

            // Apply Room type filter
            if (roomFilter != null && app.getRoomType() != roomFilter) continue;

            // Apply Project name filter
            String projID = app.getProjectID();
            String projName;
            try {
                // Retrieve project details to get the name
                Project p = ProjectManager.getByID(projID);
                projName = p.getProjectTitle();
            } catch (Exception e) {
                // Handle cases where project might not be found (though unlikely for booked applicants)
                projName = "N/A"; // Or log an error
            }
            // Check if project name filter is active and if the project name matches
            if (filterProjectName != null && !filterProjectName.isEmpty() && !projName.equalsIgnoreCase(filterProjectName)) continue;

            // Apply Age range filter
            int age = app.getAge();
            if (minAge != null && age < minAge) continue;
            if (maxAge != null && age > maxAge) continue;

            // If all filters pass (or are not active), add the applicant's data to the report
            report.add(new ReportEntry(app.getName(), age, app.getMaritalStatus(), projName, app.getRoomType()));
        }
        return report; // Return the generated list of report entries
    }
}