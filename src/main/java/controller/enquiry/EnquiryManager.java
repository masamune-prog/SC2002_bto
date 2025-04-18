package controller.enquiry;
import repository.enquiry.EnquiryRepository;
import model.enquiry.Enquiry;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class EnquiryManager {
   public static void createNewEnquiry(String enquiryID, String enquiryTitle, String creatorID, String content, String answer, Boolean answered) throws ModelAlreadyExistsException {
         // Create a new enquiry object
         Enquiry enquiry = new Enquiry(enquiryID, enquiryTitle, creatorID, content, answer, answered);
            // Add the enquiry to the database
            EnquiryRepository.getInstance().add(enquiry);

   }
    public static String createNewEnquiry( String enquiryTitle, String creatorID, String content, String answer, Boolean answered) throws ModelAlreadyExistsException {
        // Create a new enquiry object
        String enquiryID = EnquiryManager.getNewEnquiryID();
        System.out.println("Enquiry ID: " + enquiryID);
        Enquiry enquiry = new Enquiry(enquiryID, enquiryTitle, creatorID, content, answer, answered);
        // Add the enquiry to the database
        EnquiryRepository.getInstance().add(enquiry);
        return enquiryID;

    }

    public static Enquiry getEnquiryByID(String enquiryID) throws ModelNotFoundException {
            return EnquiryRepository.getInstance().getByID(enquiryID);
    }
    public static String getNewEnquiryID() {
        // Generate a new enquiry ID based on the max ID in the repository
        //loop to check
        if(EnquiryRepository.getInstance().getAll().isEmpty()){
            return "E1"; // Start with E1 if no enquiries exist
        }
        String maxID = null;
        for (Enquiry enquiry : EnquiryRepository.getInstance().getAll()) {
            if (maxID == null || enquiry.getID().compareTo(maxID) > 0) {
                maxID = enquiry.getID();
            }
        }
        if (maxID == null) {
            return "E1"; // Start with E1 if no enquiries exist
        } else {
            int newID = Integer.parseInt(maxID.substring(1)) + 1; // Increment the ID
            return "E" + newID; // Return the new ID
        }
    }
    public static void updateEnquiry(String enquiryID, String question, String answer) throws ModelNotFoundException {
        Enquiry enquiry = EnquiryRepository.getInstance().getByID(enquiryID);
        if (enquiry != null) {
            enquiry.setContent(question);
            enquiry.setAnswer(answer);
            EnquiryRepository.getInstance().update(enquiry);
        } else {
            throw new ModelNotFoundException("Enquiry with ID " + enquiryID + " not found");
        }
    }
    public static void deleteEnquiry(String enquiryID) throws ModelNotFoundException {
        Enquiry enquiry = EnquiryRepository.getInstance().getByID(enquiryID);
        if (enquiry != null) {
            EnquiryRepository.getInstance().remove(enquiryID);
        } else {
            throw new ModelNotFoundException("Enquiry with ID " + enquiryID + " not found");
        }
    }
    public static void answerEnquiry(String enquiryID, String answer) throws ModelNotFoundException {
        Enquiry enquiry = EnquiryRepository.getInstance().getByID(enquiryID);
        if (enquiry != null) {
            enquiry.setAnswer(answer);
            enquiry.setAnswered(true);
            EnquiryRepository.getInstance().update(enquiry);
        } else {
            throw new ModelNotFoundException("Enquiry with ID " + enquiryID + " not found");
        }
    }
    public static List<Enquiry> getAllEnquiries() {
        return EnquiryRepository.getInstance().getAll();
    }
    /**
     * Retrieves all enquiries that have been answered
     * @return List of answered enquiries
     */
    public static List<Enquiry> getAnsweredEnquiries() {
        List<Enquiry> allEnquiries = EnquiryRepository.getInstance().getAll();
        List<Enquiry> answeredEnquiries = new ArrayList<>();

        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getAnswered()) {
                answeredEnquiries.add(enquiry);
            }
        }

        return answeredEnquiries;
    }

    /**
     * Retrieves all enquiries that have not been answered
     * @return List of unanswered enquiries
     */
    public static List<Enquiry> getUnansweredEnquiries() {
        List<Enquiry> allEnquiries = EnquiryRepository.getInstance().getAll();
        List<Enquiry> unansweredEnquiries = new ArrayList<>();

        for (Enquiry enquiry : allEnquiries) {
            if (!enquiry.getAnswered()) {
                unansweredEnquiries.add(enquiry);
            }
        }

        return unansweredEnquiries;
    }



}
