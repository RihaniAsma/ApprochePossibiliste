/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import au.com.bytecode.opencsv.CSVWriter;
import commun.CommunQuery;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CreateCycleWithPathFichier {
   private CommunQuery cq;

    public CreateCycleWithPathFichier(CommunQuery cq) {
        this.cq = cq;
    }
  
    private enum RelTypes implements RelationshipType {

        OCCUR
    }

  
    public void cycleSize2_3(Node s, Node c, List<Node> aband, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterable<Path> paths = finder.findAllPaths(s, c);
        Iterator<Path> p = paths.iterator();
        while (p.hasNext()) {
            //chemin recuperer
            path = p.next();
            //methode permet de recupere les noeuds formant le chemin
            Itnodes = path.nodes();
            //transformer l'Iterable à une list
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId())};
                    writer.writeNext(cycle);
                }
                if (nodes.size() == 3) {
                    //construction de cycle s--c1--c2--s
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(nodes.get(1).getId())};
                    writer.writeNext(cycle);
                }

            }

        }
    }

    public void cycleSize4(Node s, Node c1, Node c2, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        List<Node> aband = new ArrayList<>();
        aband.add(s);
        Iterable<Path> paths = finder.findAllPaths(c1, c2);
        Iterator<Path> p = paths.iterator();
        while (p.hasNext()) {
            path = p.next();
            Itnodes = path.nodes();
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if (Collections.disjoint(aband, nodes) && nodes.size() >= 3) {
                String[] cycle = {String.valueOf(s.getId()), String.valueOf(c1.getId()), String.valueOf(nodes.get(1).getId()), String.valueOf(c2.getId())};
                writer.writeNext(cycle);

            }
        }
    }
    public void creationFichier(String file,Node s) throws UnsupportedEncodingException, FileNotFoundException, IOException {

        Node c1, c2;
        List<Node> aband = new ArrayList<>();
        //Node s = cq.FindNode("biologique1");
        List<Node> cibles = cq.getNodeCible(s);
        // for(Node c:cibles)
        // System.out.print(c.getId()+",");
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/cycle2_3.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
        for (int i = 0; i < cibles.size(); i++) {
            c1 = cibles.get(i);
            CSVWriter writer1 = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/"+i+"cycle4.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
            System.out.println(c1.getId() + " cibles");
            cycleSize2_3(s, c1, aband,writer);
             aband.add(c1);
            for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                cycleSize4(s, c1, c2, writer1);
            }
            writer1.close();

        }
        writer.close();


    }
    
     public void calculDataCycle(String file,Node s){}
}
