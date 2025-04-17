package repository.user;

import model.user.Manager;
import repository.Repository;
import utils.config.Location;
import utils.iocontrol.CSVReader;
import controller.account.password.PasswordHashManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class ManagerRepository extends Repository<Manager> {

    private static final String FILE_PATH = "\\ManagerList.csv";

    ManagerRepository() {
        super();
        load();
    }

    public static ManagerRepository getInstance() {
        return new ManagerRepository();
    }

    @Override
    public String getFilePath() {
        return Location.RESOURCE_LOCATION + FILE_PATH;
    }
    public Manager getByName(String name) {
        for (Manager manager : getAll()) {
            if (manager.getName().equals(name)) {
                return manager;
            }
        }
        return null;
    }
    public Manager getByNRIC(String nric) {
        for (Manager manager : getAll()) {
            if (manager.getNRIC().equals(nric)) {
                return manager;
            }
        }
        return null;
    }
    @Override
    public void load() {
        this.getAll().clear();
        String txtFilePath = getFilePath().replace(".csv", ".txt");
        File txtFile = new File(txtFilePath);
        
        if (txtFile.exists()) {
            // Load from .txt file
            load(txtFilePath);
            System.out.println("Loaded manager data from: " + txtFilePath);
        } else {
            // If .txt doesn't exist, load from CSV
            System.out.println("No .txt file found, loading from CSV: " + getFilePath());
            List<List<String>> csvData = CSVReader.read(getFilePath(), true);
            List<Map<String, String>> mappedData = convertToMapList(csvData);
            setAll(mappedData);
            // Save to .txt file for future use
            save(txtFilePath);
            System.out.println("Created new .txt file: " + txtFilePath);
        }
    }

    private List<Map<String, String>> convertToMapList(List<List<String>> csvData) {
        List<Map<String, String>> result = new ArrayList<>();

        // Define column headers based on CSV structure
        String[] headers = {
                "name", "nric", "age", "maritalStatus", "password"
        };

        // Convert each row to a map and assign ID based on order
        int id = 1;
        for (List<String> row : csvData) {
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("managerID", String.valueOf(id++)); // Set ID based on order

            for (int i = 0; i < headers.length && i < row.size(); i++) {
                rowMap.put(headers[i], row.get(i));

                // If we're at the password column, hash it
                if (headers[i].equals("password")) {
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
                getAll().add(new Manager(map));
            } catch (Exception e) {
                System.err.println("Error parsing manager data: " + e.getMessage());
            }
        }
    }
}