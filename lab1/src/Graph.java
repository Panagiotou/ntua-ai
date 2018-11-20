import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Graph {
  private ArrayList<Node> nodeList;
  private Hashtable<Node, ArrayList<Point>> nodesMap; // Maps a Node to many Points
  private Hashtable<Point, Node> pointsMap; // Maps a Point to a Node
  private ArrayList<Client> clients; // clients array list
  private HashSet<Taxi> taxis;

  public Graph(String nodesFile) {
    // creates graph topology
    Hashtable<Node, ArrayList<Point>> nodeMap = new Hashtable<Node,ArrayList<Point>>();
    Hashtable<Point, Node> pointMap = new Hashtable<Point, Node>();

    ArrayList<Point> points = new ArrayList<Point>();
    Set<Node> NodeSet = new HashSet<>();
    ArrayList<Node> nodes = new ArrayList<Node>();
    String line = "";
    String cvsSplitBy = ",";
    int i = 0;
    ArrayList<Edge> adjac = new ArrayList<Edge> ();
    // First itteration
    try (BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);
        if(Coord.length == 4){
          Point po = new Point(Double.parseDouble(Coord[0]), Double.parseDouble(Coord[1]), Integer.parseInt(Coord[2]), Coord[3], i);
          points.add(po);
        }
        else{
          Point po = new Point(Double.parseDouble(Coord[0]), Double.parseDouble(Coord[1]), Integer.parseInt(Coord[2]), "None", i);
          points.add(po);
        }
        Node no = new Node();
        no.x = Double.parseDouble(Coord[0]);
        no.y = Double.parseDouble(Coord[1]);
        no.setAdj(adjac);
        no.numOfNeighbors = -1;
        if (! NodeSet.contains(no)){
          NodeSet.add(no);
          nodes.add(no);
        }

        i += 1;
      }
    }catch (IOException e) {
      e.printStackTrace();
    }
    // Make hashtables

    for (i = 0; i < points.size(); i++){
      // create nodeMap
      Point po = points.get(i);
      Node no = new Node();
      no.x = po.x;
      no.y = po.y;
      ArrayList<Point> poArr = new ArrayList<Point>();
      if(! nodeMap.containsKey(no)){
        poArr = new ArrayList<Point>();
      }
      else{
        poArr = nodeMap.get(no);
      }
      poArr.add(po);
      nodeMap.put(no, poArr);
      //create pointMap (reverse of nodeMap)
      if( pointMap.containsKey(po)){
        continue;
      }
      else{
        pointMap.put(po, no);
      }

    }

    ArrayList<Node> newnodes = new ArrayList<Node>();
    for(Node key: nodes) {
      Node tmp = new Node();
      tmp.x = key.x;
      tmp.y = key.y;
      ArrayList<Edge> adjacent = new ArrayList<Edge> ();

      ArrayList<Point> poArr = new ArrayList<Point>();
      poArr = nodeMap.get(key);
      for(Point po: poArr){
        int currid = po.id;
        if(currid>0){
          Point prev  = points.get(currid-1);
          if(po.rhodeId == prev.rhodeId){
            // If on same rhode, find node matching to previous, make an edge, add it to the ArrayList
            Node prevNode = pointMap.get(prev);
            Edge tempEdge = new Edge(prevNode, key, prev.rhodeId);
            adjacent.add(tempEdge);
          }
        }
        if(currid +1 < points.size()){
          Point next = points.get(currid+1);
          if(po.rhodeId == next.rhodeId){
            Node nextNode = pointMap.get(next);
            Edge tempEdge = new Edge(key, nextNode, next.rhodeId);
            adjacent.add(tempEdge);
          }
        }
      }
      tmp.setAdj(adjacent);
      tmp.numOfNeighbors = adjacent.size();
      newnodes.add(tmp);
    }
    ArrayList<Node> nodeList = new ArrayList<Node>();
    this.nodeList = newnodes;
    Hashtable<Node, ArrayList<Point>> nodesMap = new Hashtable<Node,ArrayList<Point>>();
    Hashtable<Point, Node> pointsMap = new Hashtable<Point, Node>();
    this.nodesMap = nodeMap;
    this.pointsMap = pointMap;
  }


  public void parseClientFile(String clientsFile) {
    clients = new ArrayList<Client>();
    String line = "";
    String cvsSplitBy = ",";
    int NewId = 0; //counts client id
    try (BufferedReader br = new BufferedReader(new FileReader(clientsFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);

        Client customer = new Client();
        customer.x =  Double.parseDouble(Coord[0]);
        customer.y =  Double.parseDouble(Coord[1]);
        customer.setid(NewId);
        clients.add(customer);
        NewId += 1;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void printNodesMap(){
    // print nodesMap
    Set<Node> keys = this.nodesMap.keySet();

    for(Node key: keys){
      ArrayList<Point> poArr = new ArrayList<Point>();
      poArr = this.nodesMap.get(key);
      if(poArr.size() >1){
        System.out.println("Node (x,y) = ("+String.valueOf(key.x) +","+ String.valueOf(key.y)  +")");

        System.out.println("Corresponds to points:");
        for(Point po: poArr){
          po.printPoint();
        }
      }
    }
  }

  public void printPointsMap(){
    // print pointMap
    Set<Point> keysc = this.pointsMap.keySet();
    for(Point keyc: keysc){
      Node no = this.pointsMap.get(keyc);
      System.out.println("Point (x,y) = ("+String.valueOf(keyc.x) +","+ String.valueOf(keyc.y)  +")");

      System.out.println("matches to Node (x,y) = ("+String.valueOf(no.x) +","+ String.valueOf(no.y)  +")");

    }
  }

  public void printNodeList(){
    int i = 0;
    for(Node key: this.nodeList){
      System.out.println("Printing node: " + String.valueOf(i));
      key.printNode();
      i += 1;
    }
  }

  public void parseTaxiFile(String taxisFile) {
    taxis = new HashSet<Taxi>();
    
    String line = "";
    String cvsSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(taxisFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);

        Taxi t = new Taxi();
        t.x =  Double.parseDouble(Coord[0]);
        t.y =  Double.parseDouble(Coord[1]);
        t.id = Integer.parseInt(Coord[2]);
        
        taxis.add(t);
        
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Heuristic function
  public double h(Node s, Node t) {
	  return s.pNorm(t, 1);
  }
  
  // h_total works for multiple goals taking the min of h(s, g_i)
  public double h_total(Node s, HashSet<Node> goals) {
	  double result = Double.MAX_VALUE;
	  for (Node t : goals) {
		  if (h(s, t) < result) result = h(s, t);
	  }
	  return result;
  }
  
  public Solution aStar(Node s, HashSet<Node> goals) {
      // Closed set
	  HashSet<Node> closedSet = new HashSet<Node>();
	  
	  // Frontier (Open Set)
	  PriorityQueue<Estimator> frontier = new PriorityQueue<Estimator>();
	  Estimator e0 = new Estimator(s, s, 0, h_total(s, goals));
	  frontier.add(e0);
	  
	  // Scores
	  Hashtable<Node, Double> gScore = new Hashtable<Node, Double>();
	  Hashtable<Node, Double> fScore = new Hashtable<Node, Double>();
	  Hashtable<Node, Stack<Estimator>> parent = new Hashtable<Node, Stack<Estimator>>();
	  
	  // Initializations
	  for (Node n : nodeList) {
		  gScore.put(n, Double.MAX_VALUE);
		  fScore.put(n, Double.MAX_VALUE);
		  parent.put(n, new Stack<Estimator>());
	  }
	  // Initialize Estimators for starting node
	  gScore.put(s, 0.0);
	  fScore.put(s, h_total(s, goals));
	  Estimator current = null;
	  
	  while (!frontier.isEmpty()) {
		  current = frontier.remove();
		  
		  // Break if it finds a goal
		  if (goals.contains(current.from)) break;
		  
		  
		  for (Edge e : current.from.adjacent) {
			  // if it is visited
			  if (closedSet.contains(e.v)) continue;
			  
			  
			  // relax edge
			  double temp = gScore.get(current.from) + e.weight;
			  if (temp > gScore.get(e.v)) continue;
			  
			  // update score
			  gScore.put(e.v, temp);
			  fScore.put(e.v, temp + h_total(e.v, goals));
			  
			  Estimator est = new Estimator(e.u, e.v, gScore.get(e.v), h_total(e.v, goals));			  
			  
			  closedSet.add(e.v);
			  
			  if (!frontier.contains(est)) {
				  frontier.add(est);
				  Stack<Estimator> current_history = parent.get(e.v);
				  while(!current_history.isEmpty() && current_history.peek().actual_distance > est.actual_distance)
					  current_history.pop();
				  
				  current_history.add(est);
				  parent.put(e.v, current_history);
			  }
			  
		  }
		  
	  }
	  
	  // Return shortest path DAG
	  return new Solution(current.from, parent);
  }


  public static void main(String[] argv) {
    // main
    String nodesFile = "../resources/nodes.csv";
    String clientsFile = "../resources/client.csv";
    String taxisFile = "../resources/taxis.csv";
    Graph G = new Graph(nodesFile);
    G.parseClientFile(clientsFile);
    G.parseTaxiFile(taxisFile);
    
    Node p = G.nodeList.get(10);
    Node q = G.nodeList.get(5);

  }
}
