import java.util.ArrayList;
import java.util.Objects;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class Visual{
  public ArrayList<ArrayList<Node>> nodesListList;
  public Set<Node> NodeSet;
  public int routeId;
  public String color;
  private int counter;
  private final String[] colors = {"red", "blue", "green"};
  private final String[] codes = {"ff0000ff", "50f01414", "ff009900"};

  public Visual(String colour) {
    nodesListList = new ArrayList<ArrayList<Node>>();
    Set<Node> N = new HashSet<>();
    NodeSet = N;
    color = colour;
    counter = 0;

  }

  private String getColor() {
    if (color == null) {
      counter++;
      return colors[counter % colors.length];
    } else return color;
  }

  public void addNodeList(ArrayList<Node> list) {
    nodesListList.add(list);
  }

  public String createPlacemark(ArrayList<Node> nodeList, int id){
    String nodeString = "";
    for(Node no: nodeList){
      if (! this.NodeSet.contains(no)){
        nodeString += "\t\t\t\t\t"+ String.valueOf(no.x) + "," + String.valueOf(no.y) +",0\n";
      }
    }
    String kmlPlacemark =   "\t\t<Placemark>\n" +
                            "\t\t\t<name> Route " + id + " </name>\n"+
                            "\t\t\t<styleUrl>#" + getColor() +  "</styleUrl>\n" +
                            "\t\t\t<LineString>\n" +
                            "\t\t\t\t<altitudeMode>relative</altitudeMode>\n" +
                            "\t\t\t\t<coordinates>\n" +
                            nodeString +
                            "\t\t\t\t</coordinates>\n" +
                            "\t\t\t</LineString>\n" +
                            "\t\t</Placemark>\n";
   return kmlPlacemark;
  }

  public void createKML(String s){
    Writer writer = null;
    try {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(s), "utf-8"));
        String kmlStart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "\t<kml xmlns=\"http://earth.google.com/kml/2.1\">\n"+
                        "\t<Document>\n"+
                        "\t\t<name>Output KML File</name>\n";
        String kmlEnd = "\t</Document>\n"+
                        "</kml>";

        writer.write(kmlStart);
        for (int i = 0; i < colors.length; i++) {
          String kmlStyle =  "\t\t<Style id=\"" + colors[i] + "\">\n" +
          "\t\t\t<LineStyle>\n" +
          "\t\t\t\t<color>" + codes[i] + "</color>\n" +
          "\t\t\t\t<width>4</width>\n" +
          "\t\t\t</LineStyle>\n"+
          "\t\t</Style>\n";
          writer.write(kmlStyle);
        }

        int i = 0;
        for(ArrayList<Node> nolist: this.nodesListList){
          writer.write(this.createPlacemark(nolist, i));
          i++;
        }
        writer.write(kmlEnd);

    } catch (IOException ex) {

    } finally {
       try {
         writer.close();
       } catch (Exception ex) {
         ex.printStackTrace();
       }
    }
  }
}
