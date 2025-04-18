package model.project;

import model.Displayable;
import model.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Project implements Model, Displayable {
    String projectID;
    String projectTitle;
    String neighbourhood;
    Integer twoRoomFlatAvailable;
    Integer threeRoomFlatAvailable;
    Double twoRoomFlatPrice;
    Double threeRoomFlatPrice;
    LocalDate applicationOpeningDate;
    LocalDate applicationClosingDate;
    String managerNRIC;
    List<String> officerIDs;
    Boolean visibility;

    public Project(String projectID, String projectTitle, String neighbourhood,
                   LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                   Integer twoRoomFlatAvailable, Integer threeRoomFlatAvailable,
                   Double twoRoomFlatPrice, Double threeRoomFlatPrice,String managerNRIC,List<String> officerIDs, Boolean visibility) {
             this.projectID = projectID;
             this.projectTitle = projectTitle;
             this.neighbourhood = neighbourhood;
             this.managerNRIC = managerNRIC;
             this.applicationOpeningDate = applicationOpeningDate;
             this.applicationClosingDate = applicationClosingDate;
             this.twoRoomFlatAvailable = twoRoomFlatAvailable;
             this.threeRoomFlatAvailable = threeRoomFlatAvailable;
             this.twoRoomFlatPrice = twoRoomFlatPrice;
             this.threeRoomFlatPrice = threeRoomFlatPrice;
             this.officerIDs = officerIDs;
                this.visibility = visibility;
    }
    /**
     * Get the ID of the project
     * @param map the map of the project
     */
    public Project(Map<String, String> map) {
        fromMap(map);
    }

    public Boolean getVisibility() {
        return visibility;
    }
    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }
    public String getManagerNRIC() {
        return managerNRIC;
    }
    public void setManagerNRIC(String managerNRIC) {
        this.managerNRIC = managerNRIC;
    }
    public String getProjectTitle() {
        return projectTitle;
    }
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }
    public Integer getTwoRoomFlatAvailable() {
        return twoRoomFlatAvailable;
    }
    public void setTwoRoomFlatAvailable(Integer twoRoomFlatAvailable) {
        this.twoRoomFlatAvailable = twoRoomFlatAvailable;
    }
    public Integer getThreeRoomFlatAvailable() {
        return threeRoomFlatAvailable;
    }
    public void setThreeRoomFlatAvailable(Integer threeRoomFlatAvailable) {
        this.threeRoomFlatAvailable = threeRoomFlatAvailable;
    }
    public Double getTwoRoomFlatPrice() {
        return twoRoomFlatPrice;
    }
    public void setTwoRoomFlatPrice(Double twoRoomFlatPrice) {
        this.twoRoomFlatPrice = twoRoomFlatPrice;
    }
    public Double getThreeRoomFlatPrice() {
        return threeRoomFlatPrice;
    }
    public void setThreeRoomFlatPrice(Double threeRoomFlatPrice) {
        this.threeRoomFlatPrice = threeRoomFlatPrice;
    }
    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }
    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }
    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }
    public void setApplicationClosingDate(LocalDate applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }
    public List<String> getOfficerIDs() {
        return officerIDs;
    }
    public String getManagerID() {
        return managerNRIC;
    }
    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
    public void setOfficerIDs(List<String> officerIDs) {
        this.officerIDs = officerIDs;
    }
    public Boolean addOfficerID(String officerID) {
        if (officerIDs.contains(officerID) || officerIDs.size() >= 10) {
            return false;
        } else {
            officerIDs.add(officerID);
            return true;
        }
    }

    @Override
    public String getID() {
        return projectID;
    }
    // Output the project contents nicely TODO
    @Override
    public String getDisplayableString() {
        // Print table header
        System.out.println("┌──────────┬───────────────────────┬───────────────────────┬──────────────┬──────────────┬─────────────────┬─────────────────┐");
        System.out.printf("│ %-8s │ %-21s │ %-21s │ %-12s │ %-12s │ %-15s │ %-15s │\n",
                "ID", "Title", "Neighbourhood", "Open Date", "Close Date", "2-Room ($, Avail)", "3-Room ($, Avail)");
        System.out.println("├──────────┼───────────────────────┼───────────────────────┼──────────────┼──────────────┼─────────────────┼─────────────────┤");

        // Print each project
        System.out.printf("│ %-8s │ %-21s │ %-21s │ %-12s │ %-12s │ $%-6.2f, %-5d │ $%-6.2f, %-5d │\n",
                this.projectID,
                this.projectTitle,
                this.neighbourhood,
                this.applicationOpeningDate,
                this.applicationClosingDate,
                this.twoRoomFlatPrice,
                this.twoRoomFlatAvailable,
                this.threeRoomFlatPrice,
                this.threeRoomFlatAvailable);

        // Print table footer
        System.out.println("└──────────┴───────────────────────┴───────────────────────┴──────────────┴──────────────┴─────────────────┴─────────────────┘");

        return ""; // Or return the formatted string instead of printing, if needed
    }

    @Override
    public String getSplitter() {
        return "================================================================";
    }


    public int getTotalFlatsAvailable() {
        return this.twoRoomFlatAvailable + this.threeRoomFlatAvailable;
    }
}
