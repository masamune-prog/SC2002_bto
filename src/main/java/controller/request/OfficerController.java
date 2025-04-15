package controller.request;

import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.request.RequestType;
import repository.request.RequestRepository;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OfficerController {
    private final RequestRepository requestRepository;

    public OfficerController() {
        this.requestRepository = RequestRepository.getInstance();
    }

    public List<OfficerApplicationRequest> getAllRequests() {
        List<OfficerApplicationRequest> officerRequests = new ArrayList<>();
        for (Request request : requestRepository.getAll()) {
            if (request.getRequestType() == RequestType.OFFICER_REQUEST && request instanceof OfficerApplicationRequest) {
                officerRequests.add((OfficerApplicationRequest) request);
            }
        }
        return officerRequests;
    }

    public boolean approveRequest(String requestId) {
        try {
            Request request = requestRepository.getByID(requestId);
            if (!(request instanceof OfficerApplicationRequest)) {
                System.err.println("Request is not an officer request");
                return false;
            }
            OfficerApplicationRequest officerRequest = (OfficerApplicationRequest) request;
            officerRequest.setStatus(RequestStatus.APPROVED);
            requestRepository.update(officerRequest);
            return true;
        } catch (ModelNotFoundException e) {
            System.err.println("Error approving request: " + e.getMessage());
            return false;
        }
    }
} 