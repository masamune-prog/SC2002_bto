package utils.iocontrol;

import java.util.*;

import static utils.iocontrol.ObjectOutputControlCharacters.*;

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
        if (string == null || string.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> map = new HashMap<>();
        String[] pairs = string.split(SEPARATOR_STRING);
        for (String pair : pairs) {
            // Handle empty pairs
            if (pair.isEmpty()) {
                continue;
            }

            // Split by the first occurrence of DELIMITER_STRING
            int delimiterIndex = pair.indexOf(DELIMITER_STRING);
            if (delimiterIndex == -1) {
                throw new IllegalArgumentException("Invalid key-value pair: " + pair + " (missing delimiter)");
            }

            String key = pair.substring(0, delimiterIndex);
            String value = pair.substring(delimiterIndex + DELIMITER_STRING.length());

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
        if (map == null || map.isEmpty()) {
            return "";
        }

        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            pairs.add(entry.getKey() + DELIMITER_STRING + entry.getValue());
        }
        return String.join(SEPARATOR_STRING, pairs);
    }
}