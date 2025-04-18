package repository.user;

import model.user.Manager;
import repository.Repository;

import java.util.List;
import java.util.Map;

import static utils.config.Location.RESOURCE_LOCATION;

/**
 * The CoordinatorRepository class is a repository that stores Coordinator objects
 * through file I/O operations.
 * It extends the Repository class, which provides basic CRUD operations for the repository.
 */
public class ManagerRepository extends Repository<Manager> {

    /**
     * The path of the repository file.
     */
    private static final String FILE_PATH = "/data/user/ManagerList.txt";

    /**
     * Constructor for creating a new CoordinatorRepository object.
     */
    public ManagerRepository() {
        super();
        load();
    }

    /**
     * Gets a new instance of CoordinatorRepository.
     *
     * @return a new instance of CoordinatorRepository
     */
    public static ManagerRepository getInstance() {
        return new ManagerRepository();
    }

    /**
     * Gets the file path of the repository.
     *
     * @return the file path of the repository
     */
    @Override
    public String getFilePath() {
        return RESOURCE_LOCATION + FILE_PATH;
    }

    /**
     * Sets the list of mappable objects in the repository.
     *
     * @param listOfMappableObjects the list of mappable objects to set
     */
    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            getAll().add(new Manager(map));
        }
    }
}
