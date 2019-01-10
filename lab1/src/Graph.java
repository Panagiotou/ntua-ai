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
import java.io.File;


public class Graph {
  private ArrayList<Node> nodeList;
  private Hashtable<Node, ArrayList<Point>> nodesMap; // Maps a Node to many Points
  private Hashtable<Point, Node> pointsMap; // Maps a Point to a Node
  private ArrayList<Node> clients; // clients array list
  private Hashtable<Node, Integer> taxis;
  private HashSet<Point> points;
  private int ntest;
  private double TOLERANCE;

  public Graph() {
	  nodeList = new ArrayList<Node>();
	  nodesMap = new Hashtable<Node, ArrayList<Point>>();
	  clients = new ArrayList<Node>();
	  taxis = new Hashtable<Node, Integer>();
	  points = new HashSet<Point>();
	  ntest = 0;
    TOLERANCE = 0;

  }

  public Graph(String nodesFile, int Ntest, double tol) {
    // creates graph topology
	ntest = Ntest;
    Hashtable<Node, ArrayList<Point>> nodeMap = new Hashtable<Node,ArrayList<Point>>();
    Hashtable<Point, Node> pointMap = new Hashtable<Point, Node>();
    TOLERANCE = tol;
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
        if (!NodeSet.contains(no)){
          NodeSet.add(no);
          nodes.add(no);
          //create pointMap (reverse of nodeMap)
          if(pointMap.containsKey(po)){
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
	  double lambda = 0.9;
	  return  lambda * s.haversine(t);
  }

  // h_total works for multiple goals taking the min of h(s, g_i)
  public Pair h_total(Node s, HashSet<Node> goals) {
	  double result = Double.MAX_VALUE;

	  Node argmin = null;
	  for (Node t : goals) {
		  if (h(s, t) < result) {
			  result = h(s, t);
			  argmin = t;
		  }
	  }
	  return new Pair(argmin, result);
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
	  Hashtable<Node, Node> towards = new Hashtable<Node, Node>();

	  // Initializations
	  for (Node n : nodeList) {
		  gScore.put(n, Double.MAX_VALUE);
		  fScore.put(n, Double.MAX_VALUE);
	  }
	  // Initialize Estimators for starting node
	  gScore.put(s, 0.0);
	  fScore.put(s, h_total(s, goals).second);
	  Estimator current = null;
	  ArrayList<Pair> tempArr = new ArrayList<Pair>();
	  tempArr.add(new Pair(s, 0));
	  parent.put(s, tempArr);

	  System.out.println("Starting point is " + s.printCoord());

	  System.out.println("Equivalent Paths");

	  Node correct = null;
	  boolean flag = true;

	  while (!frontier.isEmpty()) {
		  current = frontier.remove();


		  // Break if it finds a goal
		  if (goals.contains(current.from)) {
			  if (correct == null) {
				  correct = current.from;
			  } else if (correct != null && current.from != correct) break;

			  System.out.println("Goal found with cost from start " + gScore.get(current.from));
			  System.out.println("Remaining frontier size: " + frontier.size());

			  System.out.print("Goal coordinates " + current.from.printCoord());

			  try {
				  System.out.print(" which corresponds to taxi " + taxis.get(current.from));
				  System.out.println(" and came from " + parent.get(current.from).get(parent.get(current.from).size() - 1).first.printCoord());

			  } catch (NullPointerException e) {
				  e.printStackTrace();
			  } finally {
				  System.out.println();
			  }

		  }

		  if (correct != null && current.actual_distance > gScore.get(correct)) break;



		  closedSet.add(current.from);


		  for (Edge e : current.from.adjacent) {
			  // if it is visited
			  if (closedSet.contains(e.v)) continue;


			  // relax edge
			  double temp = gScore.get(current.from) + e.weight;

			  // Construct estimator for the next node
			  // g[e.v] = g[current] + w
			  // h[e.v] = min over all goals of h_i [e.v, g] (keep argmin as well)
			  Estimator est = new Estimator(e.v, temp, h_total(e.v, goals));

              if (frontier.contains(est) && temp > gScore.get(e.v)) continue;

        	  // If it is not included in the frontier then it is the current best
              if (frontier.contains(est)) {
                  frontier.remove(est);
                  frontier.add(est);
              }
              else {
                  frontier.add(est);
			  }

              // update parent

              if (parent.get(e.v) == null) {
                tempArr = new ArrayList<Pair>();
                tempArr.add(new Pair(current.from, temp));
                parent.put(e.v, tempArr);
              } else {
                tempArr = parent.get(e.v);
                tempArr.add(new Pair(current.from, temp));
                parent.put(e.v, tempArr);
              }


			  // update score
			  gScore.put(e.v, temp);
			  fScore.put(e.v, temp + h_total(e.v, goals).second);
			  towards.put(e.v, h_total(e.v, goals).first);


		  }

	  }

	  ArrayList<ArrayList<Node>> result = new ArrayList<ArrayList<Node>>();

	  Node src = correct;
	  Node dst = s;

	  // Equivalent routes
	  Queue<ArrayList<Node>> q = new LinkedList<ArrayList<Node>>();
	  ArrayList<Node> path = new ArrayList<Node>();
	  path.add(src);

	  q.add(path);

	  closedSet.clear();
	  closedSet.add(correct);
	  int npaths = 0;

	  while (!q.isEmpty()) {
		  path = q.remove();
		  Node u = path.get(path.size() - 1);

		  if (u.equals(dst)) {
			  npaths++;
			  result.add(path);
		  }

		  for (Pair pr : parent.get(u)) {
              if (TOLERANCE > 0) {
                  flag = Math.abs(gScore.get(u) - pr.second) < TOLERANCE;
              }
              else {
                  flag = gScore.get(u) == pr.second;
              }
			  if (!path.contains(pr.first) && flag  && towards.get(u) == correct) {
				  ArrayList<Node> newPath = new ArrayList<Node>(path);
				  newPath.add(pr.first);
				  q.add(newPath);
			  }
		  }
	  }

	  System.out.println("Number of equivalent paths within tolerance " + TOLERANCE + " km: " + npaths);

	  // Create KML
	  String tl = null;

	  Visual printer = null;
	  if (TOLERANCE > 0) {
		  tl = "_tol";
		  printer = new Visual(result, "red");
	  }
	  else {
		  tl = "";
		  printer = new Visual(result, "green");
	  }


	  printer.createKML("results/testcase_" + ntest + "_client_" + String.valueOf(i) + tl + ".kml");

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

	 System.out.println("Running Testcases");
     File testDir = new File("../resources/data");
	 int ncases = testDir.list().length;
	 ArrayList<Double> tolerances = new ArrayList<Double>();
     tolerances.add(0.0); // no tolerance
     tolerances.add(0.001); // 10m tolerance
	 try {
		 ncases = Integer.parseInt(args[0]);
	 }
  	 catch (NumberFormatException e) {
  		 System.err.println("Argument" + args[0] + " must be an integer.");
  		 // Program ends
  		 System.exit(1);
  	 }
	 catch (ArrayIndexOutOfBoundsException e) {
		System.out.println("No ncases provided, defaults to " + String.valueOf(ncases));
	 }


	    System.out.println("===============");
		System.out.println("===Testcases===");
	    System.out.println("===============");


	 for (int i = 0; i < ncases; i++) {
		 System.out.println("Testcase #" + i);
		 String nodesFile = "../resources/data/" + i + "/nodes.csv";
		 String clientsFile = "../resources/data/" + i + "/client.csv";
		 String taxisFile = "../resources/data/" + i + "/taxis.csv";
		 for (double t: tolerances) {
            System.out.println("Tolerance: " + t);
            Graph G = new Graph(nodesFile, i, t);
		    G.parseClientFile(clientsFile);
		    G.parseTaxiFile(taxisFile);
		    G.simulateRides();
		    System.out.println("==============");
         }
	 }

  }
}
