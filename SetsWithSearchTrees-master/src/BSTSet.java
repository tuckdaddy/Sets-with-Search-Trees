
import java.util.*;

public class BSTSet<T extends Comparable<T>> implements NavigableSet<T> {

    // the root of the tree
    protected TreeNode<T> root;

    // number of TreeNodes in the tree
    public int size;

    public BSTSet() {
        root = null;
        size = 0;
    }


    /*
	Insert the element d into the Binary Search Tree 
     */
    @Override
    public void add(T e) {

        insert(root, e);
    }
	
    /* Creates a BST from the given array. To get the BST that you
    expect, the order of the data should be breadth-first order of the resulting tree

    e.g. The tree

            100
      50           200
         60     110   203
    would be achieved by passing in
    {100,50,200,60,110,203}
    */
    protected static <E extends Comparable<E>> BSTSet<E> bulkInsert(E[] data) {
            BSTSet<E> b = new BSTSet<>();
            for (int i=0; i<data.length; i++) {
                    b.insert(b.root, data[i]);
            }
            return b;
    }
    // Part 1 
    private void insert(TreeNode<T> current, T data) {

        if (root == null) {
            root = new TreeNode<>(data);
            size++;
            return;
        } else {

            if (current.data.compareTo(data) == 0) {
                return; //the same object is alread stored in the tree, so exit without inserting a a duplicate
            }

            if (current.data.compareTo(data) > 0 && current.left != null) {
                insert(current.left, data);
            } else if (current.data.compareTo(data) < 0 && current.right != null) {
                insert(current.right, data);
            } else if (current.data.compareTo(data) > 0 && current.left == null) {
                current.left = new TreeNode<>(data);
                size++;
            } else {
                current.right = new TreeNode<>(data);
                size++;
            }

        }

    }
    @Override
    public boolean contains(T e) {
        return contains(e, root);
    }
    public boolean contains(T e, TreeNode<T> subT){
        if(subT == null) return false;
        
        int compare = e.compareTo(subT.data);
        
        if(compare == 0){
            return true;
        }
        if(compare < 0){
            return contains(e, subT.left);
        }else{
            return contains(e, subT.right);
        }
    }
    
    // PART 2
    @Override
    public Iterator<T> keysInRange(T start, T end) {
        ArrayList<T> butter = new ArrayList<T>();
        if(start.compareTo(end)<0){
            keysInRange(start,end,root,butter);
        }
        return butter.iterator();
    }
    private void keysInRange(T start, T end, TreeNode<T> position, ArrayList<T> butter){
        if(position == null){return;}    
        if(start.compareTo(position.data) < 0){
            keysInRange(start, end, position.left, butter);
        }
        if(start.compareTo(position.data)<= 0 && end.compareTo(position.data) >= 0 ) {
            butter.add(position.data);
        }
        if(end.compareTo(position.data) > 0){
            keysInRange(start, end, position.right, butter);
        }  
    } 
    
    // PART 3
    /* remove the minimum TreeNode from the tree
    rooted at top. Return the removed TreeNode
    */
    protected TreeNode<T> deleteMin(TreeNode<T> top, TreeNode<T> parentOfTop) {
        // do not remove these two lines. They are intended to help you debug by
        // checking pre-conditions on deleteMin
        if (parentOfTop == null) throw new IllegalArgumentException("deleteMin should not be called on a null parent");
        if (parentOfTop.isLeaf()) throw new IllegalArgumentException("deleteMin should not be called with a parent that is a leaf");
        
        TreeNode<T> pos = top;
        
        while(pos.left != null|| pos.right != null){
            if(pos.left != null){
                pos = pos.left;
            }else{ pos = pos.right;}
        }
        
        //updates the parent node and sets new child node to null
        updateParent(pos, null);

        return pos;
    }
    
	@Override
    public boolean remove(T e) {
        TreeNode<T> nodeSacrifice = find(e,root);
        TreeNode<T> mom = null;
        
        if(nodeSacrifice == null){return false;}
        
        mom  = getParent(nodeSacrifice);
        
        //issa leaf to sacrifice 
        if(nodeSacrifice.isLeaf()){
            if(mom == null){
                root = null;
            }else{
                if(mom.left == nodeSacrifice){mom.left = null;}
                else{mom.right = null;}
            }
        }else{
            //Sacrificed node has right child
            if(nodeSacrifice.left == null && nodeSacrifice.right != null){
                if(mom == null){
                    root = nodeSacrifice.right;
                }else{
                    if(mom.left == nodeSacrifice){
                        mom.left = nodeSacrifice.right;
                    }else{mom.right = nodeSacrifice.right;}
                }
            // Sacrificed node has a left child
            }else if( nodeSacrifice.left != null && nodeSacrifice.right == null){
                if(mom == null){
                    root = nodeSacrifice.left;
                }else{
                    if(mom.left == nodeSacrifice){
                        mom.left = nodeSacrifice.left;
                    }else{
                        mom.left = nodeSacrifice.right;
                    }
                }
            }else{      // sacrificed node has two children they will leave behind
                TreeNode <T> tLeft = nodeSacrifice.left;    // two temp nodes and a replacement node
                TreeNode <T> tRight = nodeSacrifice.right;
                TreeNode <T> newNode = deleteMin(nodeSacrifice.right,nodeSacrifice);
                
                newNode.left = tLeft;
                if(tRight != newNode){
                    newNode.right = tRight;
                }else{
                    newNode.right = null;
                }
                
                if(mom == null){
                    root = newNode;
                }else{
                    if(mom.left == nodeSacrifice){
                        mom.left = newNode;
                    }else{
                        mom.right = newNode;
                    }
                }
                
            }
        }
        return true;
    }
    //helper function to find the correct node
    private TreeNode<T> find(T e, TreeNode<T> node){
        if (node == null){
            return null;
        }else if (e.compareTo(node.data) == 0){
            return node;
        }else if (e.compareTo(node.data) < 0){
            return find(e, node.left);
        }else{
            return find(e, node.right);
        }
    }
    /* Takes the existing child of the parent to replace with the new child	

    null is a valid argument for newChild but not oldChild

    example:
    BEFORE
    parent
         \
          oldChild

    AFTER
    parent
        \
        newChild

    example:
    BEFORE
        parent
        /
    oldChild

    AFTER
        parent
         /
    newChild
    */
    protected void updateParent(TreeNode<T> oldChild, TreeNode newChild) {
        TreeNode<T> parent = getParent(oldChild);
        if (parent == null) {
            root = newChild;
            return;
        }

        if (oldChild.data.compareTo(parent.data) > 0) 
            parent.right = newChild;
        else if (oldChild.data.compareTo(parent.data) < 0) {
            parent.left = newChild;
        } else {
            throw new IllegalStateException("duplicate elements in tree");
        }
    }

    protected TreeNode<T> getParent(TreeNode<T> child) {
        if (child == null) throw new IllegalArgumentException("child should not be null");
        // put the special case for child is root here so that
        // we can use the == case to check for errors in the helper method
        if (child.data.compareTo(root.data) == 0) return null;

        return getParentHelper(root, child);
    }

    private TreeNode<T> getParentHelper(TreeNode<T> current, TreeNode<T> child) {
        if (child.data.compareTo(current.data) < 0) {
            if (child.data.compareTo(current.left.data) == 0) {
                // found the child, so current is its parent
                return current;
            } else {
                return getParentHelper(current.left, child);
            }
        } else if (child.data.compareTo(current.data) > 0) {
            if (child.data.compareTo(current.right.data) == 0) {
                // found the child, so current is its parent
                return current;
            } else {
                return getParentHelper(current.right, child);
            }
        } else {
            throw new IllegalArgumentException("child is not in the tree");
        }
    }

    protected static int getHeight(TreeNode current) {
        if (current == null) {
            return 0;
        }
        return current.height;
    }

    public void updateHeightWholeTree() {
        updateHeightWholeTreeHelper(root);
    }

    private void updateHeightWholeTreeHelper(TreeNode current) {
        if (current==null) return;
        if (current.left!=null) updateHeightWholeTreeHelper(current.left);
        if (current.right!=null) updateHeightWholeTreeHelper(current.right);
        current.height = Math.max(getHeight(current.right), getHeight(current.left)) + 1;
    }

    /* Update the height attribute of all TreeNodes 
    on the path to the data
    */
    public void updateHeight(T data) {
        if (root != null) {
            updateHeightHelper(root, data);
        }
    }

    private void updateHeightHelper(TreeNode current, T data) {
        
        if (current.data.compareTo(data) != 0) {
            if (current.data.compareTo(data) > 0 && current.left != null) {
                updateHeightHelper(current.left, data);
            } else if (current.data.compareTo(data) < 0 && current.right != null) {
                updateHeightHelper(current.right, data);
            }
        }

        if (getHeight(current.right) == 0 && getHeight(current.left) == 0) {
            current.height = 1;
        } else {
            current.height = Math.max(getHeight(current.right), getHeight(current.left)) + 1;
        }
    }

    protected static boolean isBalanced(TreeNode current) {
        return ((Math.abs(getHeight(current.right) - getHeight(current.left)) < 2));
    }

    //////////////// Dont edit after here //////////////////////

    public boolean isEmpty() {
        return (root == null);
    }

    public void inorder() {
        inorder(root);
    }

    private void inorder(TreeNode current) {
        if (current == null) {
            return;
        }
        inorder(current.left);
        System.out.println(" " + current.data);
        inorder(current.right);
    }

    public void displayTree() {
        Stack<TreeNode> globalStack = new Stack<>();
        globalStack.push(root);
        int emptyLeaf = 32;
        boolean isRowEmpty = false;
        System.out.println("****..................................................................................................................................****");
        while (isRowEmpty == false) {
            Stack<TreeNode> localStack = new Stack<>();
            isRowEmpty = true;

            for (int j = 0; j < emptyLeaf; j++) {
                System.out.print("  ");
            }

            while (globalStack.isEmpty() == false) {
                TreeNode temp = globalStack.pop();
                if (temp != null) {
                    System.out.print(temp.data);
                    localStack.push(temp.left);
                    localStack.push(temp.right);
                    if (temp.left != null || temp.right != null) {
                        isRowEmpty = false;
                    }
                } else {
                    System.out.print("--");
                    localStack.push(null);
                    localStack.push(null);
                }

                for (int j = 0; j < emptyLeaf * 2 - 2; j++) {
                    System.out.print("  ");
                }
            }
            System.out.println();
            emptyLeaf /= 2;
            while (localStack.isEmpty() == false) {
                globalStack.push(localStack.pop());
            }

        }

        System.out.println("****..................................................................................................................................****");
    }

    public Object[] toArray() {
        Object[] r = new Object[size];
        if (root == null) {
            return r;
        }

        // traverse the tree to visit all nodes,
        // adding them to r
        List<TreeNode> frontier = new LinkedList<>();
        frontier.add(root);
        int soFar = 0;

        while (frontier.size() > 0) {
            TreeNode v = (TreeNode) frontier.get(0);
            r[soFar] = v.data;
            soFar++;

            if (v.left != null) {
                frontier.add(v.left);
            }
            if (v.right != null) {
                frontier.add(v.right);
            }

            frontier.remove(0);
        }
        return r;
    }

}
