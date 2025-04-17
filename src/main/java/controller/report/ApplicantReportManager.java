package controller.report;

import model.user.Applicant;
import model.user.ApplicantStatus;
import model.request.ProjectApplicationRequest;
import model.request.RequestType;
import model.request.RoomType;
import model.user.MaritalStatus;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.project.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for generating applicant booking reports.
 */
public class ApplicantReportManager {
    private final ApplicantRepository applicantRepo = ApplicantRepository.getInstance();
    private final RequestRepository requestRepo = RequestRepository.getInstance();
    private final ProjectRepository projectRepo = ProjectRepository.getInstance();

    public static class ReportEntry {
        public final String applicantName;
        public final int age;
        public final MaritalStatus maritalStatus;
        public final String projectName;
        public final RoomType roomType;

        public ReportEntry(String applicantName, int age, MaritalStatus maritalStatus,
                           String projectName, RoomType roomType) {
            this.applicantName = applicantName;
            this.age = age;
            this.maritalStatus = maritalStatus;
            this.projectName = projectName;
            this.roomType = roomType;
        }
    }

    /**
     * Generates a report of applicants with optional filters.
     * @param filterMarital may be null to include all
     * @param filterRoom may be null to include all
     * @param filterProject may be null to include all
     * @param minAge may be null
     * @param maxAge may be null
     * @return list of report entries
     */
    public List<ReportEntry> generateReport(MaritalStatus filterMarital, RoomType filterRoom,
                                            String filterProject, Integer minAge, Integer maxAge) {
        List<ProjectApplicationRequest> apps = requestRepo.getAll().stream()
            .filter(r -> r.getRequestType() == RequestType.PROJECT_APPLICATION_REQUEST)
            .map(r -> (ProjectApplicationRequest) r)
            .collect(Collectors.toList());
        List<ReportEntry> report = new ArrayList<>();
        for (ProjectApplicationRequest req : apps) {
            Applicant applicant = applicantRepo.getByID(req.getApplicantID());
            if (applicant == null || applicant.getStatus() != ApplicantStatus.BOOKED) continue;
            String projName = projectRepo.getByProjectID(req.getProjectID()).getProjectName();
            // apply filters
            if (filterMarital != null && applicant.getMaritalStatus() != filterMarital) continue;
            if (filterRoom != null && req.getRoomType() != filterRoom) continue;
            if (filterProject != null && !projName.equalsIgnoreCase(filterProject)) continue;
            int age = applicant.getAge();
            if (minAge != null && age < minAge) continue;
            if (maxAge != null && age > maxAge) continue;
            report.add(new ReportEntry(applicant.getName(), age, applicant.getMaritalStatus(), projName, req.getRoomType()));
        }
        return report;
    }
}