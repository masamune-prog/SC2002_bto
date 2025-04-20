package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.account.ViewUserProfile;
import boundary.modelviewer.ProjectViewer;
import controller.enquiry.EnquiryManager;
import controller.project.ProjectManager;
import controller.request.ApplicantManager;
import controller.request.OfficerManager;
import controller.request.RequestManager;
import model.request.ProjectApplicationRequest;
import model.request.Request;
import model.user.*;
import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.ProjectBookingRequest;
import model.enquiry.Enquiry;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.ChangePage;
import utils.ui.InputHelper;

import java.util.*;

import static boundary.modelviewer.ProjectViewer.displayProjects;

/**
 * Console UI for officers, in the style of ApplicantMainPage.
 */
public class OfficerMainPage {
    private static final Scanner scanner = new Scanner(System.in);
    private static Map<String, Integer> officerFilterNumbers = new HashMap<>();
    public static void officerMainPage(User user) {
        if (!(user instanceof Officer officer)) {
            throw new IllegalArgumentException("User is not an officer.");
        }
        try {
            ChangePage.changePage();
            System.out.println(BoundaryStrings.separator);
            System.out.println("Welcome to Officer Main Page");
            System.out.println("Hello, " + officer.getName() + "!");
            System.out.println();
            System.out.println("\t1. View my profile");
            System.out.println("\t2. Change my password");
            System.out.println("\t3. View projects in charge");
            System.out.println("\t4. Apply to be officer for projects");
            System.out.println("\t5. View my officer applications");
            System.out.println("\t6. View pending project booking requests");
            System.out.println("\t7. Approve/Reject booking requests");
            System.out.println("\t8. View enquiries");
            System.out.println("\t9. Reply to enquiries");
            System.out.println("\t10. View all projects");
            System.out.println("\t11. Change project filter/sort");
            System.out.println("\t12. Logout");
            System.out.println(BoundaryStrings.separator);
            System.out.print("Please enter your choice: ");
            int choice = IntGetter.readInt();

            switch (choice) {
                case 1 -> viewProfile(officer);
                case 2 -> changePassword(officer);
                case 3 -> viewProjectsInCharge(officer);
                case 4 -> applyToBeOfficer(officer);
                case 5 -> viewOfficerApplications(officer);
                case 6 -> viewBookingRequests(officer);
                case 7 -> handleBookingApprovals(officer);
                case 8 -> listEnquiries();
                case 9 -> replyToEnquiries();
                case 10 -> viewAllProjects(officer);
                case 11 -> changeProjectFilter(officer);
                case 12 -> { Logout.logout(); return; }
                default -> {
                    System.out.println("Invalid choice.");
                }
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            officerMainPage(officer);

        } catch (PageBackException e) {
            officerMainPage(officer);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            officerMainPage(officer);
        }
    }
    private static void changeProjectFilter(Officer officer) throws PageBackException {
        //get the filter numbers from the static map
        int filterNumber = officerFilterNumbers.getOrDefault(officer.getID(), 0);
        System.out.println("Filter number: " + filterNumber);
        System.out.println("Please enter the filter number (0-3):");
        System.out.println("0. No filter");
        System.out.println("1. Filter by 2 Room Flat");
        System.out.println("2. Filter by 3 Room Flat");
        Scanner scanner = new Scanner(System.in);
        int filterChoice = IntGetter.readInt();
        if (filterChoice < 0 || filterChoice > 3) {
            System.out.println("Invalid choice. Press Enter to go back.");
            //prompt for new input
            scanner.nextLine();
            throw new PageBackException();
        }
        if (filterChoice == 0) {
            officerFilterNumbers.put(officer.getID(), filterChoice);
            System.out.println("Filter removed.");
        } else {
            officerFilterNumbers.put(officer.getID(), filterChoice);
            System.out.println("Filter set to " + filterChoice);
        }

    }
    private static void viewAllProjects(Officer officer) throws PageBackException, ModelNotFoundException {
        ProjectViewer.viewAllProjects(officerFilterNumbers.getOrDefault(officer.getID(), 0));

    }
    private static void viewProfile(Officer officer) throws PageBackException {
        ViewUserProfile.viewUserProfilePage(officer);
        throw new PageBackException();
    }

    private static void changePassword(Officer officer) throws PageBackException {
        ChangeAccountPassword.changePassword(UserType.OFFICER, officer.getID());
    }

    private static void viewProjectsInCharge(Officer officer) throws ModelNotFoundException, PageBackException {
        List<String> projectIDs = OfficerManager.getProjectsByOfficerID(officer.getID());
        // get the projects that the officer is in charge of
        List<Project> allProjects = ProjectManager.getAllProjects();
        List<Project> list = allProjects.stream()
                .filter(project -> projectIDs.contains(project.getID()))
                .toList();
        System.out.println("Projects in charge:");
        if (list.isEmpty()) {
            System.out.println("No projects in charge.");
            throw new PageBackException();
        }
        for (Project p : list) {
            System.out.println(p.getDisplayableString());
        }
        throw new PageBackException();
    }

    private static void applyToBeOfficer(Officer officer) throws ModelNotFoundException, PageBackException, ModelAlreadyExistsException {
        System.out.println("Available projects:");
        displayProjects(ProjectManager.getAllProjects());
        List<Project> all = ProjectManager.getAllProjects();
        if (all.isEmpty()) {
            System.out.println("No available projects.");
            throw new PageBackException();
        }
        System.out.print("Enter project ID: ");
        String projectID = scanner.nextLine();
        Project project = ProjectManager.getByID(projectID);
        if (project == null) {
            System.out.println("Project not found.");
            throw new PageBackException();
        }
        // Check if the officer is already assigned to this project or there are more than 10 officers
        if (project.getOfficerIDs().contains(officer.getID())) {
            System.out.println("You are already assigned to this project.");
            throw new PageBackException();
        }
        if (project.getOfficerIDs().size() >= 10) {
            System.out.println("This project already has 10 officers.");
            throw new PageBackException();
        }
        //check if the date overlaps with any of officer current projects
        List<Project> projects = ProjectManager.getAllProjects();
        for (Project p : projects) {
            if (p.getOfficerIDs().contains(officer.getID()) && p.getID() != projectID) {
                if (p.getApplicationOpeningDate().isBefore(project.getApplicationClosingDate()) && p.getApplicationClosingDate().isAfter(project.getApplicationOpeningDate())) {
                    System.out.println("You have a date conflict with another project.");
                    throw new PageBackException();
                }
            }
        }
        // Check officer is not a applicant for this project
        Request requests = RequestManager.getApplicationRequestByApplicant(officer.getID());
        Request getBookingRequests = RequestManager.getBookingRequestByApplicant(officer.getID());
        if(requests == null || getBookingRequests == null) {
            String requestID = OfficerManager.createOfficerApplicationRequest(officer.getID(), projectID);
            System.out.println("Application submitted successfully. Request ID: " + requestID);
            throw new PageBackException();
        }
        if(requests.getProjectID().equals(project.getID())) {
            System.out.println("You have already applied for this project.");
            throw new PageBackException();
        }
        if(getBookingRequests.getProjectID().equals(project.getID())) {
            System.out.println("You have already applied for this project.");
            throw new PageBackException();
        }


    }

    private static void viewOfficerApplications(Officer officer)
            throws ModelNotFoundException, PageBackException {
        // clear screen / change page
        ChangePage.changePage();

        System.out.println("=== Your Officer Applications ===");
        // fetch requests from controller

        List<OfficerApplicationRequest> apps = OfficerManager.getOfficerApplicationsByOfficerID(officer.getID());

        if (apps.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (OfficerApplicationRequest req : apps) {
                System.out.println(req.getDisplayableString());
                System.out.println("────────────────────────────────────────");
            }
        }

        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void viewBookingRequests(Officer officer) throws ModelNotFoundException, PageBackException {
        // Get pending booking requests for this officer using the existing controller
        List<ProjectBookingRequest> pendingReqs = RequestManager.getOfficerPendingBookingRequests(officer.getID());

        System.out.println("Pending booking requests:");
        if (pendingReqs.isEmpty()) {
            System.out.println("No pending booking requests found.");
        } else {
            for (var r : pendingReqs) {
                System.out.println(r.getDisplayableString());
            }
        }
        throw new PageBackException();
    }

    private static void handleBookingApprovals(Officer officer) throws ModelNotFoundException, PageBackException {
        List<ProjectBookingRequest> pending = RequestManager.getOfficerPendingBookingRequests(officer.getID());

        System.out.println("Pending bookings:");
        if (pending.isEmpty()) {
            System.out.println("No pending booking requests found.");
            throw new PageBackException();
        }

        for (int i = 0; i < pending.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, pending.get(i).getDisplayableString());
        }

        System.out.print("Select number to process: ");
        int sel = IntGetter.readInt();

        // Validate selection range
        if (sel < 1 || sel > pending.size()) {
            System.out.println("Invalid selection. Please try again.");
            throw new PageBackException();
        }

        String id = pending.get(sel - 1).getID();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Approve? (y/n): ");
        String choice = scanner.nextLine();
        boolean ok = choice.trim().toLowerCase().startsWith("y");

        if (ok) {
            RequestManager.approveBookingRequest(id);
        } else {
            RequestManager.rejectBookingRequest(id);
        }

        System.out.println("Request updated.");
        throw new PageBackException();
    }

    private static void listEnquiries() throws PageBackException {
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

    private static void replyToEnquiries() throws PageBackException {
        System.out.println("\n=== Reply to Enquiries ===");
        List<Enquiry> enquiries = EnquiryManager.getUnansweredEnquiries();
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
}