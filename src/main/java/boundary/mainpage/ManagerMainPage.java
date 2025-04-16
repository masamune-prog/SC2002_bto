package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.modelviewer.ProjectViewer;
import controller.account.user.UserFinder;
import controller.project.ProjectManager;
import controller.request.ApplicantManager;
import controller.request.ManagerManager;
import controller.request.RequestManager;
import model.project.Project;
import model.enquiry.Enquiry;
import model.request.OfficerApplicationRequest;
import model.request.ProjectApplicationRequest;
import model.request.ProjectDeregistrationRequest;
import model.request.Request;
import model.user.Applicant;
import model.user.Manager;
import model.user.User;
import model.user.UserType;
import repository.enquiry.EnquiryRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.InputHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ManagerMainPage {
    private final Manager manager;
    private final Scanner scanner;
    private final ProjectManager projectManager;
    private final ManagerManager managerManager;
    private final RequestManager requestManager;
    private final RequestRepository requestRepository;
    private final EnquiryRepository enquiryRepository;

    public ManagerMainPage(User user) {
        if (!(user instanceof Manager)) {
            throw new IllegalArgumentException("User must be a Manager");
        }
        this.manager = (Manager) user;
        this.scanner = new Scanner(System.in);
        this.projectManager = new ProjectManager();
        this.managerManager = new ManagerManager();
        this.requestManager = new RequestManager();
        this.requestRepository = RequestRepository.getInstance();
        this.enquiryRepository = EnquiryRepository.getInstance();
    }

    public static void managerMainPage(User user) throws ModelNotFoundException, PageBackException {
        ManagerMainPage managerMainPage = new ManagerMainPage(user);
        managerMainPage.display();
    }

    public void display() throws ModelNotFoundException {
        Manager refreshedManager = ManagerRepository.getInstance().getByID(manager.getID());
        boolean isRunning = true;
        while (isRunning) {
            try {
                ChangePage.changePage();
                System.out.println("=== Manager Main Page ===");
                System.out.println("Welcome, " + refreshedManager.getName() + "!");
                System.out.println("1. Create New BTO Project");
                System.out.println("2. Edit BTO Project");
                System.out.println("3. Delete BTO Project");
                System.out.println("4. Handle Deregistration Request");
                System.out.println("5. View All BTO Projects");
                System.out.println("6. Filter BTO Projects");
                System.out.println("7. View Officer Requests");
                System.out.println("8. Approve Officer Request");
                System.out.println("9. View All Enquiries");
                System.out.println("10. Reply to Enquiries");
                System.out.println("11. Change Password");
                System.out.println("12. View Your BTO Applications");
                System.out.println("13. Approve BTO Applications");
                System.out.println("14. Reject BTO Applications");
                System.out.println("15. View All BTO Applications");
                System.out.println("16. Debug Manager ID");
                System.out.println("17. Logout");

                int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 0, 17);

                switch (choice) {
                    case 17 -> { Logout.logout(); isRunning = false; }
                    case 1 -> createNewProject();
                    case 2 -> editProject();
                    case 3 -> deleteProject();
                    case 4 -> showProjectDeregistrationRequests();
                    //Case 5 supposed to show the Requests for the officer
                    case 5 -> ProjectViewer.viewAllProject();
                    case 6 -> filterProjects();
                    case 7 -> viewOfficerRequests();
                    case 8 -> approveOfficerRequest();
                    case 9 -> viewAllEnquiries();
                    case 10 -> replyToEnquiries();
                    case 11 -> ChangeAccountPassword.changePassword(UserType.MANAGER, refreshedManager.getNric());
                    case 12 -> showProjectApplicationRequests();
                    case 13 -> approveProjectApplication();
                    case 14 -> rejectProjectApplication();
                    case 15 -> showAllProjectApplicationRequests();
                    case 16 -> debugManagerID(refreshedManager);
                    default -> System.out.println("Invalid choice. Please try again.");
                }

                if (isRunning) {
                    System.out.println("\nPress enter to continue...");
                    scanner.nextLine();
                }
            } catch (PageBackException pbe) {
                // Just return to main menu, do not logout
            }
        }
    }
    private void debugManagerID(Manager manager) {
        System.out.println("\n=== Debug Manager ID ===");
        System.out.println("Current Manager ID: " + manager.getID());
        
        // Show all requests with their manager IDs
        List<Request> requests = requestRepository.getAll();
        System.out.println("\nAll Requests and their Manager IDs:");
        if (requests.isEmpty()) {
            System.out.println("No requests found in the system.");
            return;
        }
        
        for (Request request : requests) {
            System.out.println("Request ID: " + request.getID() + 
                " | Type: " + request.getRequestType() + 
                " | Manager ID: " + request.getManagerID());
        }
        
        // Show all projects with their manager IDs
        List<Project> projects = projectManager.getAllProjects();
        System.out.println("\nAll Projects and their Manager IDs:");
        if (projects.isEmpty()) {
            System.out.println("No projects found in the system.");
            return;
        }
        
        for (Project project : projects) {
            System.out.println("Project ID: " + project.getID() + 
                " | Name: " + project.getProjectName() + 
                " | Manager ID: " + (project.getManagerInCharge() != null ? project.getManagerInCharge().getID() : "None"));
        }
    }

    private void createNewProject() {
        System.out.println("\n=== Create New BTO Project ===");
        String projectName = InputHelper.getStringInput(scanner, "Enter project name: ");
        String neighborhood = InputHelper.getStringInput(scanner, "Enter neighborhood: ");
        int twoRoomFlats = InputHelper.getIntInput(scanner, "Enter number of 2-room flats: ", 0, Integer.MAX_VALUE);
        int threeRoomFlats = InputHelper.getIntInput(scanner, "Enter number of 3-room flats: ", 0, Integer.MAX_VALUE);
        double twoRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter price for 2-room flats: ", 0, Double.MAX_VALUE);
        double threeRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter price for 3-room flats: ", 0, Double.MAX_VALUE);
        LocalDate applicationClosingDate = InputHelper.getDateInput(scanner, "Enter application closing date (YYYY-MM-DD): ");
        LocalDate applicationOpeningDate = InputHelper.getDateInput(scanner, "Enter application opening date (YYYY-MM-DD): ");
        boolean visible = InputHelper.getBooleanInput(scanner, "Make project visible? (yes/no): ");

        try {
            Project project = projectManager.createProject(
                visible,
                projectName,
                neighborhood,
                twoRoomFlats,
                threeRoomFlats,
                twoRoomFlatsPrice,
                threeRoomFlatsPrice,
                applicationOpeningDate,
                applicationClosingDate,
                manager
            );
            System.out.println("BTO project created successfully with ID: " + project.getID());
        } catch (Exception e) {
            System.out.println("Failed to create BTO project: " + e.getMessage());
        }
    }
    private void showProjectApplicationRequests() {
        System.out.println("\n=== View BTO Applications ===");
        
        // Use the ManagerManager to get only requests assigned to this manager
        List<ProjectApplicationRequest> requests = managerManager.getPendingApplicationRequests(manager);
        
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available for your projects.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
    }
    
    private void showAllProjectApplicationRequests() {
        System.out.println("\n=== View All BTO Applications ===");
        
        // Get all project application requests, not just for this manager
        List<Request> requests = requestManager.getProjectApplicationRequests();
        
        if (requests.isEmpty()) {
            System.out.println("No BTO applications available in the system.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
    }
    
    private void approveProjectApplication() {
        System.out.println("\n=== Approve BTO Application ===");
        
        // Use the ManagerManager to get only requests assigned to this manager
        List<ProjectApplicationRequest> requests = managerManager.getPendingApplicationRequests(manager);
        
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to approve.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
        ProjectApplicationRequest request = requests.get(requestIndex - 1);

        try {
            if (managerManager.approveRequest(request, manager)) {
                System.out.println("BTO application approved successfully!");
            } else {
                System.out.println("Failed to approve BTO application. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to approve BTO application: " + e.getMessage());
        }
    }
    
    private void rejectProjectApplication() {
        System.out.println("\n=== Reject BTO Application ===");
        
        // Use the ManagerManager to get only requests assigned to this manager
        List<ProjectApplicationRequest> requests = managerManager.getPendingApplicationRequests(manager);
        
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to reject.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to reject: ", 1, requests.size());
        ProjectApplicationRequest request = requests.get(requestIndex - 1);

        try {
            if (managerManager.rejectRequest(request, manager)) {
                System.out.println("BTO application rejected successfully!");
            } else {
                System.out.println("Failed to reject BTO application. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to reject BTO application: " + e.getMessage());
        }
    }
    private void editProject() {
        System.out.println("\n=== Edit BTO Project ===");
        List<Project> projects = projectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to edit.");
            return;
        }

        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to edit: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);

        System.out.println("Editing project: " + project.getProjectName());
        System.out.println("Select field to edit:");
        System.out.println("1. Project Name");
        System.out.println("2. Neighborhood");
        System.out.println("3. Two Room Flats");
        System.out.println("4. Three Room Flats");
        System.out.println("5. Two Room Flats Price");
        System.out.println("6. Three Room Flats Price");
        System.out.println("7. Application Opening Date");
        System.out.println("8. Application Closing Date");
        System.out.println("9. Visibility");

        int fieldChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 8);

        try {
            switch (fieldChoice) {
                case 1 -> {
                    String projectName = InputHelper.getStringInput(scanner, "Enter new project name: ");
                    project.setProjectName(projectName);
                }
                case 2 -> {
                    String neighborhood = InputHelper.getStringInput(scanner, "Enter new neighborhood: ");
                    project.setNeighborhood(neighborhood);
                }
                case 3 -> {
                    int twoRoomFlats = InputHelper.getIntInput(scanner, "Enter new number of 2-room flats: ", 0, Integer.MAX_VALUE);
                    project.setTwoRoomFlatsAvailable(twoRoomFlats);
                }
                case 4 -> {
                    int threeRoomFlats = InputHelper.getIntInput(scanner, "Enter new number of 3-room flats: ", 0, Integer.MAX_VALUE);
                    project.setThreeRoomFlatsAvailable(threeRoomFlats);
                }
                case 5 -> {
                    double twoRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter new price for 2-room flats: ", 0, Double.MAX_VALUE);
                    project.setTwoRoomFlatsPrice(twoRoomFlatsPrice);
                }
                case 6 -> {
                    double threeRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter new price for 3-room flats: ", 0, Double.MAX_VALUE);
                    project.setThreeRoomFlatsPrice(threeRoomFlatsPrice);
                }
                case 7 -> {
                    LocalDate openingDate = InputHelper.getDateInput(scanner, "Enter new application opening date (YYYY-MM-DD): ");
                    project.setApplicationOpeningDate(openingDate);
                }
                case 8 -> {
                    LocalDate closingDate = InputHelper.getDateInput(scanner, "Enter new application closing date (YYYY-MM-DD): ");
                    project.setApplicationClosingDate(closingDate);
                }
                case 9 -> {
                    boolean visible = InputHelper.getBooleanInput(scanner, "Make project visible? (yes/no): ");
                    project.setVisibility(visible);
                }
            }
            projectManager.updateProject(project);
            System.out.println("BTO project updated successfully!");
        } catch (Exception e) {
            System.out.println("Failed to update BTO project: " + e.getMessage());
        }
    }

    private void deleteProject() {
        System.out.println("\n=== Delete BTO Project ===");
        List<Project> projects = projectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to delete.");
            return;
        }

        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to delete: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);

        try {
            projectManager.deleteProject(project.getID());
            System.out.println("BTO project deleted successfully!");
        } catch (Exception e) {
            System.out.println("Failed to delete BTO project: " + e.getMessage());
        }
    }

    private void toggleBTOVisibility() {
        System.out.println("\n=== Toggle BTO Visibility ===");
        List<Project> projects = projectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to toggle visibility.");
            return;
        }

        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to toggle visibility: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);

        projectManager.updateProjectVisibility(project, !project.isVisible());
        try {
            projectManager.updateProject(project);
            System.out.println("BTO project visibility toggled successfully!");
        } catch (Exception e) {
            System.out.println("Failed to toggle BTO project visibility: " + e.getMessage());
        }
    }


    private void filterProjects() {
        System.out.println("\n=== Filter BTO Projects ===");
        System.out.println("Filter by:");
        System.out.println("1. Location");
        System.out.println("2. Price Range");
        System.out.println("3. Number of Units");
        int filterChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 3);

        List<Project> allProjects = projectManager.getAllProjects();
        List<Project> filteredProjects;
        switch (filterChoice) {
            case 1 -> {
                String location = InputHelper.getStringInput(scanner, "Enter location to filter: ");
                filteredProjects = allProjects.stream()
                    .filter(p -> p.getNeighborhood().equalsIgnoreCase(location))
                    .toList();
            }
            case 2 -> {
                double minPrice = InputHelper.getDoubleInput(scanner, "Enter minimum price: ", 0, Double.MAX_VALUE);
                double maxPrice = InputHelper.getDoubleInput(scanner, "Enter maximum price: ", minPrice, Double.MAX_VALUE);
                filteredProjects = allProjects.stream()
                    .filter(p -> p.getTwoRoomFlatsPrice() >= minPrice && p.getTwoRoomFlatsPrice() <= maxPrice ||
                               p.getThreeRoomFlatsPrice() >= minPrice && p.getThreeRoomFlatsPrice() <= maxPrice)
                    .toList();
            }
            case 3 -> {
                int minUnits = InputHelper.getIntInput(scanner, "Enter minimum units: ", 1, Integer.MAX_VALUE);
                int maxUnits = InputHelper.getIntInput(scanner, "Enter maximum units: ", minUnits, Integer.MAX_VALUE);
                filteredProjects = allProjects.stream()
                    .filter(p -> p.getTotalFlatsAvailable() >= minUnits && p.getTotalFlatsAvailable() <= maxUnits)
                    .toList();
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the filter criteria.");
            return;
        }
        displayProjects(filteredProjects);
    }

    private void viewOfficerRequests() {
        System.out.println("\n=== Officer Requests ===");
        List<OfficerApplicationRequest> requests = requestRepository.getAll().stream()
                .filter(request -> request instanceof OfficerApplicationRequest)
                .map(request -> (OfficerApplicationRequest) request)
                .collect(Collectors.toList());
        if (requests.isEmpty()) {
            System.out.println("No officer requests available.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
    }

    private void approveOfficerRequest() {
        System.out.println("\n=== Approve Officer Request ===");
        List<OfficerApplicationRequest> requests = requestRepository.getAll().stream()
                .filter(request -> request instanceof OfficerApplicationRequest)
                .map(request -> (OfficerApplicationRequest) request)
                .collect(Collectors.toList());
        if (requests.isEmpty()) {
            System.out.println("No officer requests available to approve.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
        OfficerApplicationRequest request = requests.get(requestIndex - 1);

        try {
            requestManager.approveRequest(request.getID());
            System.out.println("Officer request approved successfully!");
        } catch (Exception e) {
            System.out.println("Failed to approve officer request: " + e.getMessage());
        }
    }

    private void viewAllEnquiries() throws ModelNotFoundException {
        System.out.println("\n=== All Enquiries ===");
        List<Enquiry> enquiries = enquiryRepository.getAll();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available.");
            return;
        }

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryID());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Answer: " + (enquiry.getAnswer() != null ? enquiry.getAnswer() : "Not answered yet"));
            System.out.println("   Creator Name: " + UserFinder.findUserByID(enquiry.getCreatorID(),UserType.APPLICANT).getName());
            System.out.println("   --------------------");
        }
    }

    private void replyToEnquiries() {
        System.out.println("\n=== Reply to Enquiries ===");
        List<Enquiry> enquiries = enquiryRepository.getUnansweredEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available to reply.");
            return;
        }

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryID());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Creator ID: " + enquiry.getCreatorID());
            System.out.println("   --------------------");
        }

        int enquiryIndex = InputHelper.getIntInput(scanner, "Enter enquiry number to reply: ", 1, enquiries.size());
        Enquiry enquiry = enquiries.get(enquiryIndex - 1);

        String reply = InputHelper.getStringInput(scanner, "Enter your reply: ");

        try {
            enquiryRepository.answerEnquiry(enquiry.getEnquiryID(), reply);
            System.out.println("Reply sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send reply: " + e.getMessage());
        }
    }

    private void displayProjects(List<Project> projects) {
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.println("\n" + (i + 1) + ". Project: " + project.getProjectName());
            System.out.println("   ID: " + project.getID());
            System.out.println("   Neighborhood: " + project.getNeighborhood());
            System.out.println("   Total Flats: " + project.getTotalFlatsAvailable());
            System.out.println("   2-Room Flats: " + project.getTwoRoomFlatsAvailable() + " ($" + project.getTwoRoomFlatsPrice() + ")");
            System.out.println("   3-Room Flats: " + project.getThreeRoomFlatsAvailable() + " ($" + project.getThreeRoomFlatsPrice() + ")");
            System.out.println("   Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
            System.out.println("   Visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
            System.out.println("   Created by: " + (project.getManagerInCharge().getName()!= null ? project.getManagerInCharge().getName() : "N/A"));
            System.out.println("   --------------------");
        }
    }

    /**
     * Shows and manages pending project deregistration requests
     */
    private void showProjectDeregistrationRequests() {
        System.out.println("\n=== Deregistration Requests ===");
        
        // Use the ManagerManager to get only requests assigned to this manager
        List<ProjectDeregistrationRequest> requests = managerManager.getPendingDeregistrationRequests(manager);
        
        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
        
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Approve a deregistration request");
        System.out.println("2. Reject a deregistration request");
        System.out.println("3. Go back");
        
        int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 3);
        
        switch (choice) {
            case 1 -> approveDeregistrationRequest(requests);
            case 2 -> rejectDeregistrationRequest(requests);
            case 3 -> {} // Go back
        }
    }
    
    /**
     * Approves a project deregistration request
     */
    private void approveDeregistrationRequest(List<ProjectDeregistrationRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available to approve.");
            return;
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
        ProjectDeregistrationRequest request = requests.get(requestIndex - 1);

        try {
            if (managerManager.approveRequest(request, manager)) {
                // After setting the status, we need to update applicant's status
                try {
                    // We need to call requestManager to handle the business logic of deregistration
                    requestManager.approveRequest(request.getID());
                    System.out.println("Deregistration request approved successfully!");
                } catch (Exception e) {
                    System.out.println("Error updating applicant status: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to approve deregistration request. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to approve deregistration request: " + e.getMessage());
        }
    }
    
    /**
     * Rejects a project deregistration request
     */
    private void rejectDeregistrationRequest(List<ProjectDeregistrationRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available to reject.");
            return;
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to reject: ", 1, requests.size());
        ProjectDeregistrationRequest request = requests.get(requestIndex - 1);

        try {
            if (managerManager.rejectRequest(request, manager)) {
                System.out.println("Deregistration request rejected successfully!");
            } else {
                System.out.println("Failed to reject deregistration request. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to reject deregistration request: " + e.getMessage());
        }
    }
}