// 2D Point Class
import java.util.Objects;

public class Point {
	public double x;
	public double y;
	public int rhodeId;
        public String rhodeName;
        public int id;

	public Point(double X, double Y, int rId, String rN, int Id) {
		x = X;
		y = Y;
                rhodeId = rId;
                rhodeName = rN;
                id = Id;
	}

	public Point() {
		x = y = 0;
	}

	public double pNorm(Point other, int p) {
		return nthRoot(Math.pow(x - other.x, p) + Math.pow(y - other.y, p), p);
	}

    public void printPoint(){
        System.out.println("x, y = " + String.valueOf(x) +" "+ String.valueOf(y));
        System.out.println("rhodeID =  " + String.valueOf(rhodeId));
        System.out.println("rhodename =  " + rhodeName);
    }

		public int hashCode() {
			Double tmp = new Double(x + y);
			return tmp.hashCode();
		}
		public boolean equals(Object other) {
    	if (getClass() != other.getClass()) return false;
    	else {
    		Point tmp = (Point) other;
    		return Objects.equals(x, tmp.x) && Objects.equals(y,  tmp.y);

    	}
    }
    public static double nthRoot(double A, int N) {

        double xPre = Math.random() % 10;
        double eps = 0.001;

        double delX = 2147483647;

        double xK = 0.0;

        while (delX > eps)
        {
            xK = ((N - 1.0) * xPre +
            (double)A / Math.pow(xPre, N - 1)) / (double)N;
            delX = Math.abs(xK - xPre);
            xPre = xK;
        }

        return xK;
    }

}
