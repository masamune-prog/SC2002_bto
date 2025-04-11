package repository.enquiry;

import model.enquiry.Enquiry;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnquiryRepository extends Repository<Enquiry> {
    private static EnquiryRepository instance;

    protected EnquiryRepository() {
        super();
    }

    public static EnquiryRepository getInstance() {
        if (instance == null) {
            instance = new EnquiryRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        // No file path needed as we don't use CSV
        return null;
    }

    @Override
    public void load() {
        // No loading from CSV needed
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        // No CSV mapping needed
    }

    public Enquiry getByID(String enquiryID) {
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getEnquiryID().equals(enquiryID)) {
                return enquiry;
            }
        }
        return null;
    }

    public List<Enquiry> getUnansweredEnquiries() {
        List<Enquiry> results = new ArrayList<>();
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getAnswer() == null || enquiry.getAnswer().isEmpty()) {
                results.add(enquiry);
            }
        }
        return results;
    }

    public Enquiry createEnquiry(String question) {
        String newID = "E" + (getAll().size() + 1);
        Enquiry enquiry = new Enquiry(newID, question, "");
        getAll().add(enquiry);
        return enquiry;
    }

    public void answerEnquiry(String enquiryID, String answer) {
        Enquiry enquiry = getByID(enquiryID);
        if (enquiry != null) {
            enquiry.setAnswer(answer);
        }
    }

    public void deleteEnquiry(String enquiryID) {
        Enquiry enquiry = getByID(enquiryID);
        if (enquiry != null) {
            getAll().remove(enquiry);
        }
    }

    public List<Enquiry> searchByKeyword(String keyword) {
        List<Enquiry> results = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }

        String lowerKeyword = keyword.toLowerCase();
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getQuestion().toLowerCase().contains(lowerKeyword)) {
                results.add(enquiry);
            }
        }
        return results;
    }
}