import repository.project.ProjectRepository;
import repository.request.RequestRepository;
import repository.user.ApplicantRepository;
import repository.user.ManagerRepository;
import repository.user.OfficerRepository;

/**
 * This class is used to clear the database and reload the main class.
 * This is used to test the whole application.
 */
public class HardReload {
    /**
     * Clears the data from all repositories and reloads the main class.
     * This is used for testing the whole application.
     *
     * @param args The command-line arguments passed to the main method.
     */
    public static void main(String[] args) {
        ApplicantRepository.getInstance().clear();
        ManagerRepository.getInstance().clear();
        OfficerRepository.getInstance().clear();
        ProjectRepository.getInstance().clear();
        RequestRepository.getInstance().clear();
        Main.main(null);
    }
}
