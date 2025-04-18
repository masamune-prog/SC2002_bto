import model.request.ProjectApplicationRequest;
import model.request.ProjectBookingRequest;
import model.request.ProjectWithdrawalRequest;
import model.request.Request;
import repository.request.RequestRepository;
import utils.exception.ModelNotFoundException;
import utils.exception.ModelAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests persistence and loading of Request objects.
 * Simulates saving to file and reloading in a fresh repository.
 */
public class RequestSaveTest {
    private static Request[] requests;

    /**
     * Initializes a set of test requests.
     */
    public static void setUp() {
        requests = new Request[3];
        // 1) Project application request
        requests[0] = new ProjectApplicationRequest(
                "R1", "P1", "A1", model.project.RoomType.TWO_ROOM_FLAT
        );
        // 2) Booking request (link to application R1)
        requests[1] = new ProjectBookingRequest(
                "R2", "P1", "A1", "R1", model.project.RoomType.TWO_ROOM_FLAT
        );
        // 3) Withdrawal request
        requests[2] = new ProjectWithdrawalRequest(
                "R3", "P1", "A1", model.project.RoomType.TWO_ROOM_FLAT, "Change of plans"
        );
    }

    public static void main(String[] args) {
        try {
            setUp();
            RequestRepository repo = RequestRepository.getInstance();
            System.out.println("=== PHASE 1: SAVING REQUESTS ===");
            repo.clear();
            System.out.println("Repository cleared.");
            for (Request r : requests) {
                repo.add(r);
                System.out.println("Added request: " + r.getID() + " (" + r.getRequestType() + ")");
            }
            printAll(repo);

            System.out.println("\n=== PHASE 2: SIMULATE RESTART ===");
            // new instance forces reload from storage
            RequestRepository newRepo = new RequestRepository();
            System.out.println("Repository reloaded.");

            System.out.println("\n=== PHASE 3: VERIFY LOADED REQUESTS ===");
            List<Request> loaded = newRepo.getAll();
            System.out.println("Loaded count: " + loaded.size());
            printAll(newRepo);
            verifyRequests(requests, loaded);
            System.out.println("\nTest completed.");
        } catch (ModelAlreadyExistsException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printAll(RequestRepository repo) {
        System.out.println("Current requests in repository:");
        for (Request r : repo.getAll()) {
            System.out.println("- " + r.getID() + ": " + r.getRequestType());
        }
    }

    private static void verifyRequests(Request[] original, List<Request> loaded) {
        System.out.println("\nVerifying individual request data:");
        boolean allMatch = true;
        for (Request orig : original) {
            boolean found = false;
            for (Request l : loaded) {
                if (orig.getID().equals(l.getID())) {
                    found = true;
                    boolean typeMatch = orig.getRequestType() == l.getRequestType();
                    System.out.println("Request " + orig.getID() + ": type match? " + typeMatch);
                    if (!typeMatch) allMatch = false;
                    break;
                }
            }
            if (!found) {
                System.out.println("ERROR: Request " + orig.getID() + " not found after reload!");
                allMatch = false;
            }
        }
        System.out.println(allMatch ? "All requests match." : "Some requests mismatched.");
    }
}