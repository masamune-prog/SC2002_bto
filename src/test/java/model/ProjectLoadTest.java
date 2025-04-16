package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.project.Project;
import model.user.Manager;
import model.user.Officer;
import controller.project.ProjectManager; // Added import
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProjectLoadTest {

    private ProjectManager projectManager; // Changed from ProjectRepository
    private ManagerRepository managerRepository;
    private OfficerRepository officerRepository;

    @BeforeEach
    public void setUp() {
        // Use ProjectManager to load data for the test
        projectManager = new ProjectManager();
        ProjectManager.loadProjectsFromCSV(); // Load data using the manager

        // Keep references to the same repositories throughout the test
        managerRepository = ManagerRepository.getInstance();
        officerRepository = OfficerRepository.getInstance();
        officerRepository.load();
    }

    @Test
    public void testLoadProjects() {
        // Access projects through the manager
        List<Project> projects = projectManager.getAllProjects();
        assertNotNull(projects, "Project list should not be null");
        assertFalse(projects.isEmpty(), "Project list should not be empty after loading");

        // Example: Check a specific project if needed
        // Project firstProject = projects.get(0);
        // assertEquals("ExpectedProjectID", firstProject.getID());
    }

    @Test
    public void testAllUsersAndProjects() {
        // Display all managers
        System.out.println("\n===== MANAGER INFORMATION =====");
        List<Manager> managers = managerRepository.getAll();
        System.out.println("Total managers: " + (managers != null ? managers.size() : "null"));
        if (managers != null) {
            for (Manager manager : managers) {
                System.out.println("Manager ID: " + manager.getID());
                System.out.println("  Name: " + manager.getName());
                System.out.println("  NRIC: " + (manager.getNric() != null ? manager.getNric() : "N/A"));

                // Find projects managed by this manager
                List<Project> managedProjects = new ArrayList<>();
                for (Project p : projectManager.getAllProjects()) {
                    if (p.getManagerInCharge() != null &&
                            p.getManagerInCharge().getName().equals(manager.getName())) {
                        managedProjects.add(p);
                    }
                }
                System.out.println("  Managing " + managedProjects.size() + " projects:");
                for (Project p : managedProjects) {
                    System.out.println("    - " + p.getProjectName());
                }
                System.out.println("---");
            }
        }

        // Display all officers
        System.out.println("\n===== OFFICER INFORMATION =====");
        List<Officer> officers = officerRepository.getAll();
        System.out.println("Total officers: " + (officers != null ? officers.size() : "null"));
        if (officers != null) {
            for (Officer officer : officers) {
                System.out.println("Officer ID: " + officer.getID());
                System.out.println("  Name: " + officer.getName());
                System.out.println("  NRIC: " + (officer.getNric() != null ? officer.getNric() : "N/A"));

                // Find projects assigned to this officer
                List<Project> assignedProjects = new ArrayList<>();
                for (Project p : projectManager.getAllProjects()) {
                    List<Officer> projectOfficers = p.getAssignedOfficers();
                    if (projectOfficers != null) {
                        for (Officer o : projectOfficers) {
                            if (o.getName().equals(officer.getName())) {
                                assignedProjects.add(p);
                                break;
                            }
                        }
                    }
                }
                System.out.println("  Assigned to " + assignedProjects.size() + " projects:");
                for (Project p : assignedProjects) {
                    System.out.println("    - " + p.getProjectName());
                }
                System.out.println("---");
            }
        }

        // Display project details
        System.out.println("\n===== PROJECT DETAILS =====");
        List<Project> projects = projectManager.getAllProjects();
        if (projects != null) {
            for (Project project : projects) {
                System.out.println("Project: " + project.getProjectName());

                // Manager info
                Manager manager = project.getManagerInCharge();
                System.out.println("  Manager: " + (manager != null ? manager.getName() : "None"));

                // Officer info
                List<Officer> assignedOfficers = project.getAssignedOfficers();
                System.out.println("  Number of Officers: " + project.getNumOfficers());
                System.out.println("  Assigned Officers: " +
                        assignedOfficers.size());
                if (!assignedOfficers.isEmpty()) {
                    for (Officer o : assignedOfficers) {
                        System.out.println("    - " + o.getName() + " (ID: " + o.getID() + ")");
                    }
                }
                System.out.println("---");
            }
        }
    }
}