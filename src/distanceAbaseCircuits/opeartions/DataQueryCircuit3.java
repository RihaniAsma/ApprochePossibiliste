/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import commun.CommunQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class DataQueryCircuit3 {

    public DataQueryCircuit3() {
    }
    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();

    private enum RelTypes implements RelationshipType {

        OCCUR
    }

    public Map<Node, Integer> getOccurSourceCible(Node s) {

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start n=node(" + s.getId() + ") match n--c return c order by ID(c)");
        ExecutionResult result2 = engine.execute("start n=node(" + s.getId() + ") match n-[r:OCCUR]-c return r.OCCUR as occur order by ID(c)");
        Map<Node, Integer> map = new HashMap();
        Iterator<Node> n_column = result.columnAs("c");
        Iterator occurence = result2.columnAs("occur");
        while (n_column.hasNext() && occurence.hasNext()) {
            Node n = n_column.next();
            // Long id = n.getId();
            int nbrnode = (int) occurence.next();
            map.put(n, nbrnode);
            // System.out.println(id+" ==> " +maxoccur);
        }
        return map;
    }

    //methode permet de recupere le nombre des noeuds source dans un cycle
    public int nombreNodeSourceInCycle(List<Node> listcycle, List<Node> listsource) {
        int nbrsource = 0;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cycle", listcycle);
        params.put("source", listsource);
        ExecutionResult result = engine.execute("start c=node({cycle}),s=node({source})where c=s return  count(c) as b", params);
        Iterator column = result.columnAs("b");
        if (column.hasNext()) {
            nbrsource = (int) (long) column.next();
        }
        return nbrsource;
    }

    public List<CycleData> cycleSize2_3(Node s, Node c, List<Node> aband, int occurSc1) {
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        int i = 0, nbrsource, occurSc2, occurc1c2;
        float Mai;
        Path path;
        Iterable<Node> Itnodes;
        Iterable<Relationship> Itrel;
        //penser à le passer en parametre
        List<Node> source = new ArrayList<>();
        source.add(s);
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
            List<Node> nodes = new ArrayList<Node>();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            Itrel = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList<Relationship>();
            CollectionUtils.addAll(rels, Itrel.iterator());
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    cd = new CycleData();
                    //ajout des noeuds de sycle
                    cd.getListnode().addAll(nodes);
                    cd.setMai(occurSc1);
                    //cd.setListnode(cd.getListnode());
                    nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                    cd.setNbsource(nbrsource);
                    // System.out.println("taille cycle "+cd.getListnode().size());
                    listcycle.add(cd);
                    // System.out.println("ce cycle est crée " + s.getId() + "==>" + c.getId()+" de Mai "+cd.getMai());
                }
                if (nodes.size() == 3) {
                    //construction de cycle s--c1--c2--s
                    cd = new CycleData();
                    cd.getListnode().addAll(nodes);
                    //calcule de Mai :Somme des relationship property et occurSc1
                    occurSc2 = (int) rels.get(0).getProperty("OCCUR");
                    occurc1c2 = (int) rels.get(1).getProperty("OCCUR");
                    /*System.out.println("occursc1 "+occurSc1);
                     System.out.println("occursc2 "+occurSc2);
                     System.out.println("occurc1c2 "+occurc1c2);*/
                    Mai = (float) (occurSc1 + occurSc2 + occurc1c2) / 3;
                    // System.out.println("Mai  "+Mai);
                    cd.setMai(Mai);
                    nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                    cd.setNbsource(nbrsource);
                    listcycle.add(cd);
                    // System.out.println("ce cycle est crée " + s.getId() + "==>" + c.getId()+"==>"+nodes.get(1).getId()+" de Mai "+cd.getMai());

                }
                i++;
            }
        }
// System.out.println(i);
        return listcycle;
    }

    public List<CycleData> cycleSize4(Node c1, Node c2, Node s, int occurSc1, int occurSc2) {
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        int i = 0, nbrsource, occurc1x, occurc2x;
        float Mai;
        Path path;
        Iterable<Node> Itnodes;
        Iterable<Relationship> Itrel;
        //penser à le passer en parametre
        List<Node> source = new ArrayList<>();
        source.add(s);
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        List<Node> aband = new ArrayList<>();
        aband.add(s);
        Iterable<Path> paths = finder.findAllPaths(c1, c2);
        Iterator<Path> p = paths.iterator();
        while (p.hasNext()) {
            path = p.next();
        
            Itnodes = path.nodes();
            List<Node> nodes = new ArrayList<Node>();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            Itrel = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList<Relationship>();
            CollectionUtils.addAll(rels, Itrel.iterator());
            if (Collections.disjoint(aband, nodes) && nodes.size() >= 3) {
//System.out.println(path);
                //construction de cycle s--c1--c2--s
                cd = new CycleData();
                cd.addNode(s);
                cd.getListnode().addAll(nodes);
                //calcule de Mai :Somme des relationship property et occurSc1
                occurc1x = (int) rels.get(0).getProperty("OCCUR");
                occurc2x = (int) rels.get(1).getProperty("OCCUR");
                // System.out.println("occursc1 "+occurSc1);
                // System.out.println("occursc2 "+occurSc2);
                // System.out.println("occurc1c2 "+occurc1x);
                // System.out.println("occurc2x "+occurc2x);
                Mai = (float) (occurSc1 + occurSc2 + occurc1x + occurc2x) / 4;
                // System.out.println("Mai  "+Mai);
                cd.setMai(Mai);
                nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                cd.setNbsource(nbrsource);
                listcycle.add(cd);
                // System.out.println("ce cycle est crée " + s.getId() + "==>" + c1.getId()+"==>"+nodes.get(1).getId()+"==>"+c2.getId()+" de Mai "+cd.getMai());

                i++;
            }
        }
        return listcycle;
    }

    public static void main(String[] args) {
        DataQueryCircuit3 dq = new DataQueryCircuit3();
        List<CycleData> allcycle = new ArrayList<>();
        List<CycleData> cycleS23 = new ArrayList<>();
        List<CycleData> cycleS4 = new ArrayList<>();
        Node s = dq.cq.FindNode("biologique1");
        List<Node> cibles = dq.cq.getNodeCibleOfSource(s);
        Map<Node, Integer> occurSc1map = dq.getOccurSourceCible(s);
        List<Node> aband = new ArrayList<>();
        Node c1, c2;
        int x, occurSc1, occurSc2;
        for (int i = 0; i < cibles.size(); i++) {
            c1 = cibles.get(i);
            occurSc1 = occurSc1map.get(c1);
            cycleS23 = dq.cycleSize2_3(s, c1, aband, occurSc1);
            x = cycleS23.size();
            allcycle.addAll(cycleS23);
            System.out.println("nbr des cycle de taille 2 et 3 de noued " + c1.getId() + " est " + x);
            aband.add(c1);
            for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                occurSc2 = occurSc1map.get(c2);
                cycleS4 = dq.cycleSize4(c1, c2, s, occurSc1, occurSc2);
                x = cycleS4.size();
                allcycle.addAll(cycleS4);
                System.out.println("nbr des cycle de taille 4 de noued " + c1.getId() + " et de noeud " + c2.getId() + " est " + x);
            }
        }
        System.out.println(allcycle.size());
        dq.cq.shutdowndb();

    }
}
