package model.project;

import model.user.Manager;
import model.user.Officer;
import model.Model;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Project implements Model {
    //ProjectStatus status;
    private String projectID;
    private String targetedUserGroup;
    private boolean visibility;
    private String projectName; // Project ID
    private String neighborhood;
    private int twoRoomFlatsAvailable;
    private int threeRoomFlatsAvailable;
    private double twoRoomFlatsPrice;
    private double threeRoomFlatsPrice;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private Manager managerInCharge;
    private List<Officer> assignedOfficers;
    private int numOfficers;

    /**
     * Constructor for creating a new Project
     */
    public Project(String projectID, String targetedUserGroup, boolean visibility, String projectName,
                   String neighborhood, int twoRoomFlatsAvailable, int threeRoomFlatsAvailable,
                   double twoRoomFlatsPrice, double threeRoomFlatsPrice,
                   LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                   Manager managerInCharge) {
        this.projectID = projectID;
        this.targetedUserGroup = targetedUserGroup;
        this.visibility = visibility;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.twoRoomFlatsAvailable = twoRoomFlatsAvailable;
        this.threeRoomFlatsAvailable = threeRoomFlatsAvailable;
        this.twoRoomFlatsPrice = twoRoomFlatsPrice;
        this.threeRoomFlatsPrice = threeRoomFlatsPrice;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerInCharge = managerInCharge;
        this.assignedOfficers = new ArrayList<>();
        this.numOfficers = 0; // Maximum 10 slots
    }

    public Project(Map<String, String> map) {
        fromMap(map);
    }

    public void fromMap(Map<String, String> map) {
        this.projectID = map.get("projectID");
        this.targetedUserGroup = map.get("targetedUserGroup");
        this.visibility = Boolean.parseBoolean(map.get("visibility"));
        this.projectName = map.get("projectName");
        this.neighborhood = map.get("neighborhood");

        try {
            this.twoRoomFlatsAvailable = Integer.parseInt(map.get("twoRoomFlatsAvailable"));
            this.threeRoomFlatsAvailable = Integer.parseInt(map.get("threeRoomFlatsAvailable"));
            this.twoRoomFlatsPrice = Double.parseDouble(map.get("twoRoomFlatsPrice"));
            this.threeRoomFlatsPrice = Double.parseDouble(map.get("threeRoomFlatsPrice"));
            this.numOfficers = Integer.parseInt(map.getOrDefault("numOfficerSlots", "0"));
        } catch (NumberFormatException e) {
            // Set defaults if parsing fails
            this.twoRoomFlatsAvailable = 0;
            this.threeRoomFlatsAvailable = 0;
            this.twoRoomFlatsPrice = 0.0;
            this.threeRoomFlatsPrice = 0.0;
            this.numOfficers = 0;
        }

        // Parse dates if available
        String openingDate = map.get("applicationOpeningDate");
        String closingDate = map.get("applicationClosingDate");
        if (openingDate != null) {
            this.applicationOpeningDate = LocalDate.parse(openingDate);
        }
        if (closingDate != null) {
            this.applicationClosingDate = LocalDate.parse(closingDate);
        }

        this.assignedOfficers = new ArrayList<>();

        // Load manager by name if provided
        String managerName = map.get("managerInCharge");
        if (managerName != null && !managerName.isEmpty()) {
            setManagerByName(managerName);
        }
        //Load officers by name from the list provided
        // the list is comma separated
        String officerNames = map.get("officer");
        if (officerNames != null && !officerNames.isEmpty()) {
            String[] officerNameArray = officerNames.split(",");
            for (String officerName : officerNameArray) {
                Officer officer = OfficerRepository.getInstance().getOfficerByName(officerName.trim());
                if (officer != null) {
                    assignOfficer(officer);
                }
            }
        }
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

    public int getTwoRoomFlatsAvailable() {
        return twoRoomFlatsAvailable;
    }

    public void setTwoRoomFlatsAvailable(int twoRoomFlatsAvailable) {
        this.twoRoomFlatsAvailable = twoRoomFlatsAvailable;
    }

    public int getThreeRoomFlatsAvailable() {
        return threeRoomFlatsAvailable;
    }

    public void setThreeRoomFlatsAvailable(int threeRoomFlatsAvailable) {
        this.threeRoomFlatsAvailable = threeRoomFlatsAvailable;
    }

    public double getTwoRoomFlatsPrice() {
        return twoRoomFlatsPrice;
    }

    public void setTwoRoomFlatsPrice(double twoRoomFlatsPrice) {
        this.twoRoomFlatsPrice = twoRoomFlatsPrice;
    }

    public double getThreeRoomFlatsPrice() {
        return threeRoomFlatsPrice;
    }

    public void setThreeRoomFlatsPrice(double threeRoomFlatsPrice) {
        this.threeRoomFlatsPrice = threeRoomFlatsPrice;
    }

    public int getTotalFlatsAvailable() {
        return twoRoomFlatsAvailable + threeRoomFlatsAvailable;
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

    public void setManagerByName(String managerName) {
        Manager manager = ManagerRepository.getInstance().getByName(managerName);
        if (manager != null) {
            this.managerInCharge = manager;
        }
    }

    public List<Officer> getAssignedOfficers() {
        return assignedOfficers;
    }

    public boolean assignOfficer(Officer officer) {
        if (numOfficers < 10) {
            assignedOfficers.add(officer);
            numOfficers++;
            return true;
        }
        return false;
    }

    public boolean assignOfficerByName(String officerName) {
        Officer officer = OfficerRepository.getInstance().getOfficerByName(officerName);
        if (officer != null) {
            return assignOfficer(officer);
        }
        return false;
    }

    public boolean removeOfficer(Officer officer) {
        if (assignedOfficers.remove(officer)) {
            numOfficers--;
            return true;
        }
        return false;
    }

    public boolean removeOfficerByName(String officerName) {
        for (Officer officer : assignedOfficers) {
            if (officer.getName().equals(officerName)) {
                return removeOfficer(officer);
            }
        }
        return false;
    }

    public int getNumOfficers() {
        return assignedOfficers.size();
    }

    public void setNumOfficers(int numOfficers) {
        this.numOfficers = numOfficers;
    }

    @Override
    public String getID() {
        return projectID;
    }
}