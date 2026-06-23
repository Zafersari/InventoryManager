package controller;

import algorithms.BinarySearch;
import algorithms.MergeSort;
import datastructure.AVLTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;

import java.util.List;

public class InventoryController {

    // ── FXML INJECTIONS ────────────────────────────────────────
    // These fields are automatically connected to the fxml elements

    // Input fields
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField categoryField;

    // Search fields
    @FXML private TextField searchPriceField;
    @FXML private TextField searchNameField;

    // Table and columns
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> categoryColumn;

    // Labels
    @FXML private Label statusLabel;
    @FXML private Label statsLabel;

    // ── DATA ───────────────────────────────────────────────────

    // Our AVL Tree — stores all products
    private final AVLTree tree = new AVLTree();

    // Observable list — JavaFX table reads from this
    private final ObservableList<Product> tableData = FXCollections.observableArrayList();

    // ── INITIALIZE ─────────────────────────────────────────────
    // Called automatically by JavaFX after fxml is loaded

    @FXML
    public void initialize() {
        // Connect each column to the correct Product field
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Connect the table to our observable list
        productTable.setItems(tableData);

        // Add sample products so the table is not empty at startup
        loadSampleData();

        setStatus("Ready. " + tree.getSize() + " sample products loaded.");
        // Highlight low stock rows in red (stock < 20)
        productTable.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getStock() < 20) {
                    setStyle("-fx-background-color: #fadbd8;"); // light red
                } else {
                    setStyle("");
                }
            }
        });
    }

    // ── SAMPLE DATA ────────────────────────────────────────────

    private void loadSampleData() {
        tree.insert(new Product("Laptop",    999.99, 15, "Electronics"));
        tree.insert(new Product("Mouse",      29.99, 80, "Electronics"));
        tree.insert(new Product("Keyboard",   79.99, 50, "Electronics"));
        tree.insert(new Product("Monitor",   349.99, 20, "Electronics"));
        tree.insert(new Product("Headphones",149.99, 35, "Electronics"));
        tree.insert(new Product("Desk",      249.99, 10, "Furniture"));
        tree.insert(new Product("Chair",     199.99, 12, "Furniture"));
        tree.insert(new Product("Notebook",    4.99,200, "Stationery"));
        tree.insert(new Product("Pen",         1.99,500, "Stationery"));
        tree.insert(new Product("Backpack",   59.99, 40, "Accessories"));
        refreshTable(tree.getAllProducts());
        updateStats();
    }

    // ── ADD PRODUCT ────────────────────────────────────────────

    @FXML
    private void handleAdd() {
        // Read input fields
        String name     = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();
        String category = categoryField.getText().trim();

        // Validate — all fields must be filled
        if (name.isEmpty() || priceStr.isEmpty() ||
            stockStr.isEmpty() || category.isEmpty()) {
            setStatus("⚠️ Please fill in all fields.");
            return;
        }

        // Validate — price and stock must be numbers
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            setStatus("⚠️ Price must be a decimal number, Stock must be an integer.");
            return;
        }

        // Validate — price and stock must be positive
        if (price <= 0 || stock < 0) {
            setStatus("⚠️ Price must be greater than 0, Stock cannot be negative.");
            return;
        }

        // Insert into AVL Tree
        Product product = new Product(name, price, stock, category);
        tree.insert(product);

        // Refresh table with inorder (sorted by price)
        refreshTable(tree.getAllProducts());
        updateStats();

        // Clear input fields
        nameField.clear();
        priceField.clear();
        stockField.clear();
        categoryField.clear();

        setStatus("✅ Product added: " + name + " | " + price + "€  [AVL Tree insert]");
    }

    // ── SEARCH BY PRICE ────────────────────────────────────────
    // Uses AVL Tree search — O(log n)

    @FXML
    private void handleSearchByPrice() {
        String input = searchPriceField.getText().trim();

        if (input.isEmpty()) {
            setStatus("⚠️ Please enter a price to search.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            setStatus("⚠️ Please enter a valid price.");
            return;
        }

        // AVL Tree search
        Product result = tree.searchByPrice(price);

        if (result != null) {
            // Show only the found product in the table
            refreshTable(List.of(result));
            setStatus("✅ Found: " + result.getName() +
                      "  [AVL Tree search — O(log n)]");
        } else {
            setStatus("❌ No product found with price: " + price + "€");
        }

        searchPriceField.clear();
    }

    // ── SEARCH BY NAME ─────────────────────────────────────────
    // Uses MergeSort + BinarySearch — O(n log n) + O(log n)

    @FXML
    private void handleSearchByName() {
        String name = searchNameField.getText().trim();

        if (name.isEmpty()) {
            setStatus("⚠️ Please enter a name to search.");
            return;
        }

        // Step 1: Get all products from AVL Tree
        List<Product> all = tree.getAllProducts();

        // Step 2: Sort by name using Merge Sort
        List<Product> sorted = MergeSort.sort(all, "name");

        // Step 3: Binary Search on sorted list
        Product result = BinarySearch.searchByName(sorted, name);

        if (result != null) {
            refreshTable(List.of(result));
            setStatus("✅ Found: " + result.getName() +
                      "  [MergeSort + BinarySearch — O(n log n) + O(log n)]");
        } else {
            setStatus("❌ No product found with name: " + name);
        }

        searchNameField.clear();
    }

    // ── TRAVERSAL BUTTONS ──────────────────────────────────────

    @FXML
    private void handleInorder() {
        List<Product> list = tree.getAllProducts();
        refreshTable(list);
        setStatus("📋 Inorder Traversal — sorted by price ascending  [O(n)]");
    }

    @FXML
    private void handlePreorder() {
        List<Product> list = tree.getPreorderProducts();
        refreshTable(list);
        setStatus("🌲 Preorder Traversal — root first, then left, then right  [O(n)]");
    }

    @FXML
    private void handlePostorder() {
        List<Product> list = tree.getPostorderProducts();
        refreshTable(list);
        setStatus("🍂 Postorder Traversal — children first, then root  [O(n)]");
    }

    // ── MERGE SORT BUTTONS ─────────────────────────────────────

    @FXML
    private void handleSortByName() {
        List<Product> all    = tree.getAllProducts();
        List<Product> sorted = MergeSort.sort(all, "name");
        refreshTable(sorted);
        setStatus("🔤 Merge Sort by name  [O(n log n)]");
    }

    @FXML
    private void handleSortByStock() {
        List<Product> all    = tree.getAllProducts();
        List<Product> sorted = MergeSort.sort(all, "stock");
        refreshTable(sorted);
        setStatus("📦 Merge Sort by stock  [O(n log n)]");
    }

    // ── DELETE ─────────────────────────────────────────────────
    @FXML
    private void handleDelete() {
        // Get selected product from table
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            setStatus("⚠️ Please select a row in the table first.");
            return;
        }

        // Delete from AVL Tree by price
        tree.delete(selected.getPrice());

        // Refresh table
        refreshTable(tree.getAllProducts());
        updateStats();

        setStatus("🗑️ Deleted: " + selected.getName() +
                  "  [AVL Tree delete — O(log n)]");
    }
    // ── RESET ──────────────────────────────────────────────────
    @FXML
    private void handleReset() {
        refreshTable(tree.getAllProducts());
        setStatus("🔄 View reset — showing Inorder (sorted by price)");
    }

    // ── HELPER METHODS ─────────────────────────────────────────

    // Updates the table with a new list of products
    private void refreshTable(List<Product> list) {
        tableData.clear();
        tableData.addAll(list);
        productTable.refresh(); // re-applies row coloring
        updateStats();
    }

    // Updates the status bar at the bottom
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    // Updates the stats label on the left panel
    private void updateStats() {
        Product min = tree.getMinPrice();
        Product max = tree.getMaxPrice();
        String minName = (min != null) ? min.getName() : "-";
        String maxName = (max != null) ? max.getName() : "-";

        statsLabel.setText(
            "Products: " + tree.getSize() + "\n" +
            "Cheapest: " + minName + "\n" +
            "Most expensive: " + maxName
        );
    }
}