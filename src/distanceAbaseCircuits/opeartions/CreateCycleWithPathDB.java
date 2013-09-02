/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import au.com.bytecode.opencsv.CSVWriter;
import commun.CommunQuery;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CreateCycleWithPathDB {

    public CreateCycleWithPathDB() {
    }
    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();
    Transaction tx4j;// = graphDb.beginTx();
    private enum RelTypes implements RelationshipType {

        OCCUR
    }

    public Map<Node, Integer> getOccurSourceCible(Node s) {
    tx4j= graphDb.beginTx();
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
        tx4j.success();
        tx4j.finish();
        return map;
    }

    //methode permet de recupere le nombre des noeuds source dans un cycle
    public int nombreNodeSourceInCycle(List<Node> listcycle, List<Node> listsource) {
        tx4j= graphDb.beginTx();
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
        tx4j.success();
        tx4j.finish();
        return nbrsource;
    }

      public void cycleSize2_3DB(Node s, Node c, List<Node> aband,EntityManager em,EntityTransaction tx) {
        Path path;
        Iterable<Node> Itnodes;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterable<Path> paths = finder.findAllPaths(s, c);
        Iterator<Path> p = paths.iterator();
        CycleBean cb;
   String chemin;
   tx.begin();
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
                  chemin=s.getId()+"\t"+c.getId();
   cb=new CycleBean();
   cb.setChemin(chemin);
   em.persist(cb);

               }
                if (nodes.size() == 3) {
    
                    chemin=s.getId()+"\t"+c.getId()+"\t"+nodes.get(1).getId();
   cb=new CycleBean();
   cb.setChemin(chemin);
   em.persist(cb);
                }
                
            }
           
        }
    
tx.commit();
       // return listcycle;
    }

    public void cycleSize4DB(Node c1, Node c2, Node s, EntityManager em,EntityTransaction tx) {
        Path path;
        Iterable<Node> Itnodes;
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        List<Node> aband = new ArrayList();
        aband.add(s);
        Iterable<Path> paths = finder.findAllPaths(c1, c2);
        Iterator<Path> p = paths.iterator();
        String chemin;
        CycleBean cb;
        tx.begin();
        while (p.hasNext()) {
            path = p.next();
          Itnodes = path.nodes();
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if (Collections.disjoint(aband, nodes) && nodes.size() >= 3) {
                chemin=s.getId()+"\t"+c1.getId()+"\t"+nodes.get(1).getId()+"\t"+c2.getId();
          cb=new CycleBean();
   cb.setChemin(chemin);
   em.persist(cb); 
            }
        }
        tx.commit();
        //return listcycle;
    }
    

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    EntityManagerFactory emf;
     EntityManager em;
     EntityTransaction tx;
     emf = Persistence.createEntityManagerFactory("approchePossibilistePU");
     em = null;
     // on récupère un EntityManager à  partir de l'EntityManagerFactory précédent
    em = emf.createEntityManager();

           // début transaction
      tx = em.getTransaction();
        CreateCycleWithPathDB dq = new CreateCycleWithPathDB();
        Node c1, c2;
        List<Node> aband = new ArrayList<>();
        Node s = dq.cq.FindNode("biologique1");
        List<Node> cibles = dq.cq.getNodeCible(s);
         for (int i = 0; i < cibles.size(); i++) {
            c1 = cibles.get(i);
            System.out.println(c1.getId());
            
          /* dq.cycleSize2_3DB(s, c1, aband,em,tx);
           
            aband.add(c1);*/
         for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
              dq.cycleSize4DB(c1, c2, s,em,tx);
              
               }
         }
         em.close();
         emf.close();
        dq.cq.shutdowndb();

    }
}
