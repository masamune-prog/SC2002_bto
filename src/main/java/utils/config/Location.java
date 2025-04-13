package utils.config;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This class contains the location of the resources.
 */
public class Location {
    /**
     * The location of the resources.
     * Dynamically detects the resources directory from the classpath.
     */
    public static final String RESOURCE_LOCATION;

    static {
        // First try to get the resources directory from the classpath
        URL resourceUrl = Location.class.getClassLoader().getResource("");
        String path = "";

        if (resourceUrl != null) {
            try {
                File resourcesDir = new File(resourceUrl.toURI());
                // For compiled code, we need to go to resources
                File mainDir = resourcesDir.getParentFile();
                if (mainDir != null && mainDir.getName().equals("classes")) {
                    // We're running from target/classes, so resources are in target/classes
                    path = resourcesDir.getAbsolutePath();
                } else {
                    // We're running from an IDE, so resources are in src/main/resources
                    path = new File("src/main/resources").getAbsolutePath();
                }
            } catch (URISyntaxException e) {
                // Fallback to relative path
                path = new File("src/main/resources").getAbsolutePath();
            }
        } else {
            // Fallback to relative path
            path = new File("src/main/resources").getAbsolutePath();
        }

        RESOURCE_LOCATION = path;
    }
}