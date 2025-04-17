package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.modelviewer.ModelViewer;
import boundary.modelviewer.ProjectViewer;
import controller.account.user.UserFinder;
import controller.enquiry.EnquiryManager;
import controller.project.ProjectManager;
import controller.request.OfficerManager;
import controller.request.RequestManager;
import model.project.Project;
import model.enquiry.Enquiry;
import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.user.Officer;
import model.user.User;
import model.user.UserType;
import repository.enquiry.EnquiryRepository;
import repository.request.RequestRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.InputHelper;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OfficerMainPage {
    private final Officer officer;
    private final Scanner scanner;
    private final ProjectManager projectManager;
    private final OfficerManager officerManager;
    private final RequestManager requestManager;
    private final EnquiryRepository enquiryRepository;

    public OfficerMainPage(User user) {
        if (!(user instanceof Officer)) {
            throw new IllegalArgumentException("User must be an Officer");
        }
        this.officer = (Officer) user;
        this.scanner = new Scanner(System.in);
        this.projectManager = new ProjectManager();
        this.officerManager = new OfficerManager();
        this.requestManager = new RequestManager();
        this.enquiryRepository = EnquiryRepository.getInstance();
    }

    public static void officerMainPage(User user) throws ModelNotFoundException, PageBackException {
        OfficerMainPage officerMainPage = new OfficerMainPage(user);
        officerMainPage.display();
    }

    public void display() throws ModelNotFoundException {
        Officer refreshedOfficer = OfficerRepository.getInstance().getByID(officer.getID());
        boolean isRunning = true;
        while (isRunning) {
            try {
                ChangePage.changePage();
                System.out.println("=== Officer Main Page ===");
                System.out.println("Welcome, " + refreshedOfficer.getName() + "!");
                System.out.println("1. View personal details");
                System.out.println("2. View projects in charge");
                System.out.println("3. Apply to be officer for projects");
                System.out.println("4. View officer applications");
                System.out.println("5. View project booking requests");
                System.out.println("6. Approve project booking requests");
                System.out.println("7. View enquiries");
                System.out.println("8. Reply to enquiries");
                System.out.println("9. Change password");
                System.out.println("10. View all projects");
                System.out.println("11. View all BTO applications");
                System.out.println("12. Approve BTO applications");
                System.out.println("13. Reject BTO applications");
                System.out.println("14. Debug request information");
                System.out.println("0. Logout");

                int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 0, 14);

                switch (choice) {
                    case 0 -> { Logout.logout(); isRunning = false; }
                    case 1 -> viewPersonalDetails();
                    case 2 -> viewProjectsInCharge();
                    case 3 -> applyForProjects();
                    case 4 -> viewOfficerApplications();
                    case 5 -> viewBookingRequests();
                    case 6 -> approveBookingRequests();
                    case 7 -> viewEnquiries();
                    case 8 -> replyToEnquiries();
                    case 9 -> ChangeAccountPassword.changePassword(UserType.OFFICER, refreshedOfficer.getNRIC());
                    case 10 -> ProjectViewer.viewAllProject();
                    case 11 -> showProjectApplicationRequests();
                    case 12 -> approveProjectApplication();
                    case 13 -> rejectProjectApplication();
                    case 14 -> debugRequestInfo();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (PageBackException pbe) {
                // Just return to main menu, do not logout
            }
        }
    }

    private void viewPersonalDetails() throws PageBackException, ModelNotFoundException {
        System.out.println("\n=== Personal Details ===");
        System.out.println("Name: " + officer.getName());
        System.out.println("NRIC: " + officer.getNRIC());
        System.out.println("Projects in charge: ");
        List<Project> projects = projectManager.getProjectsByOfficer(officer.getID());
        if (projects.isEmpty()) {
            System.out.println("None");
        } else {
            for (Project project : projects) {
                System.out.println("- " + project.getProjectName());
            }
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        //load the user page
        OfficerMainPage.officerMainPage(officer);
    }
    private void viewProjectsInCharge() throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("=== Projects in Charge ===");
        List<Project> projects = projectManager.getProjectsByOfficer(officer.getID());
        if (projects.isEmpty()) {
            System.out.println("You are not in charge of any projects.");
        } else {
            ModelViewer.displayListOfDisplayable(projects);
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        OfficerMainPage.officerMainPage(officer);
    }
    private void showProjectApplicationRequests() throws PageBackException {
        System.out.println("\n=== View All BTO Applications ===");
        
        // Get all project application requests, not just for this officer
        List<Request> requests = requestManager.getProjectApplicationRequests();
        
        if (requests.isEmpty()) {
            System.out.println("No BTO applications available in the system.");
        } else {
            for (int i = 0; i < requests.size(); i++) {
                System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
            }
        }
        
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    
    private void approveProjectApplication() throws PageBackException {
        System.out.println("\n=== Approve BTO Application ===");
        
        // Get all project application requests that are pending
        List<Request> requests = requestManager.getProjectApplicationRequests().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
                
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to approve.");
        } else {
            for (int i = 0; i < requests.size(); i++) {
                System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
            }

            int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to approve: ", 1, requests.size());
            Request request = requests.get(requestIndex - 1);

            try {
                requestManager.approveRequest(request.getID());
                System.out.println("BTO application approved successfully!");
            } catch (Exception e) {
                System.out.println("Failed to approve BTO application: " + e.getMessage());
            }
        }
        
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    
    private void rejectProjectApplication() throws PageBackException {
        System.out.println("\n=== Reject BTO Application ===");
        
        // Get all project application requests that are pending
        List<Request> requests = requestManager.getProjectApplicationRequests().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
                
        if (requests.isEmpty()) {
            System.out.println("No pending BTO applications available to reject.");
        } else {
            for (int i = 0; i < requests.size(); i++) {
                System.out.println((i + 1) + ". " + requests.get(i).getDisplayableString());
            }

            int requestIndex = InputHelper.getIntInput(scanner, "Enter request number to reject: ", 1, requests.size());
            Request request = requests.get(requestIndex - 1);

            try {
                requestManager.rejectRequest(request.getID());
                System.out.println("BTO application rejected successfully!");
            } catch (Exception e) {
                System.out.println("Failed to reject BTO application: " + e.getMessage());
            }
        }
        
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    
    private void debugRequestInfo() throws PageBackException {
        System.out.println("\n=== Debug Request Information ===");
        System.out.println("Current Officer ID: " + officer.getID());
        
        // Show all requests
        List<Request> requests = RequestRepository.getInstance().getAll();
        System.out.println("\nAll Requests in the system:");
        if (requests.isEmpty()) {
            System.out.println("No requests found in the system.");
        } else {
            for (Request request : requests) {
                System.out.println("Request ID: " + request.getID() + 
                    " | Type: " + request.getRequestType() + 
                    " | Status: " + request.getStatus() +
                    " | Manager ID: " + request.getManagerID());
            }
        }
        
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    
    private void applyForProjects() throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("=== Apply for Projects ===");
        List<Project> availableProjects = projectManager.getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for application.");
        } else {
            ModelViewer.displayListOfDisplayable(availableProjects);
            System.out.print("Enter project ID to apply for: ");
            String projectID = scanner.nextLine();
            try {
                String requestID = officerManager.createOfficerApplication(officer.getID(), projectID);
                System.out.println("Application submitted successfully. Request ID: " + requestID);
            } catch (ModelNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        OfficerMainPage.officerMainPage(officer);
    }

    private void viewOfficerApplications() throws PageBackException {
        ChangePage.changePage();
        System.out.println("=== Officer Applications ===");
        List<Request> applications = RequestRepository.getInstance().getRequestsByOfficer(officer.getID());
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            ModelViewer.displayListOfDisplayable(applications);
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void viewBookingRequests() throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("=== Project Booking Requests ===");
        List<Request> bookingRequests = RequestRepository.getInstance().getBookingRequestsByOfficer(officer.getID());
        if (bookingRequests.isEmpty()) {
            System.out.println("No booking requests found.");
        } else {
            ModelViewer.displayListOfDisplayable(bookingRequests);
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void approveBookingRequests() throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("=== Approve Booking Requests ===");
        
        // This will only return booking requests for projects this officer is assigned to
        List<Request> pendingRequests = RequestRepository.getInstance().getBookingRequestsByOfficer(officer.getID())
                .stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .toList();

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending booking requests found for your assigned projects.");
        } else {
            System.out.println("Showing booking requests for your assigned projects:");
            ModelViewer.displayListOfDisplayable(pendingRequests);
            System.out.print("Enter request ID to approve/reject: ");
            String requestID = scanner.nextLine();
            
            // Verify the selected request belongs to the officer's assigned projects
            boolean isRequestAssignedToOfficer = pendingRequests.stream()
                    .anyMatch(request -> request.getID().equals(requestID));
                    
            if (!isRequestAssignedToOfficer) {
                System.out.println("Error: You can only approve/reject booking requests for projects you are assigned to.");
                System.out.println("Press Enter to go back.");
                scanner.nextLine();
                throw new PageBackException();
            }
            
            System.out.print("Approve (A) or Reject (R)? ");
            String choice = scanner.nextLine().toUpperCase();
            try {
                if (choice.equals("A")) {
                    requestManager.approveRequest(requestID);
                    System.out.println("Request approved successfully.");
                } else if (choice.equals("R")) {
                    requestManager.rejectRequestForStatus(requestID);
                    System.out.println("Request rejected successfully.");
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (ModelNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void viewEnquiries() throws ModelNotFoundException, PageBackException {
        System.out.println("\n=== All Enquiries ===");
        List<Enquiry> enquiries = enquiryRepository.getAll();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available.");
        } else {
            for (int i = 0; i < enquiries.size(); i++) {
                Enquiry enquiry = enquiries.get(i);
                System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryID());
                System.out.println("   Question: " + enquiry.getQuestion());
                System.out.println("   Answer: " + (enquiry.getAnswer() != null ? enquiry.getAnswer() : "Not answered yet"));
                System.out.println("   Creator Name: " + UserFinder.findUserByID(enquiry.getCreatorID(),UserType.APPLICANT).getName());
                System.out.println("   --------------------");
            }
        }
        
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
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
}
