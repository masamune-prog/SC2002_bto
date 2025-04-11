package repository.project;

import model.project.Project;
import model.user.Manager;
import repository.Repository;
import repository.user.ManagerRepository;
import utils.config.Location;
import utils.iocontrol.CSVReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectRepository extends Repository<Project> {

    private static final String FILE_PATH = "\\ProjectList.csv";
    private static ProjectRepository instance;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    private ProjectRepository() {
        super();
        load();
    }

    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return Location.RESOURCE_LOCATION + FILE_PATH;
    }

    @Override
    public void load() {
        this.getAll().clear();
        List<List<String>> csvData = CSVReader.read(getFilePath(), true);
        List<Map<String, String>> mappedData = convertToMapList(csvData);
        setAll(mappedData);
    }

    private List<Map<String, String>> convertToMapList(List<List<String>> csvData) {
        List<Map<String, String>> result = new ArrayList<>();

        // These are the actual CSV column headers
        String[] csvHeaders = {
                "Project Name", "Neighbourhood", "Type 1", "Number of units for Type 1", "Selling price for Type 1",
                "Type 2", "Number of units for Type 2", "Selling price for Type 2",
                "Application opening date", "Application closing date", "Manager",
                "Officer Slot", "Officer"
        };

        // Create a mapping from CSV headers to Project field names
        Map<String, String> headerToFieldMap = new HashMap<>();
        headerToFieldMap.put("Project Name", "projectName");
        headerToFieldMap.put("Neighbourhood", "neighborhood");
        headerToFieldMap.put("Type 1", "targetedUserGroup");
        headerToFieldMap.put("Number of units for Type 1", "twoRoomFlatsAvailable");
        headerToFieldMap.put("Selling price for Type 1", "twoRoomFlatsPrice");
        headerToFieldMap.put("Type 2", "visibility"); // Handled specially
        headerToFieldMap.put("Number of units for Type 2", "threeRoomFlatsAvailable");
        headerToFieldMap.put("Selling price for Type 2", "threeRoomFlatsPrice");
        headerToFieldMap.put("Application opening date", "applicationOpeningDate");
        headerToFieldMap.put("Application closing date", "applicationClosingDate");
        headerToFieldMap.put("Manager", "managerInCharge");
        headerToFieldMap.put("Officer Slot", "numOfficerSlots");
        headerToFieldMap.put("Officer", "officer"); // Handled by Project class

        // Process each row
        for (List<String> row : csvData) {
            Map<String, String> rowMap = new HashMap<>();

            // Generate ID if needed
            rowMap.put("projectID", String.valueOf(result.size() + 1));
            rowMap.put("visibility", "true"); // Default to visible

            // Map fields from CSV columns to Project fields
            for (int i = 0; i < csvHeaders.length && i < row.size(); i++) {
                String headerName = csvHeaders[i];
                String value = row.get(i);
                String fieldName = headerToFieldMap.get(headerName);

                if (fieldName != null && !value.isEmpty()) {
                    // Convert date formats if needed
                    if ((fieldName.equals("applicationOpeningDate") || fieldName.equals("applicationClosingDate"))
                            && !value.isEmpty()) {
                        try {
                            // Parse and format the date to ISO format (yyyy-MM-dd)
                            LocalDate date = LocalDate.parse(value, DATE_FORMATTER);
                            value = date.toString(); // Converts to ISO format
                        } catch (Exception e) {
                            System.err.println("Error parsing date: " + value);
                            // Keep original value if parsing fails
                        }
                    }
                    rowMap.put(fieldName, value);
                }
            }

            result.add(rowMap);
        }

        return result;
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            try {
                Project project = new Project(map);
                getAll().add(project);
            } catch (Exception e) {
                System.err.println("Error parsing project data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Project getByProjectName(String projectName) {
        for (Project project : getAll()) {
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null;
    }

    public List<Project> getByNeighborhood(String neighborhood) {
        List<Project> result = new ArrayList<>();
        for (Project project : getAll()) {
            if (project.getNeighborhood().equalsIgnoreCase(neighborhood)) {
                result.add(project);
            }
        }
        return result;
    }

    public List<Project> getByUserGroup(String targetedUserGroup) {
        List<Project> result = new ArrayList<>();
        for (Project project : getAll()) {
            if (project.getTargetedUserGroup().equalsIgnoreCase(targetedUserGroup)) {
                result.add(project);
            }
        }
        return result;
    }

    public List<Project> getByManager(String managerName) {
        List<Project> result = new ArrayList<>();
        for (Project project : getAll()) {
            Manager manager = project.getManagerInCharge();
            if (manager != null && manager.getName().equalsIgnoreCase(managerName)) {
                result.add(project);
            }
        }
        return result;
    }

    public List<Project> getActiveProjects() {
        List<Project> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Project project : getAll()) {
            if (project.isVisible() &&
                    project.getApplicationOpeningDate() != null &&
                    project.getApplicationClosingDate() != null &&
                    project.getApplicationOpeningDate().isBefore(today) &&
                    project.getApplicationClosingDate().isAfter(today)) {
                result.add(project);
            }
        }
        return result;
    }
}