package model.user;

public enum MaritalStatus {
    SINGLE, MARRIED, DIVORCED, WIDOWED;

    public static MaritalStatus fromString(String value) {
        if (value == null || value.isEmpty()) {
            return SINGLE;
        }

        // Direct string matching
        if (value.equalsIgnoreCase("Single")) return SINGLE;
        if (value.equalsIgnoreCase("Married")) return MARRIED;
        if (value.equalsIgnoreCase("Divorced")) return DIVORCED;
        if (value.equalsIgnoreCase("Widowed")) return WIDOWED;

        // Try standard conversion as fallback
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SINGLE; // Default
        }
    }
}