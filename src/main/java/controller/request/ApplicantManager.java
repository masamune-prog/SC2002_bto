package controller.request;

import model.project.Project;
import model.request.*;
import model.user.Applicant;
import model.user.ApplicantStatus;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import utils.exception.ModelNotFoundException;

import java.time.LocalDate;

/**
 * Manages applicant-related operations for project applications and registrations
 */
public class ApplicantManager {
    private final RequestManager requestManager;

    /**
     * Constructor for ApplicantManager
     */
    public ApplicantManager() {
        this.requestManager = new RequestManager();
    }

    /**
     * Creates a new project application request
     *
     * @param applicantID the ID of the applicant
     * @param projectID the ID of the project
     * @param roomType the type of room requested
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant or project is not found
     */
    public String createProjectApplicationRequest(String applicantID, String projectID, RoomType roomType)
            throws ModelNotFoundException {
        // Validate entities
        Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);
        Project project = ProjectRepository.getInstance().getByID(projectID);

        // Check if applicant is already registered
        if (applicant.getStatus() == ApplicantStatus.REGISTERED ||
                applicant.getStatus() == ApplicantStatus.PENDING) {
            throw new IllegalStateException("Applicant is already registered for a project");
        }

        // Check flat availability
        if (roomType == RoomType.TWO_ROOM_FLAT && project.getTwoRoomFlatsAvailable() <= 0) {
            throw new IllegalStateException("No two-room flats available in this project");
        }
        if (roomType == RoomType.THREE_ROOM_FLAT && project.getThreeRoomFlatsAvailable() <= 0) {
            throw new IllegalStateException("No three-room flats available in this project");
        }

        // Create request
        String requestID = requestManager.getNewRequestID();
        ProjectApplicationRequest request = new ProjectApplicationRequest(
                requestID,
                projectID,
                RequestStatus.PENDING,
                project.getManagerInCharge().getID(),
                applicantID,
                roomType
        );

        RequestRepository.getInstance().add(request);
        return requestID;
    }

    /**
     * Creates a project deregistration request
     *
     * @param applicantID the ID of the applicant
     * @param withdrawalReason the reason for withdrawal
     * @param originalRequestID the ID of the original application request (optional)
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant is not found
     */
    public String createProjectDeregistration(String applicantID, String withdrawalReason, String originalRequestID)
            throws ModelNotFoundException {
        Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);

        if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
            throw new IllegalStateException("Applicant is not registered for any project");
        }

        // Find project by name
        Project project = findProjectByName(applicant.getProject());
        if (project == null) {
            throw new ModelNotFoundException("Project not found for this applicant");
        }

        // Create request
        String requestID = requestManager.getNewRequestID();
        ProjectDeregistrationRequest request = new ProjectDeregistrationRequest(
                requestID,
                project.getID(),
                RequestStatus.PENDING,
                project.getManagerInCharge().getID(),
                applicantID,
                originalRequestID, // Pass the original request ID
                withdrawalReason  // Pass the withdrawal reason
        );

        RequestRepository.getInstance().add(request);
        return requestID;
    }

    /**
     * Simplified method for creating a project deregistration request
     *
     * @param applicantID the ID of the applicant
     * @param withdrawalReason the reason for withdrawal
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant is not found
     */
    public String createProjectDeregistration(String applicantID, String withdrawalReason)
            throws ModelNotFoundException {
        return createProjectDeregistration(applicantID, withdrawalReason, "N/A");
    }

    /**
     * Creates a project booking request
     *
     * @param applicantID the ID of the applicant
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant is not found
     */
    public String createProjectBooking(String applicantID, String originalRequestID,
                                       String officerID, String roomType)
            throws ModelNotFoundException {
        Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);
        LocalDate bookingDate = LocalDate.now();
        if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
            throw new IllegalStateException("Applicant must be registered before booking");
        }

        Project project = findProjectByName(applicant.getProject());
        if (project == null) {
            throw new ModelNotFoundException("Project not found for this applicant");
        }

        // Create booking request
        String requestID = requestManager.getNewRequestID();
        ProjectBookingRequest request = new ProjectBookingRequest(
                requestID,
                project.getID(),
                RequestStatus.PENDING,
                project.getManagerInCharge().getID(),
                officerID,
                applicantID,
                originalRequestID,
                roomType,
                bookingDate
        );

        RequestRepository.getInstance().add(request);
        return requestID;
    }

    /**
     * Finds a project by name
     *
     * @param projectName the name of the project
     * @return the project, or null if not found
     */
    private Project findProjectByName(String projectName) {
        for (Project project : ProjectRepository.getInstance().getAll()) {
            if (project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }
}