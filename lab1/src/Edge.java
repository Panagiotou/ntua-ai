public class Edge {

	public Node u;
	public Node v;
	public double weight;
	public int name;

	public Edge(Node from, Node to, int roadId) {
            u = from;
            v = to;
            name = roadId;
            weight = u.haversine(v);
	}

	public void printEdge() {
		System.out.println("From " + u.printCoord() + " To : " + v.printCoord() + " W = " + String.valueOf(weight));
	}

}
