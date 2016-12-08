public class Node {
    String s; /* while redundant, appending the associated string ie.,the key in the map,
    helps avoid NullPointerExceptions in a brute force-esque way*/ 
    int frequency, degree;
    boolean marked;
    Node node_child,node_right, node_left, node_parent;																		
	
    public Node() {
        frequency = 0;
	}
	
    public Node(int key) {
        this.frequency = key;
	node_right = this;
	node_left = this;
    }

    public Node(int key, String hashtag) {
	    this.frequency = key;
	    this.s = hashtag;
//        this.node_parent = null;
//        this.node_child = null;
        node_right = this;
	    node_left = this;
//        degree = 0;
    }
	
    public final int getFrequency() {
	return frequency;
    }
	
    public final String getHashtagString() {
	return s;
    }

}
