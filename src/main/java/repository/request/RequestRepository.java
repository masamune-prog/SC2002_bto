package repository.request;

import model.project.Project;
import model.request.*;
import model.user.Officer;
import repository.Repository;
import repository.user.OfficerRepository;
import repository.project.ProjectRepository;
import utils.config.Location;
import utils.exception.ModelNotFoundException;
import utils.iocontrol.CSVReader;
import controller.project.ProjectManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository for storing and retrieving Request objects
 */
public class RequestRepository extends Repository<Request> {
    private static final String FILE_PATH = "/RequestList.txt";  // Change to .txt extension
    private static RequestRepository instance;
    private final List<Request> requests;
    private final ProjectManager projectManager;

    /**
     * Private constructor for singleton pattern
     */
    private RequestRepository() {
        this.requests = new ArrayList<>();
        this.projectManager = new ProjectManager();
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
        File file = new File(getFilePath());

        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                System.out.println("Created new request file: " + getFilePath());
            } catch (Exception e) {
                System.err.println("Error creating request file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Map<String, String> map = parseLine(line);
                        Request request = createRequestFromMap(map);
                        if (request != null) {
                            getAll().add(request);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line);
                        System.err.println("Error details: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath()))) {
            for (Request request : getAll()) {
                writer.write(formatRequest(request));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private String formatRequest(Request request) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = request.toMap();
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(entry.getKey()).append("=").append(escapeSpecialChars(entry.getValue()));
            }
        }
        
        return sb.toString();
    }

    private String escapeSpecialChars(String value) {
        if (value == null) return "";
        return value.replace("|", "\\|").replace("=", "\\=");
    }

    private Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        
        // Split by unescaped | characters
        String[] parts = line.split("(?<!\\\\)\\|");
        
        for (String part : parts) {
            // Split by unescaped = character
            String[] keyValue = part.split("(?<!\\\\)=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                // Unescape special characters
                String value = keyValue[1].replace("\\|", "|").replace("\\=", "=");
                map.put(key, value);
            }
        }
        
        return map;
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
        String requestTypeStr = map.get("requestType");
        if (requestTypeStr == null) {
            return null;
        }
        
        RequestType requestType;
        try {
            requestType = RequestType.valueOf(requestTypeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid request type: " + requestTypeStr);
            return null;
        }
        
        switch (requestType) {
            case OFFICER_REQUEST:
                return new OfficerApplicationRequest(map);
            case PROJECT_APPLICATION_REQUEST:
                return new ProjectApplicationRequest(map);
            case PROJECT_BOOKING_REQUEST:
                return new ProjectBookingRequest(map);
            case PROJECT_DEREGISTRATION_REQUEST:
                return new ProjectDeregistrationRequest(map);
            default:
                System.err.println("Unsupported request type: " + requestType);
                return null;
        }
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
            save(); // Save changes to file
        }
    }

    /**
     * Removes a request from the repository
     *
     * @param request the request to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean remove(Request request) {
        boolean result = requests.remove(request);
        if (result) {
            save(); // Save changes to file
        }
        return result;
    }

    /**
     * Clears all requests from the repository
     */
    public void clear() {
        requests.clear();
        save(); // Save changes to file
    }

    /**
     * Find requests matching the specified rules
     * 
     * @param rules predicates to filter requests
     * @return list of requests matching all rules
     */
    @SafeVarargs
    public final List<Request> findByRules(Predicate<Request>... rules) {
        List<Request> result = new ArrayList<>(getAll());
        
        for (Predicate<Request> rule : rules) {
            result = result.stream().filter(rule).collect(Collectors.toList());
        }
        
        return result;
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

    /**
     * Gets booking requests related to projects the specified officer is assigned to.
     *
     * @param officerID The ID of the officer.
     * @return A list of relevant ProjectBookingRequests.
     * @throws ModelNotFoundException if the officer is not found (though currently handled by returning empty list).
     */
    public List<Request> getBookingRequestsByOfficer(String officerID) throws ModelNotFoundException {
        List<Request> officerBookingRequests = new ArrayList<>();
        Officer officer = null;
        try {
            officer = OfficerRepository.getInstance().getByID(officerID);
        } catch (ModelNotFoundException e) {
            System.err.println("Attempted to get booking requests for non-existent officer ID: " + officerID);
            return officerBookingRequests; // Return empty list if officer not found
        }

        for (Request request : getAll()) {
            if (request instanceof ProjectBookingRequest bookingRequest) {
                String projectID = bookingRequest.getProjectID();
                try {
                    Project project = projectManager.getProjectByID(projectID);
                    // Check if the project exists and if the officer is assigned to it
                    if (project != null && project.hasOfficer(officerID)) {
                        officerBookingRequests.add(request);
                    }
                } catch (ModelNotFoundException e) {
                    // Project associated with the request not found, skip this request.
                    System.err.println("Booking request " + bookingRequest.getID() + " references non-existent project ID: " + projectID);
                }
            }
        }
        return officerBookingRequests;
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
                save(); // Save changes to file
                return;
            }
        }
        throw new ModelNotFoundException("Request not found for update");
    }
}