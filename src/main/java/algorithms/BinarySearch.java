package algorithms;

import model.Product;
import java.util.List;
import java.util.ArrayList;

public class BinarySearch {

    // Public method — returns ALL products whose name matches (empty if none).
    // The list MUST be sorted by name before calling this method.
    public static List<Product> searchByName(List<Product> sortedList, String name) {
        List<Product> matches = new ArrayList<>();

        // Step 1: classic binary search → finds ONE matching position (or -1)
        int index = binarySearch(sortedList, name, 0, sortedList.size() - 1);
        if (index == -1) {
            return matches; // not found
        }

        // Step 2: the list is sorted by name, so all equal names sit next to
        // each other. Expand left and right from the hit to collect them all.
        int left = index;
        while (left - 1 >= 0 &&
               sortedList.get(left - 1).getName().equalsIgnoreCase(name)) {
            left--;
        }
        int right = index;
        while (right + 1 < sortedList.size() &&
               sortedList.get(right + 1).getName().equalsIgnoreCase(name)) {
            right++;
        }

        for (int i = left; i <= right; i++) {
            matches.add(sortedList.get(i));
        }
        return matches;
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
