package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.project.Project;
import model.user.Manager;
import model.user.Officer;
import repository.project.ProjectRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;
import utils.iocontrol.CSVReader;

import java.util.List;
import java.util.ArrayList;

public class ProjectLoadTest {

    private ProjectRepository projectRepository;
    private ManagerRepository managerRepository;
    private OfficerRepository officerRepository;

    @BeforeEach
    public void setup() {
        // Keep references to the same repositories throughout the test
        managerRepository = ManagerRepository.getInstance();
        // Load officers first and store reference
        officerRepository = OfficerRepository.getInstance();
        officerRepository.load();

        // Keep a reference to the officers to reuse later
        List<Officer> allOfficers = new ArrayList<>(officerRepository.getAll());

        // Load project repository (which will create its own officer repository)
        projectRepository = ProjectRepository.getInstance();
        projectRepository.load();

        // Manually fix officer assignments if needed
        fixOfficerAssignments(allOfficers);
    }

    // Helper method to maintain officer references
    private void fixOfficerAssignments(List<Officer> officers) {
        // Get project CSV data directly
        String filePath = projectRepository.getFilePath();
        List<List<String>> csvData = CSVReader.read(filePath, true);

        // Process each project
        for (List<String> row : csvData) {
            if (row.size() >= 13) {
                String projectName = row.get(0);
                String officerName = row.get(12);

                if (officerName != null && !officerName.isEmpty() && !officerName.equals("N/A")) {
                    // Find matching officer
                    Officer matchingOfficer = null;
                    for (Officer officer : officers) {
                        if (officer.getName().equals(officerName)) {
                            matchingOfficer = officer;
                            break;
                        }
                    }

                    // Assign to project if found
                    if (matchingOfficer != null) {
                        Project project = projectRepository.getByProjectName(projectName);
                        if (project != null) {
                            project.assignOfficer(matchingOfficer);
                        }
                    }
                }
            }
        }
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
                for (Project p : projectRepository.getAll()) {
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
                for (Project p : projectRepository.getAll()) {
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
        List<Project> projects = projectRepository.getAll();
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