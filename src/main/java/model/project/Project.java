package model.project;

import model.user.Manager;
import model.user.Officer;
import model.Model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Model;
public class Project implements Model {
    //ProjectStatus status;
    private String projectID;
    private String targetedUserGroup;
    private boolean visibility;
    private String projectName; // Project ID
    private String neighborhood;
    private Integer flatsAvailable; // Map to store flat types and their counts
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private Manager managerInCharge;
    private List<Officer> assignedOfficers;
    private int availableOfficerSlots;

    /**
     * Constructor for creating a new Project
     *
     * @param targetedUserGroup The targeted demographic for this project
     * @param visibility Whether the project is visible to applicants
     * @param projectName Project name/identifier
     * @param neighborhood The neighborhood location (e.g., Yishun, Boon Lay)
     * @param applicationOpeningDate Date when applications open
     * @param applicationClosingDate Date when applications close
     * @param managerInCharge HDB Manager responsible for this project
     */
    public Project(String projectID,String targetedUserGroup, boolean visibility, String projectName,
                   String neighborhood, int flatsAvailable,
                   LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                   Manager managerInCharge) {
        this.projectID = projectID;
        this.targetedUserGroup = targetedUserGroup;
        this.visibility = visibility;
        this.projectName = projectName;
        this.neighborhood = neighborhood;

        this.flatsAvailable = flatsAvailable;

        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerInCharge = managerInCharge;
        this.assignedOfficers = new ArrayList<>();
        this.availableOfficerSlots = 10; // Maximum 10 slots
    }
    public Project(Map<String, String> map) {
        fromMap(map);
    }
    public String getTargetedUserGroup() {
        return targetedUserGroup;
    }

    public void setTargetedUserGroup(String targetedUserGroup) {
        this.targetedUserGroup = targetedUserGroup;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Integer getFlatsAvailable() {
        return flatsAvailable;
    }

    public void setFlatsAvailable(Integer flatsAvailable) {
        this.flatsAvailable = flatsAvailable;
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

    public Manager getManagerInCharge() {
        return managerInCharge;
    }

    public void setManagerInCharge(Manager managerInCharge) {
        this.managerInCharge = managerInCharge;
    }

    public List<Officer> getAssignedOfficers() {
        return assignedOfficers;
    }

    public boolean assignOfficer(Officer officer) {
        if (availableOfficerSlots > 0) {
            assignedOfficers.add(officer);
            availableOfficerSlots--;
            return true;
        }
        return false;
    }

    public boolean removeOfficer(Officer officer) {
        if (assignedOfficers.remove(officer)) {
            availableOfficerSlots++;
            return true;
        }
        return false;
    }

    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }

    @Override
    public String getID() {
        return projectID;
    }
}