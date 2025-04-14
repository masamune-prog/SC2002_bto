package controller.enquiry;

import model.enquiry.Enquiry;
import repository.enquiry.EnquiryRepository;
import utils.exception.ModelNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the creation, answering, and modification of enquiries
 */
public class EnquiryManager {
    private final EnquiryRepository enquiryRepository;

    /**
     * Constructs an EnquiryManager with default repository
     */
    public EnquiryManager() {
        this.enquiryRepository = EnquiryRepository.getInstance();
    }

    /**
     * Creates a new enquiry with the given details
     *
     * @param enquiryID unique identifier for the enquiry
     * @param question  the question text
     * @return the created enquiry
     */
    public Enquiry createEnquiry(String enquiryID, String question, String creatorID) {
        validateEnquiryData(question);

        Enquiry enquiry = new Enquiry(enquiryID, question, creatorID);
        enquiryRepository.getAll().add(enquiry);
        return enquiry;
    }



    /**
     * Answers an existing enquiry
     *
     * @param enquiry the enquiry to answer
     * @param answer  the answer text
     * @return true if successful, false otherwise
     */
    public boolean answerEnquiry(Enquiry enquiry, String answer) {
        if (enquiry == null || answer == null || answer.trim().isEmpty()) {
            return false;
        }

        enquiry.setAnswer(answer);
        return true;
    }

    /**
     * Updates an existing enquiry's question
     *
     * @param enquiry  the enquiry to update
     * @param question new question text
     * @return true if successful, false otherwise
     */
    public boolean updateEnquiry(Enquiry enquiry, String question) {
        if (enquiry == null) {
            return false;
        }

        if (question != null && !question.trim().isEmpty()) {
            enquiry.setQuestion(question);
            return true;
        }

        return false;
    }

    /**
     * Gets an enquiry by its ID
     *
     * @param enquiryID the ID of the enquiry to find
     * @return the enquiry if found
     * @throws ModelNotFoundException if no enquiry with the ID exists
     */
    public Enquiry getEnquiryByID(String enquiryID) throws ModelNotFoundException {
        return enquiryRepository.getByID(enquiryID);
    }

    /**
     * Gets all enquiries that have been answered
     *
     * @return list of answered enquiries
     */
    public List<Enquiry> getAnsweredEnquiries() {
        return enquiryRepository.getAll().stream()
                .filter(enquiry -> enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Gets all enquiries that are pending answers
     *
     * @return list of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return enquiryRepository.getAll().stream()
                .filter(enquiry -> enquiry.getAnswer() == null || enquiry.getAnswer().isEmpty())
                .collect(Collectors.toList());
    }
    public static List<Enquiry> getAllEnquiries() {
        return EnquiryRepository.getInstance().getAll();
    }
    /**
     * Validates enquiry data before creation or update
     *
     * @param question the enquiry question
     */
    private void validateEnquiryData(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Enquiry question cannot be empty");
        }
    }
}