package repository.user;

import model.user.Applicant;
import model.user.ApplicantStatus;
import repository.Repository;
import utils.config.Location;
import utils.iocontrol.CSVReader;
import controller.account.password.PasswordHashManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class ApplicantRepository extends Repository<Applicant> {

    private static final String FILE_PATH = "\\ApplicantList.csv";

    ApplicantRepository() {
        super();
        load();
    }
    public Applicant getByID(String id) {
        for (Applicant applicant : getAll()) {
            if (applicant.getID().equals(id)) {
                return applicant;
            }
        }
        return null;
    }
    public Applicant getByNRIC(String nric) {
        for (Applicant applicant : getAll()) {
            if (applicant.getNRIC().equals(nric)) {
                return applicant;
            }
        }
        return null;
    }
//    public Applicant getByName(String name) {
//        for (Applicant applicant : getAll()) {
//            if (applicant.getName().equals(name)) {
//                return applicant;
//            }
//        }
//        return null;
//    }
    public static ApplicantRepository getInstance() {
        return new ApplicantRepository();
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
        
        if (txtFile.exists()) {
            // Load from .txt file
            load(txtFilePath);
            // Ensure all applicants have UNREGISTERED status
            for (Applicant applicant : getAll()) {
                if (applicant.getStatus() == null) {
                    applicant.setStatus(ApplicantStatus.UNREGISTERED);
                }
            }
            System.out.println("Loaded applicant data from: " + txtFilePath);
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
    public Applicant getApplicantByName(String name) {
        for (Applicant applicant : getAll()) {
            if (applicant.getName().equals(name)) {
                return applicant;
            }
        }
        return null;
    }
    // Add this method to update and persist applicant changes
    public void update(Applicant applicant) {
        // Find and update the applicant in the list
        List<Applicant> all = getAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getID().equals(applicant.getID())) {
                all.set(i, applicant);
                break;
            }
        }
        // Save the updated list to the .txt file
        String txtFilePath = getFilePath().replace(".csv", ".txt");
        save(txtFilePath);
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
            rowMap.put("applicantID", String.valueOf(id++)); // Set ID based on order

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
            
            // Set status as UNREGISTERED for all applicants
            rowMap.put("status", "UNREGISTERED");
            
            result.add(rowMap);
        }

        return result;
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            try {
                getAll().add(new Applicant(map));
            } catch (Exception e) {
                System.err.println("Error parsing applicant data: " + e.getMessage());
            }
        }
    }

    @Override
    public void save() {
        String txtFilePath = getFilePath().replace(".csv", ".txt");
        System.out.println("Attempting to save applicants to: " + txtFilePath);
        try {
            super.save(txtFilePath);
            System.out.println("Successfully saved applicants to: " + txtFilePath);
        } catch (Exception e) {
            System.err.println("Error saving applicants to: " + txtFilePath);
            e.printStackTrace();
        }
    }
}