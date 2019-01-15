import java.nio.charset.Charset;
import java.util.Objects;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.IOException;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/*
 * This class is responsible for converting CSV files
 * to Prolog facts
 *
*/

public class FactGenerator {
  private String factName;
  private String filename;
  public ArrayList<String[]> facts;
  private static final String MASK = "mask";
  private int maxlen;
  public int cutlen;

  public FactGenerator(String faName, String fName) {
      factName = faName;
      filename = fName;
      facts = new ArrayList<String[]>();
      maxlen = 0;
      cutlen = -1;
      parseFacts();

  }

  // String normalization
  public static String normalize(String x) {
    if (x.contains("www")) return MASK;
    return convertDelim(x.replaceAll(" |%","").toLowerCase(), "|");
  }

  // Convert delimiter to Prolog list
  // Example: greek|english becomes [greek, english]
  public static String convertDelim(String x, String delim) {
    if (x.contains(delim)) {
      String replaced = x.replace(delim, ",");
      return "[" + replaced + "]";
    }
    else {
      return x;
    }
  }

  // Checks if chars are pure ascii
  public static boolean isPureAscii(String v) {
    return Charset.forName("US-ASCII").newEncoder().canEncode(v);
  }

  // Filters the array of data
  private String[] filterData(String[] data) {
      for (int i = 0; i < data.length; i++) {
        if (data[i].equals("")) data[i] = MASK;
        if (!isPureAscii(data[i]) && !isNumeric(data[i])) data[i] = MASK;
        data[i] = normalize(data[i]);
      }

      return data;
  }

  // Checks if a string is purely numeric
  public static boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  // Pad a CSV line with masks if needed to match dimensions
  private ArrayList<String> pad(String[] f) {

    ArrayList<String> temp = new ArrayList<String>();
    if (cutlen == -1) {
      for (int i = 0; i < f.length; i++) {
        temp.add(f[i]);
      }
      for (int i = 0; i < maxlen - f.length; i++) {
        temp.add(MASK);
      }
    } else {
      for (int i = 0; i < cutlen; i++) {
        temp.add(f[i]);
      }

    }

    return temp;
  }

  // Parse file to extract facts
  private void parseFacts() {
    String line = "";
    String cvsSplitBy = ",";

    try (BufferedReader br = new BufferedReader(
                              new InputStreamReader(
                              new FileInputStream(filename), "greek"))) {
      line = br.readLine(); // skip labels

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] data = line.split(cvsSplitBy, Integer.MAX_VALUE);
        String[] filtered = filterData(data);

        facts.add(filtered);

        if (filtered.length > maxlen) maxlen = filtered.length;

        }

      } catch (IOException e) {
        e.printStackTrace();
      }

  }

  // Write Prolog File
  public void writeData(String outfile, boolean autoincrement) {
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

      int i = 0;
      for (String[] fact: facts) {
        String joined = String.join(",", pad(fact));
        String result = null;
        if (autoincrement) result = factName + "(" + i + "," + joined + ").\n";
        else result = factName + "(" + joined + ").\n";
        writer.write(result);
        i++;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  }

  // Write Line Directions File
  public void writeLineDirectionData(String outfile) {
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

      int i = 0;
      for (String[] fact: facts) {
        if (fact[1].equals(MASK)) continue;
        String result = "lineDirection" + "(" + fact[0] + "," + fact[3] + ").\n";
        writer.write(result);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  }

  // Write adjacency list file
  public void writeNext(String outfile, FactGenerator lines) {
    Hashtable<Integer, String> directions = new Hashtable<Integer, String>();
    Hashtable<Integer, String> highway = new Hashtable<Integer, String>();
    Hashtable<Integer, String> access = new Hashtable<Integer, String>();

    for (String[] fact: lines.facts) {
      directions.put(Integer.parseInt(fact[0]), fact[3]);
      highway.put(Integer.parseInt(fact[0]), fact[1]);
      access.put(Integer.parseInt(fact[0]), fact[9]);
    }

    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
      for (int i = 0; i < facts.size(); i++) {
        String[] node = facts.get(i);
        int lineid = Integer.parseInt(node[2]);
        String dir = directions.get(lineid);
        if (highway.get(lineid).equals(MASK) || !access.get(lineid).equals(MASK)) {
          continue;
        }

        int j = i;
        String fact = null;
        if (dir.equals("yes") && i < facts.size() - 1) {
          String[] next = facts.get(i + 1);
          int nextlineid = Integer.parseInt(next[2]);
          if (nextlineid != lineid) continue;
          fact = "nextWithLine(" + node[0] + "," + node[1] + "," + next[0] + "," + next[1] + "," + lineid +  ").\n";
          writer.write(fact);
        }
        else if (dir.equals("-1") && i > 0) {
          String[] next = facts.get(i - 1);
          int nextlineid = Integer.parseInt(next[2]);
          if (nextlineid != lineid) continue;
          fact = "nextWithLine(" + node[0] + "," + node[1] + "," + next[0] + "," + next[1] + "," + lineid + ").\n";
          writer.write(fact);
        } else if (i > 0 && i < facts.size() - 1) {
          String[] next = facts.get(i + 1);
          String[] prev = facts.get(i - 1);
          int nextlineid = Integer.parseInt(next[2]);
          int prevlineid = Integer.parseInt(prev[2]);

          String nfact = "";
          String pfact = "";
          if (lineid == nextlineid) {
            nfact = "nextWithLine(" + node[0] + "," + node[1] + "," + next[0] + "," + next[1] + "," + lineid +  ").\n";
          }
          if (lineid == prevlineid) {
            pfact = "nextWithLine(" + node[0] + "," + node[1] + "," + prev[0] + "," + prev[1] + "," + lineid +  ").\n";

          }
          fact = nfact + pfact;
          writer.write(fact);
        }

      }

    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

    }

  }

  public static void main(String[] argv) {
    // Extract .pl files for all files of the assignment
    FactGenerator nodesFactGenerator = new FactGenerator("nodes", "../resources/data/0/nodes.csv");
    nodesFactGenerator.cutlen = 3;
    nodesFactGenerator.writeData("nodes.pl", true);

    FactGenerator linesFactGenerator = new FactGenerator("lines", "../resources/data/0/lines.csv");
    // linesFactGenerator.writeData("lines.pl", false);
    linesFactGenerator.writeLineDirectionData("lines.pl");
    nodesFactGenerator.writeNext("next.pl", linesFactGenerator);

    FactGenerator taxisFactGenerator = new FactGenerator("taxis", "../resources/data/0/taxis.csv");
    taxisFactGenerator.writeData("taxis.pl", false);

    FactGenerator clientFactGenerator = new FactGenerator("client", "../resources/data/0/client.csv");
    clientFactGenerator.writeData("client.pl", true);

    FactGenerator trafficFactGenerator = new FactGenerator("traffic", "../resources/data/0/traffic.csv");
    trafficFactGenerator.writeData("traffic.pl", false);

  }
}
