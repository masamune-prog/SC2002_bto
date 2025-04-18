package utils.exception;

/**
 Thrown when a supervisor has reached the maximum number of students they can supervise.

 */
public class ProjectOfficerLimitExceed extends Exception {
    /*
    Constructs a new SupervisorStudentsLimitExceedException with a default message.
    */
    public ProjectOfficerLimitExceed() {
        super("Project has reached the maximum number of students");
    }
}
