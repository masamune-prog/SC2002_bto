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
     *
     * @param string The String representation of the map.
     * @return A Map object containing the key-value pairs from the String representation.
     * @throws IllegalArgumentException if the input string contains invalid key-value pairs.
     */
    public static Map<String, String> stringToMap(String string) {
        Map<String, String> map = new HashMap<>();
        
        // Debug output
        //System.out.println("Parsing string: " + string);
        
        // Split by separator first, escaping the separator for regex
        String[] pairs = string.split(Pattern.quote(SEPARATOR_STRING));
        //System.out.println("Split into pairs: " + Arrays.toString(pairs));
        
        for (String pair : pairs) {
            // Skip empty pairs
            if (pair.trim().isEmpty()) {
                continue;
            }
            
            // Split by delimiter, escaping the delimiter for regex
            String[] keyValue = pair.split(Pattern.quote(DELIMITER_STRING));
           // System.out.println("Split pair into key-value: " + Arrays.toString(keyValue));
            
            if (keyValue.length != 2) {
                System.err.println("Warning: Invalid key-value pair format: " + pair);
                continue; // Skip invalid pairs instead of throwing exception
            }
            
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            
            // Debug output
            //System.out.println("Adding to map - Key: '" + key + "', Value: '" + value + "'");
            
            map.put(key, value);
        }
        
        return map;
    }

    /**
     * Converts a Map object to a String representation of the map.
     *
     * @param map The Map object to convert.
     * @return A String representation of the map.
     */
    public static String mapToString(Map<String, String> map) {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            pairs.add(entry.getKey() + DELIMITER_STRING + entry.getValue());
        }
        return String.join(SEPARATOR_STRING, pairs);
    }
}
