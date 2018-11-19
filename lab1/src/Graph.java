import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Graph {
  private ArrayList<Node> nodeList;
  private Hashtable<Node, ArrayList<Point>> nodesMap; // Maps a Node to many Points
  private Hashtable<Point, Node> pointsMap; // Maps a Point to a Node
  private ArrayList<Client> clients; // clients array list

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
    clients = null;
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

  public ArrayList<Taxi> parseTaxiFile(String taxisFile) {
    ArrayList<Taxi> taxis = null;
    // parse taxis
    return taxis;
  }

  public void callTaxi(Node s, Node g) {
      HashSet<Node> visited = new HashSet<Node>();



  }


  public static void main(String[] argv) {
    // main
    String nodesFile = "../resources/nodes.csv";
    String clientsFile = "../resources/client.csv";
    Graph G = new Graph(nodesFile);

    Node p = G.nodeList.get(10);
    Node q = G.nodeList.get(5);

  }
}
