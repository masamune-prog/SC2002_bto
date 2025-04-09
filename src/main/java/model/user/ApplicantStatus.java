package model.user;

/**
 * This enum represents the status of a student
 */
public enum ApplicantStatus {
    /**
     * BTO request waiting for approval
     */
    PENDING,
    /**
     * the applicant has deregistered a project
     */
    DEREGISTERED,
    /**
     * the applicant has registered a project
     */
    REGISTERED,
    /**
     * the applicant never register for any project
     */
    UNREGISTERED
}