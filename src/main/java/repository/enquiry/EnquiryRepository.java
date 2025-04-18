package repository.enquiry;

import model.enquiry.Enquiry;
import model.project.Project;
import repository.Repository;

import java.util.List;
import java.util.Map;

import static utils.config.Location.RESOURCE_LOCATION;

/**
 * The ProjectRepository class is a repository that manages the persistence and retrieval of Project objects
 * through file I/O operations.
 * It extends the Repository class, which provides basic CRUD operations for the repository.
 */
public class EnquiryRepository extends Repository<Enquiry> {

    /**
     * The file path of the project data file.
     */
    private static final String FILE_PATH = "/data/enquiry/enquiry.txt";

    /**
     * Constructs a new ProjectRepository object and loads the data from the project data file.
     */
    public EnquiryRepository() {
        super();
        load();
    }

    /**
     * Gets a new ProjectRepository object.
     *
     * @return a new ProjectRepository object
     */
    public static EnquiryRepository getInstance() {
        return new EnquiryRepository();
    }

    /**
     * Gets the file path of the project data file.
     *
     * @return the file path of the project data file
     */
    @Override
    public String getFilePath() {
        return RESOURCE_LOCATION + FILE_PATH;
    }

    /**
     * Sets the list of mappable objects by converting a list of maps to a list of Project objects.
     *
     * @param listOfMappableObjects the list of mappable objects
     */
    @Override
    public void setAll(List<Map<String, String>> listOfMappableObjects) {
        for (Map<String, String> map : listOfMappableObjects) {
            getAll().add(new Enquiry(map));
        }
    }

    public List<Enquiry> getUnansweredEnquiries() {
        return getAll().stream()
                .filter(enquiry -> !enquiry.getAnswered())
                .toList();
    }
}
