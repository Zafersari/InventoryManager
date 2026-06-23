package algorithms;

import model.Product;
import java.util.List;

public class BinarySearch {

    // Public method — searches for a product by name in a sorted list
    // List MUST be sorted by name before calling this method
    public static Product searchByName(List<Product> sortedList, String name) {
        int index = binarySearch(sortedList, name, 0, sortedList.size() - 1);
        if (index == -1) {
            System.out.println("No product found with name: " + name);
            return null;
        }
        return sortedList.get(index);
    }

    // ── RECURSIVE BINARY SEARCH ────────────────────────────────
    private static int binarySearch(List<Product> list, String name, int low, int high) {

        // Base case — search space is empty, not found
        if (low > high) return -1;

        // Find the middle index
        int mid = (low + high) / 2;

        int comparison = name.compareToIgnoreCase(list.get(mid).getName());

        // Found it
        if (comparison == 0) return mid;

        // Name comes before middle → search left half
        if (comparison < 0) return binarySearch(list, name, low, mid - 1);

        // Name comes after middle → search right half
        return binarySearch(list, name, mid + 1, high);
    }
}