import java.util.Comparator;


public class EstimatorComparator implements Comparator<Estimator> {

	public int compare(Estimator a, Estimator b) {
		return Double.compare(a.getCost(), b.getCost());
	}
}
