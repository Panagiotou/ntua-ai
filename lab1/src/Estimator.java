
public class Estimator implements Comparable {
	public Node from;
	public Node to;
	public double actual_distance;
	public double heuristic_estimate;
	
	public Estimator(Node u, double g, double h) {
		from = u;
		actual_distance = g;
		heuristic_estimate = h;
	}
	
	public double getCost() {
		return actual_distance + heuristic_estimate;
	}
	
	@Override
	public int compareTo(Object arg0) {
		if (arg0.getClass() != getClass()) return -1;
		Estimator other = (Estimator) arg0;
		if (getCost() < other.getCost()) return -1;
		else return 1;
	}
}
