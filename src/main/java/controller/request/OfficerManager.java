package controller.request;

import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.util.List;

public class OfficerManager {
    public static String createOfficerApplicationRequest(String OfficerID, String projectID) throws ModelAlreadyExistsException {
        //get a requestID
        String requestID = RequestManager.getNewRequestID();
        //create a new request
        OfficerApplicationRequest request = new OfficerApplicationRequest(requestID, projectID, OfficerID);
        RequestRepository.getInstance().add(request);
        return requestID;
    }

    public static String getIDByOfficerName(String officerName) {
        //get the officerID by officerName
        List<Officer> officers = OfficerRepository.getInstance().getAll();
        for (Officer officer : officers) {
            if (officer.getName().equals(officerName)) {
                return officer.getID();
            }
        }
        return null;
    }
    public static Officer getOfficerByID(String officerID) throws ModelNotFoundException {
        return OfficerRepository.getInstance().getByID(officerID);
    }
    //get projects that the officer is in charge of
    public static List<String> getProjectsByOfficerID(String officerID) {
        List<Project> projects = ProjectRepository.getInstance().getAll();
        //create empty list of projects
        List<String> projectIDs = new java.util.ArrayList<>();
        for (Project project : projects) {
            if (project.getOfficerIDs().contains(officerID)) {
                projectIDs.add(project.getID());
            }
        }
        return projectIDs;
    }
    public static List<OfficerApplicationRequest> getOfficerApplicationsByOfficerID(String officerID) {
        List<Request> requests = RequestRepository.getInstance().getAll();
        //we loop through the requests and get the ones that are officer applications that contain the officerID
        return requests.stream()
                .filter(request -> request instanceof OfficerApplicationRequest)
                .map(request -> (OfficerApplicationRequest) request)
                .filter(request -> request.getOfficerID().equals(officerID))
                .toList();
    }
}
