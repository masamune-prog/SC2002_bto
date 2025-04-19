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
     * the applicant has registered a project
     */
    SUCCESSFUL,
    /**
     * the applicant never register for any project
     */
    NO_REGISTRATION,
    /**
     * the applicant has successfully booked a flat
     */
    BOOKED,
    /**
     * Applicant Rejected
     */
    REJECTED,
}