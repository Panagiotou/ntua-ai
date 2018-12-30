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

	public Point(double X, double Y) {
		x = X;
		y = Y;
	}

	public double pNorm(Point other, int p) {
		if (p == 1) {
			return Math.abs(x - other.x) + Math.abs(y - other.y);
		}
		else if (p == 2) {
			return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		}
		else if (p != Integer.MAX_VALUE) return nthRoot(Math.pow(x - other.x, p) + Math.pow(y - other.y, p), p);
		else {
			double dx = Math.abs(x - other.x);
			double dy = Math.abs(y - other.y);
			return Math.max(dx, dy);
		}
	}
	
	  public double haversine(Point p) {
		  // haversine distance calclulation
		  
		  double lon1 = p.x;
		  double lat1 = p.y;
		  double lon2 = x;
		  double lat2 = y;
		  double theta = lon1 - lon2;
		
		  double distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
				  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
		  
		  return Math.toDegrees(Math.acos(distance)) * 111.18957696;
		  
	  }
	  

    public void printPoint(){
        System.out.println("x, y = " + String.valueOf(x) +" "+ String.valueOf(y));
        System.out.println("rhodeID =  " + String.valueOf(rhodeId));
        System.out.println("rhodename =  " + rhodeName);
    }

		public int hashCode() {
			String tmp = String.valueOf(x) + String.valueOf(y);
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
