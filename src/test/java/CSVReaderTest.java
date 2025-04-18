import utils.config.Location;
import utils.iocontrol.CSVReader;

import java.util.List;

/**
 * This class tests the {@link CSVReader} class for Applicants, Managers, and Officers.
 */
public class CSVReaderTest {
    /**
     * This method tests reading CSV data for different user types.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        testCSV("Applicants", Location.RESOURCE_LOCATION + "/ApplicantList.csv");
        testCSV("Managers", Location.RESOURCE_LOCATION + "/ManagerList.csv");
        testCSV("Officers", Location.RESOURCE_LOCATION + "/OfficerList.csv");
        testCSV("Projects", Location.RESOURCE_LOCATION + "/ProjectList.csv");
    }

    /**
     * Reads and prints the contents of a CSV file.
     *
     * @param label  A label describing the type of user (e.g., Applicant).
     * @param path   The full path to the CSV file.
     */
    private static void testCSV(String label, String path) {
        System.out.println("\n--- Reading " + label + " CSV ---\n");
        List<List<String>> list = CSVReader.read(path, true);
        for (List<String> row : list) {
            System.out.println("Row size: " + row.size());
            for (String value : row) {
                System.out.print(value + "\t | \t");
            }
            System.out.println();
        }
    }
}
