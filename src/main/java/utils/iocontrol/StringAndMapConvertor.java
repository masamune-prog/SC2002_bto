package utils.iocontrol;

import java.util.*;
import java.util.regex.Pattern;

import static utils.iocontrol.ObjectOutputControlCharacters.DELIMITER_STRING;
import static utils.iocontrol.ObjectOutputControlCharacters.SEPARATOR_STRING;

/**
 * The StringAndMapConvertor class provides methods to convert a String representation of a map to a Map object,
 * and vice versa.
 */
public class StringAndMapConvertor {

    /**
     * Converts a String representation of a map to a Map object.
     * This method can handle multiple formats:
     * 1. Standard format: key|||value:::key|||value (as defined in ObjectOutputControlCharacters)
     * 2. CSV format: key=value|key=value (as used in Applicant files)
     *
     * @param string The String representation of the map.
     * @return A Map object containing the key-value pairs from the String representation.
     */
    public static Map<String, String> stringToMap(String string) {
        Map<String, String> map = new HashMap<>();
        
        // Determine which format the string is using
        boolean usesStandardFormat = string.contains(DELIMITER_STRING) && string.contains(SEPARATOR_STRING);
        boolean usesCSVFormat = string.contains("=") && string.contains("|");
        
        // Choose the appropriate separators based on the detected format
        String effectiveSeparator;
        String effectiveDelimiter;
        
        if (usesStandardFormat) {
            effectiveSeparator = SEPARATOR_STRING;
            effectiveDelimiter = DELIMITER_STRING;
        } else if (usesCSVFormat) {
            effectiveSeparator = "|";
            effectiveDelimiter = "=";
        } else {
            // If format cannot be determined, try to guess based on presence of delimiters
            if (string.contains("=")) {
                effectiveSeparator = "|"; // Assume pipe separator with equals delimiter
                effectiveDelimiter = "=";
            } else {
                // Default to standard format
                effectiveSeparator = SEPARATOR_STRING;
                effectiveDelimiter = DELIMITER_STRING;
            }
        }
        
        // Split the string by the appropriate separator
        String[] pairs;
        if (effectiveSeparator.equals(SEPARATOR_STRING)) {
            // For the standard format, we need to be careful with regex special characters
            pairs = string.split(Pattern.quote(effectiveSeparator));
        } else {
            // For CSV format, simple split is enough
            pairs = string.split(Pattern.quote(effectiveSeparator));
        }
        
        for (String pair : pairs) {
            // Skip empty pairs
            if (pair.trim().isEmpty()) {
                continue;
            }
            
            // Handle the pair based on the delimiter
            String key;
            String value;
            
            if (effectiveDelimiter.equals(DELIMITER_STRING)) {
                // For standard format
                int delimiterIndex = pair.indexOf(effectiveDelimiter);
                if (delimiterIndex == -1) {
                    System.err.println("Warning: Invalid key-value pair format: " + pair);
                    continue;
                }
                
                key = pair.substring(0, delimiterIndex).trim();
                value = "";
                
                if (delimiterIndex + effectiveDelimiter.length() < pair.length()) {
                    value = pair.substring(delimiterIndex + effectiveDelimiter.length()).trim();
                }
            } else {
                // For CSV format
                int delimiterIndex = pair.indexOf(effectiveDelimiter);
                if (delimiterIndex == -1) {
                    System.err.println("Warning: Invalid key-value pair format: " + pair);
                    continue;
                }
                
                key = pair.substring(0, delimiterIndex).trim();
                value = "";
                
                if (delimiterIndex + 1 < pair.length()) {
                    value = pair.substring(delimiterIndex + 1).trim();
                }
            }
            
            map.put(key, value);
        }
        
        return map;
    }

    /**
     * Converts a Map object to a String representation of the map.
     * This method maintains the original format used in the application:
     * 1. For data associated with Applicant, it uses CSV format (key=value|key=value)
     * 2. For other data (Manager, Officer), it uses standard format (key|||value:::key|||value)
     *
     * @param map The Map object to convert.
     * @param useStandardFormat Whether to use the standard format (true) or CSV format (false)
     * @return A String representation of the map.
     */
    public static String mapToString(Map<String, String> map, boolean useStandardFormat) {
        List<String> pairs = new ArrayList<>();
        
        if (useStandardFormat) {
            // Use standard format (key|||value:::key|||value)
            for (Map.Entry<String, String> entry : map.entrySet()) {
                pairs.add(entry.getKey() + DELIMITER_STRING + entry.getValue());
            }
            return String.join(SEPARATOR_STRING, pairs);
        } else {
            // Use CSV format (key=value|key=value)
            for (Map.Entry<String, String> entry : map.entrySet()) {
                pairs.add(entry.getKey() + "=" + entry.getValue());
            }
            return String.join("|", pairs);
        }
    }

    /**
     * Converts a Map object to a String representation of the map using CSV format.
     * This is a convenience method that calls mapToString(map, false).
     *
     * @param map The Map object to convert.
     * @return A String representation of the map in CSV format.
     */
    public static String mapToString(Map<String, String> map) {
        return mapToString(map, false); // Default to CSV format for backward compatibility
    }
}
