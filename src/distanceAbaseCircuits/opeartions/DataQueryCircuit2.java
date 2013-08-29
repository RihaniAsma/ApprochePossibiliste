/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import commun.CommunQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Rihani Asma
 */
public class DataQueryCircuit2 {

    public DataQueryCircuit2() {
    }
    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();

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

    public int getOccurC1C2(Node c1, Node c2) {
        int occur = 0;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start c1=node(" + c1.getId() + "),c2=node(" + c2.getId() + ") match c1-[r:OCCUR]-c2 return r.OCCUR as occur");
        Iterator occurence = result.columnAs("occur");
        if (occurence.hasNext()) {
            occur = (int) occurence.next();
        }
        return occur;
    }

    public void cycle(Node s) {
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        List<Node> source = new ArrayList<>();
        source.add(s);
        Map<Node, Integer> occurSourceCible = getOccurSourceCible(s);
        Node c1, c2, x;
        int occurC1C2,nbrsource,occurSC1,occurSC2,occurC1X,occurC2X;
        float Mai;
        // Obtenir la liste des noeuds cible
        List<Node> listCible = new ArrayList(occurSourceCible.keySet());
        for (int i = 0; i <listCible.size(); i++) {
            c1 = listCible.get(i);
            //formeration de cycle s--c1--s
             cd = new CycleData();
            cd.addNode(s);
            cd.addNode(c1);
            occurSC1=occurSourceCible.get(c1);
            cd.setMai(occurSC1);
            cd.setListnode(cd.getListnode());
            //calcul nombre soucre dans cycle
            nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
            cd.setNbsource(nbrsource);
             listcycle.add(cd);
          System.out.println("ce cycle est crée " + s.getId() + "==>" + c1.getId());
          for (int j = i + 1; j <listCible.size(); j++) {
                c2 = listCible.get(j);
                occurC1C2 = getOccurC1C2(c1, c2);
                if (occurC1C2 != 0) {
                    //formeration de cycle s--c1--c2--s
                     //cycle de format s--c1--c2--s
                cd = new CycleData();
                cd.addNode(s);
                cd.addNode(c1);
                cd.addNode(c2);
                //calcul Mai occur(s,c1)+occur(c1,c2)+occur(s,c2)
                occurSC2 = occurSourceCible.get(c2);//occurence entre c2 et s
                Mai = (float) (occurSC1 + occurC1C2 + occurSC2) / 3;
                // System.out.println(Mai);
                cd.setMai(Mai);
                cd.setListnode(cd.getListnode());
                //calcul nombre soucre dans cycle
                nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                cd.setNbsource(nbrsource);

                //  System.out.println("taille cycle "+cd.getListnode().size());
                listcycle.add(cd);
              System.out.println("ce cycle est crée " + s.getId() + "==>" + c1.getId() + "==>" + c2.getId() + " de mai " + cd.getMai());
            
                }

            }
        }

    }
     public static void main(String[] args) {
     System.out.println(new Date());
     DataQueryCircuit2 dqc = new DataQueryCircuit2();
     Node s = dqc.cq.FindNode("biologique1");
     // Node c=dqc.cq.FindNode("biologique1");
     dqc.cycle(s);
     dqc.cq.shutdowndb();
     }
}
