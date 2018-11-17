
public class Estimator {
	private Node from;
	private Node to;
	private double actual_distance;
	private double heuristic_estimate;
	
	public Estimator(Node u, Node v, double g, double h) {
		from = u;
		to = v;
		actual_distance = g;
		heuristic_estimate = h;
	}
	
	public double getCost() {
		return actual_distance + heuristic_estimate;
	}
}
