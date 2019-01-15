public class Edge {

	public Node u;
	public Node v;
	public double weight;

	public Edge(Node from, Node to) {
    u = from;
    v = to;
    weight = u.haversine(v);
	}

	public String toString() {
		return "From " + u.printCoord() + " To : " + v.printCoord() + " W = " + String.valueOf(weight);
	}

}
