package model.user;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.RequestStatus;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
public class Officer implements User {
    private String officerID;
    private String nric;
    private String hashedPassword;
    private String name;
    private List<String> projectsInCharge;

    /**
     * Constructs a new Officer object with the specified NRIC and default password.
     *
     * @param nric          the NRIC of the officer.
     * @param name          the name of the officer.
     */
    public Officer(String officerID, String nric, String hashedPassword, String name,String project,
                   List<String> projectsInCharge) {
        this.nric = nric;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.projectsInCharge = projectsInCharge;
    }
    public Officer(Map<String, String> informationMap) {
        fromMap(informationMap);
    }
    @Override
    public String getID() {
        return this.officerID;
    }

    @Override
    public void setID(String id) {

    }

    @Override
    public String getNric() {
        return nric;
    }

    @Override
    public void setNric(String nric) {
        this.nric = nric;
    }

    @Override
    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void fromMap(Map<String, String> map) {
        this.officerID = map.get("officerID");
        this.nric = map.get("NRIC");
        this.hashedPassword = map.get("hashedPassword");
        this.name = map.get("Name");
        // Initialize projectsInCharge if needed (might be null at first)
        // We'll need to implement project assignment separately
    }

    public List<String> getProjectsInCharge() {
        return projectsInCharge;
    }

    public void setProjectsInCharge(List<String> projectsInCharge) {
        this.projectsInCharge = projectsInCharge;
    }

    public static void displayMenu(Officer officer) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Officer Menu ---");
            System.out.println("1. View Projects");
            
            System.out.println("2. Apply to be Officer");
            System.out.println("3. View application to be Officer");
            
            System.out.println("4. View BTO Booking");
            System.out.println("5. Approve BTO Booking"); // generates receipt as well
            
            System.out.println("6. View Enquiry");
            System.out.println("7. Reply Enquiry");
            
            System.out.println("8. View BTO Requests");
            
            System.out.println("9. Exit");
            
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Projects in charge for Officer " + officer.getName() + ":");
                    for (String project : officer.getProjectsInCharge()) {
                        System.out.println("- " + project);
                    }
                    break;
                    
                case 2: // INCOMPLETE
                    scanner.nextLine();

                    // Finding input: RequestID, ProjectID, RequestStatus, ManagerID
                    String requestID = "REQ" + System.currentTimeMillis(); // simple ID generator using time (unique)

                    System.out.print("Enter Project ID you wish to apply for: ");
                    String projectID = scanner.nextLine();
                    
                    RequestStatus status = RequestStatus.PENDING; // assuming enum
                    
                    Project project = ProjectRepository.getInstance().getProjectByID(projectID);
                    String managerID = project.getManagerInCharge().getID();

                    OfficerApplicationRequest request = new OfficerApplicationRequest(requestID, projectID, status, managerID);
                    RequestRepository.getInstance().add(request);
                    
                    System.out.println("Application submitted successfully!");
                    break;
                    
                case 3: // INCOMPLETE - edit once RequestRepository is created
                    System.out.println("");
                    break;
                    
                case 4: // INCOMPLETE
                    System.out.println("");
                    break;
                    
                case 5: // INCOMPLETE
                    System.out.println("");
                    break;
                    
                case 6: // INCOMPLETE
                    System.out.println("");
                    break;
                    
                case 7: // INCOMPLETE
                    System.out.println("");
                    break;
                    
                case 8: // INCOMPLETE
                    System.out.println("");
                    break;
                    
                case 9: // INCOMPLETE
                    System.out.println("Exiting...");
                    break;
                    
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        
        } while (choice != 4);

        scanner.close();
    }
}