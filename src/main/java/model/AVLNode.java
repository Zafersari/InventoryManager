package model;

public class AVLNode {
    public Product product;
    public double key;
    public int height; // this is important for balance, if it is -2 or +2, the algortihm know that it
                       // should be make a rotation.
    public AVLNode left;
    public AVLNode right;

    public AVLNode(Product product) {
        this.product = product;
        this.key = product.getPrice();
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}