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
        C.left = rotateLeft(C.left); // step 1: rotate left child to the left
        return rotateRight(C); // step 2: rotate root to the right
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
        return rotateLeft(A); // step 2: rotate root to the left
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

            // If price is equal → another product at the same price.
            // Add it to this node's bucket; no new node, so no rebalancing needed.
        } else {
            node.products.add(product);
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

    // ── IN-ORDER COLLECTION ────────────────────────────────────
    // Returns all products as a list in ascending price order.
    // An in-order walk of a BST (Left → Root → Right) visits the nodes
    // already sorted, so this is the bridge from the tree to the
    // list-based algorithms (Merge Sort, Quick Sort, Binary Search).
    public java.util.List<Product> getAllProducts() {
        java.util.List<Product> list = new java.util.ArrayList<>();
        collectInorder(root, list);
        return list;
    }

    private void collectInorder(AVLNode node, java.util.List<Product> list) {
        if (node == null)
            return;
        collectInorder(node.left, list);
        list.addAll(node.products);   // every product in this price bucket
        collectInorder(node.right, list);
    }
    // ── SEARCH ─────────────────────────────────────────────────

    // Public method — returns all products at the given price (empty if none).
    // Several products can share a price, so this returns the whole bucket.
    public java.util.List<Product> searchByPrice(double price) {
        AVLNode result = searchNode(root, price);
        if (result == null) {
            return new java.util.ArrayList<>();
        }
        // Return a copy so callers cannot modify the tree's internal bucket
        return new java.util.ArrayList<>(result.products);
    }

    // Private recursive method — traverses the tree like BST search
    private AVLNode searchNode(AVLNode node, double price) {

        // Reached end of branch — product does not exist
        if (node == null)
            return null;

        // Found it
        if (Math.abs(price - node.key) < 0.001)
            return node;
        // Price is smaller → go left
        if (price < node.key)
            return searchNode(node.left, price);

        // Price is larger → go right
        return searchNode(node.right, price);
    }

    // Returns a product with the minimum price (leftmost node)
    public Product getMinPrice() {
        if (root == null)
            return null;
        AVLNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.products.get(0);
    }

    // Returns a product with the maximum price (rightmost node)
    public Product getMaxPrice() {
        if (root == null)
            return null;
        AVLNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.products.get(0);
    }

    // Returns total number of products in the tree
    public int getSize() {
        return countNodes(root);
    }

    private int countNodes(AVLNode node) {
        if (node == null)
            return 0;
        // Count every product, not every node — a node may hold several
        return node.products.size() + countNodes(node.left) + countNodes(node.right);
    }
    // ── DELETE ─────────────────────────────────────────────────

    // Public method — removes ONE specific product from the tree.
    // We delete by product (not just by price) because several products can
    // share a price; we must remove exactly the one the user selected.
    public void delete(Product target) {
        root = deleteProduct(root, target);
    }

    // Navigates to the price bucket and removes the target product from it.
    // The node is only removed from the tree once its bucket becomes empty.
    private AVLNode deleteProduct(AVLNode node, Product target) {
        if (node == null)
            return null;

        double price = target.getPrice();

        if (price < node.key) {
            node.left = deleteProduct(node.left, target);
        } else if (price > node.key) {
            node.right = deleteProduct(node.right, target);
        } else {
            // This node holds the bucket for that price — remove the product.
            node.products.remove(target);

            // Other products still share this price → structure is unchanged.
            if (!node.products.isEmpty()) {
                return node;
            }

            // Bucket is now empty → remove the whole node from the tree.
            return removeNode(node, node.key);
        }

        updateHeight(node);
        return rebalance(node);
    }

    // Removes the entire node with the given (unique) price — the standard AVL
    // delete with its three child cases. Also used to detach the in-order
    // successor in the two-children case.
    private AVLNode removeNode(AVLNode node, double price) {
        if (node == null)
            return null;

        if (price < node.key) {
            node.left = removeNode(node.left, price);
        } else if (price > node.key) {
            node.right = removeNode(node.right, price);
        } else {
            // Case 1: leaf node — just remove it
            if (node.left == null && node.right == null)
                return null;

            // Case 2: only one child
            if (node.left == null)
                return node.right;
            if (node.right == null)
                return node.left;

            // Case 3: two children — move the in-order successor's whole bucket
            // up into this node, then detach the successor node.
            AVLNode successor = getMinNode(node.right);
            node.key = successor.key;
            node.products = successor.products;
            node.right = removeNode(node.right, successor.key);
        }

        updateHeight(node);
        return rebalance(node);
    }

    // Re-applies AVL balance after a deletion. Picks the rotation from the
    // children's balance factors — the standard delete-rebalance cases.
    private AVLNode rebalance(AVLNode node) {
        int bf = getBalanceFactor(node);

        if (bf > 1 && getBalanceFactor(node.left) >= 0)   return rotateRight(node); // LL
        if (bf > 1 && getBalanceFactor(node.left) < 0)    return rotateLR(node);    // LR
        if (bf < -1 && getBalanceFactor(node.right) <= 0) return rotateLeft(node);  // RR
        if (bf < -1 && getBalanceFactor(node.right) > 0)  return rotateRL(node);    // RL

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