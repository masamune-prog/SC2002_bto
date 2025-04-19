package controller.request;

import controller.project.ProjectManager;
import model.project.Project;
import model.project.RoomType;
import model.request.*;
import model.user.Applicant;
import model.user.ApplicantStatus;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static controller.project.ProjectManager.removeFlat;

public class RequestManager {
    /**
     * get a request by ID
     *
     * @param requestID the request ID
     * @return the request
     * @throws ModelNotFoundException if the request is not found
     */
    public static Request getRequest(String requestID) throws ModelNotFoundException, ModelNotFoundException {
        return RequestRepository.getInstance().getByID(requestID);
    }

    public static String getNewRequestID() {
        int max = 0;
        for (Request p : RequestRepository.getInstance()) {
            int id = Integer.parseInt(p.getID().substring(1));
            if (id > max) {
                max = id;
            }
        }
        return "R" + (max + 1);
    }

    //only changes status nothing else
    public static void approveRequestForStatus(String requestID) throws ModelNotFoundException {
        Request r1 = RequestRepository.getInstance().getByID(requestID);
        try {
            r1.setStatus(RequestStatus.APPROVED);
            RequestRepository.getInstance().update(r1);
        } catch (ModelNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void rejectRequestForStatus(String requestID) throws ModelNotFoundException {
        Request r1 = RequestRepository.getInstance().getByID(requestID);
        try {
            r1.setStatus(RequestStatus.REJECTED);
            RequestRepository.getInstance().update(r1);
        } catch (ModelNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean approveProjectApplication(String requestID) throws ModelNotFoundException {
        //we would change the status of the project to approved
        //which can be done by approveRequestForStatus
        // we would change Status of the Applicant to Registered
        // Deduct the house from the Project by calling removeFlat from ProjectManager
        //get the projectID from the request
        //get the applicantID from the request
        Request request = RequestRepository.getInstance().getByID(requestID);
        //cast to ProjectApplicationRequest
        //get the projectID and applicantID from the request
        ProjectApplicationRequest projectApplicationRequest = (ProjectApplicationRequest) request;
        String projectID = projectApplicationRequest.getProjectID();
        String applicantID = projectApplicationRequest.getApplicantID();
        RoomType roomType = projectApplicationRequest.getRoomType();
        //get the project from the projectID
        //get the applicant from the applicantID
        //change the status of the applicant to registered
        removeFlat(projectID, roomType);
        //change the status of the applicant to registered
        ApplicantManager.updateApplicantStatus(applicantID, ApplicantStatus.SUCCESSFUL);
        //change the status of the request to approved
        approveRequestForStatus(requestID);
        return true;
    }

    public static Boolean rejectProjectApplication(String requestID) throws ModelNotFoundException {
        //Since we are rejecting the request and we will deduct the house from the project we have to add it back
        //get the projectID from the request
        //get the applicantID from the request
        Request request = RequestRepository.getInstance().getByID(requestID);
        //cast to ProjectApplicationRequest
        //get the projectID and applicantID from the request
        ProjectApplicationRequest projectApplicationRequest = (ProjectApplicationRequest) request;
        String projectID = projectApplicationRequest.getProjectID();
        String applicantID = projectApplicationRequest.getApplicantID();
        RoomType roomType = projectApplicationRequest.getRoomType();
        //get the project from the projectID
        //get the applicant from the applicantID
        //change the status of the applicant to registered
        ProjectManager.addFlat(projectID, roomType);
        //change the status of the applicant to registered
        ApplicantManager.updateApplicantStatus(applicantID, ApplicantStatus.REJECTED);
        //change the status of the request to rejected
        rejectRequestForStatus(requestID);
        return true;
    }

    // Java
    public static void approveBookingRequest(String requestID) throws ModelNotFoundException {
        Request request = RequestRepository.getInstance().getByID(requestID);
        // cast to ProjectBookingRequest
        ProjectBookingRequest projectBookingRequest = (ProjectBookingRequest) request;
        String projectID = projectBookingRequest.getProjectID();
        String applicantID = projectBookingRequest.getApplicantID();
        RoomType roomType = projectBookingRequest.getRoomType();
        // change the status of the request to approved
        request.setStatus(RequestStatus.APPROVED);
        // change the status of the applicant to Booked
        ApplicantManager.updateApplicantStatus(applicantID, ApplicantStatus.BOOKED);
        // update applicant fields for applicantProjectID and applicantRoomType
        ApplicantManager.updateApplicantProjectID(applicantID, projectID);
        ApplicantManager.updateApplicantRoomType(applicantID, roomType);
        // get the applicant from the applicantID
        Applicant applicant = ApplicantManager.getByNRIC(applicantID);
        // get the project from the projectID
        Project project = ProjectManager.getByID(projectID);
        RequestRepository.getInstance().update(request);
        // create a receipt string with nicely formatted details
        String receipt = "\n===== Booking Receipt =====\n" +
                "Applicant Details:\n" +
                "\tName           : " + applicant.getName() + "\n" +
                "\tNRIC           : " + applicant.getNRIC() + "\n" +
                "\tAge            : " + applicant.getAge() + "\n" +
                "\tMarital Status : " + applicant.getMaritalStatus() + "\n" +
                "\tRoom Type      : " + roomType + "\n" +
                "Project Details:\n" +
                "\tProject ID     : " + project.getID() + "\n" +
                "\tProject Name   : " + project.getProjectTitle() + "\n" +
                "=============================\n";

        // print out the receipt details
        System.out.println(receipt);
    }

    public static void rejectBookingRequest(String requestID) throws ModelNotFoundException {
        Request request = RequestRepository.getInstance().getByID(requestID);
        //cast to ProjectBookingRequest
        ProjectBookingRequest projectBookingRequest = (ProjectBookingRequest) request;
        String projectID = projectBookingRequest.getProjectID();
        String applicantID = projectBookingRequest.getApplicantID();
        RoomType roomType = projectBookingRequest.getRoomType();
        //change the status of the request to rejected
        rejectRequestForStatus(requestID);
        //change the status of the applicant to Booked
        ApplicantManager.updateApplicantStatus(applicantID, ApplicantStatus.REJECTED);
        //add back the flat
        ProjectManager.addFlat(projectID, roomType);
    }

    // Java
    public static void approveOfficerApplicationRequest(String requestID) throws ModelNotFoundException {
        // get the request by its ID
        Request request = RequestRepository.getInstance().getByID(requestID);
        if (!(request instanceof OfficerApplicationRequest)) {
            throw new IllegalArgumentException("Request is not an OfficerApplicationRequest.");
        }
        // cast to OfficerApplicationRequest
        OfficerApplicationRequest officerRequest = (OfficerApplicationRequest) request;
        // retrieve officerID and projectID from the request
        String officerID = officerRequest.getOfficerID();
        String projectID = officerRequest.getProjectID();
        // retrieve the officer using an assumed repository method from OfficerRepository
        // (this assumes that OfficerRepository.getInstance().getByID(officerID) exists)
        // update the request status to approved
        officerRequest.setStatus(RequestStatus.APPROVED);
        RequestRepository.getInstance().update(officerRequest);
        // add the officer to the project via ProjectManager
        ProjectManager.addOfficerToProject(projectID, officerID);

    }

    public static Boolean approveWithdrawalRequest(String requestID) throws ModelNotFoundException {
        // get the request by its ID
        Request request = RequestRepository.getInstance().getByID(requestID);
        if (!(request instanceof ProjectWithdrawalRequest)) {
            throw new IllegalArgumentException("Request is not a ProjectWithdrawalRequest.");
        }
        // cast to ProjectWithdrawalRequest
        ProjectWithdrawalRequest withdrawalRequest = (ProjectWithdrawalRequest) request;
        // update the request status to approved
        withdrawalRequest.setStatus(RequestStatus.APPROVED);
        RequestRepository.getInstance().update(withdrawalRequest);
        // get the projectID and applicantID from the request
        String projectID = withdrawalRequest.getProjectID();
        String applicantID = withdrawalRequest.getApplicantID();
        // get the project from the projectID
        Project project = ProjectManager.getByID(projectID);
        ProjectManager.addFlat(projectID, withdrawalRequest.getRoomType());
        // change the status of the applicant to Not Registered
        ApplicantManager.updateApplicantStatus(applicantID, ApplicantStatus.NO_REGISTRATION);
        // remove the project from the applicant's list
        ApplicantManager.updateApplicantProjectID(applicantID, "");
        // remove the room type from the applicant's list
        ApplicantManager.updateApplicantRoomType(applicantID, RoomType.NONE);
        //find all requests that are pending and have the same projectID and applicantID and set to rejected
        List<Request> allRequests = RequestRepository.getInstance().getAll();
        for (Request r : allRequests) {
            if (r instanceof ProjectApplicationRequest projectApplicationRequest) {
                if (projectApplicationRequest.getProjectID().equals(projectID) && projectApplicationRequest.getApplicantID().equals(applicantID)) {
                    projectApplicationRequest.setStatus(RequestStatus.REJECTED);
                    RequestRepository.getInstance().update(projectApplicationRequest);
                }
            }
            if (r instanceof ProjectBookingRequest projectBookingRequest) {
                if (projectBookingRequest.getProjectID().equals(projectID) && projectBookingRequest.getApplicantID().equals(applicantID)) {
                    projectBookingRequest.setStatus(RequestStatus.REJECTED);
                    RequestRepository.getInstance().update(projectBookingRequest);
                }
            }
        }
        return true;
    }
    public static Boolean rejectWithdrawalRequest(String requestID) throws ModelNotFoundException {
        // get the request by its ID
        Request request = RequestRepository.getInstance().getByID(requestID);
        if (!(request instanceof ProjectWithdrawalRequest)) {
            throw new IllegalArgumentException("Request is not a ProjectWithdrawalRequest.");
        }
        // cast to ProjectWithdrawalRequest
        ProjectWithdrawalRequest withdrawalRequest = (ProjectWithdrawalRequest) request;
        // update the request status to approved
        withdrawalRequest.setStatus(RequestStatus.REJECTED);
        return true;
    }


    public static void rejectOfficerApplicationRequest(String requestID) throws ModelNotFoundException {
        // get the request by its ID
        Request request = RequestRepository.getInstance().getByID(requestID);
        if (!(request instanceof OfficerApplicationRequest)) {
            throw new IllegalArgumentException("Request is not an OfficerApplicationRequest.");
        }
        // cast to OfficerApplicationRequest
        OfficerApplicationRequest officerRequest = (OfficerApplicationRequest) request;
        // update the request status to rejected
        officerRequest.setStatus(RequestStatus.REJECTED);
        RequestRepository.getInstance().update(officerRequest);
    }

    public static Request getAllApplicationRequestsByUser(String userID) throws ModelNotFoundException {
        List<Request> requestList = RequestRepository.getInstance().getAll();
        for (Request request : requestList) {
            if (request instanceof ProjectApplicationRequest projectApplicationRequest) {
                if (projectApplicationRequest.getApplicantID().equals(userID)) {
                    return request;
                }
            }

        }
        return null;
    }
    public static List<Request> getAllPendingApplicationRequests() throws ModelNotFoundException {
        List<Request> requestList = RequestRepository.getInstance().getAll();
        List<Request> pendingRequests = new ArrayList<>();

        for (Request request : requestList) {
            if (request instanceof ProjectApplicationRequest projectApplicationRequest) {
                if (projectApplicationRequest.getStatus() == RequestStatus.PENDING) {
                    pendingRequests.add(projectApplicationRequest);
                }
            }
        }

        return pendingRequests;
    }
    public static List<Request> getAllPendingApplicationRequestsByManager(String managerID) throws ModelNotFoundException {
        List<Request> requestList = RequestRepository.getInstance().getAll();
        List<Request> pendingRequests = new ArrayList<>();

        for (Request request : requestList) {
            if (request instanceof ProjectApplicationRequest projectApplicationRequest) {
                String projectID = projectApplicationRequest.getProjectID();
                Project project = ProjectRepository.getInstance().getByID(projectID); // may throw ModelNotFoundException

                if (project.getManagerID().equals(managerID) && projectApplicationRequest.getStatus() == RequestStatus.PENDING) {
                    pendingRequests.add(projectApplicationRequest);
                }
            }
        }

        return pendingRequests;
    }

    public static Request getAllApprovedApplicationRequestsByUser(String userID) throws ModelNotFoundException {
        List<Request> requestList = RequestRepository.getInstance().getAll();
        for (Request request : requestList) {
            if (request instanceof ProjectApplicationRequest projectApplicationRequest) {
                if (projectApplicationRequest.getApplicantID().equals(userID) && projectApplicationRequest.getStatus() == RequestStatus.APPROVED) {
                    return request;
                }
            }

        }
        return null;
    }
    public static List<Request> getAllPendingOfficerApplicationRequestsForManager(String managerID) throws ModelNotFoundException {
        List<Request> allRequests = RequestRepository.getInstance().getAll();
        List<Request> managerSpecificRequests = new ArrayList<>(); // List to store matching requests

        for (Request request : allRequests) {
            // Check if the request is specifically an OfficerApplicationRequest
            if (request instanceof OfficerApplicationRequest officerApplicationRequest) {
                // Get the project associated with this application request
                // This might throw ModelNotFoundException if the project was deleted
                Project project = ProjectManager.getByID(officerApplicationRequest.getProjectID());

                // Get the manager's ID from the project
                String projectManagerID = project.getManagerNRIC();

                // Check if the project's manager is the manager we're looking for
                if (projectManagerID.equals(managerID) && officerApplicationRequest.getStatus() == RequestStatus.PENDING) {
                    managerSpecificRequests.add(request); // Add the request to our result list
                }
            }
        }
        // Return the list containing only the requests relevant to the specified manager
        return managerSpecificRequests;
    }

    public List<ProjectWithdrawalRequest> getAllPendingWithdrawalRequests() {
        List<ProjectWithdrawalRequest> pendingRequests = new ArrayList<>();
        for (Request request : RequestRepository.getInstance().getAll()) {
            if (request instanceof ProjectWithdrawalRequest projectDeregistrationRequest) {
                if (projectDeregistrationRequest.getStatus() == RequestStatus.PENDING) {
                    pendingRequests.add(projectDeregistrationRequest);
                }
            }
        }
        return pendingRequests;
    }
    public static List<ProjectBookingRequest> getOfficerPendingBookingRequests(String officerID)
            throws ModelNotFoundException {
        List<Request> all = RequestRepository.getInstance().getAll();
        List<ProjectBookingRequest> pending = new ArrayList<>(); // Changed the list type
        for (Request req : all) {
            if (req instanceof ProjectBookingRequest pbr
                    && pbr.getStatus() == RequestStatus.PENDING) {
                // Only include if this officer is assigned to the project
                Project proj = ProjectManager.getByID(pbr.getProjectID());
                if (proj.getOfficerIDs().contains(officerID)) {
                    // Add the properly typed ProjectBookingRequest
                    pending.add(pbr); // No need to cast since pbr is already a ProjectBookingRequest
                }
            }
        }
        return pending;
    }
    public static Request getBookingRequestByApplicant(String applicantID) throws ModelNotFoundException {
        List<Request> requestList = RequestRepository.getInstance().getAll();
        for (Request request : requestList) {
            if (request instanceof ProjectBookingRequest projectBookingRequest) {
                if (projectBookingRequest.getApplicantID().equals(applicantID)) {
                    return request;
                }
            }
        }
        return null;
    }
    public static String createOfficerApplicationRequest(String officerID, String projectID) throws ModelNotFoundException, ModelAlreadyExistsException {
        // Check if the officer is already an officer in the project
        Project project = ProjectManager.getByID(projectID);
        if (project.getOfficerIDs().contains(officerID)) {
            System.out.println("Officer is already an officer in the project. Cannot apply for the project.");
            return null;

        }
        // Create a new OfficerApplicationRequest
        String requestID = getNewRequestID();
        OfficerApplicationRequest request = new OfficerApplicationRequest(requestID, projectID, officerID);
        RequestRepository.getInstance().add(request);
        return requestID;

    }
}
