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

    /**
     * Get the ID of the supervisor.
     * @return the ID of the supervisor.
     */
    String getManagerID();

    default String getSplitter() {
        return "====================================================";
    }
}