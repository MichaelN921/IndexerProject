package main;

public class DatabaseTree {
    static class Node {
        public int data;
        public Node left, right;

        public Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    public Node root;

    public DatabaseTree() {
        this.root = null;
    }

    public void insert(int newData) {
        this.root = insert(root, newData);
    }

    public Node insert(Node root, int newData) {
        if (root==null) {
            root = new Node(newData);
            return root;
        } else if (root.data >= newData) {
            root.left = insert(root.left, newData);
        } else {
            root.right = insert(root.right, newData);
        }
        return root;
    }

    public boolean search(int data) {
        return search(this.root, data);
    }

    public boolean search(Node root, int data) {
        if (root==null) {
            return false;
        } else if (root.data==data) {
            return true;
        } else if (root.data>data) {
            return search(root.left, data);
        }
        return search(root.right, data);
    }
}
