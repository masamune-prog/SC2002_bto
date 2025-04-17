package boundary.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Generic printer and sorter for lists in boundary layer.
 * Stores the comparator as user preference and applies it on print.
 */
public class ListPrinter<T> {
    private Comparator<T> comparator;
    private Predicate<T> filter;  // user-defined filter

    /**
     * Create a ListPrinter with default alphabetical order based comparator.
     * The caller should supply an appropriate comparator for T.
     */
    public ListPrinter(Comparator<T> defaultComparator) {
        this.comparator = defaultComparator;
    }

    /**
     * Update the comparator (user's sorting preference).
     */
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Set a filter predicate to limit items before printing.
     */
    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
    }

    /**
     * Clear any set filter so all items are printed.
     */
    public void clearFilter() {
        this.filter = null;
    }

    /**
     * Sorts the list using the stored comparator and prints each element.
     * Elements are printed by their toString() representation.
     */
    public void print(List<T> items) {
        if (items == null || comparator == null) return;
        // apply filter if present
        List<T> toPrint = (filter != null)
            ? items.stream().filter(filter).collect(Collectors.toList())
            : new ArrayList<>(items);
        Collections.sort(toPrint, comparator);
        for (T item : toPrint) {
            System.out.println(item);
        }
    }
}
