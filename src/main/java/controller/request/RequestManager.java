package controller.request;

import controller.project.ProjectManager;
import model.project.Project;
import model.project.ProjectStatus;
import model.request.*;
import model.user.Applicant;
import model.user.ApplicantStatus;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * RequestManager class for handling various types of requests
 */
public class RequestManager {
    private final ProjectManager projectManager;

    /**
     * Constructor for RequestManager
     */
    public RequestManager() {
        this.projectManager = new ProjectManager();
    }

    /**
     * Gets the request ID of the next request
     *
     * @return the request ID of the next request
     */
    public String getNewRequestID() {
        int max = 0;
        for (Request request : RequestRepository.getInstance().getAll()) {
            int id = Integer.parseInt(request.getID().substring(1));
            if (id > max) {
                max = id;
            }
        }
        return "R" + (max + 1);
    }


    public List<Request> getProjectApplicationRequests() {
        List<Request> requests = new ArrayList<>();
        for (Request request : RequestRepository.getInstance().getAll()) {
            if (request.getRequestType() == RequestType.PROJECT_APPLICATION_REQUEST) {
                requests.add(request);
            }
        }
        return requests;
    }
    public Request getProjectApplicationRequestByApplicantID(String applicantID) {
        for (Request request : RequestRepository.getInstance().getAll()) {
            if (request.getRequestType() == RequestType.PROJECT_APPLICATION_REQUEST) {
                ProjectApplicationRequest applicationRequest = (ProjectApplicationRequest) request;
                if (applicationRequest.getApplicantID().equals(applicantID)) {
                    return request;
                }
            }
        }
        return null;
    }
    public void approveRequestForStatus(String requestID) throws ModelNotFoundException {
        Request request = RequestRepository.getInstance().getByID(requestID);
        request.setStatus(RequestStatus.APPROVED);
        RequestRepository.getInstance().update(request);
    }

    /**
     * Rejects a request by updating its status
     *
     * @param requestID the request ID
     * @throws ModelNotFoundException if the request is not found
     */
    public void rejectRequestForStatus(String requestID) throws ModelNotFoundException {
        Request request = RequestRepository.getInstance().getByID(requestID);
        request.setStatus(RequestStatus.REJECTED);
        RequestRepository.getInstance().update(request);
    }

    /**
     * Handles officer request approval
     *
     * @param request the officer request
     * @throws IllegalArgumentException if the request is not an OfficerRequest
     */
    private void approveOfficerRequest(Request request) {
        if (request instanceof OfficerApplicationRequest officerRequest) {
            try {
                // Get the officer and project
                Officer officer = OfficerRepository.getInstance().getByNRIC(officerRequest.getNric());
                Project project = projectManager.getProjectByID(officerRequest.getProjectID());

                if (officer == null || project == null) {
                    throw new ModelNotFoundException("Officer or Project not found");
                }

                // Add officer to project
                project.addOfficer(officer.getID());
                ProjectRepository.getInstance().update(project);

                // Add project to officer's list of projects in charge using the new helper method
                officer.addProject(project.getID());
                OfficerRepository.getInstance().update(officer);

                // Mark the request as approved
                officerRequest.setStatus(RequestStatus.APPROVED);
                RequestRepository.getInstance().update(officerRequest);
                
                System.out.println("Officer " + officer.getName() + " has been assigned to project " + project.getProjectName());
            } catch (ModelNotFoundException e) {
                System.err.println("Error assigning officer to project: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Request is not an OfficerRequest");
        }
    }

    /**
     * Handles project application request approval
     *
     * @param request the project application request
     * @throws IllegalArgumentException if the request is not a ProjectApplicationRequest
     */
    private void approveProjectApplicationRequest(Request request) {
        if (request instanceof ProjectApplicationRequest applicationRequest) {
            String projectID = applicationRequest.getProjectID();
            Applicant applicant = ApplicantRepository.getInstance().getByID(applicationRequest.getApplicantID());

            try {
                Project project = projectManager.getProjectByID(projectID);

                // Update applicant status and project information
                applicant.setStatus(ApplicantStatus.REGISTERED);
                applicant.setProject(project.getProjectName());

                // Update flat availability based on selected room type
                if (applicationRequest.getRoomType() == RoomType.TWO_ROOM_FLAT) {
                    project.setTwoRoomFlatsAvailable(project.getTwoRoomFlatsAvailable() - 1);
                } else {
                    project.setThreeRoomFlatsAvailable(project.getThreeRoomFlatsAvailable() - 1);
                }

                // Save changes
                applicant.setProject(project.getProjectName());
                ApplicantRepository.getInstance().update(applicant);
                projectManager.updateProject(project);

            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Request is not a ProjectApplicationRequest");
        }
    }

    /**
     * Handles project deregistration request approval
     *
     * @param request the project deregistration request
     * @throws IllegalArgumentException if the request is not a ProjectDeregistrationRequest
     */
    private void approveProjectDeregistrationRequest(Request request) {
        if (request instanceof ProjectDeregistrationRequest deregistrationRequest) {
            String projectID = deregistrationRequest.getProjectID();
            String applicantID = deregistrationRequest.getApplicantID();
            Applicant applicant = ApplicantRepository.getInstance().getByID(applicantID);
            
            try {
                Project project = projectManager.getProjectByID(projectID);
                
                // Find original application request to determine room type
                String originalRequestID = deregistrationRequest.getOriginalRequestID();
                Request originalRequest = null;
                
                try {
                    // Try to get by original request ID if available
                    if (originalRequestID != null && !originalRequestID.isEmpty()) {
                        originalRequest = RequestRepository.getInstance().getByID(originalRequestID);
                    }
                } catch (ModelNotFoundException e) {
                    // If original request not found, search for any application request from this applicant
                    originalRequest = getProjectApplicationRequestByApplicantID(applicantID);
                }
                
                // Add back the housing slot to the project
                if (originalRequest instanceof ProjectApplicationRequest applicationRequest) {
                    RoomType roomType = applicationRequest.getRoomType();
                    
                    // Increment available flats based on the originally selected room type
                    if (roomType == RoomType.TWO_ROOM_FLAT) {
                        project.setTwoRoomFlatsAvailable(project.getTwoRoomFlatsAvailable() + 1);
                        System.out.println("Added one 2-room flat back to the project inventory.");
                    } else if (roomType == RoomType.THREE_ROOM_FLAT) {
                        project.setThreeRoomFlatsAvailable(project.getThreeRoomFlatsAvailable() + 1);
                        System.out.println("Added one 3-room flat back to the project inventory.");
                    }
                    
                    // Save changes to the project
                    projectManager.updateProject(project);
                } else {
                    // Cannot determine room type, log this situation
                    System.out.println("Warning: Cannot determine room type for deregistration. Housing slot not restored.");
                }
                
                // Update applicant status
                applicant.setStatus(ApplicantStatus.UNREGISTERED);
                applicant.setProject(null);
                
                // Save changes to applicant
                ApplicantRepository.getInstance().update(applicant);
                
            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Request is not a ProjectDeregistrationRequest");
        }
    }

    /**
     * Handles project booking request approval
     *
     * @param request the project booking request
     * @throws IllegalArgumentException if the request is not a ProjectBookingRequest
     */
    private void approveProjectBookingRequest(Request request) throws ModelNotFoundException {
        if (request instanceof ProjectBookingRequest bookingRequest) {
            String projectID = bookingRequest.getProjectID();
            List<String> officerIDs = bookingRequest.getOfficerIDs();
            Applicant applicant = ApplicantRepository.getInstance().getByID(bookingRequest.getApplicantID());
            
            // Check if applicant's status is REGISTERED before allowing booking
            if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
                System.out.println("Error: Cannot approve booking request. Applicant's status must be REGISTERED. Current status: " + applicant.getStatus());
                // Update request status to REJECTED
                bookingRequest.setStatus(RequestStatus.REJECTED);
                RequestRepository.getInstance().update(bookingRequest);
                return;
            }
            
            // Update applicant status to BOOKED since they're currently REGISTERED
            applicant.setStatus(ApplicantStatus.BOOKED);
            // Save changes
            ApplicantRepository.getInstance().update(applicant);
            
            // Update request status to APPROVED
            bookingRequest.setStatus(RequestStatus.APPROVED);
            RequestRepository.getInstance().update(bookingRequest);
            
            System.out.println("Booking request approved successfully. Applicant status updated to BOOKED.");
        } else {
            throw new IllegalArgumentException("Request is not a ProjectBookingRequest");
        }
    }

    /**
     * Approves a request and handles the specific logic based on request type
     *
     * @param request the request to approve
     * @throws ModelNotFoundException if entities referenced in the request are not found
     */
    public void approveRequest(Request request) throws ModelNotFoundException {
        switch (request.getRequestType()) {
            case OFFICER_REQUEST -> approveOfficerRequest(request);
            case PROJECT_APPLICATION_REQUEST -> approveProjectApplicationRequest(request);
            case PROJECT_DEREGISTRATION_REQUEST -> approveProjectDeregistrationRequest(request);
            case PROJECT_BOOKING_REQUEST -> approveProjectBookingRequest(request);
        }
    }

    /**
     * Approves a request by ID
     *
     * @param requestID the ID of the request to approve
     */
    public void approveRequest(String requestID) {
        try {
            Request request = RequestRepository.getInstance().getByID(requestID);
            approveRequest(request);
            // Don't call approveRequestForStatus again since approveOfficerRequest already sets status and updates
        } catch (ModelNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rejects an application request and resets related entities
     *
     * @param request the request to reject
     */
    private void rejectProjectApplicationRequest(Request request) {
        if (request instanceof ProjectApplicationRequest applicationRequest) {
            Applicant applicant = ApplicantRepository.getInstance().getByID(applicationRequest.getApplicantID());

            // Reset applicant status
            applicant.setStatus(ApplicantStatus.UNREGISTERED);
            ApplicantRepository.getInstance().update(applicant);
        } else {
            throw new IllegalArgumentException("Request is not a ProjectApplicationRequest");
        }
    }

    /**
     * Rejects a request
     *
     * @param requestID the ID of the request to reject
     * @throws PageBackException if the request is not in PENDING state
     */
    public void rejectRequest(String requestID) throws PageBackException {
        try {
            Request request = RequestRepository.getInstance().getByID(requestID);

            if (request.getStatus() != RequestStatus.PENDING) {
                System.out.println("Request is not pending");
                System.out.println("Press enter to go back.");
                new Scanner(System.in).nextLine();
                throw new PageBackException();
            }

            if (Objects.requireNonNull(request.getRequestType()) == RequestType.PROJECT_APPLICATION_REQUEST) {
                rejectProjectApplicationRequest(request);
            }

            rejectRequestForStatus(requestID);

        } catch (ModelNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a request by ID
     *
     * @param requestID the request ID
     * @return the request
     * @throws ModelNotFoundException if the request is not found
     */
    public Request getRequest(String requestID) throws ModelNotFoundException {
        return RequestRepository.getInstance().getByID(requestID);
    }
}