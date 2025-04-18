package model.request;

import model.Displayable;
import model.Model;

/**
 * This interface represents a request.
 */
public interface Request extends Model, Displayable {
    /**
     * Get the ID of the request.
     */
    String getID();

    /**
     * Get the ID of the project.
     *
     * @return the ID of the project.
     */
    String getProjectID();



    /**
     * Get the status of the request.
     *
     * @return the status of the request.
     */
    RequestStatus getStatus();

    /**
     * Set the status of the request.
     *
     * @param status the status of the request.
     */
    void setStatus(RequestStatus status);

    /**
     * Get the type of the request.
     *
     * @return the type of the request.
     */
    RequestType getRequestType();

    //removed some methods hopefully not needed
    default void printRequest() {
        System.out.println(getSplitter());
        System.out.println("Request ID: " + getID());
        System.out.println("Project ID: " + getProjectID());
        System.out.println("Status: " + getStatus());
        System.out.println("Request Type: " + getRequestType());
        System.out.println(getSplitter());
    }

    default String getSplitter() {
        return "====================================================";
    }
}
