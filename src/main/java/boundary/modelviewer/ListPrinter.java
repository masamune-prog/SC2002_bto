package boundary.modelviewer;

import model.project.Project;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A generic class that applies filters to lists and prints the filtered results.
 * @param <T> The type of elements in the list
 */
public class ListPrinter<T> {

    private List<T> items;
    private Predicate<T> filter;

    /**
     * Creates a new ListPrinter with the specified items.
     * @param items The list of items to manage
     */
    public ListPrinter(List<T> items) {
        this.items = items;
        this.filter = item -> true; // Default filter accepts everything
    }

    public ListPrinter(Comparator<Project> comparing) {
    }

    /**
     * Sets a filter to apply to the items.
     * @param filter A predicate that returns true for items to include in the output
     */
    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
    }

    /**
     * Clears the current filter, allowing all items to be displayed.
     */
    public void clearFilter() {
        this.filter = item -> true;
    }

    /**
     * Updates the list of items to be filtered.
     * @param items The new list of items
     */
    public void updateItems(List<T> items) {
        this.items = items;
    }

    /**
     * Gets the filtered list of items.
     * @return A list containing only items that match the current filter
     */
    public List<T> getFilteredItems() {
        return items.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Prints all items that match the current filter.
     * Each item is printed using its toString() method.
     */
    public void printFilteredItems() {
        List<T> filteredItems = getFilteredItems();

        if (filteredItems.isEmpty()) {
            System.out.println("No items match the current filter.");
            return;
        }

        int index = 1;
        for (T item : filteredItems) {
            System.out.println(index + ". " + item);
            index++;
        }
    }

    /**
     * Prints all items that match the current filter using a custom format.
     * @param formatter A function that converts an item to a string representation
     */
    public void printFilteredItems(java.util.function.Function<T, String> formatter) {
        List<T> filteredItems = getFilteredItems();

        if (filteredItems.isEmpty()) {
            System.out.println("No items match the current filter.");
            return;
        }

        int index = 1;
        for (T item : filteredItems) {
            System.out.println(index + ". " + formatter.apply(item));
            index++;
        }
    }
}