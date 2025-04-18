package utils.iocontrol;

import utils.parameters.EmptyID; // Ensure EmptyID.EMPTY_ID is NOT ""

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static utils.iocontrol.ObjectOutputControlCharacters.LIST_ELEMENT_SEPARATOR;

/**
 * Interface for objects that can be mapped to/from a Map<String, String>.
 * Supports String, Enum, Integer, Boolean, Double, LocalDate, and List<String>.
 *
 * Key Serialization Logic:
 * - null fields are mapped to the specific EmptyID.EMPTY_ID string.
 * - Empty List<String> fields are mapped to an empty string "".
 * - Other types use their toString() representation.
 *
 * Key Deserialization Logic:
 * - Map values equal to EmptyID.EMPTY_ID result in null fields (or primitive defaults).
 * - Map values that are "" AND the field type is List result in an empty List.
 * - Other values are parsed according to the field type.
 */
public interface Mappable {

    /**
     * Converts the object's non-static, non-transient fields into a Map representation.
     * Handles nulls (using EmptyID.EMPTY_ID), primitives, String, Enum, Integer,
     * Boolean, Double, LocalDate, and List<String>.
     * Empty Lists are mapped to an empty String "".
     *
     * @return A Map<String, String> representing the object's state.
     */
    default Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        // Use getDeclaredFields to access all fields, including private
        for (Field field : getClass().getDeclaredFields()) {
            // skip static or transient fields
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            try {
                // Allow access to private fields
                field.setAccessible(true);
                Object value = field.get(this);
                String stringValue;

                if (value == null) {
                    // Use the specific placeholder for null. MUST NOT BE ""
                    stringValue = EmptyID.EMPTY_ID;
                } else if (value instanceof List<?>) {
                    // Assume List contains Strings for serialization
                    @SuppressWarnings("unchecked") // Cast is necessary based on design assumption
                    List<String> list = (List<String>) value;
                    // Join list elements. Handles empty list correctly (results in "").
                    // Also handles null elements within the list by converting them to "".
                    stringValue = String.join(LIST_ELEMENT_SEPARATOR,
                            list.stream()
                                    .map(s -> s == null ? "" : s) // handle potential nulls in list
                                    .toList());
                } else {
                    // For other types (String, Enum, Integer, Boolean, Double, LocalDate),
                    // rely on their toString() method.
                    stringValue = value.toString();
                }
                map.put(field.getName(), stringValue);

            } catch (IllegalAccessException e) {
                // Log error or throw a custom runtime exception for reflection issues
                System.err.println("FATAL: Reflection error accessing field " + field.getName() + " in toMap: " + e.getMessage());
                e.printStackTrace(); // Consider using a proper logging framework
                // Depending on requirements, might re-throw as RuntimeException
                throw new RuntimeException("Failed to map field " + field.getName() + " due to access error", e);
            } catch (ClassCastException e) {
                // Log error if List contains non-String elements - design violation
                System.err.println("ERROR: Field " + field.getName() + " is a List but contains non-String elements in toMap: " + e.getMessage());
                e.printStackTrace();
                // Decide on error handling: maybe map to empty/null representation or throw
                map.put(field.getName(), EmptyID.EMPTY_ID); // Or "" if empty list is preferred on error
            }
        }
        return map;
    }

    /**
     * Populates the object's non-static, non-transient fields from a Map representation.
     * Handles the EmptyID.EMPTY_ID placeholder (setting field to null or primitive default),
     * primitives, String, Enum, Integer, Boolean, Double, LocalDate, and List<String>.
     * An empty String "" for a List field results in an empty ArrayList.
     *
     * @param map A Map<String, String> containing the object's state. Must not be null.
     */
    default void fromMap(Map<String, String> map) {
        Objects.requireNonNull(map, "Input map cannot be null for fromMap");

        // Use getDeclaredFields to access all fields, including private
        for (Field field : getClass().getDeclaredFields()) {
            // skip static or transient fields
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            try {
                // Allow access to private fields
                field.setAccessible(true);
                String stringValue = map.get(field.getName()); // Get value from map
                Class<?> type = field.getType(); // Get the type of the field

                // --- CRITICAL CHECK ---
                // Handle ONLY actual null from map OR the specific EmptyID placeholder.
                // This check MUST NOT catch "" if "" means empty List.
                // This relies on EmptyID.EMPTY_ID NOT being equal to "".
                if (stringValue == null || stringValue.equals(EmptyID.EMPTY_ID)) {
                    // Value is missing or explicitly marked as null placeholder
                    if (type.isPrimitive()) {
                        // Provide default values for primitives
                        if      (type == int.class)     field.setInt(this, 0);
                        else if (type == boolean.class) field.setBoolean(this, false);
                        else if (type == double.class)  field.setDouble(this, 0.0);
                        else if (type == long.class)    field.setLong(this, 0L);
                        else if (type == float.class)   field.setFloat(this, 0.0f);
                        else if (type == short.class)   field.setShort(this, (short) 0);
                        else if (type == byte.class)    field.setByte(this, (byte) 0);
                        else if (type == char.class)    field.setChar(this, '\u0000');
                    } else {
                        // Set non-primitive fields (Objects, including List) to null
                        field.set(this, null);
                    }
                    continue; // Move to the next field
                }
                // --- END OF CRITICAL CHECK ---

                // --- Type-specific processing ---
                // stringValue is now guaranteed NOT null and NOT the EmptyID placeholder.
                // It might be "", "value1;value2", "true", "123", etc.

                if (List.class.isAssignableFrom(type)) {
                    // Handle List<String> specifically.
                    // Split the string into items. Handles empty string "" correctly.
                    // The -1 limit ensures trailing empty strings are kept if separator is at the end.
                    String[] items = stringValue.isEmpty()
                            ? new String[0] // If stringValue is "", create an empty array
                            : stringValue.split(LIST_ELEMENT_SEPARATOR, -1);
                    // Create a mutable ArrayList from the items
                    field.set(this, new ArrayList<>(Arrays.asList(items)));
                } else if (type.isEnum()) {
                    // Parse Enum constant
                    // Using rawtypes and unchecked suppresses warnings for generic Enum.valueOf
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Enum<?> enumVal = Enum.valueOf((Class<Enum>) type, stringValue);
                    field.set(this, enumVal);
                } else if (type == Integer.class || type == int.class) {
                    // Parse Integer
                    field.set(this, Integer.parseInt(stringValue));
                } else if (type == Boolean.class || type == boolean.class) {
                    // Parse Boolean ("true" -> true, anything else -> false)
                    field.set(this, Boolean.parseBoolean(stringValue));
                } else if (type == Double.class || type == double.class) {
                    // Parse Double
                    field.set(this, Double.parseDouble(stringValue));
                } else if (type == LocalDate.class) {
                    // Parse LocalDate using standard ISO format (e.g., "2023-10-27")
                    field.set(this, LocalDate.parse(stringValue));
                } else if (type == String.class) {
                    // Set String value directly
                    field.set(this, stringValue);
                } else {
                    // --- Fallback / Unsupported Type Handling ---
                    // This block is reached for types not explicitly handled above.
                    // Log a warning as this indicates a potentially unsupported field type.
                    System.err.println("WARNING: Unsupported field type '" + type.getName() +
                            "' for field '" + field.getName() +
                            "' during fromMap. Value: '" + stringValue + "'. Field will be left unchanged or potentially null if initialization failed.");
                    // Option 1: Do nothing (field keeps its default value, often null)
                    // Option 2: Try to set as String if possible (very risky, likely to fail)
                    /*
                     try {
                         if (type.isAssignableFrom(String.class)) { field.set(this, stringValue); }
                     } catch (IllegalArgumentException ignored) {}
                    */
                    // Option 3: Throw an exception to indicate programmer error
                    throw new IllegalArgumentException("Unsupported field type in fromMap: " + type.getName() + " for field " + field.getName());
                }

            } catch (IllegalAccessException e) {
                // Error setting field value (should not happen with setAccessible(true))
                System.err.println("FATAL: Reflection error setting field " + field.getName() + " in fromMap: " + e.getMessage());
                e.printStackTrace(); // Use logger
                throw new RuntimeException("Failed to map field " + field.getName() + " due to access error", e);
            } catch (DateTimeParseException e) {
                // Specific parsing error for LocalDate
                System.err.println("ERROR: Failed to parse LocalDate for field '" + field.getName() +
                        "' from value '" + map.get(field.getName()) + "'. Check format (YYYY-MM-DD). " + e.getMessage());
                e.printStackTrace(); // Use logger
                // Handle error: Maybe set to null or throw? Setting to null for non-primitives.
                if (!field.getType().isPrimitive()) {
                    try { field.set(this, null); } catch (IllegalAccessException ignored) {}
                }
            } catch (NumberFormatException e) {
                // Specific parsing error for Integer/Double
                System.err.println("ERROR: Failed to parse number for field '" + field.getName() +
                        "' from value '" + map.get(field.getName()) + "'. " + e.getMessage());
                e.printStackTrace(); // Use logger
                if (!field.getType().isPrimitive()) {
                    try { field.set(this, null); } catch (IllegalAccessException ignored) {}
                }
            } catch (IllegalArgumentException e) {
                // Catch-all for other parsing/setting issues (e.g., invalid Enum constant, fallback type issue)
                System.err.println("ERROR: Failed to process field '" + field.getName() +
                        "' with value '" + map.get(field.getName()) + "' in fromMap: " + e.getMessage());
                e.printStackTrace(); // Use logger
                // Handle error: Maybe set to null or re-throw? Setting to null for non-primitives.
                if (!field.getType().isPrimitive()) {
                    try { field.set(this, null); } catch (IllegalAccessException ignored) {}
                }
            } catch (Exception e) {
                // Catch any other unexpected exceptions during field processing
                System.err.println("FATAL: Unexpected error processing field " + field.getName() + " in fromMap: " + e.getMessage());
                e.printStackTrace(); // Use logger
                throw new RuntimeException("Unexpected error mapping field " + field.getName(), e); // Fail fast
            }
        }
    }
}