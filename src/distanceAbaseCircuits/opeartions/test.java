/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import commun.CommunQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Rihani Asma
 */
public class test {

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        CommunQuery cq = new CommunQuery();
        CreateCycleWithPathFichier dqPath = new CreateCycleWithPathFichier(cq);
        List<Node> source = cq.getAllNodeSource("biologique1");
        for(Node s:source) {
         System.out.println("source "+s.getId());
         File file = new File("cycle/"+s.getProperty("texte"));
         String filePath=file.getPath();
         if (file.mkdir()) {
         System.out.println("Ajout du dossier : " + file.getPath());
         } 
        // dqPath.creationFichier(filePath, s);
        dqPath.calculDataCycle2_3(filePath, s, source);
         dqPath.calculDataCycle4(filePath, s, source);
         }
      
        cq.shutdowndb();
    }
}
