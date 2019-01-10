import java.io.IOException;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import java.util.ArrayList;


public class Prolog {
  private JIPEngine jip;
  private JIPQuery jipQuery;
  private JIPTerm term;
  private JIPTermParser parser;

  public Prolog(String db) throws JIPSyntaxErrorException, IOException {
    jip = new JIPEngine();
    jip.consultFile(db);
		parser = jip.getTermParser();
  }

  // Get taxis (goals) for a certain client
  public ArrayList<Integer> goals(int clientId) {
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("goal(" + clientId + ", G)"));
    term = jipQuery.nextSolution();
    ArrayList<Integer> result = new ArrayList<Integer>();
    while (term != null) {
      int resId = Integer.parseInt(term.getVariablesTable().get("G").toString());
      term = jipQuery.nextSolution();
      result.add(resId);
    }
    return result;
  }

  // Get adjacent nodes according to a certain client (avoiding high traffic)
  public ArrayList<Edge> next(Node u, boolean avoidTraffic, int clientId) {
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
      Edge e = new Edge(u, n, 0);
      result.add(e);
    }
    return result;
  }


  // Closest point to place taxi or client
  public Node closestPoint(Node u) {
    jipQuery = jip.openSynchronousQuery(parser.parseTerm("getPoint(I, X, Y)"));
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



  public static void main(String[] args) throws JIPSyntaxErrorException, IOException {
    Prolog pl = new Prolog("world.pl");

      Node u = new Node(23.7614542,37.9864972);
      ArrayList<Edge> l = pl.next(u, true, 0);
      System.out.println(l.toString());



  }

}
