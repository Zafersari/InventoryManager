package algorithms;

import model.Product;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class QuickSort {

    // Public method — sorts the products by the given criterion.
    // criterion: "price", "stock", "name", "category"
    //
    // Quick Sort's defining property is that it sorts IN PLACE inside an array.
    // To keep the AVL Tree untouched, we copy the products into an array,
    // sort that array in place, and hand back the result as a new list.
    public static List<Product> sort(List<Product> list, String criterion) {
        return sort(list, criterion, true);
    }

    // Overload — sorts ascending (true) or descending (false).
    public static List<Product> sort(List<Product> list, String criterion, boolean ascending) {
        // Base case — a list with 0 or 1 element is already sorted
        if (list.size() <= 1) return new ArrayList<>(list);

        Product[] a = list.toArray(new Product[0]);
        quickSort(a, 0, a.length - 1, criterion, ascending);
        return new ArrayList<>(Arrays.asList(a));
    }

    // ── DIVIDE AND CONQUER ─────────────────────────────────────
    // Sorts the sub-array a[low..high] in place.
    private static void quickSort(Product[] a, int low, int high, String criterion, boolean ascending) {

        // Base case — sub-array of size 0 or 1 is already sorted
        if (low < high) {

            // Divide: choose a pivot and move it to its final position.
            // After this, everything left of p is <= pivot, everything right is >= pivot.
            int p = partition(a, low, high, criterion, ascending);

            // Conquer: recursively sort the two halves.
            // The pivot at p is already in its correct place and is left out.
            quickSort(a, low, p - 1, criterion, ascending);
            quickSort(a, p + 1, high, criterion, ascending);
        }
    }

    // ── PARTITION (Lomuto scheme) ──────────────────────────────
    // Pivot = the last element a[high] (as suggested in the lecture).
    // Walks through the sub-array and grows a "<= pivot" region on the left.
    // Returns the final index of the pivot.
    private static int partition(Product[] a, int low, int high, String criterion, boolean ascending) {
        Product pivot = a[high];   // choose the last element as the pivot
        int i = low - 1;           // i marks the right edge of the "<= pivot" region

        for (int j = low; j < high; j++) {
            // If the current element belongs in the left region, grow it by one
            if (compare(a[j], pivot, criterion, ascending) <= 0) {
                i++;
                swap(a, i, j);
            }
        }

        // Place the pivot directly after the "<= pivot" region → its final spot
        swap(a, i + 1, high);
        return i + 1;
    }

    private static void swap(Product[] a, int x, int y) {
        Product tmp = a[x];
        a[x] = a[y];
        a[y] = tmp;
    }

    // ── COMPARATOR ─────────────────────────────────────────────
    // Returns negative if a < b, 0 if equal, positive if a > b (ascending).
    // For descending order we simply flip the result.
    private static int compare(Product a, Product b, String criterion, boolean ascending) {
        int result;
        switch (criterion) {
            case "price":    result = Double.compare(a.getPrice(), b.getPrice()); break;
            case "stock":    result = Integer.compare(a.getStock(), b.getStock()); break;
            case "name":     result = a.getName().compareToIgnoreCase(b.getName()); break;
            case "category": result = a.getCategory().compareToIgnoreCase(b.getCategory()); break;
            default:         result = Double.compare(a.getPrice(), b.getPrice()); break;
        }
        return ascending ? result : -result;
    }
}
