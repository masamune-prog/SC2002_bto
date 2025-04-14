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

    /**
     * Approves a request by updating its status
     *
     * @param requestID the request ID
     * @throws ModelNotFoundException if the request is not found
     */
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
            String projectID = officerRequest.getProjectID();
            String officerID = officerRequest.getOfficerID();

            try {
                Project project = ProjectRepository.getInstance().getByID(projectID);
                Officer officer = OfficerRepository.getInstance().getByID(officerRequest.getOfficerID());
                projectManager.assignOfficerToProject(project, officer);
                ProjectRepository.getInstance().update(project);
            } catch (ModelNotFoundException e) {
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
                Project project = ProjectRepository.getInstance().getByID(projectID);

                // Update applicant status and project information
                applicant.setStatus(ApplicantStatus.REGISTERED);
                applicant.setProject(project.getProjectName());

                // Update flat availability based on selected room type
                // Update flat availability based on selected room type
                if (applicationRequest.getRoomType() == RoomType.TWO_ROOM_FLAT) {
                    project.setTwoRoomFlatsAvailable(project.getTwoRoomFlatsAvailable() - 1);
                } else {
                    project.setThreeRoomFlatsAvailable(project.getThreeRoomFlatsAvailable() - 1);
                }

                // Save changes
                //Write changes to Applicant
                applicant.setProject(project.getProjectName());
                ApplicantRepository.getInstance().update(applicant);
                ProjectRepository.getInstance().update(project);

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
            // Fix: Call getApplicantID() on the instance, not the class
            Applicant applicant = ApplicantRepository.getInstance().getByID(deregistrationRequest.getApplicantID());

            try {
                Project project = ProjectRepository.getInstance().getByID(projectID);

                // Update applicant status
                applicant.setStatus(ApplicantStatus.UNREGISTERED);
                applicant.setProject(null);

                // Save changes
                ApplicantRepository.getInstance().update(applicant);
                ProjectRepository.getInstance().update(project);

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
    private void approveProjectBookingRequest(Request request) {
        if (request instanceof ProjectBookingRequest bookingRequest) {
            String projectID = bookingRequest.getProjectID();
            Applicant applicant = ApplicantRepository.getInstance().getByID(bookingRequest.getApplicantID());

            try {
                Project project = ProjectRepository.getInstance().getByID(projectID);

                // Update applicant status
                applicant.setStatus(ApplicantStatus.REGISTERED);

                // Save changes
                ApplicantRepository.getInstance().update(applicant);

            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            }
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
            approveRequestForStatus(requestID);
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
            try {
                ApplicantRepository.getInstance().update(applicant);
            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            }
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