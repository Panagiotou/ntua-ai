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
import java.io.IOException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;


public class Graph {
  private ArrayList<Client> clients;
  private Hashtable<Integer, Node> nodes;
  private int ntest;
  public double TOLERANCE;
  public Prolog pl;
  private Set<Integer> nodeKeys;
  private Hashtable<Node, Integer> taxis;
  private Hashtable<Integer, Node> taxisInverse;

  public Graph(int Ntest, double tol, String world) throws IOException, JIPSyntaxErrorException {
    // creates graph topology
    ntest = Ntest;
    TOLERANCE = tol;
    pl = new Prolog(world);

    clients = pl.getClients();
    nodes = pl.getNodes();
    nodeKeys = nodes.keySet();
    taxis = pl.getTaxis();

    Set<Node> taxiKeys = taxis.keySet();

    taxisInverse = new Hashtable<Integer, Node>();
    for (Node t: taxiKeys) {
      Integer id = taxis.get(t);
      taxisInverse.put(id, t);
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

    for (Integer idx : nodeKeys) {
      Node n = nodes.get(idx);
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

    System.out.println("Starting point is " + s.toString());

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

        System.out.print("Goal coordinates " + current.from.toString());

        try {
          System.out.print(" which corresponds to taxi " + taxis.get(current.from));
          System.out.println(" and came from " + parent.get(current.from).get(parent.get(current.from).size() - 1).first.toString());

        } catch (NullPointerException e) {
          e.printStackTrace();
        } finally {
          System.out.println();
        }

        break;
      }

      if (correct != null && current.actual_distance > gScore.get(correct)) break;

      closedSet.add(current.from);

      ArrayList<Edge> adjacent = pl.getNext(current.from, false, 0);

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
    for (Client client: clients) simulateClient(client);

  }

  public void simulateClient(Client client) throws NullPointerException {
    System.out.println("Serving client: " + client.toString());

    System.out.println("Available taxis to serve");
    ArrayList<Integer> availableTaxis = pl.getGoals(client.clientId);
    System.out.println(availableTaxis.toString());

    // Show ranks
    for (Integer availableTaxiId: availableTaxis) {
      HashSet<Node> goals = new HashSet<Node>();
      Node taxi = taxisInverse.get(availableTaxiId);
      goals.add(taxi);
      aStar(client.source , goals, availableTaxiId);
    }

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
        G.simulateRides();
      }
    }

  }
}
