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
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Rihani Asma
 */
public class CreateCycleWithQueryFichier {

    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();
      Transaction tx4j;// = graphDb.beginTx();

      public CreateCycleWithQueryFichier() {
    }
//methode recuper les noeuds cibles  commun entre  source s et un noeud cible c

    public void IntersectionSC(Node s, Node c, List<Node> cibleNonVisit, CSVWriter writer) {
        tx4j= graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cible", cibleNonVisit);
        ExecutionResult result = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c.getId() + "),c2=node({cible}) where c1--c2 and c2<>s return ID(c2) as id order by ID(c2)", params);
        Iterator n_column = result.columnAs("id");
        int idc2;
        while (n_column.hasNext()) {
            idc2 = (int) (long) n_column.next();
            String[] cycle = {String.valueOf(s.getId()), String.valueOf(idc2), String.valueOf(c.getId())};
             writer.writeNext(cycle);
        }
       tx4j.success();
       tx4j.finish();
    }
    //methode recuper les noeuds cibles  commun entre  source s et un noeud cible c

    public void IntersectionCC(Node s, Node c1, Node c2, CSVWriter writer) {
        tx4j= graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start s=node(" + s.getId() + "), c1=node(" + c1.getId() + "),c2=node(" + c2.getId() + ") match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx");
        Iterator columnidx = result.columnAs("idx");
        int idx;
        while (columnidx.hasNext()) {
            idx = (int) (long) columnidx.next();
            String cycle[] = {String.valueOf(s.getId()), String.valueOf(c1.getId()), String.valueOf(idx), String.valueOf(c2.getId())};
            writer.writeNext(cycle);
        }
  tx4j.success();
       tx4j.finish();
    }

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        //methode enregistre dans fichier
        CreateCycleWithQueryFichier dq = new CreateCycleWithQueryFichier();
        Node c,c2;
        List<Node> cibleNonVisit = new ArrayList<>();
        Node s = dq.cq.FindNode("biologique1");
        List<Node> cibles = dq.cq.getNodeCible(s);
        cibleNonVisit.addAll(cibles);
      // CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream("cycle/"+s.getId()+"S23.csv"), "UTF-8"), '\t',CSVWriter.NO_QUOTE_CHARACTER);
       for (int i = 0; i < cibles.size(); i++) {
            c = cibles.get(i);
           CSVWriter writer1 = new CSVWriter(new OutputStreamWriter(new FileOutputStream("cycle/" + s.getId() + c.getId() + "C4.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
            System.out.println(c.getId() + " cibles");
           /*  String [] cycle={String.valueOf(s.getId()),String.valueOf(c.getId())};
             writer.writeNext(cycle);
             cibleNonVisit.remove(c);
             dq.IntersectionSC(s, c, cibleNonVisit, writer);*/
          for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                dq.IntersectionCC(s, c, c2, writer1);
            }
            writer1.close();
        }
       //  writer.close();
        dq.cq.shutdowndb();
    }
}
