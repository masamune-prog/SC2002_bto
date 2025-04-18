import model.enquiry.Enquiry;
import repository.enquiry.EnquiryRepository;
import utils.exception.ModelAlreadyExistsException;
import utils.exception.ModelNotFoundException;

import java.util.List;

/**
 * Tests persistence and loading of Enquiry objects.
 * Simulates saving to storage and reloading in a fresh repository.
 */
public class EnquirySaveTest {
    private static Enquiry[] enquiries;

    /**
     * Sets up sample enquiries for testing.
     */
    public static void setUp() {
        enquiries = new Enquiry[3];
        enquiries[0] = new Enquiry("E1", "Question One", "UserA", "Content of enquiry one", null, false);
        enquiries[1] = new Enquiry("E2", "Question Two", "UserB", "Content of enquiry two", "Answer two", true);
        enquiries[2] = new Enquiry("E3", "Question Three", "UserC", "Content of enquiry three", null, false);
    }

    public static void main(String[] args) {
        try {
            setUp();
            EnquiryRepository repo = EnquiryRepository.getInstance();
            System.out.println("=== PHASE 1: SAVING ENQUIRIES ===");
            repo.clear();
            System.out.println("Repository cleared.");
            for (Enquiry e : enquiries) {
                repo.add(e);
                System.out.println("Added: " + e.getID() + " - " + e.getQuestion() + " (answered: " + e.isAnswered() + ")");
            }
            printAll(repo);

            System.out.println("\n=== PHASE 2: SIMULATE RESTART ===");
            // new instance forces reload
            EnquiryRepository newRepo = new EnquiryRepository();
            System.out.println("Repository reloaded.");

            System.out.println("\n=== PHASE 3: VERIFY LOADED ENQUIRIES ===");
            List<Enquiry> loaded = newRepo.getAll();
            System.out.println("Loaded count: " + loaded.size());
            printAll(newRepo);
            verifyEnquiries(enquiries, loaded);
            System.out.println("\nTest completed.");
        } catch (ModelAlreadyExistsException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printAll(EnquiryRepository repo) {
        System.out.println("Current enquiries in repository:");
        for (Enquiry e : repo.getAll()) {
            System.out.println("- " + e.getID() + ": " + e.getQuestion() + " (answered: " + e.isAnswered() + ")");
        }
    }

    private static void verifyEnquiries(Enquiry[] original, List<Enquiry> loaded) {
        System.out.println("\nVerifying individual enquiry data:");
        boolean allMatch = true;
        for (Enquiry orig : original) {
            boolean found = false;
            for (Enquiry l : loaded) {
                if (orig.getID().equals(l.getID())) {
                    found = true;
                    boolean questionMatch = orig.getQuestion().equals(l.getQuestion());
                    boolean answeredMatch = orig.isAnswered() == l.isAnswered();
                    System.out.println("Enquiry " + orig.getID() + ": question match? " + questionMatch + ", answered match? " + answeredMatch);
                    if (!questionMatch || !answeredMatch) {
                        allMatch = false;
                    }
                    break;
                }
            }
            if (!found) {
                System.out.println("ERROR: Enquiry " + orig.getID() + " not found after reload!");
                allMatch = false;
            }
        }
        System.out.println(allMatch ? "All enquiries match." : "Some enquiries mismatched.");
    }
}