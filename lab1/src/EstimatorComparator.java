import java.util.Comparator;


public class EstimatorComparator implements Comparator<Estimator> { 
	
	public int compare(Estimator a, Estimator b) {
		if (a.equals(b)) return 0;
		else if (a.getCost() < b.getCost()) return -1;
		else return 1;
	}
}
