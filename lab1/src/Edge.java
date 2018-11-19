public class Edge {

	private Node u;
	private Node v;
	private double weight;
	public int name;

	public Edge(Node from, Node to, int roadId) {
            u = from;
            v = to;
            name = roadId;
            weight = u.pNorm(v, 2);
	}

	public void printEdge() {
		System.out.println("From " + String.valueOf(u) + " To : " + String.valueOf(v) + " W = " + String.valueOf(weight));
	}

}
