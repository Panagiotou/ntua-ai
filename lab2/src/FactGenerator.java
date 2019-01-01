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

  public FactGenerator(String faName, String fName) {
      factName = faName;
      filename = fName;
      facts = new ArrayList<String[]>();
      maxlen = 0;
      parseFacts();

  }

  // String normalization
  public static String normalize(String x) {
    return x.replaceAll(" |%","").toLowerCase();
  }

  // Checks if chars are pure ascii
  public static boolean isPureAscii(String v) {
    return Charset.forName("US-ASCII").newEncoder().canEncode(v);
  }

  // Filters the array of data
  private String[] filterData(String[] data) {
      for (int i = 0; i < data.length; i++) {
        if (data[i].equals("")) data[i] = "nil";
        if (!isPureAscii(data[i])) data[i] = MASK;
        data[i] = normalize(data[i]);
      }

      return data;
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
        String joined = String.join(",", fact);
        String result = null;
        if (autoincrement) result = factName + "(" + joined + "," + i + ").\n";
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


  public static void main(String[] argv) {
    // Extract .pl files for all files of the assignment
    FactGenerator nodesFactGenerator = new FactGenerator("nodes", "../resources/data/0/nodes.csv");
    nodesFactGenerator.writeData("nodes.pl", true);

    FactGenerator linesFactGenerator = new FactGenerator("lines", "../resources/data/0/lines.csv");
    linesFactGenerator.writeData("lines.pl", false);

    FactGenerator taxisFactGenerator = new FactGenerator("taxis", "../resources/data/0/taxis.csv");
    taxisFactGenerator.writeData("taxis.pl", false);

    FactGenerator clientFactGenerator = new FactGenerator("client", "../resources/data/0/client.csv");
    clientFactGenerator.writeData("client.pl", false);

    FactGenerator trafficFactGenerator = new FactGenerator("traffic", "../resources/data/0/traffic.csv");
    trafficFactGenerator.writeData("traffic.pl", false);

  }
}
