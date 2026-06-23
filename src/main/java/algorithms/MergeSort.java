package algorithms;

import model.Product;
import java.util.List;
import java.util.ArrayList;

public class MergeSort {

    // Public method — sorts the list ascending by the given criterion.
    // criterion: "name", "stock", "category"
    public static List<Product> sort(List<Product> list, String criterion) {
        return sort(list, criterion, true);
    }

    // Overload — sorts ascending (true) or descending (false).
    public static List<Product> sort(List<Product> list, String criterion, boolean ascending) {
        if (list.size() <= 1) return list;
        return mergeSort(list, criterion, ascending);
    }

    // ── STEP 1: DIVIDE ─────────────────────────────────────────
    // Keep splitting the list in half until each piece has 1 element
    private static List<Product> mergeSort(List<Product> list, String criterion, boolean ascending) {

        // Base case — a list of 1 element is already sorted
        if (list.size() <= 1) return list;

        // Find the middle index
        int mid = list.size() / 2;

        // Split into left and right halves
        List<Product> left  = new ArrayList<>(list.subList(0, mid));
        List<Product> right = new ArrayList<>(list.subList(mid, list.size()));

        // Recursively sort each half
        left  = mergeSort(left,  criterion, ascending);
        right = mergeSort(right, criterion, ascending);

        // ── STEP 2: MERGE ─────────────────────────────────────
        return merge(left, right, criterion, ascending);
    }

    // Merges two sorted lists into one sorted list
    private static List<Product> merge(List<Product> left, List<Product> right, String criterion, boolean ascending) {

        List<Product> result = new ArrayList<>();
        int i = 0; // pointer for left list
        int j = 0; // pointer for right list

        // Compare elements one by one, always take the smaller one
        while (i < left.size() && j < right.size()) {
            if (compare(left.get(i), right.get(j), criterion, ascending) <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        // Add remaining elements from left (if any)
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }

        // Add remaining elements from right (if any)
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }

        return result;
    }

    // ── COMPARATOR ─────────────────────────────────────────────
    // Returns negative if a < b, 0 if equal, positive if a > b (ascending).
    // For descending order we simply flip the result — equal elements still
    // return 0, so the merge stays stable in both directions.
    private static int compare(Product a, Product b, String criterion, boolean ascending) {
        int result;
        switch (criterion) {
            case "name":     result = a.getName().compareToIgnoreCase(b.getName()); break;
            case "stock":    result = Integer.compare(a.getStock(), b.getStock()); break;
            case "category": result = a.getCategory().compareToIgnoreCase(b.getCategory()); break;
            default:         result = a.getName().compareToIgnoreCase(b.getName()); break;
        }
        return ascending ? result : -result;
    }
}