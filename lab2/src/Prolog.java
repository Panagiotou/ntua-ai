import java.io.IOException;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;


public class Prolog {
  private JIPEngine jip;

  private JIPTerm term;
  private JIPTermParser parser;

  public Prolog(String db) throws JIPSyntaxErrorException, IOException {
    jip = new JIPEngine();
    jip.consultFile(db);
		parser = jip.getTermParser();
  }

  // Get taxis (goals) for a certain client
  public ArrayList<Integer> getGoals(int clientId) {
		JIPQuery jipQuery = jip.openSynchronousQuery(parser.parseTerm("goal(" + clientId + ", G)"));
    ArrayList<Integer> result = new ArrayList<Integer>();
    term = jipQuery.nextSolution();
    while (term != null) {
      int resId = Integer.parseInt(term.getVariablesTable().get("G").toString());
      term = jipQuery.nextSolution();
      result.add(resId);

    }
    return result;
  }

  // Get rating for a taxi
  public String getRating(int taxiId) {
    
    JIPQuery jipQuery = jip.openSynchronousQuery(parser.parseTerm("rating(" + String.valueOf(taxiId) + ", G)"));
    term = jipQuery.nextSolution();
    // JIProlog does not support floats very well
    try {
      double rating = Double.parseDouble(term.getVariablesTable().get("G").toString());

      if (rating >= 0 && rating < 5) return "poor";
      else if (rating >= 5 && rating < 7) return "average";
      else if (rating >= 7 && rating < 8) return "good";
      else if (rating >= 8 && rating <= 10) return "exepllent";
      else return "unknown";
    } catch (Exception e) {
      return "unknown";
    }

  }

  // Get adjacent nodes according to a certain client (avoiding high traffic)
  public ArrayList<Edge> getNext(Node u, boolean avoidTraffic, int clientId) {
    JIPQuery jipQuery = null;
    if (avoidTraffic) {
      jipQuery = jip.openSynchronousQuery(parser.parseTerm("nextAvoidingTraffic(" + u.x + "," + u.y + ", X, Y, " + clientId + ", [high])"));

    } else {
      jipQuery = jip.openSynchronousQuery(parser.parseTerm("next(" + u.x + "," + u.y + ", X, Y)"));
    }
    term = jipQuery.nextSolution();
    ArrayList<Edge> result = new ArrayList<Edge>();
    while (term != null) {
      double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
      double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
      term = jipQuery.nextSolution();
      Node n = new Node(x, y);
      Edge e = new Edge(u, n);
      result.add(e);
    }
    return result;
  }


  // Closest point to place taxi or client
  public Node closestPoint(Node u) {
    JIPQuery jipQuery = jip.openSynchronousQuery(parser.parseTerm("getPoint(X, Y)"));
    term = jipQuery.nextSolution();
    double min = Double.MAX_VALUE;
    Node argmin = null;

    while (term != null) {
      double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
      double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
      Node n = new Node(x, y);
      if (u.haversine(n) <= min) {
        min = u.haversine(n);
        argmin = n;
      }

      term = jipQuery.nextSolution();
    }

    return argmin;
  }

  // Get points to hashtable
  public HashSet<Node> getNodes() {
    HashSet<Node> points = new HashSet<Node>();
    JIPQuery jipQuery =jip.openSynchronousQuery(parser.parseTerm("getPoint(X, Y)"));
    term = jipQuery.nextSolution();

    while (term != null) {
      double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
      double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
      Node n = new Node(x, y);
      if (!points.contains(n)) {
        points.add(n);
      }
      term = jipQuery.nextSolution();
    }

    return points;
  }

  // Get taxis to hashtable
  public Hashtable<Node, Integer> getTaxis() {
    Hashtable<Node, Integer> points = new Hashtable<Node, Integer>();
    JIPQuery jipQuery =jip.openSynchronousQuery(parser.parseTerm("getTaxi(I, X, Y)"));
    term = jipQuery.nextSolution();

    while (term != null) {
      int id = Integer.parseInt(term.getVariablesTable().get("I").toString());
      double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
      double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
      Node n = new Node(x, y);
      Node closest = closestPoint(n);
      points.put(closest, id);
      term = jipQuery.nextSolution();
    }

    return points;
  }

  // Get all clients
  public ArrayList<Client> getClients() {
    ArrayList<Client> clients = new ArrayList<Client>();

    JIPQuery jipQuery =jip.openSynchronousQuery(parser.parseTerm("getClient(I, X, Y, U, V)"));
    term = jipQuery.nextSolution();

    while (term != null) {
      int id = Integer.parseInt(term.getVariablesTable().get("I").toString());
      double x = Double.parseDouble(term.getVariablesTable().get("X").toString());
      double y = Double.parseDouble(term.getVariablesTable().get("Y").toString());
      double u = Double.parseDouble(term.getVariablesTable().get("U").toString());
      double v = Double.parseDouble(term.getVariablesTable().get("V").toString());
      Node src = new Node(x, y);
      Node closest_src = closestPoint(src);
      Node dest = new Node(u, v);
      Node closest_dest = closestPoint(dest);
      Client client = new Client(id, closest_src, closest_dest);
      clients.add(client);
      term = jipQuery.nextSolution();
    }

    return clients;
  }


  public static void main(String[] arg) throws IOException, JIPSyntaxErrorException {
    Prolog p = new Prolog("world.pl");
    System.out.println(p.getRating(130));
  }

}
