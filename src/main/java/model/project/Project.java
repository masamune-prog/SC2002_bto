package model.project;

import model.Displayable;
import model.user.Manager;
import model.user.Officer;
import model.Model;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Project implements Model, Displayable {
    //ProjectStatus status;
    private String projectID;
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
    public Project(String projectID, boolean visibility, String projectName,
                   String neighborhood, int twoRoomFlatsAvailable, int threeRoomFlatsAvailable,
                   double twoRoomFlatsPrice, double threeRoomFlatsPrice,
                   LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
                   Manager managerInCharge) {
        this.projectID = projectID;
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
    private String getProjectInformationString() {
        return "| Project Status              | %-39s |\n";
    }
    private String getSingleProjectString() {
        StringBuilder display = new StringBuilder();

        // Create the header
        display.append("|--------------------------------------------------------------|\n");
        display.append(String.format("| Project ID                  | %-30s |\n", projectID));
        display.append(String.format("| Project Name                | %-30s |\n", projectName));


        return display.toString();
    }
    @Override
    public String getDisplayableString() {
        StringBuilder display = new StringBuilder(getSingleProjectString());

        // Add neighborhood information
        display.append(String.format("| Neighborhood                | %-30s |\n", neighborhood));

        // Add flat availability
        display.append(String.format("| 2-Room Flats Available      | %-30d |\n", twoRoomFlatsAvailable));
        display.append(String.format("| 3-Room Flats Available      | %-30d |\n", threeRoomFlatsAvailable));
        display.append(String.format("| Total Flats Available       | %-30d |\n", getTotalFlatsAvailable()));

        // Add pricing information
        display.append(String.format("| 2-Room Flats Price          | $%-29.2f |\n", twoRoomFlatsPrice));
        display.append(String.format("| 3-Room Flats Price          | $%-29.2f |\n", threeRoomFlatsPrice));

        // Add dates
        display.append(String.format("| Application Opening Date    | %-30s |\n", applicationOpeningDate));
        display.append(String.format("| Application Closing Date    | %-30s |\n", applicationClosingDate));

        // Add manager information
        String managerName = managerInCharge != null ? managerInCharge.getName() : "Not assigned";
        display.append(String.format("| Manager In Charge           | %-30s |\n", managerName));

        // Add officer information
        display.append(String.format("| Number of Officers          | %-30d |\n", getNumOfficers()));
        if (!assignedOfficers.isEmpty()) {
            display.append("| Assigned Officers:          |                                |\n");
            for (Officer officer : assignedOfficers) {
                display.append(String.format("|    %-25s | %-30s |\n", "", officer.getName()));
            }
        }

        // Add visibility information
        display.append(String.format("| Visibility                  | %-30s |\n", visibility ? "Visible" : "Hidden"));

        // Close the box
        display.append("|--------------------------------------------------------------|");

        return display.toString();
    }

    @Override
    public String getSplitter() {
        return "\n\n";
    }


}