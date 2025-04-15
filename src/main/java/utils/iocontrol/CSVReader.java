package utils.iocontrol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for reading CSV files and returning their
 * contents as a list of rows, each represented as a list of
 * strings.
 */
public class CSVReader {

    /**
     * Reads a CSV file and returns its contents as a list of rows,
     * each represented as a list of strings.
     *
     * @param filePath  the path of the CSV file to be read
     * @param hasHeader a boolean indicating whether the CSV file has
     *                  a header row
     * @return a list of rows, each represented as a list of strings
     */
    public static List<List<String>> read(String filePath, boolean hasHeader) {
        List<List<String>> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            if (hasHeader) {
                // Skip the first line, assuming it's a header
                br.readLine();
            }
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }
                
                List<String> row = new ArrayList<>();
                StringBuilder currentValue = new StringBuilder();
                boolean inQuotes = false;
                
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    } else if (c == ',' && !inQuotes) {
                        // End of value
                        row.add(currentValue.toString().trim());
                        currentValue = new StringBuilder();
                    } else {
                        currentValue.append(c);
                    }
                }
                
                // Add the last value
                row.add(currentValue.toString().trim());
                
                // Only add non-empty rows
                if (!row.isEmpty() && !row.get(0).isEmpty()) {
                    list.add(row);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
}