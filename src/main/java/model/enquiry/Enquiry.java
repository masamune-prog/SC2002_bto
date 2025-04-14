package model.enquiry;
import model.Model;
import utils.iocontrol.Mappable;
import utils.parameters.EmptyID;

import java.util.Map;

public class Enquiry implements Model, Mappable {
    private String question;
    private String answer;
    private String EnquiryID;
    private String creatorID;

    public Enquiry(String EnquiryID, String question, String answer, String creatorID) {
        this.EnquiryID = EnquiryID;
        this.question = question;
        this.answer = answer;
        this.creatorID = creatorID;
    }

    public Enquiry(Map<String, String> map) {
        fromMap(map);
    }

    public Enquiry(String enquiryID, String question, String creatorID) {
        this.EnquiryID = enquiryID;
        this.question = question;
        this.answer = null; // Default to null if not provided
        this.creatorID = creatorID;
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

    public String getCreatorID() {
        return creatorID;
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

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    @Override
    public String getID() {
        return EnquiryID;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("enquiryID", EnquiryID != null ? EnquiryID : EmptyID.EMPTY_ID);
        map.put("question", question != null ? question : EmptyID.EMPTY_ID);
        map.put("answer", answer != null ? answer : EmptyID.EMPTY_ID);
        map.put("creatorID", creatorID != null ? creatorID : EmptyID.EMPTY_ID);
        return map;
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.EnquiryID = map.getOrDefault("enquiryID", EmptyID.EMPTY_ID);
        this.question = map.getOrDefault("question", EmptyID.EMPTY_ID);
        this.answer = map.getOrDefault("answer", EmptyID.EMPTY_ID);
        this.creatorID = map.getOrDefault("creatorID", EmptyID.EMPTY_ID);
    }

    @Override
    public String toString() {
        return String.format("Enquiry{ID='%s', question='%s', answer='%s', creatorID='%s'}", 
            EnquiryID, question, answer, creatorID);
    }
}