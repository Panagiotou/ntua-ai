public class Client  {
	public Node source, dest;
	public int clientId;

	public Client(int id, Node s, Node t) {
		source = s;
		dest = t;
		clientId = id;
	}

	public String toString() {
		return "Client: " + clientId + " From:" + source.toString() + " Dest: "  + dest.toString();
	}

}
