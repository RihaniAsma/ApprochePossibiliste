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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Rihani Asma
 */
public class DataQueryDB {

    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();

    public DataQueryDB() {
    }
//methode recuper les noeuds cibles  commun entre  source s et un noeud cible c
    public void IntersectionSC(Node s, Node c, List<Node> cibleNonVisit,EntityManager em,EntityTransaction tx) {
        String chemin;
       CycleBean cb;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cible", cibleNonVisit);
        ExecutionResult result = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c.getId() + "),c2=node({cible}) where c1--c2 and c2<>s return ID(c2) as id order by ID(c2)", params);
        Iterator n_column = result.columnAs("id");
        int idc2;
        tx.begin();
        while (n_column.hasNext()) {
          cb=new CycleBean();
          idc2=(int)(long) n_column.next();
         chemin=s.getId()+"\t"+idc2+"\t"+c.getId();
         cb.setChemin(chemin);
         em.persist(cb);
          //System.out.println(chemin);
        }
      tx.commit();
    }
    //methode recuper les noeuds cibles  commun entre  source s et un noeud cible c
    public void IntersectionCC(Node s,Node c1,Node c2,EntityManager em,EntityTransaction tx) {//,List<Node> cibleNonVisit,EntityManager em,EntityTransaction tx)
       /* String chemin;
        CycleBean cb;
         ExecutionEngine engine = new ExecutionEngine(graphDb);
         Map<String, Object> params = new HashMap<String, Object>();
       params.put("cible", cibleNonVisit);
        ExecutionResult result = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(c2) as idc2 order by ID(c2)",params);
        Iterator columnidc2 = result.columnAs("idc2");
         ExecutionResult result1 = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx order by ID(c2)",params);
        Iterator columnidx = result1.columnAs("idx");
        int idc2,idx;
         tx.begin();
        while (columnidc2.hasNext()&& columnidx.hasNext()) { 
          idc2=(int)(long) columnidc2.next();
          idx=(int)(long) columnidx.next();
            cb=new CycleBean();
          chemin=s.getId()+"\t"+c1.getId()+"\t"+idx+"\t"+idc2;
          cb.setChemin(chemin);
          em.persist(cb);
          //System.out.println(chemin); 
        }
        tx.commit();*/
        String chemin;
        CycleBean cb;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node(" + c2.getId() + ") match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx");
         Iterator columnidx = result.columnAs("idx");
        int idx;
        tx.begin();
        while (columnidx.hasNext()) { 
          idx=(int)(long) columnidx.next();
            cb=new CycleBean();
          chemin=s.getId()+"\t"+c1.getId()+"\t"+idx+"\t"+c2.getId();
         // System.out.println(chemin); 
          cb.setChemin(chemin);
          em.persist(cb);
          //System.out.println(chemin); 
        }
        tx.commit();
    }
    
     //methode recuper les noeuds cibles  commun entre  source s et un noeud cible c
    public void IntersectionCC1(Node s,Node c1,List<Node> cibleNonVisit,EntityManager em,EntityTransaction tx){
        String chemin;
        CycleBean cb;
         ExecutionEngine engine = new ExecutionEngine(graphDb);
         Map<String, Object> params = new HashMap<String, Object>();
       params.put("cible", cibleNonVisit);
        ExecutionResult result = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(c2) as idc2 order by ID(c2)",params);
        Iterator columnidc2 = result.columnAs("idc2");
         ExecutionResult result1 = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx order by ID(c2)",params);
        Iterator columnidx = result1.columnAs("idx");
        int idc2,idx;
         tx.begin();
        while (columnidc2.hasNext()&& columnidx.hasNext()) { 
          idc2=(int)(long) columnidc2.next();
          idx=(int)(long) columnidx.next();
            cb=new CycleBean();
          chemin=s.getId()+"\t"+c1.getId()+"\t"+idx+"\t"+idc2;
          cb.setChemin(chemin);
          em.persist(cb);
          //System.out.println(chemin); 
        }
        tx.commit();
       /* String chemin;
        CycleBean cb;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node(" + c2.getId() + ") match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx");
         Iterator columnidx = result.columnAs("idx");
        int idx;
        tx.begin();
        while (columnidx.hasNext()) { 
          idx=(int)(long) columnidx.next();
            cb=new CycleBean();
          chemin=s.getId()+"\t"+c1.getId()+"\t"+idx+"\t"+c2.getId();
         // System.out.println(chemin); 
          cb.setChemin(chemin);
          em.persist(cb);
          //System.out.println(chemin); 
        }
        tx.commit();*/
    }
     public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException  {
     EntityManagerFactory emf;
     EntityManager em;
     EntityTransaction tx;
     emf = Persistence.createEntityManagerFactory("approchePossibilistePU");
     em = null;
     // on récupère un EntityManager à  partir de l'EntityManagerFactory précédent
    em = emf.createEntityManager();

           // début transaction
      tx = em.getTransaction();
        DataQueryDB dq = new DataQueryDB();
      Node c1,c2;
        Node s = dq.cq.FindNode("biologique1");
        List<Node> cibles=dq.cq.getNodeCible(s);
        List<Node>cibleNonVisit=new ArrayList();
         cibleNonVisit.addAll(cibles);
         for(int i=0;i<cibles.size();i++){
            c1=cibles.get(i);
                System.out.println(c1.getId());
                cibleNonVisit.remove(c1);
    dq.IntersectionCC1(s, c1,cibleNonVisit, em, tx);
          
        }
      /* for(int i=0;i<cibles.size();i++){
            c1=cibles.get(i);
                System.out.println(c1.getId());
          for(int j=i+1;j<cibles.size();j++){
            c2=cibles.get(j);
    dq.IntersectionCC(s, c1,c2, em, tx);
          }
        }*/
        dq.cq.shutdowndb();
      em.close();
      emf.close();

    }
}
