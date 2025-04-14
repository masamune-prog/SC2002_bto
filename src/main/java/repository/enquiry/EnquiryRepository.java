package repository.enquiry;

import model.enquiry.Enquiry;
import repository.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryRepository extends Repository<Enquiry> {
    private static final String FILE_PATH = "src/main/resources/EnquiryList.txt";
    private static EnquiryRepository instance;

    public EnquiryRepository() {
        super();
        load();
    }

    public static EnquiryRepository getInstance() {
        if (instance == null) {
            instance = new EnquiryRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return FILE_PATH;
    }

    @Override
    public void load() {
        this.getAll().clear();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (Exception e) {
                System.err.println("Error creating enquiry file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Map<String, String> map = parseLine(line);
                        getAll().add(new Enquiry(map));
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line);
                        System.err.println("Error details: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        String[] parts = line.split(",");

        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Handle the problematic [answer] format
                if (key.equals("answer") && value.equals("[answer]")) {
                    value = "";
                }

                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Enquiry enquiry : getAll()) {
                writer.write(formatEnquiry(enquiry));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving enquiries: " + e.getMessage());
        }
    }

    private String formatEnquiry(Enquiry enquiry) {
        return "enquiryID=" + enquiry.getEnquiryID() + "," +
                "question=" + escapeSpecialChars(enquiry.getQuestion()) + "," +
                "answer=" + escapeSpecialChars(enquiry.getAnswer()) + "," +
                "creatorID=" + enquiry.getCreatorID();
    }

    private String escapeSpecialChars(String value) {
        if (value == null) return "";
        return value.replace(",", "\\,").replace("=", "\\=");
    }

    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            try {
                getAll().add(new Enquiry(map));
            } catch (Exception e) {
                System.err.println("Error parsing enquiry data: " + e.getMessage());
            }
        }
    }

    public Enquiry getByID(String enquiryID) {
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getEnquiryID().equals(enquiryID)) {
                return enquiry;
            }
        }
        return null;
    }

    public List<Enquiry> getUnansweredEnquiries() {
        List<Enquiry> results = new ArrayList<>();
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getAnswer() == null || enquiry.getAnswer().isEmpty()) {
                results.add(enquiry);
            }
        }
        return results;
    }

    public Enquiry createEnquiry(String question, String creatorID) {
        String newID = "E" + (getAll().size() + 1);
        Enquiry enquiry = new Enquiry(newID, question, "", creatorID);
        getAll().add(enquiry);
        save();
        return enquiry;
    }

    public void answerEnquiry(String enquiryID, String answer) {
        Enquiry enquiry = getByID(enquiryID);
        if (enquiry != null) {
            enquiry.setAnswer(answer);
            save();
        }
    }

    public void deleteEnquiry(String enquiryID) {
        Enquiry enquiry = getByID(enquiryID);
        if (enquiry != null) {
            getAll().remove(enquiry);
            save();
        }
    }

    public List<Enquiry> searchByKeyword(String keyword) {
        List<Enquiry> results = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }

        String lowerKeyword = keyword.toLowerCase();
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getQuestion().toLowerCase().contains(lowerKeyword)) {
                results.add(enquiry);
            }
        }
        return results;
    }

    public List<Enquiry> getEnquiriesByCreator(String creatorID) {
        List<Enquiry> results = new ArrayList<>();
        for (Enquiry enquiry : getAll()) {
            if (enquiry.getCreatorID().equals(creatorID)) {
                results.add(enquiry);
            }
        }
        return results;
    }
}