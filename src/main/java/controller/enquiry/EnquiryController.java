package controller.enquiry;

import model.enquiry.Enquiry;
import repository.enquiry.EnquiryRepository;
import utils.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class EnquiryController {
    private final EnquiryRepository enquiryRepository;

    public EnquiryController() {
        this.enquiryRepository = EnquiryRepository.getInstance();
    }

    public List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiryRepository.getAll());
    }

    public boolean replyToEnquiry(String enquiryId, String reply) {
        try {
            Enquiry enquiry = enquiryRepository.getByID(enquiryId);
            enquiry.setAnswer(reply);
            enquiryRepository.update(enquiry);
            return true;
        } catch (ModelNotFoundException e) {
            System.err.println("Error replying to enquiry: " + e.getMessage());
            return false;
        }
    }
} 