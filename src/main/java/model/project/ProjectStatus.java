package model.project;

/**
 * This enum represents the status of a project
 */
public enum ProjectStatus {
    /**
     * The project is available for allocation
     */
    AVAILABLE,
    /**
     * The project is unavailable for allocation
     */
    UNAVAILABLE;

    /**
     * Get the status of the project in a colorful string
     * @return the status of the project in a colorful string
     */
    public String showColorfulString() {
        return switch (this) {
            case AVAILABLE -> "\u001B[32m" + this + "\u001B[0m"; // green
            case UNAVAILABLE -> "\u001B[31m" + this + "\u001B[0m"; // red
        };
    }
}