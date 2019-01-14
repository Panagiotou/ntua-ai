import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
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

  public Node aStar(Node s, HashSet<Node> goals, int i) {
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

    //System.out.println("Starting point is " + s.toString());

    //System.out.println("Equivalent Paths");

    Node correct = null;
    boolean flag = true;

    while (!frontier.isEmpty()) {
      current = frontier.remove();

      // Break if it finds a goal
      if (goals.contains(current.from)) {
        if (correct == null) {
          correct = current.from;
        } else if (correct != null && current.from != correct) break;

        //System.out.println("Goal found with cost from start " + gScore.get(current.from));
        // add it to taxiClientDist hashtable
        taxiClientDist.put(i, gScore.get(current.from));
        //System.out.println("Remaining frontier size: " + frontier.size());

        //System.out.print("Goal coordinates " + current.from.toString());

        try {
          //System.out.print(" which corresponds to taxi " + taxis.get(current.from));
          //System.out.println(" and came from " + parent.get(current.from).get(parent.get(current.from).size() - 1).first.toString());

        } catch (NullPointerException e) {
          e.printStackTrace();
        } finally {
          System.out.println();
        }
        break;
      }

      if (correct != null && current.actual_distance > gScore.get(correct)) break;

      ArrayList<Edge> adjacent = null;
      if (adjacencyList.contains(current.from)) {
      adjacent = adjacencyList.get(current.from);
      }
      else {
      adjacent = pl.getNext(current.from, false, 0);
      adjacencyList.put(current.from, adjacent);
      }


      for (Edge e : adjacent) {
        // System.out.println("Edge" + e.toString());
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

    //System.out.println("Number of equivalent paths within tolerance " + TOLERANCE + " km: " + npaths);

    /*
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
    */
    allKmls.add(result);
    return correct;
  }

  public void simulateRides(Integer topk) {
    for (Client client: clients) simulateClient(client, topk);

  }

  public void simulateClient(Client client, Integer topk) {
    System.out.println("Serving client: " + client.toString());

    System.out.println("Available taxis to serve");
    ArrayList<Integer> availableTaxis = pl.getGoals(client.clientId);
    System.out.println(availableTaxis.toString());
    HashSet<Node> goals = new HashSet<Node>();
    // Show ranks
    for (Integer availableTaxiId: availableTaxis) {
      Node taxi = taxisInverse.get(availableTaxiId);
      goals.add(taxi);
    }
    Hashtable<Integer, Integer> iToId = new Hashtable<Integer, Integer>(); // maps i to taxiId
    System.out.println("Please choose 1 out of the " + String.valueOf(topk) + " following available taxis:");
    for(int i=1; i <= topk; i++){
       Node solution = aStar(client.source , goals, i);
       goals.remove(solution);
       iToId.put(i, taxis.get(solution));// map current i to solutions taxi id
       System.out.println("Taxi " + String.valueOf(i) + " is " + String.valueOf(taxiClientDist.get(i)) + " km away from you.");
    }
    Scanner in = new Scanner(System.in);
    Integer choice = in.nextInt();
    int chosenid = iToId.get(choice);// get taxi Id from i chosen by client
    Node chosenTaxi = taxisInverse.get(chosenid);
    Double chosenCost = taxiClientDist.get(choice); // we call this with i, because astar is called with i and not ID
    // Final aStar to get source - dest result
    HashSet<Node> destination = new HashSet<Node>();
    destination.add(client.dest);
    aStar(client.source , destination, -100);
    // get client source - dest distance
    Double routeCost = taxiClientDist.get(-100);

    Double totalCost = chosenCost + routeCost;
    int level = 0;
    for (ArrayList<ArrayList<Node>> path : allKmls) {
      // Create KML
      String tl = null;

      Visual printer = null;
      if (TOLERANCE > 0) {
        tl = "_tol";
        printer = new Visual(path, "red");
      }
      else {
        tl = "";
        printer = new Visual(path, "green");
      }
      printer.createKML("results/level" + String.valueOf(level)  + tl + ".kml");
      level++;
    }
    // biggest level kml is the source destination route.
    System.out.println("Total route length is " + String.valueOf(totalCost) + " km.");
  }


  public static void main(String[] args) throws IOException, JIPSyntaxErrorException {

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

    for (int i = 0; i < ncases; i++) {
      System.out.println("Testcase #" + i);
      Graph G = new Graph(i, tolerances.get(0), "world.pl");
      for (double t: tolerances) {
        G.TOLERANCE = t;
        System.out.println("Tolerance: " + t);
        G.simulateRides(5);
      }
    }

  }
}
