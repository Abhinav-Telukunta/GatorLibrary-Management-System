import java.io.*;
import java.util.*;

// RedBlackTree Node class which stores information about required properties such as bookId, bookName, reservations , color etc.

class RedBlackTreeNode {
    int bookId;
    String bookName;
    String authorName;
    String availabilityStatus;
    int borrowedBy;
    MinHeap reservationHeap;
    RedBlackTreeNode left;
    RedBlackTreeNode right;
    RedBlackTreeNode parent;
    String color;

    public RedBlackTreeNode(int bookId,String bookName,String authorName,String availabilityStatus) {
        this.bookId=bookId;
        this.bookName=bookName;
        this.authorName=authorName;
        this.availabilityStatus=availabilityStatus;
        this.borrowedBy=-1;
        this.reservationHeap=null;
        this.left=null;
        this.right=null;
        this.parent=null;
        this.color="";
    }
}

// Red Black Tree class which contains all the functions for operations on it such as insert, delete, etc.

class RedBlackTree {

    String RED = "RED";
    String BLACK = "BLACK";
    int colorFlips=0;
    Map<Integer,String>oldColorMap=new HashMap<>();

    RedBlackTreeNode root; // root node of red black tree

    public RedBlackTree() {
      root = null; // initially root is null
    }

    // function to search a node in Red Black tree
    public RedBlackTreeNode search(int bookId) {
        RedBlackTreeNode node = root;
        while (node != null) {
            if (bookId == node.bookId) {
              return node;
            } else if (bookId < node.bookId) {
              node = node.left;
            } else {
              node = node.right;
            }
        }
        return null;
    }

    // function to perform range search on red black tree from low to high bookId's
    public List<RedBlackTreeNode> rangeSearch(int lowBookId, int highBookId){
        RedBlackTreeNode node = root;
        List<RedBlackTreeNode> arr = new ArrayList<>();
        rangeSearchHelper(arr,node,lowBookId,highBookId);
        return arr;
    }

    // search helper based on low and high bookId range
    public void rangeSearchHelper(List<RedBlackTreeNode>arr,RedBlackTreeNode node,int lowBookId,int highBookId){
        if(node==null) return;
        if(lowBookId<node.bookId) rangeSearchHelper(arr,node.left,lowBookId,highBookId);
        if(lowBookId<=node.bookId && highBookId>=node.bookId) arr.add(node);
        rangeSearchHelper(arr, node.right, lowBookId, highBookId);
    }

    // function to find closest bookId for given target
    // it returns the nodes whose bookId's have minimum difference from target bookId
    public List<RedBlackTreeNode> findClosest(int targetBookId){
        List<RedBlackTreeNode> arr = new ArrayList<>();
        inorder(arr,root);
        int min_diff=Integer.MAX_VALUE;
        for(RedBlackTreeNode node:arr){
          min_diff=Math.min(min_diff,Math.abs(node.bookId-targetBookId));
        }
        List<RedBlackTreeNode>result=new ArrayList<>();
        for(RedBlackTreeNode node: arr){
          if(Math.abs(node.bookId-targetBookId)==min_diff) result.add(node);
        }
        return result;
    }

    // inorder traversal of red black tree
    public void inorder(List<RedBlackTreeNode> arr, RedBlackTreeNode root){
        if(root==null) return;
        inorder(arr,root.left);
        arr.add(root);
        inorder(arr,root.right);
    }

    // if book is available, borrow it, otherwise create reservation heap and insert the patron
    public boolean borrowBook(int patronId, int bookId, int patronPriority){
        RedBlackTreeNode node=search(bookId);
        if(node.availabilityStatus.equals("Yes")){
            node.availabilityStatus="No";
            node.borrowedBy=patronId;
            return true;
        }
        if(node.reservationHeap==null) node.reservationHeap=new MinHeap(20);
        MinHeapNode mhNode=new MinHeapNode(patronId, patronPriority, System.currentTimeMillis());
        node.reservationHeap.insert(mhNode);
        return false;
    }

    // change availability to yes and if reservation heap is not empty, allocate that book to top patron in min heap
    public int returnBook(int patronId,int bookId){
        RedBlackTreeNode node=search(bookId);
        node.availabilityStatus="Yes";
        node.borrowedBy=-1;
        if(node.reservationHeap==null) return -1;
        MinHeapNode minNode = node.reservationHeap.removeMin();
        node.availabilityStatus="No";
        node.borrowedBy=minNode.patronId;
        return minNode.patronId;
    }

    // function which returns color flip count
    public int colorFlipCount(){
        return colorFlips;
    }

    // function to count the color flips after every insert and delete operation
    // it basically compares two maps (old color map and new color map)
    // old color map is the colors of all nodes before insert or delete operation
    // new color map is the colors of all nodes after insert or delete operation
    // it iterates and checks the oldcolor map nodes and if the color is changed in new color map, it increments count of color flips
    // finally oldcolormap becomes new color map for further iterations
    public void updateColorFlips(){
        List<RedBlackTreeNode>arr=new ArrayList<>();
        inorder(arr,root);
        Map<Integer,String>newColorMap=new HashMap<>();
        for(RedBlackTreeNode node:arr){
            newColorMap.put(node.bookId,node.color);
        }
        for(Map.Entry<Integer,String>entry:oldColorMap.entrySet()){
            String oldColor=entry.getValue();
            if(newColorMap.get(entry.getKey())!=null){
                String newColor=newColorMap.get(entry.getKey());
                if(!oldColor.equals(newColor)) colorFlips++;
            }
        }
        oldColorMap.clear();
        oldColorMap.putAll(newColorMap);
    }

    // Funtion to insert a value in the node
    public void insert(int bookId, String bookName, String authorName, String availabilityStatus) {
        RedBlackTreeNode node = root;
        RedBlackTreeNode parent = null;
        while (node != null) {
            parent = node;
            if (bookId < node.bookId) {
              node = node.left;
            } else if (bookId > node.bookId) {
              node = node.right;
            }
        }
        // Inserting the new node
        RedBlackTreeNode newNode = new RedBlackTreeNode(bookId,bookName,authorName,availabilityStatus);
        newNode.color = RED;
        if (parent == null) {
          root = newNode;
        } else if (bookId < parent.bookId) {
          parent.left = newNode;
        } else {
          parent.right = newNode;
        }
        newNode.parent = parent;

        fixRBTPropertiesAfterInsert(newNode);
    }

    // Find Maximum in left sub tree for inorder predecessor
    public RedBlackTreeNode findMaximum(RedBlackTreeNode node) {
        while (node.right != null) {
          node = node.right;
        }
        return node;
    }

    // Function to fix the RedBlack Tree after the insert operation
    public void fixRBTPropertiesAfterInsert(RedBlackTreeNode node) {
        RedBlackTreeNode parent = node.parent;

        // If parent is null, we are inserting first node which is root, so color is black and return
        if (parent == null) {
          node.color = BLACK;
          return;
        }

        // If parent is black return
        if (parent.color.equals(BLACK)) {
          return;
        }

        // 2 consecutive reds
        RedBlackTreeNode grandparent = parent.parent;

        // If no grandparent, parent is root
        if (grandparent == null) {
          parent.color = BLACK;
          return;
        }

        RedBlackTreeNode uncle = getRBTUncle(parent);
        if (uncle != null && uncle.color.equals(RED)) {
          uncleRed(parent, grandparent, uncle);
        }

        // Grandparent left child is parent
        else if (parent == grandparent.left) {
          grandParentLeftChildParent(node, parent, grandparent);

        }

        // Grandparent right child is parent
        else {
          grandParentRightChildParent(node, parent, grandparent);

        }
    }

    public void uncleRed(RedBlackTreeNode parent, RedBlackTreeNode grandparent, RedBlackTreeNode uncle) {

        // Uncle is red: recolor parent, uncle and grandparent
        parent.color = BLACK;
        uncle.color = BLACK;
        grandparent.color = RED;
        

        // call it recursively on grandparent to further fix 2 consecutive reds
        fixRBTPropertiesAfterInsert(grandparent);

    }

    public void grandParentLeftChildParent(RedBlackTreeNode node, RedBlackTreeNode parent, RedBlackTreeNode grandparent) {
        // Node is left->right inner child of its grandparent and uncle is black or null

        if (node == parent.right) {
          leftRotate(parent);

          // Point "parent" to the new root node of the rotated sub-tree.
          parent = node;
        }

        // Node is left->left outer child of its grandparent and uncle is black or null
        rightRotate(grandparent);

        // Recoloring parent and grandparent
        parent.color = BLACK;
        grandparent.color = RED;

    }

    public void grandParentRightChildParent(RedBlackTreeNode node, RedBlackTreeNode parent, RedBlackTreeNode grandparent) {
        // Uncle is black and node is inner child of its grandparent(right->left)

        if (node == parent.left) {
          rightRotate(parent);

          // Point "parent" to the new root node of the rotated sub-tree.
          parent = node;
        }

        // Node is right->right outer child of its grandparent and uncle is black

        leftRotate(grandparent);

        // Recoloring parent and grandparent
        parent.color = BLACK;
        grandparent.color = RED;
    }

    // function which gets the uncle of a node (node's grandparent another child)
    public RedBlackTreeNode getRBTUncle(RedBlackTreeNode parent) {
        RedBlackTreeNode grandparent = parent.parent;
        if (grandparent.left == parent) {
          return grandparent.right;
        } else if (grandparent.right == parent) {
          return grandparent.left;
        } else {
            return null;
        }
    }

    // Function to delete
    public String delete(int bookId) {
        RedBlackTreeNode node = search(bookId); // search the node with that bookId

        // If node is not found
        if (node == null) {
          return "-1";
        }
        // stores the patronId's and returns them
        String patronsList = node.reservationHeap==null?"-1":node.reservationHeap.printHeap();

        RedBlackTreeNode movedUpNode;
        String deletedNodeColor;

        // If the node has one or zero child
        if (node.left == null || node.right == null) {
          movedUpNode = deleteZeroOrOneChildNode(node);
          deletedNodeColor = node.color;
          if(movedUpNode!=null && movedUpNode.color.equals(RED)){ // if moved up node color is red, simply recolor it to black
            movedUpNode.color = BLACK;
            return patronsList;
          }
        }

        // For node with two children
        else {
          // Left subtree maximum node
          RedBlackTreeNode inOrderPredecessor = findMaximum(node.left);

          // Copy the data and color remains same
          clone(node,inOrderPredecessor);

          // The predecessor is deleted
          movedUpNode = deleteZeroOrOneChildNode(inOrderPredecessor);
          deletedNodeColor = inOrderPredecessor.color;
        }

        if (deletedNodeColor.equals(BLACK)) {
          fixRedBlackPropertiesAfterDelete(movedUpNode);

          // The temporary NIL node is removed
          if (movedUpNode.getClass() == NilNode.class) {
            replaceRBTParentsChild(movedUpNode.parent, movedUpNode, null);
          }
        }
        return patronsList;
    }
    // function to copy all the contents of one node to another (called when inorder successor needs to be deleted)
    public void clone(RedBlackTreeNode node1, RedBlackTreeNode node2){
        node1.bookId=node2.bookId;
        node1.bookName=node2.bookName;
        node1.authorName=node2.authorName;
        node1.availabilityStatus=node2.availabilityStatus;
        node1.borrowedBy=node2.borrowedBy;
        node1.reservationHeap=node2.reservationHeap;
    }

    public RedBlackTreeNode deleteZeroOrOneChildNode(RedBlackTreeNode node) {
        // Replace it with left child if the node has only left child
        if (node.left != null) {
          replaceRBTParentsChild(node.parent, node, node.left);
          return node.left;
        }

        // Replace it with right child if the node has only right child
        else if (node.right != null) {
          replaceRBTParentsChild(node.parent, node, node.right);
          return node.right;
        }

        // If node is red and has no children remove it else if it's black replace it by nil
        else {
          RedBlackTreeNode newChild = node.color.equals(BLACK) ? new NilNode() : null;
          replaceRBTParentsChild(node.parent, node, newChild);
          return newChild;
        }
    }

    public void fixRedBlackPropertiesAfterDelete(RedBlackTreeNode node) {
        // Examined node is root, end of recursion
        if (node == root) return;

        RedBlackTreeNode sibling = getRBTNodeSibling(node);

        // Red sibling
        if (sibling.color.equals(RED)) {
          lookafterRedChild(node, sibling);
          sibling = getRBTNodeSibling(node);
        }

        // Two black children of black sibling
        if (checkNotRed(sibling.left) && checkNotRed(sibling.right)) {
          sibling.color = RED;

          // Black sibling: two black children & red parent
          if (node.parent.color.equals(RED)) {
            node.parent.color = BLACK;
          }

          // Black sibling: two black children & black parent
          else {
            fixRedBlackPropertiesAfterDelete(node.parent);
          }
        }

        // Black sibling with at least one red child
        else {
          fixAtLeastOneRedChildBlackSibling(node, sibling);
        }
    }

    public void lookafterRedChild(RedBlackTreeNode node, RedBlackTreeNode sibling) {
        // recolor
        sibling.color = BLACK;
        node.parent.color = RED;

        // rotate
        if (node == node.parent.left) {
          leftRotate(node.parent);
        } else {
          rightRotate(node.parent);
        }
    }

    public void fixAtLeastOneRedChildBlackSibling(RedBlackTreeNode node, RedBlackTreeNode sibling) {
        boolean nodeIsLeftChild = (node == node.parent.left);

        // Recolor sibling and its child, and rotate around sibling
        if ((nodeIsLeftChild && checkNotRed(sibling.right)) || (nodeIsLeftChild && !checkNotRed(sibling.right) && !checkNotRed(sibling.left))) {
          sibling.left.color = BLACK;
          sibling.color = RED;
          rightRotate(sibling);
          sibling = node.parent.right;
        } else if ((!nodeIsLeftChild && checkNotRed(sibling.left)) || (!nodeIsLeftChild && !checkNotRed(sibling.left) && !checkNotRed(sibling.right))) {
          sibling.right.color = BLACK;
          sibling.color = RED;
          leftRotate(sibling);
          sibling = node.parent.left;
        }

        // Rotate around parent and recolor sibling, parent, sibling's child
        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        if (nodeIsLeftChild) {
          sibling.right.color = BLACK;
          leftRotate(node.parent);
        } else {
          sibling.left.color = BLACK;
          rightRotate(node.parent);
        }
    }

    // function which gets the red black tree node's sibling (node's parent another child)
    public RedBlackTreeNode getRBTNodeSibling(RedBlackTreeNode node) {
        RedBlackTreeNode parent = node.parent;
        if (node == parent.left) {
          return parent.right;
        } else if (node == parent.right) {
          return parent.left;
        } else {
          throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }

    public boolean checkNotRed(RedBlackTreeNode node) {
      return node == null || node.color.equals(BLACK);
    }

    // NilNode class for temporary purpose in deletion algorithm (when deleted node is black leaf, we get NilNode)
    public class NilNode extends RedBlackTreeNode {
        private NilNode() {
          super(-1,"","","");
          this.color = BLACK;
        }
    }

    // function to replace parent's old child with new child (basically, parent's child pointer gets updated to new child instead of old)
    public void replaceRBTParentsChild(RedBlackTreeNode parent, RedBlackTreeNode oldChild, RedBlackTreeNode newChild) {
        if (parent == null) {
          root = newChild;
        } else if (parent.left == oldChild) {
          parent.left = newChild;
        } else if (parent.right == oldChild) {
          parent.right = newChild;
        } else {
          throw new IllegalStateException("Parent is not the child of the node");
        }

        if (newChild != null) {
          newChild.parent = parent;
        }
    }

    // rotate left function
    public void leftRotate(RedBlackTreeNode node) {
        RedBlackTreeNode parent = node.parent;
        RedBlackTreeNode rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
          rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceRBTParentsChild(parent, node, rightChild);
    }

    // rotate right function
    public void rightRotate(RedBlackTreeNode node) {
        RedBlackTreeNode parent = node.parent;
        RedBlackTreeNode leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
          leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceRBTParentsChild(parent, node, leftChild);
    }
}

//MinHeapNode class which stores information about patronId, patronPriority and timestamp 

class MinHeapNode{
    int patronId;
    int patronPriority;
    long timestamp;
    public MinHeapNode(int patronId,int patronPriority, long timestamp){
        this.patronId=patronId;
        this.patronPriority=patronPriority;
        this.timestamp=timestamp;
    }
}

// Min heap class for creating reservations for book based on patron priority and timestamps order

class MinHeap {
    private MinHeapNode[] heap; // array of min heap nodes ordered by patron priority and timestamp
    private int size;
    private int capacity;
    void swap(int i,int j){ // swap function to bubble up or down the heap
        MinHeapNode temp=heap[i];
        heap[i]=heap[j];
        heap[j]=temp;
    }
    void heapify(int index){ // heapify function to preserve min heap property
        int smallest=index;
        int left=2*index+1;
        int right=2*index+2;
        if(left<size && (heap[left].patronPriority<heap[smallest].patronPriority || (heap[left].patronPriority==heap[smallest].patronPriority && heap[left].timestamp<heap[smallest].timestamp))) smallest=left;
        if(right<size && (heap[right].patronPriority<heap[smallest].patronPriority || (heap[right].patronPriority==heap[smallest].patronPriority && heap[right].timestamp<heap[smallest].timestamp))) smallest=right;
        if(smallest!=index){
            swap(index,smallest); // swap the parent with child which has lower patron priority or lesser timestamp for breaking ties
            heapify(smallest);
        }
    }
    MinHeap(int capacity) {
        this.heap=new MinHeapNode[capacity]; // restrict the reservation heap limit to 20 (capacity = 20)
        this.size=0;
        this.capacity=capacity;
    }

    // function to remove minimum element from heap and heapify remaining elements
    MinHeapNode removeMin() {
        if(size==0) return null;
        MinHeapNode minNode=heap[0];
        swap(0,size-1);
        size--;
        heapify(0);
        return minNode;

    }

    // function to insert min heap node into min heap
    void insert(MinHeapNode newNode) {
        if(size==capacity) return;
        heap[size]=newNode;
        size++;
        int i=size-1;
        while(i>=0){ // bubble up heapify (go from insert point to root and swap if child is lesser than parent)
            int pIndex=(i-1)/2;
            MinHeapNode parent=heap[pIndex];
            MinHeapNode child=heap[i];
            if(parent.patronPriority>child.patronPriority){
                swap(pIndex,i);
                i=pIndex;
            }
            else if(parent.patronPriority==child.patronPriority && parent.timestamp>child.timestamp){
                swap(pIndex,i);
                i=pIndex;
            }
            else break;
        }
    }
    // return the contents of patronId's in heap 
    String printHeap(){
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        for(int i=0;i<size;++i){
          sb.append(heap[i].patronId);
          if(i!=size-1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
// main class - start point of program
public class gatorLibrary {
    static RedBlackTree rbTree=new RedBlackTree(); // create red black tree instance
    static List<String>outputData=new ArrayList<>(); // outputData arraylist which keeps track of the output after each operation
    static boolean isTerminate=false; // isTerminate flag for quit check
    // main function
    public static void main(String[] args) {
        try {
            String inputFileName=args[0]; // get the input file name from command line
            int inlen=inputFileName.length();
            String outputFileName=inputFileName.substring(0,inlen-4)+"_output_file.txt"; // output file name would be concatenation of input file name + "_output_file.txt"
            FileReader reader = new FileReader(inputFileName); 
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            List<String>inputData=new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) { // reading input text file and adding data to inputData list
                inputData.add(line);
            }
            reader.close();
            // for every input in inputData arraylist, identify the operation whether it is print or insert or delete and map to appropriate function using switch case
            for(String input:inputData){
                if(isTerminate) break;
                String operation = input.substring(0,input.indexOf("("));
                switch(operation){
                    case "PrintBook":
                        printBook(input);
                        break;
                    case "PrintBooks":
                        printBooks(input);
                        break;
                    case "InsertBook":
                        insertBook(input);
                        break;
                    case "BorrowBook":
                        borrowBook(input);
                        break;
                    case "ReturnBook":
                        returnBook(input);
                        break;
                    case "DeleteBook":
                        deleteBook(input);
                        break;
                    case "FindClosestBook":
                        findClosestBook(input);
                        break;
                    case "ColorFlipCount":
                        colorFlipCount();
                        break;
                    case "Quit":
                        quit();
                        break;

                }
            }
            writeToFile(outputFileName, outputData); // write the outputData arraylist content into output text file

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // function to write the output data arraylist to a text file
    public static void writeToFile(String filename, List<String>list){
        try {
            FileWriter writer = new FileWriter(filename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for(String str:list){
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // invokes the search(bookId) function on red black tree instance and outputs the data
    public static void printBook(String input){
        int bookId=Integer.parseInt(input.substring(input.indexOf("(") + 1, input.indexOf(")")));
        RedBlackTreeNode node = rbTree.search(bookId);
        if(node==null){
            outputData.add(String.format("Book %s not found in the Library",bookId)); // if node not found, output book not found
        }
        else{ // if node is found, output it's data
            outputData.add(String.format("BookID = %d",node.bookId));
            outputData.add(String.format("Title = \"%s\"",node.bookName));
            outputData.add(String.format("Author = \"%s\"",node.authorName));
            outputData.add(String.format("Availability = \"%s\"",node.availabilityStatus));
            outputData.add("BorrowedBy = "+(node.borrowedBy==-1?"None":node.borrowedBy));
            String reservationList = node.reservationHeap==null?"[]":node.reservationHeap.printHeap();
            outputData.add(String.format("Reservations = %s",reservationList));
        }
        outputData.add("");

    }
    // invokes the rangeSearch(lowBookId,highBookId) function on red black tree instance and outputs the data
    public static void printBooks(String input){
        String contents = input.substring(input.indexOf("(") + 1, input.indexOf(")"));
        String[] parts = contents.split(",\\s*");
        int lowBookId=Integer.parseInt(parts[0]);
        int highBookId=Integer.parseInt(parts[1]);
        List<RedBlackTreeNode>arr=rbTree.rangeSearch(lowBookId,highBookId);
        for(RedBlackTreeNode node:arr){
            outputData.add(String.format("BookID = %d",node.bookId));
            outputData.add(String.format("Title = \"%s\"",node.bookName));
            outputData.add(String.format("Author = \"%s\"",node.authorName));
            outputData.add(String.format("Availability = \"%s\"",node.availabilityStatus));
            outputData.add("BorrowedBy = "+(node.borrowedBy==-1?"None":node.borrowedBy));
            String reservationList = node.reservationHeap==null?"[]":node.reservationHeap.printHeap();
            outputData.add(String.format("Reservations = %s",reservationList));
            outputData.add("");
        }

    }
    // invokes the insert(bookId,bookName,authorName,availabilityStatus) function on red black tree instance
    public static void insertBook(String input){
        String contents = input.substring(input.indexOf("(") + 1, input.indexOf(")"));
        String[] parts = contents.split(",\\s*");
        int bookId = Integer.parseInt(parts[0]);
        String bookName=parts[1].substring(1,parts[1].length()-1),authorName=parts[2].substring(1,parts[2].length()-1),availabilityStatus=parts[3].substring(1,parts[3].length()-1);
        rbTree.insert(bookId,bookName,authorName,availabilityStatus);
        rbTree.updateColorFlips(); //  after insert operation, call updateColorFlips() function on red black tree to count color flip changes
    }
    // invokes the borrowBook(patronId,bookId,patronPriority) function on red black tree instance 
    public static void borrowBook(String input){
        String contents = input.substring(input.indexOf("(") + 1, input.indexOf(")"));
        String[] parts = contents.split(",\\s*");
        int patronId=Integer.parseInt(parts[0]);
        int bookId=Integer.parseInt(parts[1]);
        int patronPriority=Integer.parseInt(parts[2]);
        boolean isBorrowSuccess = rbTree.borrowBook(patronId,bookId,patronPriority);
        if(isBorrowSuccess) outputData.add(String.format("Book %d Borrowed by Patron %d",bookId,patronId)); // if borrow is successful, output borrowed
        else outputData.add(String.format("Book %d Reserved by Patron %d",bookId,patronId)); // if borrow is unsuccessful, output reserved
        outputData.add("");
    }
    // invokes the returnBook(patronId,bookId) function on red black tree instance 
    //and outputs details about allocation of that book to first patron in heap if reservation exists
    public static void returnBook(String input){
        String contents = input.substring(input.indexOf("(") + 1, input.indexOf(")"));
        String[] parts = contents.split(",\\s*");
        int patronId=Integer.parseInt(parts[0]);
        int bookId=Integer.parseInt(parts[1]);
        int patronId2 = rbTree.returnBook(patronId,bookId);
        outputData.add(String.format("Book %d Returned by Patron %d",bookId,patronId));
        if(patronId2!=-1) {
          outputData.add("");
          outputData.add(String.format("Book %d Allotted to Patron %d",bookId,patronId2));
        }
        outputData.add("");
    }
    // invokes the delete(bookId) function on red black tree instance and outputs the patron list if any reservations exist for it
    public static void deleteBook(String input){
      int bookId=Integer.parseInt(input.substring(input.indexOf("(") + 1, input.indexOf(")")));
      String patronsList = rbTree.delete(bookId);
      if(patronsList.equals("-1")){
        outputData.add(String.format("Book %d is no longer available.",bookId));
      }
      else{
        String patrons = patronsList.substring(1,patronsList.length()-1);
        int patronLen = patrons.split(",").length;
        if(patronLen == 1) outputData.add(String.format("Book %d is no longer available. Reservation made by Patron %s has been cancelled!",bookId,patrons));
        else outputData.add(String.format("Book %d is no longer available. Reservations made by Patrons %s have been cancelled!",bookId,patrons));
      }
      outputData.add("");
      rbTree.updateColorFlips(); // after delete operation, call updateColorFlips() function on red black tree to count color flip changes

    }
    // invokes the findClosest(bookId) function on red black tree instance and outputs it
    public static void findClosestBook(String input){
        int targetBookId=Integer.parseInt(input.substring(input.indexOf("(") + 1, input.indexOf(")")));
        List<RedBlackTreeNode>arr=rbTree.findClosest(targetBookId);
        for(RedBlackTreeNode node:arr){
          outputData.add(String.format("BookID = %d",node.bookId));
          outputData.add(String.format("Title = \"%s\"",node.bookName));
          outputData.add(String.format("Author = \"%s\"",node.authorName));
          outputData.add(String.format("Availability = \"%s\"",node.availabilityStatus));
          outputData.add("BorrowedBy = "+(node.borrowedBy==-1?"None":node.borrowedBy));
          String reservationList = node.reservationHeap==null?"[]":node.reservationHeap.printHeap();
          outputData.add(String.format("Reservations = %s",reservationList));
          outputData.add("");
        }
    }
    // invokes the colorFlipCount() function on red black tree instance and outputs it
    public static void colorFlipCount(){
        int count = rbTree.colorFlipCount();
        outputData.add(String.format("Colour Flip Count: %d",count));
        outputData.add("");    
    }
    // terminates the program on quit
    public static void quit(){
        outputData.add("Program Terminated!!");
        isTerminate=true;
    }

}
