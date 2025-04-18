package model.enquiry;

import model.Displayable;
import model.Model;

import java.util.Map;

public class Enquiry implements Model, Displayable {
    private String enquiryID;
    private String enquiryTitle;
    private String creatorID;
    private String content;
    private String answer;
    private Boolean answered;
    public Enquiry(String enquiryID,String enquiryTitle, String creatorID, String content, String answer, Boolean answered) {
        this.enquiryID = enquiryID;
        this.enquiryTitle = enquiryTitle;
        this.creatorID = creatorID;
        this.content = content;
        this.answer = answer;
        this.answered = answered;
    }
    public Enquiry(Map<String, String> map) {
        fromMap(map);
    }
    // Output the Enquiry contents nicely TODO
    public String getEnquiryTitle() {
        return enquiryTitle;
    }
    public String getCreatorID() {
        return creatorID;
    }
    public String getContent() {
        return content;
    }
    public String getAnswer() {
        return answer;
    }
    public Boolean getAnswered() {
        return answered;
    }
    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }
    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
    }
    public void setEnquiryTitle(String enquiryTitle) {
        this.enquiryTitle = enquiryTitle;
    }
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String getDisplayableString() {
        return "";
    }

    @Override
    public String getSplitter() {
        return "================================================================";
    }

    @Override
    public String getID() {
        return enquiryID;
    }

    public String getQuestion() {
        return content;
    }

    public boolean isAnswered() {
        return answered != null && answered;
    }
}
