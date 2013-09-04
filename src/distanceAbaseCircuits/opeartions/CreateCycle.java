/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import au.com.bytecode.opencsv.CSVWriter;
import commun.Neo4jQuery;
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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CreateCycle {

    private Neo4jQuery cq;

    public CreateCycle(Neo4jQuery cq) {
        this.cq = cq;
    }

    public CreateCycle() {
    }

    private enum RelTypes implements RelationshipType {

        OCCUR
    }

    public void cycleSize2WithData(Node s, Node c, List<Node> source, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        Iterable<Relationship> Itrels;
        int occsc1, Nsi;
        float psi;
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

            //methode permet de recupere les relations de chemin
            Itrels = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList();
            CollectionUtils.addAll(rels, Itrels.iterator());
            Nsi = CollectionUtils.intersection(nodes, source).size();
            occsc1 = (int) rels.get(0).getProperty("OCCUR");
            psi = (float) (occsc1 / Nsi);
            String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(psi)};
            writer.writeNext(cycle);
        }
    }

    public void cycleSize2_3WithData(Node s, Node c, List<Node> source, Map<Node, Integer> occurSC, List<Node> aband, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        int occsc1, occsc2, occc1c2, Nsi;
        Node c2;
        float Mai;
        Iterable<Relationship> Itrels;
        float psi;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterable<Path> paths = finder.findAllPaths(s, c);
        Iterator<Path> p = paths.iterator();
        occsc1 = occurSC.get(c);
        while (p.hasNext()) {
            //chemin recuperer
            path = p.next();
            //methode permet de recupere les noeuds formant le chemin
            Itnodes = path.nodes();
            //transformer l'Iterable à une list
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            //methode permet de recupere les relations de chemin
            Itrels = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList();
            CollectionUtils.addAll(rels, Itrels.iterator());
            //eliminer les chemins qui se repete
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (occsc1 / Nsi);
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(psi)};
                    writer.writeNext(cycle);
                }
                if (nodes.size() == 3) {
                    //construction de cycle s--c1--c2--s
                    c2 = nodes.get(1);
                    //System.out.println("this is c2 "+c2.getId());
                    occsc2 = occurSC.get(c2);
                    occc1c2 = (int) rels.get(1).getProperty("OCCUR");
                    Mai = (float) (occsc1 + occsc2 + occc1c2) / 3;
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (Mai / Nsi);
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(c2.getId()), String.valueOf(psi)};
                    writer.writeNext(cycle);
                }

            }

        }
    }

     public void cycleSize4WithData(Node s, Node c1, Node c2,List<Node> source,Map<Node,Integer> occurSc, CSVWriter writer) {
     Path path;
     Iterable<Node> Itnodes;
     Iterable<Relationship> Itrels;
     PathFinder<Path> finder = GraphAlgoFactory.allPaths(
     Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
     /*List<Node> aband = new ArrayList<>();
     aband.add(s);*/
     Iterable<Path> paths = finder.findAllPaths(c1, c2);
     Iterator<Path> p = paths.iterator();
     int occsc1=occurSc.get(c1);
     // System.out.println("s "+s.getId() +" c1 "+c1.getId()+" occur==> "+occsc1);
     int occsc2=occurSc.get(c2);
     // System.out.println("s "+s.getId() +" c2 "+c2.getId()+" occur==> "+occsc2);
     int occc1x,occc2x,Nsi;
     float Mai,psi;
     Node x;
     while (p.hasNext()) {
     path = p.next();
     Itnodes = path.nodes();
     List<Node> nodes = new ArrayList();
     CollectionUtils.addAll(nodes, Itnodes.iterator());
     if ((!nodes.contains(s)) && nodes.size() >= 3) {
          Itrels = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList();
            CollectionUtils.addAll(rels, Itrels.iterator());
            x=nodes.get(1);
     occc1x=(int) rels.get(0).getProperty("OCCUR");
     // System.out.println("c1 "+c1.getId() +" x "+nodes.get(1).getId()+" occur==> "+occc1x);
     occc2x=(int) rels.get(1).getProperty("OCCUR");
     //System.out.println("c2 "+c2.getId() +" x "+nodes.get(1).getId()+" occur==> "+occc2x);
     Mai=(float)(occsc1+occsc2+occc1x+occc2x)/4;
     nodes.add(s);
     Nsi = CollectionUtils.intersection(nodes, source).size();
     psi=(float)(Mai/Nsi);
     String[] cycle = {String.valueOf(s.getId()), String.valueOf(c1.getId()), String.valueOf(x.getId()),String.valueOf(c2.getId()),String.valueOf(psi)};
     writer.writeNext(cycle);

     }
     }
     }


    /* public void creationFichierWithData(String file,Node s,List<Node> source,Map<Node,Integer> occurSc) throws UnsupportedEncodingException, FileNotFoundException, IOException {

     Node c1, c2;
     List<Node> aband = new ArrayList<>();
     List<Node> cibles = cq.getNodeCible(s);
     CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/cycle2_3.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
     for (int i = 0; i < cibles.size(); i++) {
     c1 = cibles.get(i);
     CSVWriter writer1 = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/"+i+"cycle4.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
     System.out.println(c1.getId() + " cibles");
     cycleSize2_3WithData(s, c1,source,occurSc,aband,writer);
     aband.add(c1);
     for (int j = i + 1; j < cibles.size(); j++) {
     c2 = cibles.get(j);
     cycleSize4WithData(s, c1, c2,source,occurSc, writer1);
     }
     writer1.close();

     }
     writer.close();


     }*/
    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        Neo4jQuery cq = new Neo4jQuery();
        CreateCycle dq = new CreateCycle(cq);
        Node c1, c2;
        List<Node> source = cq.getAllNodeSource("clair");
        List<Node> aband = new ArrayList();
        Node s = cq.FindNode("clair");
        Map<Node,Integer> occurSC=cq.getOccurSC(s);
        List<Node> cibles = dq.cq.getNodeCible(s);
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream("cycle/" + s.getId() + "cycle.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
       /* for (Node c : cibles) {
            System.out.println(c.getId());
            dq.cycleSize2_3WithData(s, c, source,occurSC,aband, writer);
             aband.add(c);
        }*/
         for (int i = 0; i < cibles.size(); i++) {
             c1 = cibles.get(i);
            // System.out.println(c1.getId());
            dq.cycleSize2_3WithData(s, c1, source,occurSC,aband, writer);
             aband.add(c1);
       //  CSVWriter writer1 = new CSVWriter(new OutputStreamWriter(new FileOutputStream("cycle/" + c1.getId() + "pathC4.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
         System.out.println(c1.getId() + " cibles");
         for (int j = i + 1; j < cibles.size(); j++) {
         c2 = cibles.get(j);
         dq.cycleSize4WithData(s, c1, c2,source,occurSC,writer);
         }
        // writer1.close();
           
         }
        writer.close();
        cq.shutdowndb();

    }
}
