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

public class OfficerRepository extends Repository<Officer> {

    private static final String FILE_PATH = "\\OfficerList.csv";
    private static OfficerRepository instance;

    protected OfficerRepository() {
        super();
    }

    public static OfficerRepository getInstance() {
        if (instance == null) {
            instance = new OfficerRepository();
            instance.load();
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

        // Define column headers based on CSV structure
        // Keep these capitalized to match exactly what's in the CSV
        String[] headers = {
                "Name", "NRIC", "Age", "Marital Status", "Password"
        };

        // Convert each row to a map and assign ID based on order
        int id = 1;
        for (List<String> row : csvData) {
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("officerID", String.valueOf(id++)); // Set ID based on order

            for (int i = 0; i < headers.length && i < row.size(); i++) {
                rowMap.put(headers[i], row.get(i));

                // If we're at the password column, hash it
                if (headers[i].equals("Password")) {
                    String password = row.get(i);
                    if (password != null && !password.isEmpty()) {
                        String hashedPassword = PasswordHashManager.hashPassword(password);
                        rowMap.put("hashedPassword", hashedPassword);
                    }
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
                Officer officer = new Officer(map);
                // Debug output to see what's happening
                //System.out.println("Created officer: ID=" + officer.getID() + ", Name=" + officer.getName() + ", NRIC=" + officer.getNric());
                getAll().add(officer);
            } catch (Exception e) {
                System.err.println("Error parsing officer data: " + e.getMessage());
                e.printStackTrace(); // Print stack trace for better debugging
            }
        }
    }
}