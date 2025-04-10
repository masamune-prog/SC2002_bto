package model.enquiry;
import model.Model;

public class Enquiry implements Model {
    private String question;
    private String answer;
    private String EnquiryID;

    public Enquiry(String EnquiryID, String question, String answer) {
        this.EnquiryID = EnquiryID;
        this.question = question;
        this.answer = answer;
    }

    // Getters
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getEnquiryID() {
        return EnquiryID;
    }

    // Setters
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setEnquiryID(String EnquiryID) {
        this.EnquiryID = EnquiryID;
    }

    @Override
    public String getID() {
        return EnquiryID;
    }
}