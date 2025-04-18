package repository.user;


import repository.Repository;
import utils.config.Location;
import model.user.Officer;

import java.util.List;
import java.util.Map;

/**
 * The FacultyRepository class is a repository for storing and managing Supervisor objects in a file
 * through file I/O operations.
 * It extends the Repository class, which provides basic CRUD operations for the repository.
 */
public class OfficerRepository extends Repository<Officer> {

    /**
     * The path of the repository file.
     */
    final private static String FILE_PATH = "/data/user/OfficerList.txt";

    /**
     * Constructor for creating a new instance of the FacultyRepository class.
     */
    public OfficerRepository() {
        super();
        load();
    }

    /**
     * Gets a new instance of the FacultyRepository class.
     *
     * @return a new instance of the FacultyRepository class
     */
    public static OfficerRepository getInstance() {
        return new OfficerRepository();
    }

    /**
     * Gets the path of the repository file.
     *
     * @return the path of the repository file
     */
    @Override
    public String getFilePath() {
        return Location.RESOURCE_LOCATION + FILE_PATH;
    }

    /**
     * Sets the list of mappable objects to a list of Supervisor objects.
     *
     * @param listOfMappableObjects the list of mappable objects
     */
    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            getAll().add(new Officer(map));
        }
    }
}
