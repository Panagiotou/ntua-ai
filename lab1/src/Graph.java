import java.util.ArrayList;
import java.util.Hashtable;


public class Graph {
	private ArrayList<Node> nodes;
	private Hashtable<Point, Integer> pointMap;
	
	public Graph(String nodesFile) {
		// creates graph topology
	}
	
	public ArrayList<Client> parseClientFile(String clientsFile) {
		ArrayList<Client> clients = null;
		// parse clients
		return clients;
	}
	
	public ArrayList<Taxi> parseTaxiFile(String taxisFile) {
		ArrayList<Taxi> taxis = null;
		// parse taxis
		return taxis;
	}
	
	public void callTaxi(Client c, ArrayList<Taxi> goals) {
		
	}
	
	public void callTaxis(ArrayList<Client> clients, ArrayList<Taxi> taxis) {
		
	}
	
	public void exportFile(String outfile) {
		
	}
	
	
	public static void main(String[] argv) {
		// main
		
	}
}
