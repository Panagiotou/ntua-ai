import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.IOException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import java.util.Iterator;


public class Graph {
  private ArrayList<Client> clients;
  private HashSet<Node> nodes;
  private int ntest;
  public double TOLERANCE;
  public Prolog pl;
  private Hashtable<Node, Integer> taxis;
  private Hashtable<Integer, Node> taxisInverse;
  private Hashtable<Integer, Double> taxiClientDist;
  private Hashtable<Node, ArrayList<Edge>> adjacencyList;
  private  ArrayList<ArrayList<ArrayList<Node>>> allKmls;

  public Graph(int Ntest, double tol, String world) throws IOException, JIPSyntaxErrorException {
    // creates graph topology
    ntest = Ntest;
    TOLERANCE = tol;
    pl = new Prolog(world);

    // Initialization
    clients = pl.getClients();
    nodes = pl.getNodes();
    taxis = pl.getTaxis();

    Set<Node> taxiKeys = taxis.keySet();

    taxisInverse = new Hashtable<Integer, Node>();
    taxiClientDist = new Hashtable<Integer, Double>();
    for (Node t: taxiKeys) {
      Integer id = taxis.get(t);
      taxisInverse.put(id, t);
    }

    // Memoization of adjacent nodes
    adjacencyList = new Hashtable<Node, ArrayList<Edge>>();

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

  public Node aStar(Node s, HashSet<Node> goals, Visual visual, int taxiId, int clientId) {
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

    for (Node n: nodes) {
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

    Node correct = null;
    boolean flag = true;

    while (!frontier.isEmpty()) {
      current = frontier.remove();

      // Break if it finds a goal
      if (goals.contains(current.from)) {
        if (correct == null) {
          correct = current.from;
        } else if (correct != null && current.from != correct) break;

        // add it to taxiClientDist hashtable
        if (taxiClientDist.contains(taxiId)) {
          // Increment if we have chosen this taxi
          double new_score = taxiClientDist.get(taxiId) + gScore.get(current.from);
          taxiClientDist.put(taxiId, new_score);
        } else {
          taxiClientDist.put(taxiId, gScore.get(current.from));
        }


      }

      if (correct != null && current.actual_distance > gScore.get(correct)) break;

      closedSet.add(current.from);

      ArrayList<Edge> adjacent = null;
      if (adjacencyList.contains(current.from)) {
        adjacent = adjacencyList.get(current.from);
      } else {
        // Get nodes without heavy traffic
        adjacent = pl.getNext(current.from, true, clientId);
        adjacencyList.put(current.from, adjacent);
      }


      for (Edge e : adjacent) {
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
        visual.addNodeList(path);
      }

      for (Pair pr : parent.get(u)) {
        if (TOLERANCE > 0) {
          flag = Math.abs(gScore.get(u) - pr.second) < TOLERANCE;
        }
        else {
          flag = gScore.get(u) == pr.second;
        }
        if (!path.contains(pr.first) && flag) {
          ArrayList<Node> newPath = new ArrayList<Node>(path);
          newPath.add(pr.first);
          q.add(newPath);
        }
      }
    }

    System.out.println("Number of equivalent paths within tolerance " + TOLERANCE + " km: " + npaths);

    return correct;
  }

  public void simulateRides() {
    for (Client client: clients) simulateClient(client);

  }

  private void displayRanks() {
   ArrayList<Map.Entry<Integer, Double>> sorted = new ArrayList(taxiClientDist.entrySet());
   Collections.sort(sorted, new Comparator<Map.Entry<Integer, Double>>(){
     public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }});


    for (Map.Entry<Integer, Double> taxiScore: sorted) {
      System.out.println("Taxi: " + taxiScore.getKey() +
                        " Distance: " + taxiScore.getValue() + " km" +
                        " Rating: " + pl.getRating(taxiScore.getKey()));
    }

 }

  public void simulateClient(Client client) {

    taxiClientDist.clear();
    System.out.println("Serving client: " + client.toString());

    System.out.print("Available taxis to serve ");
    ArrayList<Integer> availableTaxis = pl.getGoals(client.clientId);
    System.out.println(availableTaxis.toString());

    HashSet<Node> goal = new HashSet<Node>();
    goal.add(client.source);
    HashSet<Node> destGoal = new HashSet<Node>();
    destGoal.add(client.dest);

    // Visualize taxi routes to same KML files
    Visual taxiVisual = new Visual(null);

    // Separate file for client route
    Visual routeVisual = new Visual("red");

    for (int taxiId: availableTaxis) {
      Node taxi = taxisInverse.get(taxiId);
      aStar(taxi, goal, taxiVisual, taxiId, client.clientId);
    }

    // Export KML Files
    taxiVisual.createKML("taxis_" + String.valueOf(TOLERANCE) + ".kml");

    // Display ranks
    System.out.println("Please choose one taxi of the following");
    displayRanks();

    Scanner in = new Scanner(System.in);

    // Choose a taxi
    int choice = -1;
    do {
      System.out.print("Enter a choice: ");
      choice = in.nextInt();
    } while (!availableTaxis.contains(choice));

    System.out.println("Calculating distance to destination");
    aStar(client.source, destGoal, routeVisual, -1, client.clientId);
    double total = taxiClientDist.get(choice) + taxiClientDist.get(-1);

    System.out.println("Route length: " + taxiClientDist.get(-1) + " km");
    System.out.println("Total distance travelled by taxi " + choice + "to destination: " + total + " km");

    routeVisual.createKML("route_" + String.valueOf(TOLERANCE) + ".kml");

  }


  public static void main(String[] args) throws IOException, JIPSyntaxErrorException {

    System.out.println("Running Testcases");
    File testDir = new File("../resources/data");
    int ncases = testDir.list().length;
    double tolerance = 0.0;
    ArrayList<Double> tolerances = new ArrayList<Double>();
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

    try {
      tolerance = Double.parseDouble(args[1]);
    }
    catch (NumberFormatException e) {
      System.err.println("Argument" + args[1] + " must be a double.");
      // Program ends
      System.exit(1);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Default tolerance is " + tolerance + "km");
    }

    for (int i = 0; i < ncases; i++) {
      Graph G = new Graph(i, tolerance, "world.pl");
      G.simulateRides();
    }

  }
}
