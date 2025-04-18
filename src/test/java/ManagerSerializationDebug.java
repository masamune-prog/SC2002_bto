import model.user.Manager;
import utils.iocontrol.StringAndMapConvertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ManagerSerializationDebug {
    public static void main(String[] args) {
        // Create a test manager
        Manager manager = new Manager(
                "M1234567A",
                "password",
                "Test Manager",
                new ArrayList<>(Arrays.asList("P101", "P102"))
        );
        
        // Convert to map
        Map<String, String> map = manager.toMap();
        System.out.println("Map from Manager:");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        
        // Convert map to string
        String stringRep = StringAndMapConvertor.mapToString(map);
        System.out.println("\nString representation:");
        System.out.println(stringRep);
        
        // Try to convert string back to map
        try {
            Map<String, String> parsedMap = StringAndMapConvertor.stringToMap(stringRep);
            System.out.println("\nMap parsed from string:");
            for (Map.Entry<String, String> entry : parsedMap.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        } catch (Exception e) {
            System.out.println("\nError parsing string:");
            e.printStackTrace();
        }
    }
}