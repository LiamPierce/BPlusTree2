import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;



public class BinaryTree <K extends Comparable<? super K>,V> {

    private Node root;

    enum Traversal{
        LEVEL_ORDER,
    }

    enum TreeDirection{
        LEFT, RIGHT, NONE
    }

    class Node{
        public K key;
        public V value;

        public Node parent;
        public Node leftChild;
        public Node rightChild;

        public int leftHeight = 0;
        public int rightHeight = 0;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }

        public void setParent(Node toNode){
            this.parent = toNode;

            if (toNode == null){
                root = this;
            }
        }

        public boolean setChild(TreeDirection dir, Node toNode){

            if (dir == TreeDirection.LEFT){
                this.leftChild = toNode;
            }else{
                this.rightChild = toNode;
            }

            if (toNode != null) {
                toNode.setParent(this);
                toNode.recalculateHeights();
            }

            return true;
        }

        public boolean changeChild(Node fromNode, Node toNode){
            return this.setChild(getTreeDirection(fromNode),toNode);
        }

        public TreeDirection getTreeDirection(Node ofNode){
            if (this.leftChild == ofNode){
                return TreeDirection.LEFT;
            }else if (this.rightChild == ofNode){
                return TreeDirection.RIGHT;
            }else{
                return TreeDirection.NONE;
            }
        }

        public int getBalance(){
            return leftHeight - rightHeight;
        }

        public int getHeight(){
            return Math.max(leftHeight, rightHeight);
        }

        public void recalculateHeights(){
            if (this.leftChild != null){
                this.leftHeight = this.leftChild.getHeight() + 1;
            }else{
                this.leftHeight = 0;
            }

            if (this.rightChild != null){
                this.rightHeight = this.rightChild.getHeight() + 1;
            }else{
                this.rightHeight = 0;
            }
        }

        public boolean isBalanced(){
            return Math.abs(getBalance()) <= 1;
        }

        public TreeDirection getBias(){
            if (this.leftHeight > this.rightHeight){
                return TreeDirection.LEFT;
            }else if (this.leftHeight < this.rightHeight){
                return TreeDirection.RIGHT;
            }else{
                return TreeDirection.NONE;
            }
        }

        public Node rotate(TreeDirection direction){

            Node toMove = this.parent;

            if (this.parent.parent != null){
                this.parent.parent.changeChild(toMove,this);
            }else{
                this.setParent(null);
            }

            if (direction == TreeDirection.LEFT){

                System.out.println("LEFT ROTATION AROUND "+this);

                toMove.setChild(TreeDirection.RIGHT,this.leftChild);
                this.setChild(TreeDirection.LEFT,toMove);

            }else{

                System.out.println("RIGHT ROTATION AROUND "+this);

                toMove.setChild(TreeDirection.LEFT,this.rightChild);
                this.setChild(TreeDirection.RIGHT,toMove);

            }

            this.recalculateHeights();

            if (this.parent != null){
                this.parent.recalculateHeights();
            }



            System.out.println(this);
            System.out.println("Left Child :"+this.leftChild);
            System.out.println("Right Child :"+this.rightChild);
            System.out.println("Finished.");

            //System.exit(1);
            return this;
        }

        public int insert(K key, V value) {

            int depth;

            if (key.compareTo(this.key) < 0) {

                if (leftChild != null){
                    depth = leftChild.insert(key, value);

                }else{
                    leftChild = new Node(key, value);
                    leftChild.parent = this;

                    depth = 0;
                }

                this.leftHeight = Math.max(leftHeight,depth + 1);

            }else{

                if (rightChild != null){
                    depth = rightChild.insert(key, value);
                }else {
                    rightChild = new Node(key, value);
                    rightChild.parent = this;

                    depth = 0;
                }

                this.rightHeight = Math.max(rightHeight, depth + 1);
            }

            if (!this.isBalanced()){
                System.out.println("Node unbalance : at " + this);
                //System.out.println("Not balanced. " + this);
                if (this.getBalance() > 0){ //Left unbalanced.
                    if (this.leftChild.getBias() == TreeDirection.LEFT) {
                        System.out.println("LEFT : A");
                        this.leftChild.rotate(TreeDirection.RIGHT);
                    }else{
                        System.out.println("LEFT : B");
                        this.leftChild.rightChild.rotate(TreeDirection.LEFT);
                        this.leftChild.rotate(TreeDirection.RIGHT);
                    }
                }else{ //Right unbalanced.
                    System.out.println("Right unbalanced.");

                    if (this.rightChild.getBias() == TreeDirection.RIGHT){
                        System.out.println("RIGHT: A");
                        this.rightChild.rotate(TreeDirection.LEFT);
                    }else{
                        System.out.println("RIGHT: B");
                        this.rightChild.leftChild.rotate(TreeDirection.RIGHT);
                        this.rightChild.rotate(TreeDirection.LEFT);
                    }

                }

                return getHeight();
            }

            return depth + 1;
        }

        private Node getFirstNode(K key){
            if (key.compareTo(this.key) < 0) {
                if (this.leftChild == null){
                    return null;
                }

                return leftChild.getFirstNode(key);
            }else if (key.compareTo(this.key) > 0){
                if (this.rightChild == null){
                    return null;
                }

                return rightChild.getFirstNode(key);
            }else{
                return this;
            }
        }

        public void display(int depth){
            System.out.println("\t".repeat(depth)+key+" ("+(isBalanced() ? "balanced" : "unbalanced")+": leftHeight "+leftHeight+", rightHeight "+rightHeight+" balance "+getBalance()+")");

            if (leftChild != null){
                System.out.println("\t".repeat(depth) + "Left:");
                leftChild.display(depth + 1);
            }

            if (rightChild != null){
                System.out.println("\t".repeat(depth) + "Right:");
                rightChild.display(depth + 1);
            }
        }

        public String toString(){
            return "Node : "+this.key + " (balance: "+this.getBalance()+", left: "+leftHeight+", right: "+rightHeight+"). Parent: "+(this.parent != null ? this.parent.key : "This node is root.");
        }
    }

    public int getHeight(){
        return root.getHeight();
    }

    public boolean insert(K key, V value){
        if (root != null){
            root.insert(key, value);
            return true;
        }

        this.root = new Node(key, value);

        return true;
    }

    public V getFirst(K key){
        if (root == null){
            return null;
        }

        Node first = root.getFirstNode(key);
        return first != null ? first.value : null;
    }

    public ArrayList<V> getAll(K key){
        ArrayList<V> all = new ArrayList<>();

        Node point = root.getFirstNode(key);

        all.add(point.value);

        while (point.rightChild != null){
            if (point.rightChild.key == key){
                all.add(point.value);
            }else{
                break;
            }

            point = point.rightChild;
        }

        return all;
    }

    public void display(){
        this.root.display(0);
    }
}
