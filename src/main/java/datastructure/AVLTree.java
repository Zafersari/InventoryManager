package datastructure;

import model.AVLNode;
import model.Product;

public class AVLTree {
    private AVLNode root;

    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    private int max(int a, int b) {
        if (a > b)
            return a;
        else
            return b;
    }

    private int getBalanceFactor(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + max(height(node.left), height(node.right)); // 1 + because the node itself has a height of 1.
    }

}
