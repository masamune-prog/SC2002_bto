package repository.user;

import model.user.Applicant;
import repository.Repository;
import utils.config.Location;
import utils.iocontrol.CSVReader;
import controller.account.password.PasswordHashManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (applicant.getNric().equals(nric)) {
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
        List<List<String>> csvData = CSVReader.read(getFilePath(), true);
        List<Map<String, String>> mappedData = convertToMapList(csvData);
        setAll(mappedData);
    }
    public Applicant getApplicantByName(String name) {
        for (Applicant applicant : getAll()) {
            if (applicant.getName().equals(name)) {
                return applicant;
            }
        }
        return null;
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
}