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

  public Visual(ArrayList<ArrayList<Node>> e, int routeid) {
    this.nodesListList = e;
    Set<Node> N = new HashSet<>();
    this.NodeSet = N;
    this.routeId = routeid;
  }

  public String createPlacemark(ArrayList<Node> nodeList){
    String nodeString = "";
    for(Node no: nodeList){
      if (! this.NodeSet.contains(no)){
        nodeString += "\t\t\t\t\t"+ String.valueOf(no.x) + "," + String.valueOf(no.y) +",0\n";
      }
    }
    String kmlPlacemark =   "\t\t<Placemark>\n" +
                            "\t\t\t<name> Route " + String.valueOf(routeId) + " </name>\n"+
                            "\t\t\t<styleUrl>#green</styleUrl>\n" +
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
                                "\t\t\t\t<width>10</width>\n" +
                                "\t\t\t</LineStyle>\n"+
                                "\t\t</Style>\n";

        String kmlStyleRed  =   "\t\t<Style id=\"red\">\n" +
                                "\t\t\t<LineStyle>\n" +
                                "\t\t\t\t<color>ff009900</color>\n" +
                                "\t\t\t\t<width>4</width>\n" +
                                "\t\t\t</LineStyle>\n" +
                                "\t\t</Style>\n";


        String kmlEnd = "\t</Document>\n"+
                        "</kml>";

        writer.write(kmlStart);
        writer.write(kmlStyleGreen);
        writer.write(kmlStyleRed);
        for(ArrayList<Node> nolist: this.nodesListList){
          writer.write(this.createPlacemark(nolist));
        }
        writer.write(kmlEnd);

    } catch (IOException ex) {
    // Report
    } finally {
       try {writer.close();} catch (Exception ex) {/*ignore*/}
    }
  }
}
