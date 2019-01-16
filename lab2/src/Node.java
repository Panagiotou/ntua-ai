import java.util.ArrayList;
import java.util.Objects;

public class Node {
  public double x;
  public double y;

  public Node(double X, double Y) {
    x = X;
    y = Y;
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

}
