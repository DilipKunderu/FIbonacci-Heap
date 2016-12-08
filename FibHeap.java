public class FibHeap {
    int sizeOfHeap; // number of nodes
    Node maxNode;

    public FibHeap (Node maxNode, int size) {
        this.maxNode = maxNode;
        this.sizeOfHeap = size;
    }
    
    public FibHeap (Node node) {
        this.maxNode = node;
        this.sizeOfHeap = 1;
    }
    
    public FibHeap() {
        this.maxNode = null;
        sizeOfHeap = 0;
    }

    public boolean isEmpty() { // true if heap is empty
        return (maxNode == null);
    }

    public void insert(Node node) {
        if (maxNode != null) {
            node.node_left = maxNode;
            node.node_right = maxNode.node_right;
            maxNode.node_right = node;
            node.node_right.node_left = node;

            if (node.frequency > maxNode.frequency) // set the new max node            
                maxNode = node;
        } else maxNode = node; // this line reached when the heap is empty
        sizeOfHeap++;
    }

    public Node increaseKey(Node node, int newKey) {
        Node parent = null;
        node.frequency = newKey;
        parent = node.node_parent;

        if ((parent != null) && (node.frequency > parent.frequency)) { /*cuts called when new frequency 
                                                                        is greater than parent's*/
            cut(node, parent);
            cascadingCut(parent);
        }

        if (node.frequency > maxNode.frequency) //Setting the maxNode          
            maxNode = node;
        return node;
    }

    /*Called to get the current max Node; used to extract the string associated*/
    public Node extractMax() {
        Node stubNode = maxNode;
        if (stubNode != null) {
            Node child = stubNode.node_child;
            int childCount = stubNode.degree;
            Node temp;
            
            while (childCount > 0) {
                temp = child.node_right;
                
                child.node_left.node_right = child.node_right; //removing from child list
                child.node_right.node_left = child.node_left;
                
                child.node_left = maxNode; //adding to root list
                child.node_right = maxNode.node_right;
                maxNode.node_right = child;
                child.node_right.node_left = child;
                
                child.node_parent = null;   //setting parent to null
                child.marked = false;
                child = temp;
                childCount--;
            }
            
            stubNode.node_left.node_right = stubNode.node_right;    //removing from the list
            stubNode.node_right.node_left = stubNode.node_left;

            if (stubNode == stubNode.node_right) 
                maxNode = null;
            else {
                maxNode = stubNode.node_right;
                meld();   //heap unification ie., pairwise combine
            }
            stubNode.node_right = stubNode;
            stubNode.node_left = stubNode;
            stubNode.degree = 0;
            stubNode.node_child = null;
            stubNode.node_parent = null;
            sizeOfHeap--;
        }
        return stubNode;
    }

    /*Cut is called to remove the legacy child node with the new frequency and re-introduce it in roots' circular linked list*/
    public void cut(Node child, Node parent) {
        
        child.node_left.node_right = child.node_right; //remove child
        child.node_right.node_left = child.node_left;
        parent.degree--;
        
        if (parent.node_child == child)        //adjust the heap pointers of parent
            parent.node_child = child.node_right;
        
        if (parent.degree == 0) parent.node_child = null;
            
        child.node_left = maxNode; //addition to root linked list
        child.node_right = maxNode.node_right;
        maxNode.node_right = child;
        child.node_right.node_left = child;
        child.node_parent = null;
        child.marked = false;     //The node has had a single child cut
    }
    
    /*Marking is done to store legacy of a node so as to preserve the properties of the heap.
    
    Value is 
    ---> false when no children have been cut from the node
    ---> true when a single child has been cut
    ---> invalid(irrelavant) when the other child of a marked node is cut ie., cut of a marked node makes it eligible
         to join the root list
    ---> invalid(irrelavant) for root nodes
    */

    /*This is a recurring cut, which terminates when a non-marked ie., marked == false parent is
    detected
    
    Cascading stops when a marked node is reached*/
    public void cascadingCut(Node child) {
        Node parent = child.node_parent;
        if (parent != null) {
            if (!child.marked) child.marked = true;
            else {
                cut(child, parent);
                cascadingCut(parent);
            }
        }
    }

   /* pair-wise combination of trees whose nodes are of the same degree,
    iterating over the nodes in the circular linked list of all the nodes
    
    ---> the cut node is un-marked*/
    public void meld() {               
        int n = 0;   // Storing the number of root nodes
        int size = sizeOfHeap + 1;   
        Node[] rootsList = new Node[size]; 
        
        for (int i = 0; i < size; i++) 
            rootsList[i] = null;

        Node currNode = maxNode;
        if (!(currNode == null)) {
            n++;
            currNode = currNode.node_left;
            while (currNode != maxNode) {
                n++;
                currNode = currNode.node_left;
            }
        }

        while (n > 0) {
            int d = currNode.degree;
            Node next = currNode.node_left;
            while (rootsList[d] != null) { //detecting a same degree root node
                Node listNode = rootsList[d];
                if (rootsList[d] == currNode) {
                    break;
                }
                if (currNode.frequency < listNode.frequency) {
                    Node temp = listNode;
                    listNode = currNode;
                    currNode = temp;
                }

                listNode.node_left.node_right = listNode.node_right; //removal
                listNode.node_right.node_left = listNode.node_left;
                listNode.node_parent = currNode;
                
                if (currNode.node_child == null) {
                    currNode.node_child = listNode;
                    listNode.node_right = listNode;
                    listNode.node_left = listNode;
                } else {
                    Node childNode = currNode.node_child;
                    listNode.node_left = childNode;
                    listNode.node_right = childNode.node_right;
                    childNode.node_right.node_left = listNode;
                    childNode.node_right = listNode;
//                    currNode.node_child.node_right = listNode;
//                    listNode.node_right.node_left = listNode;
                }
                //Increment the degree of the currNode
                currNode.degree++;
                //Un-marking the cut children
                listNode.marked = false;

                rootsList[d] = null;
                d++;
            }
            rootsList[d] = currNode;
            currNode = next;
            n--;
        }
        
        maxNode = null;
        
        for (int i = 0; i < size; i++) {    //updating the list of the root nodes in heap
            if (rootsList[i] != null) {
                if (maxNode != null) {
                    rootsList[i].node_left.node_right = rootsList[i].node_right;
                    rootsList[i].node_right.node_left = rootsList[i].node_left;
                    rootsList[i].node_left = maxNode;
                    rootsList[i].node_right = maxNode.node_right;
                    maxNode.node_right.node_left = rootsList[i];
                    maxNode.node_right = rootsList[i];
                    if (rootsList[i].frequency > maxNode.frequency) 
                        maxNode = rootsList[i];        
                }else maxNode = rootsList[i];            
            }
        }
    }
}
