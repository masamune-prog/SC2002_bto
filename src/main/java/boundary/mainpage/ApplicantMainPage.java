package boundary.mainpage;

import java.util.Scanner;

import boundary.modelviewer.ProjectViewer;
import boundary.welcome.ExitPage;

import model.user.Applicant;
import model.user.ApplicantStatus;
import model.request.ProjectApplicationRequest;
import model.request.ProjectBookingRequest;
import model.request.ProjectDeregistrationRequest;
import model.enquiry.Enquiry;

import utils.exception.PageBackException;



public class ApplicantMainPage {
	private static final Scanner sc = new Scanner(System.in);
	private Applicant applicant;
	
	public void start() throws PageBackException{
		int choice = 1;
		
		do {
			System.out.println("\n--- Applicant Menu ---");
			System.out.println("1. View available BTO projects");
			System.out.println("2. Apply for a BTO project");
			System.out.println("3. View details of applied project");
			System.out.println("4. Book a flat");
			System.out.println("5. Submit an enquiry");
			System.out.println("6. Request to withdraw application");
			System.out.println("7. Log out");
			System.out.println("\nEnter your choice (1-7): ");
			
			choice = sc.nextInt();
			sc.nextLine();
			
			switch(choice) {
			case 1:
				ProjectViewer.viewAvailableProjectList(applicant);
				break;
				
			case 2:
				System.out.println("Enter project name to apply:");
				String projectName = sc.nextLine();
				// access project by projectName (incomplete)
				
				// create an application request
				ProjectApplicationRequest application = new ProjectApplicationRequest(requestID, projectName, requestStatus, managerID, applicant.getID(), roomType);
				application.toMap();
				break;
				
			case 3:
				ProjectViewer.viewApplicantProject(applicant);
				break;
				
			case 4:
				if (applicant.getStatus() == ApplicantStatus.REGISTERED) {
					ProjectBookingRequest booking = new ProjectBookingRequest(requestID, applicant.getProject(), requestStatus, managerID, officerID, applicant.getID(), originalRequestID, roomType, bookingDate);
					booking.toMap();
				}
				else {
					System.out.println("Not allowed to book flat currently.");
				}
				break;
				
			case 5:
				System.out.println("Enter your enquiry:");
				String enquiry = sc.nextLine();
				
				
				break;
				
			case 6:
				System.out.println("Enter reason for withdrawal: ");
				String reason = sc.nextLine();
				ProjectDeregistrationRequest withdrawal = new ProjectDeregistrationRequest(requestID, projectID, requestStatus, managerID, applicant.getID(), originalRequestID, reason);
				withdrawal.toMap();
				break;
				
			case 7:
				ExitPage.exitPage();
				break;
				
			default:
				System.out.println("\nInvalid choice. Please enter a number from 1 to 7.");
				break;
			}
		} while (choice >= 1 && choice < 7);
	}	
}
