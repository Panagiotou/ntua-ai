import java.util.Hashtable;
import java.util.Stack;

public class Solution {
	public Node goal;
	public Hashtable<Node, Stack<Node>> parent;
	
	public Solution(Node g, Hashtable<Node, Stack<Node>> p) {
		goal = g;
		parent = p;
	}
	
}