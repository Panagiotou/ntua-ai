
public class Estimator implements Comparable {
	public Node from;
	public double actual_distance;
	public Pair heuristic_estimate;

	public Estimator(Node u, double g, Pair h) {
		from = u;
		actual_distance = g;
		heuristic_estimate = h;
	}

	public double getCost() {
		return actual_distance + heuristic_estimate.second;
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0.getClass() != getClass()) return -1;
		Estimator other = (Estimator) arg0;
		if (getCost() == other.getCost() && from.equals(other.from)) return 0;
		else if (getCost() < other.getCost()) return -1;
		else return 1;
	}

	public int hashCode() {
		return from.hashCode();
	}

	public boolean equals(Object o) {
		if (getClass() != o.getClass()) return false;
		else {
			Estimator tmp = (Estimator) o;
			return from.equals(tmp.from);
		}
	}
}
