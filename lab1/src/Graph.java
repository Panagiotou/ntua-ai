import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Graph {
  private ArrayList<Node> nodeList;
  private Hashtable<Node, ArrayList<Point>> nodesMap; // Maps a Node to many Points
  private Hashtable<Point, Node> pointsMap; // Maps a Point to a Node
  private ArrayList<Node> clients; // clients array list
  private Hashtable<Node, Integer> taxis;
  private HashSet<Point> points;
  private int ntest;

  public Graph() {
	  nodeList = new ArrayList<Node>();
	  nodesMap = new Hashtable<Node, ArrayList<Point>>();
	  clients = new ArrayList<Node>();
	  taxis = new Hashtable<Node, Integer>();
	  points = new HashSet<Point>();
	  ntest = 0;
	  
  }

  public Graph(String nodesFile, int Ntest) {
    // creates graph topology
	ntest = Ntest;
    Hashtable<Node, ArrayList<Point>> nodeMap = new Hashtable<Node,ArrayList<Point>>();
    Hashtable<Point, Node> pointMap = new Hashtable<Point, Node>();

    ArrayList<Point> points = new ArrayList<Point>();
    Set<Node> NodeSet = new HashSet<>();
    ArrayList<Node> nodes = new ArrayList<Node>();
    String line = "";
    String cvsSplitBy = ",";
    int i = 0;
    // First itempArreration
    try (BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);
        Point po;
        if(Coord.length == 4){
          po = new Point(Double.parseDouble(Coord[0]), Double.parseDouble(Coord[1]), Integer.parseInt(Coord[2]), Coord[3], i);
          points.add(po);
        }
        else{
          po = new Point(Double.parseDouble(Coord[0]), Double.parseDouble(Coord[1]), Integer.parseInt(Coord[2]), "None", i);
          points.add(po);
        }
        Node no = new Node(Double.parseDouble(Coord[0]), Double.parseDouble(Coord[1]));

        no.numOfNeighbors = -1;
        if (! NodeSet.contains(no)){
          NodeSet.add(no);
          nodes.add(no);
          //create pointMap (reverse of nodeMap)
          if( pointMap.containsKey(po)){
            continue;
          }
          else{
            pointMap.put(po, no);
          }
        }

        i += 1;
      }
    }catch (IOException e) {
      e.printStackTrace();
    }
    // Make hashtables

    for (Point po: points){
      // create nodeMap

      // Critical Point i need to get the node no (of the list nodes) that hax no.x == po.x and no.y == po.y
      Node no = pointMap.get(po);
      ArrayList<Point> poArr = new ArrayList<Point>();
      if(! nodeMap.containsKey(no)){
        poArr = new ArrayList<Point>();
      }
      else{
        poArr = nodeMap.get(no);
      }
      poArr.add(po);
      nodeMap.put(no, poArr);
    }

    ArrayList<Node> newnodes = new ArrayList<Node>();
    for(Node key: nodes) {


      ArrayList<Point> poArr = new ArrayList<Point>();
      poArr = nodeMap.get(key);
      for(Point po: poArr){
        int currid = po.id;
        if(currid>0){
          Point prev  = points.get(currid-1);
          if(po.rhodeId == prev.rhodeId){
            // If on same rhode, find node matching to previous, make an edge, add it to the ArrayList
            Node prevNode = pointMap.get(prev);
            Edge tempEdge = new Edge(key,prevNode, prev.rhodeId);
            key.adjacent.add(tempEdge);
          }
        }
        if(currid +1 < points.size()){
          Point next = points.get(currid+1);
          if(po.rhodeId == next.rhodeId){
            Node nextNode = pointMap.get(next);
            Edge tempEdge = new Edge(key, nextNode, next.rhodeId);
            key.adjacent.add(tempEdge);
          }
        }
      }
      key.numOfNeighbors = key.adjacent.size();
      newnodes.add(key);
    }
    ArrayList<Node> nodeList = new ArrayList<Node>();
    this.nodeList = newnodes;
    Hashtable<Node, ArrayList<Point>> nodesMap = new Hashtable<Node,ArrayList<Point>>();
    Hashtable<Point, Node> pointsMap = new Hashtable<Point, Node>();
    this.nodesMap = nodeMap;
    this.pointsMap = pointMap;
    this.points = new HashSet<Point>(this.pointsMap.keySet());

  }

  public Node getClosestPoint(Point p) {
	  double dist = Double.MAX_VALUE;
	  Point argmin = null;
	  for (Point q : points) {
		  if (p.haversine(q) <= dist) {
			  dist = p.haversine(q);
			  argmin = q;
		  }
	  }

	 Node t = pointsMap.get(argmin);
	 return t;

  }


  public void parseClientFile(String clientsFile) {
    clients = new ArrayList<Node>();
    String line = "";
    String cvsSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(clientsFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);
        double x =  Double.parseDouble(Coord[0]);
        double y =  Double.parseDouble(Coord[1]);
        Point tmp = new Point(x, y);

        Node customer = getClosestPoint(tmp);
        clients.add(customer);

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
        key.printNode();


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
    taxis = new Hashtable<Node, Integer>();

    String line = "";
    String cvsSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(taxisFile))) {

      line = br.readLine(); // skip first line.

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] Coord = line.split(cvsSplitBy);
        double x =  Double.parseDouble(Coord[0]);
        double y =  Double.parseDouble(Coord[1]);
        Integer id = new Integer(Integer.parseInt(Coord[2]));
        Point p = new Point(x, y);
        Node t = getClosestPoint(p);
        taxis.put(t, id);

      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Heuristic function
  public double h(Node s, Node t) {
	  return 0.707 * s.haversine(t);
  }

  // h_total works for multiple goals taking the min of h(s, g_i)
  public double h_total(Node s, HashSet<Node> goals) {
	  double result = Double.MAX_VALUE;
	  for (Node t : goals) {
		  if (h(s, t) < result) result = h(s, t);
	  }
	  return result;
  }

  public void aStar(Node s, HashSet<Node> goals, int i) {
      // Closed set
	  HashSet<Node> closedSet = new HashSet<Node>();

	  // Frontier (Open Set)
	  PriorityQueue<Estimator> frontier = new PriorityQueue<Estimator>();
	  Estimator e0 = new Estimator(s, 0, h_total(s, goals));
	  frontier.add(e0);

	  // Scores
	  Hashtable<Node, Double> gScore = new Hashtable<Node, Double>();
	  Hashtable<Node, Double> fScore = new Hashtable<Node, Double>();
	  Hashtable<Node, ArrayList<Pair>> parent = new Hashtable<Node, ArrayList<Pair>>();

	  // Initializations
	  for (Node n : nodeList) {
		  gScore.put(n, Double.MAX_VALUE);
		  fScore.put(n, Double.MAX_VALUE);
	  }
	  // Initialize Estimators for starting node
	  gScore.put(s, 0.0);
	  fScore.put(s, h_total(s, goals));
	  Estimator current = null;
	  ArrayList<Pair> tempArr = new ArrayList<Pair>();
	  tempArr.add(new Pair(s, 0));
	  parent.put(s, tempArr);

	  System.out.println("Starting point is " + s.printCoord());

	  while (!frontier.isEmpty()) {
		  current = frontier.remove();
		  
		  
		  // Break if it finds a goal
		  if (goals.contains(current.from)) {
			  System.out.println("Goal found with cost from start " + gScore.get(current.from));
			  System.out.print("Goal coordinates " + current.from.printCoord());

			  try {
				  System.out.println(" which corresponds to taxi " + taxis.get(current.from));

			  } catch (NullPointerException e) {
				  e.printStackTrace();
			  } finally {
				  System.out.println();
			  }

			  break;
		  }

		  closedSet.add(current.from);
		  

		  for (Edge e : current.from.adjacent) {
			  // if it is visited
			  if (closedSet.contains(e.v)) continue;


			  // relax edge
			  double temp = gScore.get(current.from) + e.weight;
			  if (temp > gScore.get(e.v)) continue;
			  
              
			  // update score
			  gScore.put(e.v, temp);
			  fScore.put(e.v, temp + h_total(e.v, goals));

			  Estimator est = new Estimator(e.v, gScore.get(e.v), h_total(e.v, goals));

			  closedSet.add(e.v);

        // If it is not included in the frontier
			  if (!frontier.contains(est)) {
				  frontier.add(est);
				  tempArr = new ArrayList<Pair>();
				  tempArr.add(new Pair(current.from, temp));
				  parent.put(e.v, tempArr);

			  }
        // If there is an estimate in the frontier
			  else {
				  tempArr = parent.get(e.v);
				  tempArr.add(new Pair(current.from, temp));
				  parent.put(e.v, tempArr);
			  }

		  }

	  }

	  ArrayList<ArrayList<Node>> result = new ArrayList<ArrayList<Node>>();

    // Create edges for all equivalent paths
	  Queue<Node> q = new LinkedList<Node>();
	  q.add(current.from);

	  closedSet.clear();
	  closedSet.add(current.from);
	  while (!q.isEmpty()) {
		  Node u = q.remove();
		  for (Pair pr : parent.get(u)) {
			  ArrayList<Node> segment = new ArrayList<Node>();
        // find optimal estimates
			  if (!closedSet.contains(pr.first) && pr.second == gScore.get(u)) {
				  segment.add(u);
				  segment.add(pr.first);
				  result.add(segment);
				  closedSet.add(pr.first);
				  q.add(pr.first);
			  }
		  }

	  }

    // Create KML
	  Visual printer = new Visual(result, i);
	  printer.createKML("results/testcase_" + ntest + "_client_" + String.valueOf(i) + ".kml");

  }

  public void simulateRides() {
	  HashSet<Node> goals = new HashSet<Node>(taxis.keySet());
	  int i = 0;

	  for (Node c : clients) {
		  System.out.println("Serving client: " + i);
		  aStar(c, goals, i);
		  i++;
	  }
  }


  public static void main(String[] args) {

	 System.out.println("Running Testcases (defaults to 3 but can be provided as an argument)");
	 int ncases = 3;
	 
	 try {
		 ncases = Integer.parseInt(args[0]);
	 }
  	 catch (NumberFormatException e) {
  		 System.err.println("Argument" + args[0] + " must be an integer.");
  		 // Program ends
  		 System.exit(1);
  	 }
	 catch (ArrayIndexOutOfBoundsException e) {
		System.out.println("No ncases provided, defaults to 3"); 
		ncases = 3; 
	 }
	  
	    
	    System.out.println("===============");
		System.out.println("===Testcases==="); 
	    System.out.println("===============");
	 
	  
	 for (int i = 0; i < ncases; i++) {
		 System.out.println("Testcase #" + i);
		 String nodesFile = "../resources/data/" + i + "/nodes.csv";
		 String clientsFile = "../resources/data/" + i + "/client.csv";
		 String taxisFile = "../resources/data/" + i + "/taxis.csv";
		 Graph G = new Graph(nodesFile, i);
		 G.parseClientFile(clientsFile);
		 G.parseTaxiFile(taxisFile);
		 G.simulateRides();
		 System.out.println("==============");

	 }

  }
}
