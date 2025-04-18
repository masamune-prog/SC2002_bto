package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.modelviewer.ProjectViewer;
import boundary.modelviewer.ListPrinter;
import controller.account.user.UserFinder;
import controller.enquiry.EnquiryManager;
import controller.project.ProjectManager;
//import controller.report.ApplicantReportManager;
import controller.report.ApplicantReportManager;
import controller.request.ApplicantManager;
import controller.request.ManagerManager;
import controller.request.OfficerManager;
import controller.request.RequestManager;
import model.Displayable;
import model.project.Project;
import model.enquiry.Enquiry;
import model.request.OfficerApplicationRequest;
import model.request.ProjectApplicationRequest;
import model.request.ProjectWithdrawalRequest;
import model.request.Request;
import model.project.RoomType;
import model.user.Applicant;
import model.user.Manager;
import model.user.MaritalStatus;
import model.user.User;
import model.user.UserType;
import repository.enquiry.EnquiryRepository;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.InputHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Comparator;

public class ManagerMainPage {
    private final Manager manager;
    private final Scanner scanner;
    private final ProjectManager projectManager;
    private final ManagerManager managerManager;
    private final RequestManager requestManager;
    private final RequestRepository requestRepository;
    private final EnquiryRepository enquiryRepository;
    private final ListPrinter<Project> projectPrinter;  // persistent filter+sort for projects

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
        // Initialize project printer with default alphabetical order by project name
        this.projectPrinter = new ListPrinter<>(Comparator.comparing(Project::getProjectTitle));
    }

    public static void managerMainPage(User user) throws ModelNotFoundException, PageBackException {
        ManagerMainPage managerMainPage = new ManagerMainPage(user);
        managerMainPage.display();
    }

    public void display() throws ModelNotFoundException, PageBackException {
        // Main loop for MVP functionality
        while (true) {
            System.out.println("=== Manager Main Page ===");
            System.out.println("Welcome, " + manager.getName() + "!");
            System.out.println("1. List All BTO Projects");
            System.out.println("2. Create New BTO Project");
            System.out.println("3. Edit BTO Project");
            System.out.println("4. Delete BTO Project");
            System.out.println("5. View Officer Requests");
            System.out.println("6. Approve Officer Request");
            System.out.println("7. List All Enquiries");
            System.out.println("8. Reply to an Enquiry");
            System.out.println("9. View Pending BTO Requests");
            System.out.println("10. Approve/Reject BTO Application");
            System.out.println("11. View Project Withdrawal Requests");
            System.out.println("12. Approve/Reject Project Withdrawal Requests");
            System.out.println("13. See Filtered Projects");
            System.out.println("14. See Reports");
            System.out.println("15. Logout");
            int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 15);
            switch (choice) {
                case 1 -> displayProjects(ProjectManager.getAllProjects());
                case 2 -> createNewProject();
                case 3 -> editProject();
                case 4 -> deleteProject();
                case 5 -> viewOfficerRequests();
                case 6 -> approveOfficerRequest();
                case 7 -> listAllEnquiries();
                case 8 -> replyToEnquiries();
                case 9 -> viewAllProjectApplicationRequests();
                case 10 -> approveRejectProjectApplicationRequests();
                case 11 ->  showProjectDeregistrationRequests();
                case 12 -> approveRejectWithdrawalRequest();
                case 13 -> filterProjects(manager);
                case 14 -> generateApplicantReport();
                case 15 -> { Logout.logout(); return; }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println("\nPress enter to continue..."); scanner.nextLine();
        }
    }
    private void generateApplicantReport() {
        System.out.println("\n=== Generate Applicant Report ===");

        // --- Get Filter Choices from User ---
        MaritalStatus maritalFilter = null;
        if (InputHelper.getBooleanInput(scanner, "Filter by marital status? (yes/no): ")) {
            MaritalStatus[] statuses = MaritalStatus.values();
            System.out.println("Available Marital Statuses:");
            for (int i = 0; i < statuses.length; i++) {
                // Exclude NONE if it exists and is not a valid filter option
                System.out.println((i + 1) + ". " + statuses[i]);

            }
            // Adjust prompt and validation range as needed based on displayed options
            int msChoice = InputHelper.getIntInput(scanner, "Select marital status: ", 1, statuses.length);
            maritalFilter = statuses[msChoice - 1]; // Make sure index matches displayed options
        }

        RoomType roomFilter = null;
        if (InputHelper.getBooleanInput(scanner, "Filter by flat type? (yes/no): ")) {
            RoomType[] types = RoomType.values();
            System.out.println("Available Flat Types:");
            for (int i = 0; i < types.length; i++) {
                // Exclude NONE if it exists and is not a valid filter option
                if (types[i] != RoomType.NONE) {
                    System.out.println((i + 1) + ". " + types[i]);
                }
            }
            // Adjust prompt and validation range as needed
            int rtChoice = InputHelper.getIntInput(scanner, "Select flat type: ", 1, types.length);
            roomFilter = types[rtChoice - 1]; // Make sure index matches displayed options
        }

        String filterProject = null;
        if (InputHelper.getBooleanInput(scanner, "Filter by project name? (yes/no): ")) {
            filterProject = InputHelper.getStringInput(scanner, "Enter project name: ");
        }

        Integer minAge = null, maxAge = null;
        if (InputHelper.getBooleanInput(scanner, "Filter by age range? (yes/no): ")) {
            minAge = InputHelper.getIntInput(scanner, "Enter minimum age: ", 0, Integer.MAX_VALUE);
            // Ensure maxAge is not less than minAge
            maxAge = InputHelper.getIntInput(scanner, "Enter maximum age: ", minAge, Integer.MAX_VALUE);
        }

        // --- Generate and Display Report ---
        ApplicantReportManager reportManager = new ApplicantReportManager();
        // Call the generateReport method with the chosen filters
        var report = reportManager.generateReport(maritalFilter, roomFilter, filterProject, minAge, maxAge);

        if (report.isEmpty()) {
            System.out.println("No matching entries found for the selected filters.");
            return; // Exit if the report is empty
        }

        // Display the report header
        System.out.println("\n--- Applicant Booking Report ---");
        System.out.printf("%-20s %-5s %-15s %-20s %-15s%n", "Applicant Name", "Age", "Marital Status", "Project Name", "Flat Type");
        System.out.println("-----------------------------------------------------------------------------"); // Separator line

        // Display each entry in the report
        for (ApplicantReportManager.ReportEntry entry : report) {
            System.out.printf("%-20s %-5d %-15s %-20s %-15s%n",
                    entry.applicantName,
                    entry.age,
                    entry.maritalStatus, // Assumes MaritalStatus enum has a meaningful toString()
                    entry.projectName,
                    entry.roomType // Assumes RoomType enum has a meaningful toString()
            );
        }
        System.out.println("-----------------------------------------------------------------------------"); // Footer separator
    }
    private void approveRejectWithdrawalRequest(){
        System.out.println("\n=== Approve/Reject BTO Project Withdrawal Request ===");
        List<ProjectWithdrawalRequest> requests = requestManager.getAllPendingWithdrawalRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending BTO project withdrawal requests available.");
            return;
        }
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve/reject: ", 1, requests.size());
        ProjectWithdrawalRequest request = requests.get(requestIndex - 1);
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int actionChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 2);
        try {
            if (actionChoice == 1) {
                RequestManager.approveWithdrawalRequest(request.getID());
                System.out.println("BTO project withdrawal request approved successfully!");
            } else {
                RequestManager.rejectWithdrawalRequest(request.getID());
                System.out.println("BTO project withdrawal request rejected successfully!");
            }
        } catch (Exception e) {
            System.out.println("Failed to process BTO project withdrawal request: " + e.getMessage());
        }

    }
    private void editProject() throws ModelNotFoundException {
        System.out.println("\n=== Edit BTO Project ===");
        List<Project> projects = ProjectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to edit.");
            return;
        }
        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to edit: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);
        System.out.println("Editing project: " + project.getProjectTitle());
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
        int fieldChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 9);
        try {
            switch (fieldChoice) {
                case 1 -> {
                    String projectName = InputHelper.getStringInput(scanner, "Enter new project name: ");
                    project.setProjectTitle(projectName);
                }
                case 2 -> {
                    String neighborhood = InputHelper.getStringInput(scanner, "Enter new neighborhood: ");
                    project.setNeighbourhood(neighborhood);
                }
                case 3 -> {
                    int twoRoomFlats = InputHelper.getIntInput(scanner, "Enter new number of 2-room flats: ", 0, Integer.MAX_VALUE);
                    project.setTwoRoomFlatAvailable(twoRoomFlats);
                }
                case 4 -> {
                    int threeRoomFlats = InputHelper.getIntInput(scanner, "Enter new number of 3-room flats: ", 0, Integer.MAX_VALUE);
                    project.setThreeRoomFlatAvailable(threeRoomFlats);
                }
                case 5 -> {
                    double twoRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter new price for 2-room flats: ", 0, Double.MAX_VALUE);
                    project.setTwoRoomFlatPrice(twoRoomFlatsPrice);
                }
                case 6 -> {
                    double threeRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter new price for 3-room flats: ", 0, Double.MAX_VALUE);
                    project.setThreeRoomFlatPrice(threeRoomFlatsPrice);
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
            ProjectRepository.getInstance().update(project);
            System.out.println("BTO project updated successfully!");
        } catch (Exception e) {
            System.out.println("Failed to update BTO project: " + e.getMessage());
        }
    }
    private void viewAllProjectApplicationRequests() throws ModelNotFoundException {
        System.out.println("\n=== Approve/Reject BTO Application ===");
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available.");
            return;
        }
        System.out.println("All Pending BTO application requests:");
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
        System.out.println("────────────────────────────────────────");

    }
    private void approveRejectProjectApplicationRequests() throws ModelNotFoundException {
        System.out.println("\n=== Approve/Reject BTO Application ===");
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available.");
            return;
        }
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }
        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve/reject: ", 1, requests.size());
        Request request = requests.get(requestIndex - 1);
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int actionChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 2);
        try {
            if (actionChoice == 1) {
                RequestManager.approveProjectApplication(request.getID());
                System.out.println("BTO application approved successfully!");
            } else {
                RequestManager.rejectProjectApplication(request.getID());
                System.out.println("BTO application rejected successfully!");
            }
        } catch (Exception e) {
            System.out.println("Failed to process BTO application: " + e.getMessage());
        }
    }

    private void createNewProject() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Create New BTO Project ===");
        String projectName = InputHelper.getStringInput(scanner, "Enter project name: ");
        String neighborhood = InputHelper.getStringInput(scanner, "Enter neighborhood: ");
        int twoRoomFlats = InputHelper.getIntInput(scanner, "Enter number of 2-room flats: ", 0, Integer.MAX_VALUE);
        int threeRoomFlats = InputHelper.getIntInput(scanner, "Enter number of 3-room flats: ", 0, Integer.MAX_VALUE);
        double twoRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter price for 2-room flats: ", 0, Double.MAX_VALUE);
        double threeRoomFlatsPrice = InputHelper.getDoubleInput(scanner, "Enter price for 3-room flats: ", 0, Double.MAX_VALUE);
        LocalDate applicationOpeningDate = InputHelper.getDateInput(scanner, "Enter application opening date (YYYY-MM-DD): ");
        LocalDate applicationClosingDate = InputHelper.getDateInput(scanner, "Enter application closing date (YYYY-MM-DD): ");
        boolean visible = InputHelper.getBooleanInput(scanner, "Make project visible? (yes/no): ");

        try {
            String projectID = controller.project.ProjectManager.createProject(
                    projectName, neighborhood,
                    applicationOpeningDate, applicationClosingDate,
                    twoRoomFlats, threeRoomFlats,
                    twoRoomFlatsPrice, threeRoomFlatsPrice,
                    manager.getID(), List.of(), visible
            );
            System.out.println("BTO project created with ID: " + projectID);
        } catch (Exception e) {
            System.out.println("Failed to create project: " + e.getMessage());
        }
    }

    private void listAllEnquiries() throws PageBackException {
        List<Enquiry> enquiries = EnquiryManager.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            //get list printer to work later TODO
            System.out.println("Enquiries:");
            System.out.println("────────────────────────────────────────");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getID());
                System.out.println("Title: " + enquiry.getEnquiryTitle());
                System.out.println("Description: " + enquiry.getQuestion());
                System.out.println("Status: " + (enquiry.getAnswered() ? "Answered" : "Unanswered"));
                if(enquiry.getAnswered()) {
                    System.out.println("Answer: " + enquiry.getAnswer());
                }
                System.out.println();
            }
        }
        System.out.println("Press Enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }

    private void showProjectApplicationRequests() throws ModelNotFoundException {
        System.out.println("\n=== View BTO Applications ===");
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available for your projects.");
            return;
        }
        // Use ListPrinter for output
        ListPrinter<Request> printer1 = new ListPrinter<>(requests);
        printer1.printFilteredItems(Displayable::getDisplayableString);
    }

    private void showAllProjectApplicationRequests() throws ModelNotFoundException {
        System.out.println("\n=== View All BTO Applications ===");
        List<Request> requests = requestManager.getAllPendingApplicationRequests();
        if (requests.isEmpty()) {
            System.out.println("No BTO applications available in the system.");
            return;
        }
        // Use ListPrinter for output
        ListPrinter<Request> printer2 = new ListPrinter<>(requests);
        printer2.printFilteredItems(Request::getDisplayableString);
    }

    private void approveProjectApplication() throws ModelNotFoundException {
        System.out.println("\n=== Approve BTO Application ===");

        // Can approve all requests
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();

        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to approve.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
        Request request = requests.get(requestIndex - 1);

        try {
            if (RequestManager.approveProjectApplication(request.getID())) {
                System.out.println("BTO application approved successfully!");
            } else {
                System.out.println("Failed to approve BTO application. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to approve BTO application: " + e.getMessage());
        }
    }

    private void rejectProjectApplication() throws ModelNotFoundException {
        System.out.println("\n=== Reject BTO Application ===");

        // Use the ManagerManager to get only requests assigned to this manager
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to reject.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to reject: ", 1, requests.size());
        Request request = requests.get(requestIndex - 1);

        try {
            if (RequestManager.rejectProjectApplication(request.getID())) {
                System.out.println("BTO application rejected successfully!");
            } else {
                System.out.println("Failed to reject BTO application. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to reject BTO application: " + e.getMessage());
        }
    }

    private static List<Request> getRequests() throws ModelNotFoundException {
        List<Request> requests = RequestManager.getAllPendingApplicationRequests();
        ;
        return requests;
    }


    private void deleteProject() throws ModelNotFoundException {
        System.out.println("\n=== Delete BTO Project ===");
        List<Project> projects = ProjectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to delete.");
            return;
        }

        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to delete: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);

        try {
            ProjectManager.deleteProject(project.getID());
            System.out.println("BTO project deleted successfully!");
        } catch (Exception e) {
            System.out.println("Failed to delete BTO project: " + e.getMessage());
        }
    }

    private void toggleBTOVisibility() throws ModelNotFoundException {
        System.out.println("\n=== Toggle BTO Visibility ===");
        List<Project> projects = ProjectManager.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to toggle visibility.");
            return;
        }

        displayProjects(projects);
        int projectIndex = InputHelper.getIntInput(scanner, "Enter project number to toggle visibility: ", 1, projects.size());
        Project project = projects.get(projectIndex - 1);
        boolean currentVisibility = project.getVisibility();
        project.setVisibility(!currentVisibility);
        try {
            ProjectRepository.getInstance().update(project);
            System.out.println("BTO project visibility toggled successfully!");
        } catch (Exception e) {
            System.out.println("Failed to toggle BTO project visibility: " + e.getMessage());
        }
    }


    private void filterProjects(Manager manager) throws ModelNotFoundException {
        System.out.println("\n=== Filter BTO Projects ===");
        System.out.println("Filter by:");
        System.out.println("1. Location");
        System.out.println("2. Price Range");
        System.out.println("3. Number of Units");
        System.out.println("4. My Projects");
        int filterChoice = InputHelper.getIntInput(scanner, "Enter your choice: ", 1, 4);

        List<Project> allProjects = projectManager.getAllProjects();
        List<Project> filteredProjects;
        switch (filterChoice) {
            case 1 -> {
                String location = InputHelper.getStringInput(scanner, "Enter location to filter: ");
                filteredProjects = allProjects.stream()
                        .filter(p -> p.getNeighbourhood().equalsIgnoreCase(location))
                        .toList();
            }
            case 2 -> {
                double minPrice = InputHelper.getDoubleInput(scanner, "Enter minimum price: ", 0, Double.MAX_VALUE);
                double maxPrice = InputHelper.getDoubleInput(scanner, "Enter maximum price: ", minPrice, Double.MAX_VALUE);
                filteredProjects = allProjects.stream()
                        .filter(p -> p.getTwoRoomFlatPrice() >= minPrice && p.getTwoRoomFlatPrice() <= maxPrice ||
                                p.getThreeRoomFlatPrice() >= minPrice && p.getThreeRoomFlatPrice() <= maxPrice)
                        .toList();
            }
            case 3 -> {
                int minUnits = InputHelper.getIntInput(scanner, "Enter minimum units: ", 1, Integer.MAX_VALUE);
                int maxUnits = InputHelper.getIntInput(scanner, "Enter maximum units: ", minUnits, Integer.MAX_VALUE);
                filteredProjects = allProjects.stream()
                        .filter(p -> p.getTotalFlatsAvailable() >= minUnits && p.getTotalFlatsAvailable() <= maxUnits)
                        .toList();
            }
            case 4 -> {
                //get Manager ID
                String managerID = manager.getID();
                //get all projects by this manager
                filteredProjects = allProjects.stream()
                        .filter(p -> p.getManagerID().equalsIgnoreCase(managerID))
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
        System.out.println("=== Officer Requests ===");
        List<OfficerApplicationRequest> requests = requestRepository.getAll().stream()
                .filter(r -> r instanceof OfficerApplicationRequest)
                .map(r -> (OfficerApplicationRequest) r)
                .collect(Collectors.toList());
        if (requests.isEmpty()) {
            System.out.println("No officer requests available.");
            return;
        }
        // Use ListPrinter for output
        ListPrinter<OfficerApplicationRequest> printer = new ListPrinter<>(requests);
        printer.printFilteredItems(OfficerApplicationRequest::getDisplayableString);
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
            RequestManager.approveOfficerApplicationRequest(request.getID());
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
        // Use ListPrinter for output
        ListPrinter<Enquiry> printer3 = new ListPrinter<>(enquiries);
        printer3.printFilteredItems(e -> String.format("ID:%s Q:%s A:%s", e.getID(), e.getQuestion(), e.getAnswer() != null ? e.getAnswer() : "Not answered"));
    }

    private void replyToEnquiries() throws PageBackException {
        System.out.println("\n=== Reply to Enquiries ===");
        List<Enquiry> enquiries = enquiryRepository.getUnansweredEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available to reply.");
            return;
        }

        //get all unanswered enquiries
        System.out.println("Enquiries:");
        System.out.println("────────────────────────────────────────");
        for (Enquiry enquiry : enquiries) {
            System.out.println("Enquiry Number: " + (enquiries.indexOf(enquiry) + 1));
            System.out.println("Enquiry ID: " + enquiry.getID());
            System.out.println("Title: " + enquiry.getEnquiryTitle());
            System.out.println("Description: " + enquiry.getQuestion());
            System.out.println("Status: " + (enquiry.getAnswered() ? "Answered" : "Unanswered"));
            System.out.println();
        }

        int enquiryIndex = InputHelper.getIntInput(scanner, "Enter enquiry number to reply: ", 1, enquiries.size());
        Enquiry enquiry = enquiries.get(enquiryIndex - 1);

        String reply = InputHelper.getStringInput(scanner, "Enter your reply: ");

        try {
            EnquiryManager.answerEnquiry(enquiry.getID(), reply);
            System.out.println("Reply sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send reply: " + e.getMessage());
        }
        System.out.println();
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void displayProjects(List<Project> projects) throws ModelNotFoundException {
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.println("\n" + (i + 1) + ". Project: " + project.getProjectTitle());
            System.out.println("   ID: " + project.getID());
            System.out.println("   Neighborhood: " + project.getNeighbourhood());
            System.out.println("   Total Flats: " + project.getTotalFlatsAvailable());
            System.out.println("   2-Room Flats: " + project.getTwoRoomFlatAvailable() + " ($" + project.getTwoRoomFlatPrice() + ")");
            System.out.println("   3-Room Flats: " + project.getThreeRoomFlatAvailable() + " ($" + project.getThreeRoomFlatPrice() + ")");
            System.out.println("   Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
            System.out.println("   Visibility: " + (project.getVisibility() ? "Visible" : "Hidden"));
            Manager manager = ManagerManager.getManagerByID(project.getManagerID());
            String managerName = manager != null ? manager.getName() : "N/A";
            System.out.println("   Created by: " + (managerName));
            //print out officers assigned to the project
            List<String> officers = project.getOfficerIDs();
            if (officers != null && !officers.isEmpty()) {
                System.out.println("   Officers assigned: ");
                for (String officer : officers) {
                    System.out.println("   - " + OfficerManager.getOfficerByID(officer).getName());
                }
            } else {
                System.out.println("   No officers assigned.");
            }
            System.out.println("   --------------------");
        }
    }

    /**
     * Shows and manages pending project deregistration requests
     */
    private void showProjectDeregistrationRequests() throws PageBackException {
        System.out.println("\n=== Deregistration Requests ===");

        // Use the ManagerManager to get only requests assigned to this manager
        List<ProjectWithdrawalRequest> requests = requestManager.getAllPendingWithdrawalRequests();

        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
        }

        System.out.println("────────────────────────────────────────");
        //System.out.println("Press Enter to go back.");
    }

    /**
     * Approves a project deregistration request
     */
    private void approveDeregistrationRequest(List<ProjectWithdrawalRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available to approve.");
            return;
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
        ProjectWithdrawalRequest request = requests.get(requestIndex - 1);

        try {
            if (RequestManager.approveWithdrawalRequest(request.getID())) {
                System.out.println("Deregistration request approved successfully!");
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
    private void rejectDeregistrationRequest(List<ProjectWithdrawalRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("No pending deregistration requests available to reject.");
            return;
        }

        int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to reject: ", 1, requests.size());
        ProjectWithdrawalRequest request = requests.get(requestIndex - 1);

        try {
            if (RequestManager.rejectWithdrawalRequest(request.getID())) {
                System.out.println("Deregistration request rejected successfully!");
            } else {
                System.out.println("Failed to reject deregistration request. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("Failed to reject deregistration request: " + e.getMessage());
        }
    }


    private void changeProjectFilter() {
        System.out.println("\n=== Change Project Filter ===");
        System.out.println("1. Filter by Neighborhood");
        System.out.println("2. Filter by Visibility");
        System.out.println("3. Clear Filter");
        System.out.println("0. Back to Main Menu");
        int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 0, 3);

        switch (choice) {
            case 1 -> {
                String neighborhood = InputHelper.getStringInput(scanner, "Enter neighborhood to filter: ");
                projectPrinter.setFilter(project -> project.getNeighbourhood().equalsIgnoreCase(neighborhood));
                System.out.println("Neighborhood filter applied.");
            }
            case 2 -> {
                boolean visible = InputHelper.getBooleanInput(scanner, "Filter by visibility (yes/no): ");
                projectPrinter.setFilter(project -> project.getVisibility() == visible);
                System.out.println("Visibility filter applied.");
            }
            case 3 -> {
                projectPrinter.clearFilter();
                System.out.println("All filters cleared.");
            }
            case 0 -> {
                // back to menu
            }
            default -> System.out.println("Invalid choice.");
        }
    }


    /**
     * Lists all requests in the system using ListPrinter
     */
    private void listAllRequests() {
        System.out.println("\n=== All Requests ===");
        List<Request> reqs = requestRepository.getAll();
        if (reqs.isEmpty()) {
            System.out.println("No requests found."); return;
        }
        ListPrinter<Request> printer = new ListPrinter<>(reqs);
        printer.printFilteredItems(Request::getDisplayableString);
    }
}