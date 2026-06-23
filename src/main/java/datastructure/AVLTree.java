package datastructure;

import model.AVLNode;
import model.Product;

public class AVLTree {

    private AVLNode root;

    // ── HELPER METHODS ──────────────────────────────────────
    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // Returns the bigger
    private int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // Calculate balance factor
    // left height - right height
    private int getBalanceFactor(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    // updates node's height
    // Called after every insert/rotate
    private void updateHeight(AVLNode node) {
        node.height = 1 + max(height(node.left), height(node.right));
    }

    // ── ROTATIONS ──────────────────────────────────────────────

    // RR Rotation — tree is leaning right, rotate left
    //
    //   A                B
    //    \              / \
    //     B     →      A   C
    //      \
    //       C
    //
    private AVLNode rotateLeft(AVLNode A) {
        AVLNode B = A.right;
        AVLNode T2 = B.left; // save B's left child before overwriting

        // perform rotation
        B.left = A;
        A.right = T2; // reattach T2 so it doesn't get lost

        // update heights — A first because it is now below B
        updateHeight(A);
        updateHeight(B);

        return B; // B is the new root of this subtree
    }

    // LL Rotation — tree is leaning left, rotate right
    //
    //       C          B
    //      /          / \
    //     B    →     A   C
    //    /
    //   A
    //
    private AVLNode rotateRight(AVLNode C) {
        AVLNode B = C.left;
        AVLNode T2 = B.right; // save B's right child before overwriting

        // perform rotation
        B.right = C;
        C.left = T2; // reattach T2 so it doesn't get lost

        // update heights — C first because it is now below B
        updateHeight(C);
        updateHeight(B);

        return B; // B is the new root of this subtree
    }

    // LR Rotation — left child is right-heavy, two steps needed
    //
    //     C            C          B
    //    /            /          / \
    //   A      →     B    →    A    C
    //    \           /
    //     B         A
    //
    private AVLNode rotateLR(AVLNode C) {
        C.left = rotateLeft(C.left);  // step 1: rotate left child to the left
        return rotateRight(C);         // step 2: rotate root to the right
    }

    // RL Rotation — right child is left-heavy, two steps needed
    //
    //   A          A              B
    //    \          \            / \
    //     C    →     B    →    A    C
    //    /              \
    //   B                C
    //
    private AVLNode rotateRL(AVLNode A) {
        A.right = rotateRight(A.right); // step 1: rotate right child to the right
        return rotateLeft(A);            // step 2: rotate root to the left
    }

    // ── INSERT ─────────────────────────────────────────────────

    // Public method — called from outside the class
    public void insert(Product product) {
        root = insertNode(root, product);
    }

    // Private recursive method — does the actual work
    private AVLNode insertNode(AVLNode node, Product product) {

        // ── STEP 1: STANDARD BST INSERT ──────────────────────
        // If we reached a null spot, this is where the new node belongs
        if (node == null) {
            return new AVLNode(product);
        }

        // If new product's price is smaller → go left
        if (product.getPrice() < node.key) {
            node.left = insertNode(node.left, product);

        // If new product's price is larger → go right
        } else if (product.getPrice() > node.key) {
            node.right = insertNode(node.right, product);

        // If price is equal → duplicate, do not insert
        } else {
            return node;
        }

        // ── STEP 2: UPDATE HEIGHT ─────────────────────────────
        // Now that we inserted below, this node's height may have changed
        updateHeight(node);

        // ── STEP 3: CHECK BALANCE ─────────────────────────────
        int bf = getBalanceFactor(node);

        // ── STEP 4: APPLY ROTATION IF NEEDED ─────────────────

        // LL case — left heavy, new node inserted to the left of left child
        if (bf > 1 && product.getPrice() < node.left.key) {
            return rotateRight(node);
        }

        // RR case — right heavy, new node inserted to the right of right child
        if (bf < -1 && product.getPrice() > node.right.key) {
            return rotateLeft(node);
        }

        // LR case — left heavy, new node inserted to the right of left child
        if (bf > 1 && product.getPrice() > node.left.key) {
            return rotateLR(node);
        }

        // RL case — right heavy, new node inserted to the left of right child
        if (bf < -1 && product.getPrice() < node.right.key) {
            return rotateRL(node);
        }

        // Tree is still balanced — return node unchanged
        return node;
    }
    // ── TRAVERSAL ──────────────────────────────────────────────

    // INORDER: Left → Root → Right
    // Always returns nodes in ascending order by price
    public void inorder() {
        if (root == null) {
            System.out.println("Tree is empty.");
            return;
        }
        System.out.println("=== Inorder (sorted by price) ===");
        inorderNode(root);
        System.out.println();
    }

    private void inorderNode(AVLNode node) {
        if (node == null) return;
        inorderNode(node.left);
        System.out.println("  " + node.product);
        inorderNode(node.right);
    }

    // PREORDER: Root → Left → Right
    // Shows the structure of the tree as it is built
    public void preorder() {
        if (root == null) {
            System.out.println("Tree is empty.");
            return;
        }
        System.out.println("=== Preorder (tree structure) ===");
        preorderNode(root);
        System.out.println();
    }

    private void preorderNode(AVLNode node) {
        if (node == null) return;
        System.out.println("  " + node.product);
        preorderNode(node.left);
        preorderNode(node.right);
    }

    // POSTORDER: Left → Right → Root
    // Children are always processed before their parent
    public void postorder() {
        if (root == null) {
            System.out.println("Tree is empty.");
            return;
        }
        System.out.println("=== Postorder (children first) ===");
        postorderNode(root);
        System.out.println();
    }

    private void postorderNode(AVLNode node) {
        if (node == null) return;
        postorderNode(node.left);
        postorderNode(node.right);
        System.out.println("  " + node.product);
    }

    // Returns all products as a list — used by MergeSort and BinarySearch
    public java.util.List<Product> getAllProducts() {
        java.util.List<Product> list = new java.util.ArrayList<>();
        collectInorder(root, list);
        return list;
    }

    private void collectInorder(AVLNode node, java.util.List<Product> list) {
        if (node == null) return;
        collectInorder(node.left, list);
        list.add(node.product);
        collectInorder(node.right, list);
    }
    // ── SEARCH ─────────────────────────────────────────────────

    // Public method — search by exact price
    public Product searchByPrice(double price) {
        AVLNode result = searchNode(root, price);
        if (result == null) {
            System.out.println("No product found with price: " + price);
            return null;
        }
        return result.product;
    }

    // Private recursive method — traverses the tree like BST search
    private AVLNode searchNode(AVLNode node, double price) {

        // Reached end of branch — product does not exist
        if (node == null) return null;

        // Found it
        if (price == node.key) return node;

        // Price is smaller → go left
        if (price < node.key) return searchNode(node.left, price);

        // Price is larger → go right
        return searchNode(node.right, price);
    }

    // Returns the product with the minimum price (leftmost node)
    public Product getMinPrice() {
        if (root == null) return null;
        AVLNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.product;
    }

    // Returns the product with the maximum price (rightmost node)
    public Product getMaxPrice() {
        if (root == null) return null;
        AVLNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.product;
    }
    // Returns all products in preorder — for displaying tree structure in UI
    public java.util.List<Product> getPreorderProducts() {
        java.util.List<Product> list = new java.util.ArrayList<>();
        collectPreorder(root, list);
        return list;
    }

    private void collectPreorder(AVLNode node, java.util.List<Product> list) {
        if (node == null) return;
        list.add(node.product);
        collectPreorder(node.left, list);
        collectPreorder(node.right, list);
    }

    // Returns all products in postorder — for displaying in UI
    public java.util.List<Product> getPostorderProducts() {
        java.util.List<Product> list = new java.util.ArrayList<>();
        collectPostorder(root, list);
        return list;
    }

    private void collectPostorder(AVLNode node, java.util.List<Product> list) {
        if (node == null) return;
        collectPostorder(node.left, list);
        collectPostorder(node.right, list);
        list.add(node.product);
    }

    // Returns total number of products in the tree
    public int getSize() {
        return countNodes(root);
    }

    private int countNodes(AVLNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    // ── DELETE ─────────────────────────────────────────────────

    // Public method — called from outside
    public void delete(double price) {
        root = deleteNode(root, price);
    }

    // Private recursive method — does the actual work
    private AVLNode deleteNode(AVLNode node, double price) {

        // STEP 1: STANDARD BST DELETE ─────────────────────────
        if (node == null) return null;

        if (price < node.key) {
            node.left = deleteNode(node.left, price);
        } else if (price > node.key) {
            node.right = deleteNode(node.right, price);
        } else {
            // Found the node to delete — 3 cases:

            // Case 1: Leaf node — just remove it
            if (node.left == null && node.right == null) {
                return null;
            }

            // Case 2a: Only right child exists
            if (node.left == null) return node.right;

            // Case 2b: Only left child exists
            if (node.right == null) return node.left;

            // Case 3: Two children
            // Find inorder successor (smallest node in right subtree)
            AVLNode successor = getMinNode(node.right);

            // Replace current node's data with successor's data
            node.product = successor.product;
            node.key     = successor.key;

            // Delete the successor from right subtree
            node.right = deleteNode(node.right, successor.key);
        }

        // STEP 2: UPDATE HEIGHT ───────────────────────────────
        updateHeight(node);

        // STEP 3: CHECK BALANCE ───────────────────────────────
        int bf = getBalanceFactor(node);

        // STEP 4: APPLY ROTATION IF NEEDED ────────────────────

        // LL case
        if (bf > 1 && getBalanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }
        // LR case
        if (bf > 1 && getBalanceFactor(node.left) < 0) {
            return rotateLR(node);
        }
        // RR case
        if (bf < -1 && getBalanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }
        // RL case
        if (bf < -1 && getBalanceFactor(node.right) > 0) {
            return rotateRL(node);
        }

        return node;
    }

    // Returns the leftmost (smallest) node in a subtree
    private AVLNode getMinNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

}