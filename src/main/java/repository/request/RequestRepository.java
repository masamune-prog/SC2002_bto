package repository.request;

import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.request.RequestType;
import repository.Repository;
import repository.user.OfficerRepository;
import repository.project.ProjectRepository;
import utils.config.Location;
import utils.exception.ModelNotFoundException;
import utils.iocontrol.CSVReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * Repository for storing and retrieving Request objects
 */
public class RequestRepository extends Repository<Request> {
    private static final String FILE_PATH = "/RequestList.csv";  // Fixed path separator
    private static RequestRepository instance;
    private final List<Request> requests;

    /**
     * Private constructor for singleton pattern
     */
    private RequestRepository() {
        this.requests = new ArrayList<>();
        load();
    }

    @Override
    public String getFilePath() {
        return Location.RESOURCE_LOCATION + FILE_PATH;
    }

    /**
     * Gets the singleton instance of the repository
     *
     * @return the repository instance
     */
    public static RequestRepository getInstance() {
        if (instance == null) {
            instance = new RequestRepository();
        }
        return instance;
    }

    /**
     * Gets all requests in the repository
     *
     * @return list of all requests
     */
    public List<Request> getAll() {
        return requests;
    }

    @Override
    public void load() {
        this.getAll().clear();
        String txtFilePath = getFilePath().replace(".csv", ".txt");
        File txtFile = new File(txtFilePath);

        try {
            if (txtFile.exists()) {
                // Load from .txt file
                load(txtFilePath);
                System.out.println("Loaded requests from: " + txtFilePath);
            } else {
                // If .txt doesn't exist, load from CSV
                System.out.println("No .txt file found, loading from CSV: " + getFilePath());
                List<List<String>> csvData = CSVReader.read(getFilePath(), true);
                if (csvData == null || csvData.isEmpty()) {
                    System.err.println("Warning: No data found in CSV file");
                    return;
                }

                List<Map<String, String>> mappedData = convertToMapList(csvData);
                setAll(mappedData);
                // Save to .txt file for future use
                save(txtFilePath);
                System.out.println("Created new .txt file: " + txtFilePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Map<String, String>> convertToMapList(List<List<String>> csvData) {
        List<Map<String, String>> result = new ArrayList<>();

        // Process each row (skip header row)
        for (int i = 1; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            if (row.size() < 5) {  // Check if row has all required fields
                System.err.println("Warning: Skipping invalid row - insufficient columns");
                continue;
            }

            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("requestID", row.get(0));
            rowMap.put("requestType", row.get(1));
            rowMap.put("status", row.get(2));
            rowMap.put("userName", row.get(3));  // Changed from userID to userName
            rowMap.put("nric", row.get(4));      // Changed from userType to nric

            // Add additional fields based on request type
            if (row.size() > 5) {
                rowMap.put("projectID", row.get(5));
            }

            result.add(rowMap);
        }

        return result;
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        getAll().clear();
        for (Map<String, String> map : listOfMappableObjects) {
            try {
                Request request = createRequestFromMap(map);
                if (request != null) {
                    getAll().add(request);
                }
            } catch (Exception e) {
                System.err.println("Error parsing request data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Request createRequestFromMap(Map<String, String> map) {
        String requestType = map.get("requestType");
        String requestID = map.get("requestID");
        String userName = map.get("userName");
        String nric = map.get("nric");
        String projectID = map.get("projectID");
        RequestStatus status = map.get("status") != null ?
                RequestStatus.valueOf(map.get("status")) :
                RequestStatus.PENDING;

        if (RequestType.valueOf(requestType) == RequestType.OFFICER_REQUEST) {
            return new OfficerApplicationRequest(requestID, userName, nric, projectID, status);
        } else if (RequestType.valueOf(requestType) == RequestType.PROJECT_BOOKING_REQUEST) {
            // Assuming ProjectBookingRequest has a similar constructor
            // If not, you'll need to implement this class
            return null; // Replace with actual implementation when available
        }
        return null;
    }

    /**
     * Gets a request by its ID
     *
     * @param requestID the ID of the request to find
     * @return the request with the specified ID
     * @throws ModelNotFoundException if no request with the ID exists
     */
    public Request getByID(String requestID) throws ModelNotFoundException {
        for (Request request : requests) {
            if (request.getID().equals(requestID)) {
                return request;
            }
        }
        throw new ModelNotFoundException("Request with ID " + requestID + " not found");
    }

    /**
     * Adds a request to the repository
     *
     * @param request the request to add
     */
    public void add(Request request) {
        if (request != null) {
            requests.add(request);
        }
    }

    /**
     * Removes a request from the repository
     *
     * @param request the request to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean remove(Request request) {
        return requests.remove(request);
    }

    /**
     * Clears all requests from the repository
     */
    public void clear() {
        requests.clear();
    }

    public List<Request> getRequestsByOfficer(String officerNRIC) {
        List<Request> officerRequests = new ArrayList<>();
        for (Request request : getAll()) {
            if (request instanceof OfficerApplicationRequest) {
                OfficerApplicationRequest officerRequest = (OfficerApplicationRequest) request;
                if (officerRequest.getNric().equals(officerNRIC)) {
                    officerRequests.add(request);
                }
            }
        }
        return officerRequests;
    }

    public List<Request> getBookingRequestsByOfficer(String officerNRIC) {
        // This needs to be implemented once ProjectBookingRequest is available
        return new ArrayList<>();
    }

    /**
     * Updates a request in the repository
     *
     * @param request the request to update
     * @throws ModelNotFoundException if the request is not found
     */
    public void update(Request request) throws ModelNotFoundException {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getID().equals(request.getID())) {
                requests.set(i, request);
                return;
            }
        }
        throw new ModelNotFoundException("Request not found for update");
    }
}