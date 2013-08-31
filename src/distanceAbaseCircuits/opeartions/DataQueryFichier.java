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
public class DataQueryFichier {

    CommunQuery cq = new CommunQuery();
    GraphDatabaseService graphDb = cq.getGraphDb();

    public DataQueryFichier() {
    }
//methode recuper les noeuds cibles  commun entre  source s et un noeud cible c
    public void IntersectionSC(Node s, Node c, List<Node> prevcible,CSVWriter writer){
        List<String[]> chemins=new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cible", prevcible);
        ExecutionResult result = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c.getId() + "),c2=node({cible}) where c1--c2 and c2<>s return ID(c2) as id order by ID(c2)", params);
        Iterator n_column = result.columnAs("id");
        int idc2;
        while (n_column.hasNext()) {
          idc2=(int)(long) n_column.next();
          String [] cycle={String.valueOf(s.getId()),String.valueOf(idc2),String.valueOf(c.getId())};
          chemins.add(cycle);
        }
        writer.writeAll(chemins);
        chemins.clear();
    }
    //methode recuper les noeuds cibles  commun entre  source s et un noeud cible c
    public void IntersectionCC(Node s,Node c1, List<Node> prevcibles,CSVWriter writer) {
       
        ExecutionEngine engine = new ExecutionEngine(graphDb);
         Map<String, Object> params = new HashMap<String, Object>();
        params.put("cible", prevcibles);
        ExecutionResult result = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(c2) as idc2 order by ID(c2)",params);
        Iterator columnidc2 = result.columnAs("idc2");
         ExecutionResult result1 = engine.execute("start s=node("+s.getId()+"), c1=node(" + c1.getId() + "),c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return ID(x) as idx order by ID(c2)",params);
        Iterator columnidx = result1.columnAs("idx");
        int idx,idc2;
       while (columnidc2.hasNext()&& columnidx.hasNext()) {
          idc2=(int)(long) columnidc2.next();
          idx=(int)(long) columnidx.next();
        
        }
     
    }
     public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException  {
         //methode enregistre dans fichier
   CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream("cycle/path.csv"), "UTF-8"), '\t',CSVWriter.NO_QUOTE_CHARACTER);
       DataQueryFichier dq = new DataQueryFichier();
       Node c;
          List<Node>prevcibles=new ArrayList<>();
        Node s = dq.cq.FindNode("biologique1");
        List<Node> cibles=dq.cq.getNodeCible(s);
        for(int i=0;i<cibles.size();i++){
            c=cibles.get(i);
             System.out.println(c.getId()+" cibles");
    dq.IntersectionSC(s, c, prevcibles,writer);
    prevcibles.add(c);
        }
        writer.close();
        dq.cq.shutdowndb();
    }
}
