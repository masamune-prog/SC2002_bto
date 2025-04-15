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
import model.request.Request;
import model.request.RequestStatus;
import model.user.Officer;
import model.user.User;
import model.user.UserType;
import repository.enquiry.EnquiryRepository;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.ui.ChangePage;
import utils.ui.InputHelper;

import java.util.List;
import java.util.Scanner;

public class OfficerMainPage {
    private final Officer officer;
    private final Scanner scanner;
    private final ProjectManager projectManager;
    private final ProjectRepository projectRepository;
    private final OfficerManager officerManager;
    private final RequestManager requestManager;
    private final RequestRepository requestRepository;
    private final EnquiryRepository enquiryRepository;

    public OfficerMainPage(User user) {
        if (!(user instanceof Officer)) {
            throw new IllegalArgumentException("User must be an Officer");
        }
        this.officer = (Officer) user;
        this.scanner = new Scanner(System.in);
        this.projectManager = new ProjectManager();
        this.projectRepository = ProjectRepository.getInstance();
        this.officerManager = new OfficerManager();
        this.requestManager = new RequestManager();
        this.requestRepository = RequestRepository.getInstance();
        this.enquiryRepository = EnquiryRepository.getInstance();
    }

    public static void officerMainPage(User user) throws ModelNotFoundException, PageBackException {
        OfficerMainPage officerMainPage = new OfficerMainPage(user);
        officerMainPage.display();
    }

    public void display() throws ModelNotFoundException, PageBackException {
        // Refresh officer data
        Officer refreshedOfficer = OfficerRepository.getInstance().getByID(officer.getID());
        boolean isRunning = true;

        while (isRunning) {
            ChangePage.changePage();
            System.out.println("=== Officer Main Page ===");
            System.out.println("Welcome, " + refreshedOfficer.getName() + "!");
            System.out.println("1. View my profile");
            System.out.println("2. View projects in charge");
            System.out.println("3. Apply to be officer for projects");
            System.out.println("4. View officer applications");
            System.out.println("5. View project booking requests");
            System.out.println("6. Approve project booking requests");
            System.out.println("7. View enquiries");
            System.out.println("8. Reply to enquiries");
            System.out.println("9. Change password");
            System.out.println("10. Logout");

            int choice = InputHelper.getIntInput(scanner, "Enter your choice: ", 0, 9);

            switch (choice) {
                case 9 -> Logout.logout();
                case 1 -> viewProjectsInCharge();
                case 2 -> applyForProjects();
                case 3 -> viewOfficerApplications();
                case 4 -> viewBookingRequests();
                case 5 -> approveBookingRequests();
                case 6 -> viewEnquiries();
                case 7 -> replyToEnquiries();
                case 8 -> ChangeAccountPassword.changePassword(UserType.OFFICER, refreshedOfficer.getNric());
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewProjectsInCharge() throws PageBackException {
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
        throw new PageBackException();
    }

    private void applyForProjects() throws PageBackException {
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
        throw new PageBackException();
    }

    private void viewOfficerApplications() throws PageBackException {
        ChangePage.changePage();
        System.out.println("=== Officer Applications ===");
        List<Request> applications = requestRepository.getRequestsByOfficer(officer.getID());
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            ModelViewer.displayListOfDisplayable(applications);
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void viewBookingRequests() throws PageBackException {
        ChangePage.changePage();
        System.out.println("=== Project Booking Requests ===");
        List<Request> bookingRequests = requestRepository.getBookingRequestsByOfficer(officer.getID());
        if (bookingRequests.isEmpty()) {
            System.out.println("No booking requests found.");
        } else {
            ModelViewer.displayListOfDisplayable(bookingRequests);
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void approveBookingRequests() throws PageBackException {
        ChangePage.changePage();
        System.out.println("=== Approve Booking Requests ===");
        List<Request> pendingRequests = requestRepository.getBookingRequestsByOfficer(officer.getID())
                .stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .toList();

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending booking requests found.");
        } else {
            ModelViewer.displayListOfDisplayable(pendingRequests);
            System.out.print("Enter request ID to approve/reject: ");
            String requestID = scanner.nextLine();
            System.out.print("Approve (A) or Reject (R)? ");
            String choice = scanner.nextLine().toUpperCase();
            try {
                if (choice.equals("A")) {
                    requestManager.approveRequestForStatus(requestID);
                    System.out.println("Request approved successfully.");
                } else if (choice.equals("R")) {
                    requestManager.rejectRequestForStatus(requestID);
                    System.out.println("Request rejected successfully.");
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (ModelNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void viewEnquiries() throws ModelNotFoundException {
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
}
