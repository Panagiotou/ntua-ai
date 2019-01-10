import java.util.ArrayList;
import java.util.Objects;

public class Node{
  public double x;
  public double y;
  public int numOfNeighbors;
  public ArrayList<Edge> adjacent;

  public Node(double X, double Y) {
    x = X;
    y = Y;
    numOfNeighbors = 0;
    adjacent = new ArrayList<Edge>();
  }

  public Node() {
    x = 0;
    y = 0;
    numOfNeighbors = 0;
    adjacent = new ArrayList<Edge>();
  }

  public double pNorm(Node other, int p) {
	  	if (p == 1) {
	  		return Math.abs(x - other.x) + Math.abs(y - other.y);
	  	}
	  	else if (p == 2) {
			return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		}
		else return nthRoot(Math.pow(Math.abs(x - other.x), p) + Math.pow(Math.abs(y - other.y), p), p);
  }
  public int hashCode() {
    String tmp = String.valueOf(x) + String.valueOf(y);
    return tmp.hashCode();
  }
  public boolean equals(Object other) {
    if (getClass() != other.getClass()) return false;
    else {
      Node tmp = (Node) other;
      return Objects.equals(x, tmp.x) && Objects.equals(y,  tmp.y);

    }
  }
  public void printNode(){
    System.out.println("Node (x,y) = ("+String.valueOf(x) +","+ String.valueOf(y)  +")");
    if( !adjacent.isEmpty()){
      System.out.println("This Node has #neighbors = " + String.valueOf(numOfNeighbors));
      for (Edge e : adjacent) {
        e.printEdge();
      }
    }
    else{
      System.out.println("This Node has no Neighbors");
    }
  }

  public String toString(){
    return "(x,y) = ("+String.valueOf(x) +","+ String.valueOf(y)  +")";
  }


  public double haversine(Node p) {
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
