import java.util.Hashtable;
import java.util.Stack;

public class Solution {
	public Node goal;
	public Hashtable<Node, Stack<Estimator>> parent;
	
	public Solution(Node g, Hashtable<Node, Stack<Estimator>> p) {
		goal = g;
		parent = p;
	}
	
}