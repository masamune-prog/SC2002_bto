package utils.config;

import java.nio.file.Paths;

/**
 * This class determines the location of the resources dynamically.
 * Note: Loading resources via the classpath is generally preferred over this approach.
 */
public class Location {
    /**
     * Dynamically determines the location of the resources based on the project's
     * current working directory. Assumes the standard Maven project structure.
     * This approach might be less reliable than classpath loading depending on execution context.
     */
    public static final String RESOURCE_LOCATION = determineResourceLocation();

    /**
     * Helper method to determine the resource location path.
     * @return The calculated path to the src/main/resources directory.
     */
    private static String determineResourceLocation() {
        // Get the current working directory (usually the project root when run from IDE/Maven)
        String projectRoot = System.getProperty("user.dir");
        // Construct the path to the resources folder relative to the project root
        // Using Paths.get().toString() helps normalize path separators (e.g., '/' vs '\')
        return Paths.get(projectRoot, "src", "main", "resources").toString();
    }
}