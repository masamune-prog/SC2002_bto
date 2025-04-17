package boundary.mainpage;

import boundary.account.ChangeAccountPassword;
import boundary.account.Logout;
import boundary.modelviewer.ModelViewer;
import boundary.modelviewer.ProjectViewer;
import controller.enquiry.EnquiryManager;
import controller.project.ProjectManager;
import controller.request.ApplicantManager;
import controller.request.RequestManager;
import model.project.Project;
import model.request.ProjectApplicationRequest;
import model.request.Request;
import model.request.RoomType;
import model.user.*;
import repository.project.ProjectRepository;
import repository.user.ApplicantRepository;
import repository.enquiry.EnquiryRepository;
import model.enquiry.Enquiry;
import utils.exception.ModelNotFoundException;
import utils.exception.PageBackException;
import utils.iocontrol.IntGetter;
import utils.ui.BoundaryStrings;
import utils.ui.ChangePage;

import java.util.List;
import java.util.Scanner;

public class ApplicantMainPage {
    private final Applicant applicant;
    private final Scanner scanner;
    private final ProjectManager projectManager;
    private final ApplicantManager applicantManager;

    public ApplicantMainPage(User user) {
        if (!(user instanceof Applicant)) {
            throw new IllegalArgumentException("User must be an Applicant");
        }
        this.applicant = (Applicant) user;
        this.scanner = new Scanner(System.in);
        this.projectManager = new ProjectManager();
        this.applicantManager = new ApplicantManager();
    }

    public static void applicantMainPage(User user) {
        ApplicantMainPage applicantMainPage = new ApplicantMainPage(user);
        applicantMainPage.display();
    }

    public void display() {
        boolean displaying = true;

        while (displaying) {
            try {
                ChangePage.changePage();
                System.out.println(BoundaryStrings.separator);
                System.out.println("Welcome to Applicant Main Page");
                System.out.println("Hello, " + applicant.getName() + "!");
                System.out.println();
                System.out.println("\t1. View my profile");
                System.out.println("\t2. Change my password");
                //Option 3 does not work
                System.out.println("\t3. View available projects");
                System.out.println("\t4. View my application status");
                System.out.println("\t5. Apply for a project");
                //Withdraw application is broken
                System.out.println("\t6. Withdraw application");
                System.out.println("\t7. Book a flat");
                System.out.println("\t8. Submit enquiry");
                System.out.println("\t9. View enquiry");
                System.out.println("\t10. Delete enquiry");
                System.out.println("\t11. Edit enquiry");
                System.out.println("\t12. View booking request");
                System.out.println("\t13. Logout");
                System.out.println(BoundaryStrings.separator);

                System.out.println();
                System.out.print("Please enter your choice: ");

                // Handle integer input directly instead of using IntGetter
                int choice;
                try {
                    String input = scanner.nextLine().trim();
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    continue;
                }

                // Refresh applicant data
                Applicant refreshedApplicant = ApplicantRepository.getInstance().getByID(applicant.getID());

                switch (choice) {
                    case 1 -> displayApplicantProfile(refreshedApplicant);
                    case 2 -> ChangeAccountPassword.changePassword(UserType.APPLICANT, refreshedApplicant.getNRIC());
                    case 3 -> ProjectViewer.viewAvailableProjectList(refreshedApplicant);
                    case 4 -> viewApplicationStatus(refreshedApplicant);
                    case 5 -> applyForProject(refreshedApplicant);
                    case 6 -> withdrawApplication(refreshedApplicant);
                    case 7 -> bookFlat(refreshedApplicant);
                    case 8 -> submitEnquiry(refreshedApplicant);
                    case 9 -> viewEnquiry(refreshedApplicant);
                    case 10 -> deleteEnquiry(refreshedApplicant);
                    case 11 -> editEnquiry(refreshedApplicant);
                    case 12 -> viewBookingRequest(refreshedApplicant);
                    case 13 -> {
                        Logout.logout();
                        displaying = false;
                    }
                    default -> {
                        System.out.println("Invalid choice. Please press enter to try again.");
                        scanner.nextLine();
                    }
                }
            } catch (PageBackException e) {
                // Just continue the loop
            } catch (ModelNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                try {
                    scanner.nextLine();
                } catch (Exception ex) {
                    // If scanner has issues, create a new one
                    Scanner tempScanner = new Scanner(System.in);
                    tempScanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                try {
                    scanner.nextLine();
                } catch (Exception ex) {
                    // If scanner has issues, create a new one
                    Scanner tempScanner = new Scanner(System.in);
                    tempScanner.nextLine();
                }
            }
        }
    }
    private void displayApplicantProfile(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        //get all attributes of the applicant
        System.out.println("Applicant Profile");
        System.out.println("-----------------");
        System.out.println("Name: " + applicant.getName());
        System.out.println("NRIC: " + applicant.getNRIC());
        System.out.println("Project: " + applicant.getProject());
        System.out.println("Status: " + applicant.getStatus());
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    private void viewApplicationStatus(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("Your current application status: " + applicant.getStatus());
        if (applicant.getStatus() != ApplicantStatus.UNREGISTERED) {
            // Show the request status
            System.out.println("Your current project: " + applicant.getProject());

            // Initialize RequestManager
            RequestManager requestManager = new RequestManager();

            // Show the Request tied to User with the Type ProjectApplication
            Request applicantRequest = requestManager.getProjectApplicationRequestByApplicantID(applicant.getID());

            if (applicantRequest != null) {
                System.out.println("\nApplication Details:");
                System.out.println("------------------");
                System.out.println("Request ID: " + applicantRequest.getID());
                System.out.println("Status: " + applicantRequest.getStatus());
                System.out.println("Project ID: " + applicantRequest.getProjectID());
                System.out.println("Project Name: " + ProjectRepository.getInstance().getByID(applicantRequest.getProjectID()).getProjectName());



                if (applicantRequest instanceof ProjectApplicationRequest) {
                    ProjectApplicationRequest appRequest = (ProjectApplicationRequest) applicantRequest;
                    System.out.println("Room Type: " + appRequest.getRoomType());
                }
            } else {
                System.out.println("No application request found.");
            }
        }
        System.out.println("\nPress Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void applyForProject(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        if (applicant.getStatus() != ApplicantStatus.UNREGISTERED) {
            System.out.println("You already have an active application or registration.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.println("Available Projects:");
        ModelViewer.displayListOfDisplayable(projectManager.getAvailableProjects(applicant));
        //if no projects available
        if (projectManager.getAvailableProjects(applicant).isEmpty()) {
            System.out.println("No available projects. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.print("Enter project ID: ");
        String projectID = scanner.nextLine();
        Project selectedProject = null;
        try {
            selectedProject = projectManager.getProjectByID(projectID); // Use ProjectManager
            if (selectedProject == null) throw new ModelNotFoundException("Project not found.");
        } catch (ModelNotFoundException e) {
            System.out.println("Project not found. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.println("Selected project:");
        ModelViewer.displaySingleDisplayable(selectedProject);
        //TODO add error handling when user is not allowed to apply for the room type
        System.out.println("Select room type:");
        System.out.println("1. Two-room flat");
        System.out.println("2. Three-room flat");
        int roomChoice = IntGetter.readInt();
        RoomType roomType;
        if (roomChoice == 1) {
            roomType = RoomType.TWO_ROOM_FLAT;
        } else if (roomChoice == 2) {
            roomType = RoomType.THREE_ROOM_FLAT;
        } else {
            System.out.println("Invalid choice. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.print("Confirm application? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                String requestID = applicantManager.createProjectApplicationRequest(
                        applicant.getID(),
                        selectedProject.getID(),
                        roomType);
                System.out.println("Application submitted successfully. Request ID: " + requestID);
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Application cancelled.");
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void withdrawApplication(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
            System.out.println("You are not currently registered for any project.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        System.out.println("You are currently registered for: " + applicant.getProject());
        System.out.print("Please provide a reason for withdrawal: ");
        String reason = scanner.nextLine();
        System.out.print("Confirm withdrawal? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                String requestID = applicantManager.createProjectDeregistration(
                        applicant.getID(),
                        reason);
                System.out.println("Withdrawal request submitted successfully. Request ID: " + requestID);
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Withdrawal cancelled.");
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void bookFlat(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        if (applicant.getStatus() != ApplicantStatus.REGISTERED) {
            System.out.println("You must be registered for a project before booking a flat.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        
        // Initialize RequestManager
        RequestManager requestManager = new RequestManager();
        
        // Get the original application request
        Request applicationRequest = requestManager.getProjectApplicationRequestByApplicantID(applicant.getID());
        
        if (applicationRequest == null) {
            System.out.println("Error: Could not find your original application request.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        
        // Extract the room type from the original application if it's a ProjectApplicationRequest
        String roomType = null;
        String originalRequestID = "N/A";
        
        if (applicationRequest instanceof ProjectApplicationRequest) {
            ProjectApplicationRequest appRequest = (ProjectApplicationRequest) applicationRequest;
            roomType = appRequest.getRoomType().toString();
            originalRequestID = appRequest.getID();
            System.out.println("Room type from your original application: " + roomType.replace("_", " ").toLowerCase());
        } else {
            System.out.println("Error: Could not determine room type from original application.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }
        
        System.out.println("You are currently registered for: " + applicant.getProject());
        System.out.print("Confirm booking for a " + roomType.replace("_", " ").toLowerCase() + "? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        
        if (confirm.equals("Y")) {
            try {
                // Find assigned officers
                Project project = projectManager.getProjectByName(applicant.getProject()); // Use ProjectManager
                List<String> officerIDs = new java.util.ArrayList<>(); // Initialize empty list
                
                if (project != null && project.getAssignedOfficers() != null && !project.getAssignedOfficers().isEmpty()) {
                    // Get IDs from all assigned officers
                    for (Officer officer : project.getAssignedOfficers()) {
                        officerIDs.add(officer.getID());
                    }
                } else {
                    // Handle case with no officers if necessary, e.g., add a default or leave empty
                    // For now, we'll pass an empty list if no officers are assigned.
                }
                
                // Assuming createProjectBooking now takes List<String> officerIDs
                String requestID = applicantManager.createProjectBooking(
                        applicant.getID(),
                        originalRequestID,  // Use the original application request ID
                        officerIDs,         // Pass the list of officer IDs
                        roomType);
                        
                System.out.println("Booking request submitted successfully. Request ID: " + requestID);
                System.out.println("Your booking is linked to your original application: " + originalRequestID);
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Booking cancelled.");
        }
        
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void submitEnquiry(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        System.out.println("Submit Enquiry");
        System.out.println("Please enter your enquiry:");
        String question = scanner.nextLine();
        
        if (question.trim().isEmpty()) {
            System.out.println("Enquiry cannot be empty. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        Enquiry enquiry = EnquiryRepository.getInstance().createEnquiry(question, applicant.getID());
        System.out.println("Enquiry submitted successfully!");
        System.out.println("Your enquiry ID is: " + enquiry.getEnquiryID());
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void viewEnquiry(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        System.out.println("View Enquiries");
        List<Enquiry> enquiries = EnquiryRepository.getInstance().getEnquiriesByCreator(applicant.getID());

        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            System.out.println("Your Enquiries:");
            System.out.println("---------------");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getEnquiryID());
                System.out.println("Question: " + enquiry.getQuestion());
                System.out.println("Answer: " + (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty() 
                    ? enquiry.getAnswer() : "Not answered yet"));
                System.out.println("---------------");
            }
        }
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void deleteEnquiry(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        List<Enquiry> enquiries = EnquiryRepository.getInstance().getEnquiriesByCreator(applicant.getID());
        System.out.println("Delete Enquiry");
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            System.out.println("Your Enquiries:");
            System.out.println("---------------");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getEnquiryID());
                System.out.println("Question: " + enquiry.getQuestion());
                System.out.println("Answer: " + (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty()
                        ? enquiry.getAnswer() : "Not answered yet"));
                System.out.println("---------------");
            }
        }
        System.out.println("Please enter the enquiry ID to delete:");
        String enquiryID = scanner.nextLine();
        
        Enquiry enquiry = EnquiryRepository.getInstance().getByID(enquiryID);
        if (enquiry == null) {
            System.out.println("Enquiry not found. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        if (!enquiry.getCreatorID().equals(applicant.getID())) {
            System.out.println("You can only delete your own enquiries. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        System.out.print("Are you sure you want to delete this enquiry? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        
        if (confirm.equals("Y")) {
            EnquiryRepository.getInstance().deleteEnquiry(enquiryID);
            System.out.println("Enquiry deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
        
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }

    private void editEnquiry(Applicant applicant) throws PageBackException {
        ChangePage.changePage();
        System.out.println("Edit Enquiry");
        //Show the enquiry list first
        List<Enquiry> enquiries = EnquiryRepository.getInstance().getEnquiriesByCreator(applicant.getID());

        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            System.out.println("Your Enquiries:");
            System.out.println("---------------");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Enquiry ID: " + enquiry.getEnquiryID());
                System.out.println("Question: " + enquiry.getQuestion());
                System.out.println("Answer: " + (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty()
                        ? enquiry.getAnswer() : "Not answered yet"));
                System.out.println("---------------");
            }
        }
        System.out.println("Please enter the enquiry ID to edit:");
        String enquiryID = scanner.nextLine();
        
        Enquiry enquiry = EnquiryRepository.getInstance().getByID(enquiryID);
        if (enquiry == null) {
            System.out.println("Enquiry not found. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        if (!enquiry.getCreatorID().equals(applicant.getID())) {
            System.out.println("You can only edit your own enquiries. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        if (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty()) {
            System.out.println("Cannot edit an enquiry that has been answered.");
            System.out.println("Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        System.out.println("Current question: " + enquiry.getQuestion());
        System.out.println("Enter new question:");
        String newQuestion = scanner.nextLine();
        
        if (newQuestion.trim().isEmpty()) {
            System.out.println("Question cannot be empty. Press Enter to go back.");
            scanner.nextLine();
            throw new PageBackException();
        }

        System.out.print("Are you sure you want to update this enquiry? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();
        
        if (confirm.equals("Y")) {
            enquiry.setQuestion(newQuestion);
            System.out.println("Enquiry updated successfully!");
        } else {
            System.out.println("Update cancelled.");
        }
        
        System.out.println("Press Enter to go back.");
        scanner.nextLine();
        throw new PageBackException();
    }
    //TODO: Implement view successful booking
    private void viewBookingRequest(Applicant applicant) throws PageBackException, ModelNotFoundException {
        ChangePage.changePage();
        System.out.println("View Booking Request");
        List<Request> bookingRequests = applicantManager.getBookingRequestsByApplicant(applicant.getID());
        //cast to ProjectBookingRequest, and get the project name and the request with the largest ID
        if (bookingRequests.isEmpty()) {
            System.out.println("No booking requests found.");
        } else {
            System.out.println("Your Booking Requests:");
            System.out.println("----------------------");
            for (Request request : bookingRequests) {
                if (request instanceof ProjectApplicationRequest) {
                    ProjectApplicationRequest bookingRequest = (ProjectApplicationRequest) request;
                    System.out.println("Request ID: " + bookingRequest.getID());
                    System.out.println("Project Name: " + ProjectRepository.getInstance().getByID(bookingRequest.getProjectID()).getProjectName());
                    System.out.println("Status: " + bookingRequest.getStatus());
                    System.out.println("Room Type: " + bookingRequest.getRoomType());
                    System.out.println("----------------------");
                }
            }
        }
    }
}