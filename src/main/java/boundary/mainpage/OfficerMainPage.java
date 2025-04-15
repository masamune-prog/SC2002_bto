package boundary.mainpage;

import controller.project.ProjectManager;
import controller.enquiry.EnquiryController;
import model.project.Project;
import model.enquiry.Enquiry;
import model.user.Officer;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class OfficerMainPage {
    private final Scanner scanner = new Scanner(System.in);
    private final ProjectManager projectManager = new ProjectManager();
    private final EnquiryController enquiryController = new EnquiryController();
    private final Officer officer;

    public OfficerMainPage(Officer officer) {
        this.officer = officer;
    }

    public void display() {
        while (true) {
            System.out.println("\n=== Officer Main Page ===");
            System.out.println("1. View My Projects");
            System.out.println("2. View Project Details");
            System.out.println("3. View All Enquiries");
            System.out.println("4. Reply to Enquiry");
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            int choice = getIntInput(0, 4);

            switch (choice) {
                case 1 -> viewMyProjects();
                case 2 -> viewProjectDetails();
                case 3 -> viewAllEnquiries();
                case 4 -> replyToEnquiry();
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
            }
        }
    }

    private void viewMyProjects() {
        System.out.println("\n=== My Projects ===");
        List<Project> projects = projectManager.getOfficerProjects(officer);
        if (projects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }
        displayProjects(projects);
    }

    private void viewProjectDetails() {
        System.out.println("\n=== Project Details ===");
        List<Project> projects = projectManager.getOfficerProjects(officer);
        if (projects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }

        displayProjects(projects);
        System.out.print("Enter project number to view details: ");
        int projectIndex = getIntInput(1, projects.size());
        Project project = projects.get(projectIndex - 1);
        System.out.println(project.getDisplayableString());
    }

    private void viewAllEnquiries() {
        System.out.println("\n=== All Enquiries ===");
        List<Enquiry> enquiries = enquiryController.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available.");
            return;
        }
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryID());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Answer: " + (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty() 
                ? enquiry.getAnswer() : "Not answered yet"));
            System.out.println("   Creator ID: " + enquiry.getCreatorID());
            System.out.println("   --------------------");
        }
    }

    private void replyToEnquiry() {
        System.out.println("\n=== Reply to Enquiry ===");
        List<Enquiry> enquiries = enquiryController.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries available to reply.");
            return;
        }

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryID());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Answer: " + (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty() 
                ? enquiry.getAnswer() : "Not answered yet"));
            System.out.println("   Creator ID: " + enquiry.getCreatorID());
            System.out.println("   --------------------");
        }

        System.out.print("Enter enquiry number to reply: ");
        int enquiryIndex = getIntInput(1, enquiries.size());
        Enquiry enquiry = enquiries.get(enquiryIndex - 1);

        System.out.print("Enter your reply: ");
        String reply = scanner.nextLine().trim();
        if (enquiryController.replyToEnquiry(enquiry.getID(), reply)) {
            System.out.println("Reply sent successfully!");
        } else {
            System.out.println("Failed to send reply.");
        }
    }

    private void displayProjects(List<Project> projects) {
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
        }
    }

    private int getIntInput(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }
}
