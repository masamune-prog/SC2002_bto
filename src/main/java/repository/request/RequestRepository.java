package repository.request;

import model.request.Request;
import repository.Repository;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository for storing and retrieving Request objects
 */
public class RequestRepository extends Repository<Request> {
    private static RequestRepository instance;
    private final List<Request> requests;

    /**
     * Private constructor for singleton pattern
     */
    private RequestRepository() {
        this.requests = new ArrayList<>();
    }

    @Override
    public String getFilePath() {
        return "";
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
    protected void setAll(List<Map<String, String>> listOfMappableObjects) {

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
}