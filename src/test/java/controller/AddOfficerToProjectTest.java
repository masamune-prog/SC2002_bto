package controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.project.ProjectManager;
import controller.request.OfficerManager;
import controller.request.RequestManager;
import model.project.Project;
import model.request.OfficerApplicationRequest;
import model.request.Request;
import model.request.RequestStatus;
import model.user.Manager;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.exception.ModelNotFoundException;

/**
 * Unit tests for adding officers to projects
 */
public class AddOfficerToProjectTest {
    
    private ProjectManager projectManager;
    private OfficerManager officerManager;
    private RequestManager requestManager;
    private ManagerRepository managerRepository;
    private OfficerRepository officerRepository;
    private ProjectRepository projectRepository;
    private RequestRepository requestRepository;
    
    @BeforeEach
    public void setUp() {
        // Initialize repositories and managers
        projectManager = new ProjectManager();
        officerManager = new OfficerManager();
        requestManager = new RequestManager();
        
        managerRepository = ManagerRepository.getInstance();
        officerRepository = OfficerRepository.getInstance();
        projectRepository = ProjectRepository.getInstance();
        requestRepository = RequestRepository.getInstance();
        
        // Load data
        managerRepository.load();
        officerRepository.load();
        projectRepository.load();
        requestRepository.load();
    }
    
    /**
     * Helper method to print project details
     */
    private void printProjectDetails(Project project, String prefix) {
        System.out.println("\n" + prefix + " PROJECT DETAILS =====");
        System.out.println("Project ID: " + project.getID());
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Manager in charge: " + (project.getManagerInCharge() != null ? 
                           project.getManagerInCharge().getName() : "None"));
        
        System.out.println("Number of officers assigned: " + project.getNumOfficers());
        System.out.println("Officer IDs list: " + (project.getOfficerIDs() != null ? 
                           project.getOfficerIDs() : "null"));
        
        List<Officer> officers = project.getAssignedOfficers();
        System.out.println("Assigned Officers count: " + (officers != null ? officers.size() : "0"));
        if (officers != null && !officers.isEmpty()) {
            for (Officer o : officers) {
                System.out.println("  - " + o.getName() + " (ID: " + o.getID() + ")");
            }
        }
    }
    
    /**
     * Helper method to print officer details
     */
    private void printOfficerDetails(Officer officer, String prefix) {
        System.out.println("\n" + prefix + " OFFICER DETAILS =====");
        System.out.println("Officer ID: " + officer.getID());
        System.out.println("Officer Name: " + officer.getName());
        System.out.println("Officer NRIC: " + officer.getNric());
        
        List<String> projectsInCharge = officer.getProjectsInCharge();
        System.out.println("Projects in charge count: " + (projectsInCharge != null ? 
                           projectsInCharge.size() : "0"));
        System.out.println("Projects in charge list: " + projectsInCharge);
        
        if (projectsInCharge != null && !projectsInCharge.isEmpty()) {
            System.out.println("Projects in charge details:");
            for (String projectID : projectsInCharge) {
                try {
                    Project p = projectManager.getProjectByID(projectID);
                    System.out.println("  - " + p.getProjectName() + " (ID: " + p.getID() + ")");
                } catch (ModelNotFoundException e) {
                    System.out.println("  - Project not found: " + projectID);
                }
            }
        }
    }
    
    /**
     * Test adding an officer directly to a project using Project.addOfficer()
     */
    @Test
    public void testAddOfficerDirectly() throws ModelNotFoundException {
        System.out.println("\n\n===== TEST: ADD OFFICER DIRECTLY =====");
        
        // Get a manager to create a project
        List<Manager> managers = managerRepository.getAll();
        assertTrue(managers != null && !managers.isEmpty(), "Manager list should not be empty");
        Manager manager = managers.get(0);
        
        // Get an officer to add to the project
        List<Officer> officers = officerRepository.getAll();
        assertTrue(officers != null && !officers.isEmpty(), "Officer list should not be empty");
        Officer officer = officers.get(0);
        
        // Print officer details before
        printOfficerDetails(officer, "BEFORE");
        
        // Create a test project
        String projectName = "Test Project " + System.currentTimeMillis();
        Project project = projectManager.createProject(
            true,
            projectName,
            "Test Neighborhood",
            10,
            10,
            300000.0,
            400000.0,
            LocalDate.now(),
            LocalDate.now().plusMonths(1),
            manager
        );
        
        assertNotNull(project, "Project should be created successfully");
        
        // Print project details before
        printProjectDetails(project, "BEFORE");
        
        System.out.println("\n----- ADDING OFFICER TO PROJECT -----");
        
        // Add officer to project directly
        project.addOfficer(officer.getID());
        projectRepository.update(project);
        
        // Reload project to verify changes were saved
        Project updatedProject = projectManager.getProjectByID(project.getID());
        
        // Print project details after
        printProjectDetails(updatedProject, "AFTER");
        
        // Reload officer to see updates
        Officer updatedOfficer = officerRepository.getByID(officer.getID());
        
        // Print officer details after
        printOfficerDetails(updatedOfficer, "AFTER");
        
        // Verify officer was added
        assertTrue(updatedProject.hasOfficer(officer.getID()), 
            "Project should have the officer after adding directly");
        
        // Clean up - delete test project
        projectManager.deleteProject(project.getID());
        System.out.println("\nTest project deleted: " + project.getID());
    }
    
    /**
     * Test adding an officer to a project through the officer application and approval flow
     */
    @Test
    public void testAddOfficerThroughApprovalFlow() throws ModelNotFoundException {
        System.out.println("\n\n===== TEST: ADD OFFICER THROUGH APPROVAL FLOW =====");
        
        // Get a manager
        List<Manager> managers = managerRepository.getAll();
        assertTrue(managers != null && !managers.isEmpty(), "Manager list should not be empty");
        Manager manager = managers.get(0);
        
        // Get an officer
        List<Officer> officers = officerRepository.getAll();
        assertTrue(officers != null && !officers.isEmpty(), "Officer list should not be empty");
        Officer officer = officers.get(0);
        
        // Print officer details before
        printOfficerDetails(officer, "BEFORE");
        
        // Create a test project
        String projectName = "Approval Test Project " + System.currentTimeMillis();
        Project project = projectManager.createProject(
            true,
            projectName,
            "Test Neighborhood",
            10,
            10,
            300000.0,
            400000.0,
            LocalDate.now(),
            LocalDate.now().plusMonths(1),
            manager
        );
        
        assertNotNull(project, "Project should be created successfully");
        
        // Print project details before
        printProjectDetails(project, "BEFORE");
        
        System.out.println("\n----- CREATING OFFICER APPLICATION REQUEST -----");
        
        // Create an officer application request
        String requestID = officerManager.createOfficerApplication(officer.getID(), project.getID());
        assertNotNull(requestID, "Officer application request should be created");
        
        System.out.println("Created request ID: " + requestID);
        
        // Print request details
        Request request = requestRepository.getByID(requestID);
        System.out.println("Request type: " + request.getRequestType());
        System.out.println("Request status: " + request.getStatus());
        
        System.out.println("\n----- APPROVING OFFICER APPLICATION REQUEST -----");
        
        // Approve the request
        requestManager.approveRequest(requestID);
        
        // Verify request was approved
        request = requestRepository.getByID(requestID);
        assertEquals(RequestStatus.APPROVED, request.getStatus(), 
            "Request status should be APPROVED after approval");
        
        System.out.println("Request status after approval: " + request.getStatus());
        
        // Reload project to verify changes
        Project updatedProject = projectManager.getProjectByID(project.getID());
        
        // Print project details after
        printProjectDetails(updatedProject, "AFTER");
        
        // Reload officer to see updates
        Officer updatedOfficer = officerRepository.getByID(officer.getID());
        
        // Print officer details after
        printOfficerDetails(updatedOfficer, "AFTER");
        
        // Verify officer was added to project
        assertTrue(updatedProject.hasOfficer(officer.getID()), 
            "Project should have the officer after request approval");
        
        // Verify project was added to officer's projects in charge
        List<String> projectsInCharge = updatedOfficer.getProjectsInCharge();
        assertNotNull(projectsInCharge, "Officer's projects in charge should not be null");
        assertTrue(projectsInCharge.contains(project.getID()), 
            "Officer's projects in charge should contain the project ID");
        
        // Clean up - delete test project
        projectManager.deleteProject(project.getID());
        
        // Clean up - delete test request
        requestRepository.remove(requestID);
        
        System.out.println("\nTest project deleted: " + project.getID());
        System.out.println("Test request removed: " + requestID);
    }
    
    /**
     * Test the case where multiple officers are added to the same project
     */
    @Test
    public void testAddMultipleOfficersToProject() throws ModelNotFoundException {
        System.out.println("\n\n===== TEST: ADD MULTIPLE OFFICERS TO PROJECT =====");
        
        // Get a manager
        List<Manager> managers = managerRepository.getAll();
        assertTrue(managers != null && !managers.isEmpty(), "Manager list should not be empty");
        Manager manager = managers.get(0);
        
        // Get officers
        List<Officer> officers = officerRepository.getAll();
        assertTrue(officers != null && officers.size() >= 2, "Need at least 2 officers for this test");
        Officer officer1 = officers.get(0);
        Officer officer2 = officers.get(1);
        
        // Print officer details before
        System.out.println("\n----- OFFICERS BEFORE ADDING TO PROJECT -----");
        printOfficerDetails(officer1, "OFFICER 1 BEFORE");
        printOfficerDetails(officer2, "OFFICER 2 BEFORE");
        
        // Create a test project
        String projectName = "Multi-Officer Test " + System.currentTimeMillis();
        Project project = projectManager.createProject(
            true,
            projectName,
            "Test Neighborhood",
            10,
            10,
            300000.0,
            400000.0,
            LocalDate.now(),
            LocalDate.now().plusMonths(1),
            manager
        );
        
        assertNotNull(project, "Project should be created successfully");
        
        // Print project details before
        printProjectDetails(project, "BEFORE");
        
        System.out.println("\n----- ADDING FIRST OFFICER DIRECTLY -----");
        
        // Add first officer directly
        project.addOfficer(officer1.getID());
        projectRepository.update(project);
        
        // Print intermediate state
        Project intermediateProject = projectManager.getProjectByID(project.getID());
        printProjectDetails(intermediateProject, "INTERMEDIATE");
        
        System.out.println("\n----- ADDING SECOND OFFICER THROUGH APPROVAL FLOW -----");
        
        // Create and approve an application for the second officer
        String requestID = officerManager.createOfficerApplication(officer2.getID(), project.getID());
        System.out.println("Created request ID: " + requestID);
        requestManager.approveRequest(requestID);
        
        // Reload project
        Project updatedProject = projectManager.getProjectByID(project.getID());
        
        // Print project details after
        printProjectDetails(updatedProject, "AFTER");
        
        // Print officer details after
        System.out.println("\n----- OFFICERS AFTER ADDING TO PROJECT -----");
        Officer updatedOfficer1 = officerRepository.getByID(officer1.getID());
        Officer updatedOfficer2 = officerRepository.getByID(officer2.getID());
        printOfficerDetails(updatedOfficer1, "OFFICER 1 AFTER");
        printOfficerDetails(updatedOfficer2, "OFFICER 2 AFTER");
        
        // Verify both officers were added
        assertTrue(updatedProject.hasOfficer(officer1.getID()), 
            "Project should have the first officer");
        assertTrue(updatedProject.hasOfficer(officer2.getID()), 
            "Project should have the second officer");
        
        // Verify the project has 2 officers assigned
        List<String> officerIDs = updatedProject.getOfficerIDs();
        assertEquals(2, officerIDs.size(), "Project should have exactly 2 officers");
        
        // Clean up
        projectManager.deleteProject(project.getID());
        requestRepository.remove(requestID);
        
        System.out.println("\nTest project deleted: " + project.getID());
        System.out.println("Test request removed: " + requestID);
    }
}
