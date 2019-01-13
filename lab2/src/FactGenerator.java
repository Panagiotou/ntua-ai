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

  public static boolean isNumeric(String str)
  {
    return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
  }

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

  // Write Prolog File
  public void writeLineDirectionData(String outfile) {
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

      int i = 0;
      for (String[] fact: facts) {
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

  public static void main(String[] argv) {
    // Extract .pl files for all files of the assignment
    FactGenerator nodesFactGenerator = new FactGenerator("nodes", "../resources/data/0/nodes.csv");
    nodesFactGenerator.cutlen = 3;
    nodesFactGenerator.writeData("nodes.pl", true);

    FactGenerator linesFactGenerator = new FactGenerator("lines", "../resources/data/0/lines.csv");
    // linesFactGenerator.writeData("lines.pl", false);
    linesFactGenerator.writeLineDirectionData("lines.pl");

    FactGenerator taxisFactGenerator = new FactGenerator("taxis", "../resources/data/0/taxis.csv");
    taxisFactGenerator.writeData("taxis.pl", false);

    FactGenerator clientFactGenerator = new FactGenerator("client", "../resources/data/0/client.csv");
    clientFactGenerator.writeData("client.pl", true);

    FactGenerator trafficFactGenerator = new FactGenerator("traffic", "../resources/data/0/traffic.csv");
    trafficFactGenerator.writeData("traffic.pl", false);

  }
}
