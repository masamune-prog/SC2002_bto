package utils.iocontrol;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An abstract class for managing objects that can be mapped to and from key-value pairs.
 *
 * @param <MappableObject> a class that can be mapped to and from key-value pairs
 */
public abstract class Savable<MappableObject extends Mappable> {

    /**
     * Gets the list of mappable objects.
     *
     * @return the list of mappable objects
     */
    protected abstract List<MappableObject> getAll();

    /**
     * Sets the list of mappable objects.
     *
     * @param listOfMappableObjects the list of mappable objects to set
     */
    protected abstract void setAll(List<Map<String, String>> listOfMappableObjects);

    /**
     * Saves the list of mappable objects to a file.
     *
     * @param FILE_PATH the path of the file to save to
     * @throws RuntimeException if the data could not be saved to the file
     */
    protected void save(final String FILE_PATH) {
        // Create a new .txt file path by replacing .csv with .txt
        String txtFilePath = FILE_PATH.replace(".csv", ".txt");
        File txtFile = new File(txtFilePath);
        
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(txtFile))) {
            final List<MappableObject> listOfMappableObjects = getAll();
            for (MappableObject mappableObject : listOfMappableObjects) {
                printWriter.println(StringAndMapConvertor.mapToString(mappableObject.toMap()));
            }
            printWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Data could not be saved to file: " + txtFilePath + ". Error: " + e.getMessage());
        }
    }

    /**
     * Loads the list of mappable objects from a file.
     *
     * @param FILE_PATH the path of the file to load from
     * @throws RuntimeException if the data could not be loaded from the file
     */
    protected void load(final String FILE_PATH) {
        // Try to load from .txt file first, fall back to .csv if .txt doesn't exist
        String txtFilePath = FILE_PATH.replace(".csv", ".txt");
        File txtFile = new File(txtFilePath);
        File csvFile = new File(FILE_PATH);
        
        List<Map<String, String>> listOfMappableObjects = new ArrayList<>();
        BufferedReader bufferedReader;
        
        try {
            if (txtFile.exists()) {
                bufferedReader = new BufferedReader(new FileReader(txtFile));
            } else if (csvFile.exists()) {
                // If .txt doesn't exist but .csv does, use .csv
                bufferedReader = new BufferedReader(new FileReader(csvFile));
            } else {
                // If neither exists, create the directory and .txt file
                File parent = txtFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                txtFile.createNewFile();
                bufferedReader = new BufferedReader(new FileReader(txtFile));
            }
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                listOfMappableObjects.add(StringAndMapConvertor.stringToMap(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Data could not be loaded from file: " + txtFilePath + ". Error: " + e.getMessage());
        }
        
        setAll(listOfMappableObjects);
    }
}