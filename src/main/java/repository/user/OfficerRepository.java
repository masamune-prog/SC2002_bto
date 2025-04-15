package repository.user;

import model.user.Officer;
import repository.Repository;
import utils.config.Location;
import utils.iocontrol.CSVReader;
import controller.account.password.PasswordHashManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class OfficerRepository extends Repository<Officer> {

    private static final String FILE_PATH = "\\OfficerList.csv";
    private static OfficerRepository instance;

    protected OfficerRepository() {
        super();
        load();
    }

    public static OfficerRepository getInstance() {
        if (instance == null) {
            instance = new OfficerRepository();
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
        String txtFilePath = getFilePath().replace(".csv", ".txt");
        File txtFile = new File(txtFilePath);

        try {
            if (txtFile.exists()) {
                // Load from .txt file
                load(txtFilePath);
                System.out.println("Loaded officer data from: " + txtFilePath);
            } else {
                // If .txt doesn't exist, load from CSV
                System.out.println("No .txt file found, loading from CSV: " + getFilePath());
                List<List<String>> csvData = CSVReader.read(getFilePath(), true);
                if (csvData == null || csvData.isEmpty()) {
                    System.err.println("Warning: No data found in CSV file");
                    return;
                }

                // Debug output for CSV data
                System.out.println("CSV Data read successfully. Number of rows: " + csvData.size());
                for (List<String> row : csvData) {
                    System.out.println("CSV Row: " + String.join(", ", row));
                }

                List<Map<String, String>> mappedData = convertToMapList(csvData);
                System.out.println("Number of mapped officers: " + mappedData.size());
                setAll(mappedData);
                // Save to .txt file for future use
                save(txtFilePath);
                System.out.println("Created new .txt file: " + txtFilePath);
            }

            // Verify loaded data
            if (getAll().isEmpty()) {
                System.err.println("Warning: No officers were loaded");
            } else {
                System.out.println("Successfully loaded " + getAll().size() + " officers");
                // Print details of loaded officers
                for (Officer officer : getAll()) {
                    System.out.println("Loaded Officer - ID: " + officer.getID() +
                            ", Name: " + officer.getName() +
                            ", NRIC: " + officer.getNric());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading officers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Officer getOfficerByName(String name) {
        for (Officer officer : getAll()) {
            if (officer.getName() != null && officer.getName().equals(name)) {
                return officer;
            }
        }
        return null;
    }
    public Officer getByNRIC(String nric) {
        for (Officer officer : getAll()) {
            if (officer.getNric() != null && officer.getNric().equals(nric)) {
                return officer;
            }
        }
        return null;
    }
    private List<Map<String, String>> convertToMapList(List<List<String>> csvData) {
        List<Map<String, String>> result = new ArrayList<>();

        // Process each row (skip header row)
        for (int i = 0; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            if (row.size() < 5) {  // Check if row has all required fields
                System.err.println("Warning: Skipping invalid row - insufficient columns");
                continue;
            }

            Map<String, String> rowMap = new HashMap<>();

            // Set ID based on row number (starting from 1)
            rowMap.put("officerID", String.valueOf(i + 1));

            // Map the CSV columns to the expected fields
            // CSV format: Name,NRIC,Age,Marital Status,Password
            String name = row.get(0).trim();
            String nric = row.get(1).trim();
            String password = row.get(4).trim();

            // Debug output for raw data
            System.out.println("Raw CSV data - Name: '" + name + "', NRIC: '" + nric + "', Password: '" + password + "'");

            rowMap.put("name", name);
            rowMap.put("nric", nric);

            // Hash the password
            if (password != null && !password.isEmpty()) {
                String hashedPassword = PasswordHashManager.hashPassword(password);
                rowMap.put("hashedPassword", hashedPassword);
            }

            // Initialize projectsInCharge as empty list
            rowMap.put("projectsInCharge", "[]");

            result.add(rowMap);

            // Debug output for mapped data
            System.out.println("Mapped data - Name: '" + rowMap.get("name") +
                    "', NRIC: '" + rowMap.get("nric") +
                    "', ID: " + rowMap.get("officerID"));
        }

        return result;
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        getAll().clear(); // Clear existing data
        for (Map<String, String> map : listOfMappableObjects) {
            try {
                Officer officer = new Officer(map);
                getAll().add(officer);
                System.out.println("Added officer: " + officer.getName() +
                        " (ID: " + officer.getID() +
                        ", NRIC: " + officer.getNric() + ")");
            } catch (Exception e) {
                System.err.println("Error parsing officer data: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Total officers loaded: " + getAll().size());
    }
}