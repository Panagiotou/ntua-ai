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

  public Visual(ArrayList<ArrayList<Node>> e, String colour) {
    nodesListList = e;
    Set<Node> N = new HashSet<>();
    NodeSet = N;
    color = colour;
    counter = 0;
  }

  private String getColor() {
    if (color == null) {
      counter++;
      if (counter % 2 == 0) return "green";
      else return "blue";
    } else {
      return color;
    }
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

        String kmlStyleGreen =  "\t\t<Style id=\"green\">\n" +
                                "\t\t\t<LineStyle>\n" +
                                "\t\t\t\t<color>ff009900</color>\n" +
                                "\t\t\t\t<width>4</width>\n" +
                                "\t\t\t</LineStyle>\n"+
                                "\t\t</Style>\n";

        String kmlStyleBlue =  "\t\t<Style id=\"blue\">\n" +
                                "\t\t\t<LineStyle>\n" +
                                "\t\t\t\t<color>50F01414</color>\n" +
                                "\t\t\t\t<width>4</width>\n" +
                                "\t\t\t</LineStyle>\n"+
                                "\t\t</Style>\n";

        String kmlStyleRed  =   "\t\t<Style id=\"red\">\n" +
                                "\t\t\t<LineStyle>\n" +
                                "\t\t\t\t<color>ff0000ff</color>\n" +
                                "\t\t\t\t<width>4</width>\n" +
                                "\t\t\t</LineStyle>\n" +
                                "\t\t</Style>\n";


        String kmlEnd = "\t</Document>\n"+
                        "</kml>";

        writer.write(kmlStart);
        writer.write(kmlStyleGreen);
        writer.write(kmlStyleRed);
        writer.write(kmlStyleBlue);
        int i = 0;
        for(ArrayList<Node> nolist: this.nodesListList){
          writer.write(this.createPlacemark(nolist, i));
          i++;
        }
        writer.write(kmlEnd);

    } catch (IOException ex) {
    // Report
    } finally {
       try {writer.close();} catch (Exception ex) {/*ignore*/}
    }
  }
}
