import java.util.ArrayList;
import java.util.Objects;

public class Node{
        public double x;
	public double y;
  public int numOfNeighbors;
	public ArrayList<Edge> adjacent;

        public double pNorm(Node other, int p) {
		return nthRoot(Math.pow(x - other.x, p) + Math.pow(y - other.y, p), p);
	}
        public int hashCode() {
      Double tmp = new Double(x+y);
			return tmp.hashCode();
		}
    public boolean equals(Object other) {
    	if (getClass() != other.getClass()) return false;
    	else {
    		Node tmp = (Node) other;
    		return Objects.equals(x, tmp.x) && Objects.equals(y,  tmp.y);

    	}
    }
    public void setAdj(ArrayList<Edge> adj){
        ArrayList<Edge> adja = new ArrayList<Edge>(adj);
        adjacent = adja;
    }
    public void printNode(Node n){
        System.out.println("Node (x,y) = ("+String.valueOf(n.x) +","+ String.valueOf(n.y)  +")");
        if( ! n.adjacent.isEmpty()){
            System.out.println("This Node has #neighbors=" + String.valueOf(n.numOfNeighbors));
        }
        else{
          System.out.println("This Node has no Neighbors");
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
