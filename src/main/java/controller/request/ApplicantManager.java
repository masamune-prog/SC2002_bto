package controller.request;

import controller.project.ProjectManager;
import model.project.Project;
import model.project.RoomType;
import model.request.*;
import model.user.Applicant;
import model.user.ApplicantStatus;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;

// TODO: Add method for registration of new users and updating of user password
public class ApplicantManager {

    public static String createProjectApplicationRequest(String applicantNRIC, String projectID, RoomType roomType) throws ModelNotFoundException, ModelAlreadyExistsException {
        // Check if the applicant is also a Manager or Officer included in the Project
        Project project = ProjectManager.getByID(projectID);

        if (project.getOfficerIDs().contains(applicantNRIC) || applicantNRIC.equals(project.getManagerNRIC())) {
            System.out.println("Applicant is a manager or officer in the project. Cannot apply for the project.");
            return null;
        }

        String requestID = RequestManager.getNewRequestID();
        ProjectApplicationRequest request = new ProjectApplicationRequest(requestID, projectID,applicantNRIC, roomType);
        RequestRepository.getInstance().getAll().add(request);

        // Update the applicant's status to PENDING
        updateApplicantStatus(applicantNRIC, ApplicantStatus.PENDING);

        // Reserve the room for the applicant by decreasing the available room count
        ProjectManager.removeFlat(projectID, roomType);
        RequestRepository.getInstance().add(request);

        return requestID;
    }
    public static List<Applicant> getBookedApplicants(){
        List<Applicant> applicants = ApplicantRepository.getInstance().getAll();
        List<Applicant> bookedApplicants = new ArrayList<>();
        for (Applicant applicant : applicants) {
            if (applicant.getApplicantStatus() == ApplicantStatus.BOOKED) {
                bookedApplicants.add(applicant);
            }
        }
        return bookedApplicants;

    }
    public static String createBookingRequest(String applicantNRIC, RoomType roomType) throws ModelNotFoundException, ModelAlreadyExistsException {
        String requestID = RequestManager.getNewRequestID();

        // Find the successful application request
        List<Request> requests = RequestRepository.getInstance().getAll();
        for (Request request : requests) {
            if (request instanceof ProjectApplicationRequest) {
                ProjectApplicationRequest applicationRequest = (ProjectApplicationRequest) request;
                if (applicationRequest.getApplicantID().equals(applicantNRIC) && applicationRequest.getStatus() == RequestStatus.APPROVED) {
                    // Create a booking request
                    ProjectBookingRequest bookingRequest = new ProjectBookingRequest(
                            requestID,
                            applicationRequest.getProjectID(),
                            applicationRequest.getApplicantID(),
                            applicationRequest.getID(),
                            applicationRequest.getRoomType()
                    );
                    RequestRepository.getInstance().add(bookingRequest);
                    return requestID;
                }
            }
        }

        return null;
    }

    public static String createWithdrawalRequest(String projectID, String applicantID, RoomType roomType, String reason) throws ModelNotFoundException, ModelAlreadyExistsException {
        String requestID = RequestManager.getNewRequestID();
        ProjectWithdrawalRequest withdrawalRequest = new ProjectWithdrawalRequest(requestID, projectID, applicantID, roomType, reason);

        RequestRepository.getInstance().add(withdrawalRequest);
        updateApplicantStatus(applicantID, ApplicantStatus.PENDING);
        return requestID;

    }

    /**
     * Retrieves an applicant by their NRIC
     *
     * @param applicantNRIC the NRIC of the applicant
     * @return the applicant object
     * @throws ModelNotFoundException if the applicant is not found
     */
    public static Applicant getByNRIC(String applicantNRIC) throws ModelNotFoundException {
        return ApplicantRepository.getInstance().getByID(applicantNRIC);
    }

    public static void updateApplicantStatus(String applicantNRIC, ApplicantStatus status) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        applicant.setApplicantStatus(status);
        ApplicantRepository.getInstance().update(applicant);
    }

    public static void updateApplicantRoomType(String applicantNRIC, RoomType roomType) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        applicant.setRoomType(roomType);
        ApplicantRepository.getInstance().update(applicant);
    }

    public static void updateApplicantProjectID(String applicantNRIC, String projectID) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        applicant.setProjectID(projectID);
        ApplicantRepository.getInstance().update(applicant);
    }


    public static ApplicantStatus getApplicantStatus(String applicantNRIC) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        return applicant.getApplicantStatus();
    }

    public static RoomType getApplicantRoomType(String applicantNRIC) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        return applicant.getRoomType();
    }

    public static String getApplicantProjectID(String applicantNRIC) throws ModelNotFoundException {
        Applicant applicant = getByNRIC(applicantNRIC);
        return applicant.getProjectID();
    }
}
