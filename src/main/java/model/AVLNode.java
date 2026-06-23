package model;

import java.util.ArrayList;
import java.util.List;

public class AVLNode {
    // All products that share this price (the "bucket"). Keeping a list here
    // lets several different products have the same price, while the tree is
    // still organised by the single price key below.
    public List<Product> products;
    public double key;
    public int height; // this is important for balance, if it is -2 or +2, the algortihm know that it
                       // should be make a rotation.
    public AVLNode left;
    public AVLNode right;

    public AVLNode(Product product) {
        this.products = new ArrayList<>();
        this.products.add(product);
        this.key = product.getPrice();
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}
