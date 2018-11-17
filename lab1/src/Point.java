// 2D Point Class
public class Point {
	public double x;
	public double y;
	
	public Point(double X, double Y) {
		x = X;
		y = Y;
	}
	
	public Point() {
		x = y = 0;
	}
	
	public double pNorm(Point other, int p) {
		return nthRoot(Math.pow(x - other.x, p) + Math.pow(y - other.y, p), p);
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
