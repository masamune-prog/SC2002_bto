package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.account.ViewUserProfile;
import boundary.modelviewer.ProjectViewer;
import controller.enquiry.EnquiryManager;
import controller.project.ProjectManager;
import controller.request.ApplicantManager;
import controller.request.RequestManager;
import model.enquiry.Enquiry;
import model.project.Project;
import model.project.RoomType;
import model.request.ProjectApplicationRequest;
import model.request.Request;
import model.user.*;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.ChangePage;

import java.util.List;
import java.util.Scanner;

public class ApplicantMainPage {

    public static void applicantMainPage(User user) {
        if (user instanceof Applicant applicant) {
            ChangePage.changePage();
            System.out.println(BoundaryStrings.separator);
            System.out.println("Welcome to Applicant Main Page");
            System.out.println("Hello, " + applicant.getName() + "!");
            System.out.println();
            System.out.println("\t1. View my profile");
            System.out.println("\t2. Change my password");
            System.out.println("\t3. View available projects");
            System.out.println("\t4. View my application status");
            System.out.println("\t5. Apply for a project");
            System.out.println("\t6. Withdraw application");
            System.out.println("\t7. Book a flat");
            System.out.println("\t8. Submit enquiry");
            System.out.println("\t9. View enquiry");
            System.out.println("\t10. Delete enquiry");
            System.out.println("\t11. Edit enquiry");
            System.out.println("\t12. View booking request");;
            System.out.println("\t13. Change Project Filter/Sort");
            System.out.println("\t14. Logout");
            System.out.println(BoundaryStrings.separator);

            System.out.println();
            System.out.print("Please enter your choice: ");

            int choice = IntGetter.readInt();
            try {
                applicant = ApplicantManager.getByNRIC(applicant.getID());
            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            }

            try {
                switch (choice) {
                    case 1 -> viewProfile(applicant);
                    case 2 -> changePassword(applicant);
                    case 3 -> viewAvailableProjects(applicant);
                    case 4 -> viewApplicationStatus(applicant.getID());
                    case 5 -> applyForProject(applicant);
                    case 6 -> withdrawApplication(applicant);
                    case 7 -> bookFlat(applicant);
                    case 8 -> submitEnquiry(applicant);
                    case 9 -> viewEnquiries(applicant);
                    case 10 -> deleteEnquiry(applicant);
                    case 11 -> editEnquiry(applicant);
                    case 12 -> viewBookingRequest(applicant);
                    case 13 -> changeProjectFilter(applicant);
                    case 14 -> logout();
                    default -> {
                        System.out.println("Invalid choice. Please press enter to try again.");
                        new Scanner(System.in).nextLine();
                        throw new PageBackException();
                    }
                }
            } catch (PageBackException e) {
                applicantMainPage(applicant);
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Press enter to continue.");
                new Scanner(System.in).nextLine();
                applicantMainPage(applicant);
            }
        } else {
            throw new IllegalArgumentException("User is not an applicant.");
        }
    }

    private static void viewProfile(Applicant applicant) throws PageBackException {
        ViewUserProfile.viewUserProfilePage(applicant);
    }

    private static void changePassword(Applicant applicant) throws PageBackException {
        ChangeAccountPassword.changePassword(UserType.APPLICANT, applicant.getID());
    }

    private static void viewAvailableProjects(Applicant applicant) throws PageBackException, ModelNotFoundException {
        if (applicant.getApplicantStatus() != ApplicantStatus.NO_REGISTRATION) {
            System.out.println("You already have an active application or registration.");
            System.out.println("Press Enter to go back.");
            throw new PageBackException();
        }
        List<Project> projects = ProjectManager.getAvailableProject(applicant.getNRIC());
        ProjectViewer.displayProjects(projects);
    }

    private static void viewApplicationStatus(String applicantID) throws PageBackException, ModelNotFoundException {
        Request requests = RequestManager.getAllApplicationRequestsByUser(applicantID);
        if (requests == null) {
            System.out.println("No application requests found.");
        } else {
            requests.printRequest();
        }
        System.out.println("Press Enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void applyForProject(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        Scanner scanner = new Scanner(System.in);
        if (applicant.getApplicantStatus() != ApplicantStatus.NO_REGISTRATION) {
            System.out.println("You already have an active application or registration.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.println("Available Projects:");
        List<Project> projects = ProjectManager.getAvailableProject(applicant.getNRIC());
        ProjectViewer.displayProjects(projects);
        if (projects.isEmpty()) {
            System.out.println("No available projects. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.print("Enter project ID: ");
        String projectID = scanner.nextLine();
        Project selectedProject;
        try {
            selectedProject = ProjectManager.getByID(projectID);
        } catch (ModelNotFoundException e) {
            System.out.println("Project not found. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        // Do not allow user to apply for project type they shld not be able to do
        System.out.println("Selected project:");
        selectedProject.getDisplayableString();
        System.out.println("Select room type:");
        System.out.println("1. Two-room flat");
        if(applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
            System.out.println("2. Three-room flat");
        }
        int roomChoice = IntGetter.readInt();
        RoomType roomType;
        if (roomChoice == 1) roomType = RoomType.TWO_ROOM_FLAT;
        else if (roomChoice == 2) roomType = RoomType.THREE_ROOM_FLAT;
        else {
            System.out.println("Invalid choice. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.print("Confirm application? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                String requestID = ApplicantManager.createProjectApplicationRequest(
                        applicant.getID(), selectedProject.getID(), roomType);
                System.out.println("Application submitted successfully. Request ID: " + requestID);
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Application cancelled.");
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void withdrawApplication(Applicant applicant) throws PageBackException, ModelNotFoundException, ModelAlreadyExistsException {
        if (applicant.getApplicantStatus() == ApplicantStatus.NO_REGISTRATION) {
            System.out.println("You have no active application or registration.");
            new Scanner(System.in).nextLine();
            throw new PageBackException();
        }
        System.out.println("Reason for withdrawal:");
        String reason = new Scanner(System.in).nextLine();
        //Get the person ProjectApplicationRequest
        ProjectApplicationRequest request = (ProjectApplicationRequest) RequestManager.getAllApplicationRequestsByUser(applicant.getID());
        assert request != null;
        ApplicantManager.createWithdrawalRequest(request.getProjectID(), applicant.getID(), request.getRoomType(), reason);
        System.out.println("Application for withdrawal submitted successfully.");
        System.out.println("Press Enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void bookFlat(Applicant applicant) throws PageBackException, ModelNotFoundException {
        if (applicant.getApplicantStatus() != ApplicantStatus.SUCCESSFUL) {
            System.out.println("You have no bookings you can make.");
            new Scanner(System.in).nextLine();
            throw new PageBackException();
        }
        Request request = RequestManager.getAllApprovedApplicationRequestsByUser(applicant.getID());
        ProjectApplicationRequest projectApplicationRequest = (ProjectApplicationRequest) request;
        request.printRequest();
        System.out.println("1. Book the flat");
        System.out.println("2. Go back");
        int choice = IntGetter.readInt();
        if (choice == 2) {
            throw new PageBackException();
        } else {
            try {
                ApplicantManager.createBookingRequest(applicant.getID(), projectApplicationRequest.getRoomType());
                System.out.println("Flat booked successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Press Enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void submitEnquiry(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your enquiry Title:");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Enquiry Title cannot be empty. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        System.out.println("Please enter your enquiry Description:");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println("Enquiry Description cannot be empty. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        System.out.println("Confirm submission? (Y/N):");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Enquiry not submitted. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }


        try {
            String enquiryID = EnquiryManager.createNewEnquiry(
                    title,
                    applicant.getID(),
                    description,
                    null,      // answer initially null
                    false      // not answered yet
            );
            System.out.println("Enquiry submitted successfully with ID: " + enquiryID);
        } catch (ModelAlreadyExistsException e) {
            System.out.println("Failed to submit enquiry: " + e.getMessage());
        }

        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }


    private static void viewEnquiries(Applicant applicant) throws PageBackException {
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
                System.out.println();
            }
        }
        System.out.println("Press Enter to go back.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void deleteEnquiry(Applicant applicant) throws PageBackException, ModelNotFoundException {
        List<Enquiry> enquiries = EnquiryManager.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            //get list printer to work later TODO
            System.out.println("Enquiries:");
            System.out.println("────────────────────────────────────────");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getID());
                System.out.println("Title: " + enquiry.getQuestion());
                System.out.println("Description: " + enquiry.getAnswer());
                System.out.println("Status: " + (enquiry.getAnswered() ? "Answered" : "Unanswered"));
                System.out.println();
            }
        }
        System.out.println("Which Enquiry would you like to delete?");
        System.out.println("Enter the Enquiry ID:");
        Scanner scanner = new Scanner(System.in);
        String enquiryID = scanner.nextLine();
        EnquiryManager.getEnquiryByID(enquiryID);
        EnquiryManager.deleteEnquiry(enquiryID);
        System.out.println("Enquiry deleted successfully.");
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private static void editEnquiry(Applicant applicant) throws PageBackException, ModelNotFoundException {
        List<Enquiry> enquiries = EnquiryManager.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            //get list printer to work later TODO
            System.out.println("Enquiries:");
            System.out.println("────────────────────────────────────────");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getID());
                System.out.println("Title: " + enquiry.getQuestion());
                System.out.println("Description: " + enquiry.getAnswer());
                System.out.println("Status: " + (enquiry.getAnswered() ? "Answered" : "Unanswered"));
                System.out.println();
            }
        }
        System.out.println("Which Enquiry would you like to edit?");
        System.out.println("Enter the Enquiry ID:");
        Scanner scanner = new Scanner(System.in);
        String enquiryID = scanner.nextLine();
        Enquiry enquiry = EnquiryManager.getEnquiryByID(enquiryID);
        System.out.println("Which field would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        int choice = IntGetter.readInt();
        if (choice == 1) {
            System.out.println("Enter new Title:");
            String newTitle = scanner.nextLine();
            EnquiryManager.editEnquiry(enquiryID, newTitle, enquiry.getContent());
        } else if (choice == 2) {
            System.out.println("Enter new Description:");
            String newDescription = scanner.nextLine();
            EnquiryManager.editEnquiry(enquiryID, enquiry.getEnquiryTitle(), newDescription);
        } else {
            System.out.println("Invalid choice. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.println("Enquiry updated successfully.");
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();

    }

    private static void viewBookingRequest(Applicant applicant) throws PageBackException, ModelNotFoundException {
        Request request = RequestManager.getBookingRequestByApplicant(applicant.getID());
        if (request == null) {
            System.out.println("No booking request found.");
        } else {
            request.printRequest();
        }

    }

    private static void changeProjectFilter(Applicant applicant) throws PageBackException {
        //FilterManager.changeProjectFilter(applicant);
    }

    private static void logout() throws PageBackException {
        Logout.logout();
    }
}