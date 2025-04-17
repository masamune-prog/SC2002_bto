package repository.project;

import model.project.Project;
import model.user.Manager;
import repository.Repository;
import utils.config.Location;
import utils.iocontrol.CSVReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.io.File;

public class ProjectRepository extends Repository<Project> {

    private static final String FILE_PATH = "\\ProjectList.csv";
    private static final String TXT_FILE_PATH = "\\ProjectList.txt";
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
    
    public String getTxtFilePath() {
        return Location.RESOURCE_LOCATION + TXT_FILE_PATH;
    }

    @Override
    public void load() {
        this.getAll().clear();
        String txtFilePath = getTxtFilePath();
        File txtFile = new File(txtFilePath);
        
        if (txtFile.exists()) {
            // Load from .txt file
            loadFromTxt();
            System.out.println("Loaded project data from: " + txtFilePath);
        } else {
            // If .txt doesn't exist, load from CSV
            System.out.println("No .txt file found, loading from CSV: " + getFilePath());
            loadFromCSV();
            // Save to .txt file for future use
            saveTxtFormat();
            System.out.println("Created new .txt file: " + txtFilePath);
        }
    }
    
    /**
     * Loads projects from CSV file
     */
    private void loadFromCSV() {
        try {
            List<List<String>> csvData = CSVReader.read(getFilePath(), true);
            List<Map<String, String>> mappedData = convertToMapList(csvData);
            setAll(mappedData);
            System.out.println("Loaded " + getAll().size() + " projects from CSV file: " + getFilePath());
        } catch (Exception e) {
            System.err.println("Error loading projects from CSV: " + e.getMessage());
        }
    }
    
    /**
     * Loads projects from TXT file
     */
    private void loadFromTxt() {
        try {
            String txtPath = getTxtFilePath();
            File file = new File(txtPath);
            if (!file.exists()) {
                System.out.println("TXT file not found: " + txtPath);
                return;
            }
            
            // Track existing project IDs to avoid duplicates
            List<String> existingProjectIDs = getAll().stream()
                    .map(Project::getID)
                    .collect(Collectors.toList());
            
            int initialCount = getAll().size();
            int addedCount = 0;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(txtPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Parse the line into a map
                    Map<String, String> projectMap = new HashMap<>();
                    String[] pairs = line.split("\\|");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=", 2); // Split only on the first '=' 
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            // Unescape special characters
                            value = value.replace("\\|", "|").replace("\\=", "=");
                            projectMap.put(key, value);
                        }
                    }
                    
                    // Check if this project is already loaded from CSV
                    String projectID = projectMap.get("projectID");
                    if (projectID != null && !existingProjectIDs.contains(projectID)) {
                        try {
                            Project project = new Project(projectMap);
                            getAll().add(project);
                            existingProjectIDs.add(projectID);
                            addedCount++;
                        } catch (Exception e) {
                            System.err.println("Error creating project from TXT data: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            System.out.println("Loaded " + addedCount + " additional projects from TXT file: " + txtPath);
            
            // If no projects were loaded from TXT file, try loading from CSV
            if (getAll().isEmpty()) {
                System.out.println("No projects loaded from TXT file, attempting to load from CSV...");
                loadFromCSV();
            }
        } catch (IOException e) {
            System.err.println("Error loading projects from TXT: " + e.getMessage());
            e.printStackTrace();
            
            // If there's an error reading TXT file, fall back to CSV
            System.out.println("Falling back to CSV due to TXT file reading error");
            loadFromCSV();
        }
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

    @Override
    public void save() {
        try {
            // Create a FileWriter for the CSV file
            try (FileWriter writer = new FileWriter(getFilePath())) {
                // Write the header line
                writer.write("Project Name,Neighbourhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer\n");
                
                // Write each project as a CSV line
                for (Project project : getAll()) {
                    StringBuilder line = new StringBuilder();
                    
                    // Project Name
                    line.append(csvEscape(project.getProjectName())).append(",");
                    
                    // Neighborhood
                    line.append(csvEscape(project.getNeighborhood())).append(",");
                    
                    // Type 1 (2-room flats)
                    line.append("2-room").append(",");
                    
                    // Number of units for Type 1
                    line.append(project.getTwoRoomFlatsAvailable()).append(",");
                    
                    // Selling price for Type 1
                    line.append(project.getTwoRoomFlatsPrice()).append(",");
                    
                    // Type 2 (3-room flats)
                    line.append("3-room").append(",");
                    
                    // Number of units for Type 2
                    line.append(project.getThreeRoomFlatsAvailable()).append(",");
                    
                    // Selling price for Type 2
                    line.append(project.getThreeRoomFlatsPrice()).append(",");
                    
                    // Application opening date
                    line.append(formatDate(project.getApplicationOpeningDate())).append(",");
                    
                    // Application closing date
                    line.append(formatDate(project.getApplicationClosingDate())).append(",");
                    
                    // Manager
                    if (project.getManagerInCharge() != null) {
                        line.append(csvEscape(project.getManagerInCharge().getName()));
                    }
                    line.append(",");
                    
                    // Officer Slot
                    line.append(project.getNumOfficers()).append(",");
                    
                    // Officers - join multiple officer names with semicolons
                    if (!project.getAssignedOfficers().isEmpty()) {
                        List<String> officerNames = project.getAssignedOfficers().stream()
                                .map(officer -> officer.getName())
                                .collect(Collectors.toList());
                        line.append(csvEscape(String.join(";", officerNames)));
                    }
                    
                    // End the line
                    line.append("\n");
                    writer.write(line.toString());
                }
                
                // Save also to ProjectList.txt for better compatibility with other parts of the system
                saveTxtFormat();
                
                System.out.println("Saved " + getAll().size() + " projects to " + getFilePath());
            }
        } catch (IOException e) {
            System.err.println("Error saving projects to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save projects in .txt format as well (for better compatibility with other parts of the system)
     */
    private void saveTxtFormat() {
        try {
            String txtPath = getFilePath().replace(".csv", ".txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtPath))) {
                for (Project project : getAll()) {
                    // Create a map of the project's properties
                    Map<String, String> map = project.toMap();
                    
                    // Format as key=value|key=value
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getValue() != null) {
                            if (sb.length() > 0) {
                                sb.append("|");
                            }
                            sb.append(entry.getKey()).append("=").append(escapeSpecialChars(entry.getValue()));
                        }
                    }
                    writer.write(sb.toString());
                    writer.newLine();
                }
                System.out.println("Saved " + getAll().size() + " projects to " + txtPath);
            }
        } catch (IOException e) {
            System.err.println("Error saving projects to TXT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Explicitly save projects to a text file.
     * This method can be called directly when you need to save only to TXT format.
     * 
     * @param filePath Optional custom file path. If null, uses default path.
     * @return Number of projects saved
     */
    public int saveProjectsToTxt(String filePath) {
        String txtPath = filePath;
        if (txtPath == null) {
            txtPath = getFilePath().replace(".csv", ".txt");
        }
        
        try {
            // Ensure directory exists
            File file = new File(txtPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("Created directory: " + parentDir.getAbsolutePath());
            }
            
            System.out.println("Writing " + getAll().size() + " projects to: " + txtPath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtPath))) {
                for (Project project : getAll()) {
                    // Create a map of the project's properties
                    Map<String, String> map = project.toMap();
                    
                    // Debug: print the map content
                    System.out.println("Project " + project.getID() + " data: " + map.size() + " fields");
                    
                    // Format as key=value|key=value
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getValue() != null) {
                            if (sb.length() > 0) {
                                sb.append("|");
                            }
                            sb.append(entry.getKey()).append("=").append(escapeSpecialChars(entry.getValue()));
                        }
                    }
                    writer.write(sb.toString());
                    writer.newLine();
                }
                
                System.out.println("Successfully saved " + getAll().size() + " projects to TXT file: " + txtPath);
                return getAll().size();
            }
        } catch (IOException e) {
            System.err.println("Error saving projects to TXT: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Formats a date for CSV output
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Escapes a string for CSV output
     */
    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        // If the value contains a comma, quotes, or newline, wrap it in quotes and escape any quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Escapes special characters for TXT format
     */
    private String escapeSpecialChars(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "\\|").replace("=", "\\=");
    }

    public Project getByProjectName(String projectName) {
        for (Project project : getAll()) {
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null;
    }
    //get by ID
    public Project getByProjectID(String projectID) {
        for (Project project : getAll()) {
            if (project.getID().equalsIgnoreCase(projectID)) {
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
    public String getNewProjectID() {
        //get the largest projectID in the list and add 1
        int maxID = 0;
        for (Project project : getAll()) {
            int id = Integer.parseInt(project.getID());
            if (id > maxID) {
                maxID = id;
            }
        }
        return String.valueOf(maxID + 1);
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

    /**
     * Finds projects matching the specified rules (predicates).
     * 
     * @param rules predicates to filter projects
     * @return list of projects matching all rules
     */
    @SafeVarargs
    public final List<Project> findByRules(Predicate<Project>... rules) {
        List<Project> result = new ArrayList<>(getAll());
        
        for (Predicate<Project> rule : rules) {
            result = result.stream().filter(rule).collect(Collectors.toList());
        }
        
        return result;
    }
}