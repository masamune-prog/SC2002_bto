package controller.request;

import controller.project.ProjectManager; // Added import
import model.project.Project;
import model.request.*;
import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.Manager;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import utils.exception.ModelNotFoundException;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages applicant-related operations for project applications and registrations
 */
public class ApplicantManager {
    private final RequestManager requestManager;
    private final ProjectManager projectManager; // Added ProjectManager field

    /**
     * Constructor for ApplicantManager
     */
    public ApplicantManager() {
        this.requestManager = new RequestManager();
        this.projectManager = new ProjectManager(); // Initialize ProjectManager
    }

    /**
     * Creates a new project application request
     *
     * @param applicantID the ID of the applicant
     * @param projectID   the ID of the project
     * @param roomType    the type of room requested
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant or project is not found
     */
    public Boolean isApplicantAManager(String applicantNRIC) throws ModelNotFoundException {
        Manager manager = ManagerRepository.getInstance().getByNRIC(applicantNRIC);
        if (manager != null) {
            return false;
        } else {
            return true;
        }

    }
    public String createProjectApplicationRequest(String applicantID, String projectID, RoomType roomType)
            throws ModelNotFoundException {
        // Validate entities
        Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);
        Project project = projectManager.getProjectByID(projectID); // Use ProjectManager

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
        String managerID = project.getManagerInCharge() != null ? project.getManagerInCharge().getID() : null;

        // Debug output to help diagnose manager ID issues
        System.out.println("DEBUG: Creating new application request");
        System.out.println("DEBUG: Project ID: " + projectID);
        System.out.println("DEBUG: Project Manager: " + (project.getManagerInCharge() != null ?
                project.getManagerInCharge().getName() : "null"));
        System.out.println("DEBUG: Manager ID being assigned: " + managerID);

        ProjectApplicationRequest request = new ProjectApplicationRequest(
                requestID,
                projectID,
                RequestStatus.PENDING,
                managerID,
                applicantID,
                roomType
        );

        RequestRepository.getInstance().add(request);
        // Update applicant status and booked room type
        applicant.setStatus(ApplicantStatus.PENDING);
        applicant.setRoomType(roomType);
        ApplicantRepository.getInstance().update(applicant); // Persist applicant changes
        return requestID;
    }

    /**
     * Creates a project deregistration request
     *
     * @param applicantID       the ID of the applicant
     * @param withdrawalReason  the reason for withdrawal
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

        // Find project by name using ProjectManager
        Project project = projectManager.getProjectByName(applicant.getProject()); // Use ProjectManager
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
        // Clear applicant's roomType upon deregistration
        applicant.setRoomType(null);
        ApplicantRepository.getInstance().update(applicant);
        return requestID;
    }

    /**
     * Simplified method for creating a project deregistration request
     *
     * @param applicantID      the ID of the applicant
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
     * @param applicantID       the ID of the applicant
     * @param originalRequestID the ID of the original application request
     * @param officerIDs        List of officer IDs assigned to the project
     * @param roomType          the type of room requested
     * @return the ID of the created request
     * @throws ModelNotFoundException if the applicant or project is not found
     */
    public String createProjectBooking(String applicantID, String originalRequestID,
                                       List<String> officerIDs, String roomType)
            throws ModelNotFoundException {
        Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);
        // prevent users who are managers from booking a flat
        try {
            if (!isApplicantAManager(applicant.getNRIC())) {
                throw new IllegalStateException("Managers are not allowed to book flats");
            }
        } catch (ModelNotFoundException e) {
            throw new IllegalStateException("Unable to verify user role: " + e.getMessage());
        }
        LocalDate bookingDate = LocalDate.now();
        if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
            throw new IllegalStateException("Applicant must be registered before booking");
        }

        Project project = projectManager.getProjectByName(applicant.getProject()); // Use ProjectManager
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
                officerIDs, // Pass the list of officer IDs
                applicantID,
                originalRequestID,
                roomType,
                bookingDate
        );

        RequestRepository.getInstance().add(request);
        return requestID;
    }
    public List<Request> getBookingRequestsByApplicant(String applicantID) throws ModelNotFoundException {
        List<Request> bookingRequests = requestManager.getAllBookingRequests();
        List<Request> requests = new ArrayList<>();

        for (Request request : bookingRequests) {
            if (request instanceof ProjectBookingRequest) {
                ProjectBookingRequest bookingRequest = (ProjectBookingRequest) request;
                if (bookingRequest.getApplicantID().equals(applicantID)) {
                    requests.add(request);
                }
            }
        }

        return requests;
    }

}