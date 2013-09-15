/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.ReadableIndex;

/**
 *
 * @author Rihani Asma
 */
public class Neo4jQuery {
    
     GraphDatabaseService graphDb;/* = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder("C:/Users/acer/Desktop/NEO4J-HOME/data/graph.db").
            setConfig(GraphDatabaseSettings.node_keys_indexable, "texte").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
             setConfig(GraphDatabaseSettings.mapped_memory_page_size, "60M").
             setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "60M").
             setConfig(GraphDatabaseSettings.query_cache_size, "600000").
             setConfig(GraphDatabaseSettings.relationshipstore_mapped_memory_size, "60M").
            newGraphDatabase();*/
    Transaction tx4j;// = graphDb.beginTx();

    public Neo4jQuery() {
    }

    public void run() {
      graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder("C:/Users/acer/Desktop/NEO4J-HOME/data/graph.db").
            setConfig(GraphDatabaseSettings.node_keys_indexable, "texte").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
             setConfig(GraphDatabaseSettings.mapped_memory_page_size, "60M").
             setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "60M").
             setConfig(GraphDatabaseSettings.query_cache_size, "600000").
             setConfig(GraphDatabaseSettings.relationshipstore_mapped_memory_size, "60M").
            newGraphDatabase();
    }

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }
    
    
/*************************CommunQuery***********************************************/
    /**
     * verif si un noeud existe dans la base ou pas
     *
     * @param label un lemma de requete
     * @return le noeud correspondant s'il existe dans la base
     */
    public Node FindNode(String label) {
       // tx4j = graphDb.beginTx();
        Node find = null;
        ReadableIndex<Node> autoNodeIndex = graphDb.index()
                .getNodeAutoIndexer()
                .getAutoIndex();
        find = autoNodeIndex.get("texte", label).getSingle();
      //  tx4j.success();
      //  tx4j.finish();
        return find;
    }
   
    /**
     * recup les id des noeuds
     *
     * @param req requete entre par l'user
     * @return id node
     */
    public List<Long> getNodeId(String req) {

        List<Long> list_id = new ArrayList();
        String[] split_req = req.toLowerCase().split(" ");
        for (String mot : split_req) {
            Node n = FindNode(mot);
            if (n != null) {
                list_id.add(n.getId());
            } else {
                System.out.println(mot + " n'existe pas dans le graph");
            }
        }
        return list_id;

    }

    /**
     * recup list noeud source
     *
     * @param req requete entré par l'user
     * @return les noeuds correspondant au lemma de requete dans la base s'il
     * existe
     */
    public List<Node> getAllNodeSource(String req) {
        tx4j = graphDb.beginTx();
        List list_node = new ArrayList();
        List list_id = getNodeId(req);
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", list_id);
        ExecutionResult result = engine.execute("start n=node({id}) return distinct n order by ID(n)", params);
        Iterator<Node> n_column = result.columnAs("n");
        while (n_column.hasNext()) {
            Node node = n_column.next();
            list_node.add(node);
        }
        tx4j.success();
        tx4j.finish();
        return list_node;
    }
    
    public List<Long> getIDNode(List<Node> source) {
        tx4j = graphDb.beginTx();
        List<Long> list_id = new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
        ExecutionResult result = engine.execute("start n=node({node}) return ID(n) as id", params);
        Iterator n_column = result.columnAs("id");
        while (n_column.hasNext()) {
           long id = (long) n_column.next();
            list_id.add(id);
        }
        tx4j.success();
        tx4j.finish();
        return list_id;
    }

    /**
     * recup les noeuds cible d'une requete
     *
     * @param source list des noueds sources
     * @return les noeuds correspondant au lemma qui occure avec les lemma de
     * requete dans la base s'il existe
     */
    public List<Node> getAllNodeCible(List<Node> source) {
        tx4j = graphDb.beginTx();
        List list_node = new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
        ExecutionResult result = engine.execute("start n=node({node}) match n--b return distinct b", params);
        Iterator<Node> n_column = result.columnAs("b");
        while (n_column.hasNext()) {
            Node node = n_column.next();
                list_node.add(node);
        }
        tx4j.success();
        tx4j.finish();
        return list_node;
    }
    
      public void shutdowndb() {
        graphDb.shutdown();
    } 
    /*******************************Query For Approche Possib***************************************************/
     public Map<Integer,Float> Frequency(Node c,List<Node> source){
       tx4j = graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
         Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
        ExecutionResult result = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]-b return r.OCCUR order by ID(n)",params);
       // ExecutionResult result = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]->b return r.OCCUR order by ID(n)",params);
        Iterator occur_column = result.columnAs("r.OCCUR");
        ExecutionResult result2 = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]-b return ID(n) order by ID(n)",params);
       // ExecutionResult result2 = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]->b return ID(n) order by ID(n)",params);
        Iterator n_column = result2.columnAs("ID(n)");
        int occur;
        int n;
         Map<Integer, Integer> map = new TreeMap();
        while (occur_column.hasNext()&& n_column.hasNext()) {
            occur = (int) occur_column.next();
            n= (int) (long)n_column.next();
             map.put(n, occur);
        }
        int max=Collections.max(map.values());
    
         Map<Integer, Float> mapft = new TreeMap();
         int id;
         float ft;
        for(Node s:source)
        {
            id=(int) s.getId();
           //  System.out.println(id);
        if(map.containsKey(id)){
             ft=(float)map.get(id)/(float)max;
            //  System.out.println(tf);
            mapft.put(id, ft);
                    }
        else 
        {
            ft=0;
            mapft.put(id,ft);
        }
        }
        tx4j.success();
        tx4j.finish();
                return mapft;
    }
    
      public Map<Integer, Float> log(List<Node> source,int nCa) {
          tx4j = graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
        ExecutionResult result = engine.execute("start n=node({node}) match n--c return n,count(distinct c) as b", params);
        ExecutionResult result2 = engine.execute("start n=node({node}) match n--c return n,count(distinct c) as b", params);
       // ExecutionResult result = engine.execute("start n=node({node}) match n-->c return n,count(distinct c) as b", params);
       // ExecutionResult result2 = engine.execute("start n=node({node}) match n-->c return n,count(distinct c) as b", params);
       // int nCa=getNombreAllNodeCible(source);
        Map<Integer, Float> map = new TreeMap();
        Iterator<Node> n_column = result.columnAs("n");
        Iterator  column2 = result2.columnAs("b");
        int nAi;
        float log10;
       while (n_column.hasNext() && column2.hasNext()) {
            Node n=n_column.next();
             int id = (int)(long) n.getId();
           nAi = (int)(long) column2.next();
           log10=(float) Math.log10((double) nCa / (double) nAi);
            map.put(id, log10);
           // System.out.println(id+" ==> " +maxoccur);
        }
       tx4j.success();
       tx4j.finish();
        return map;
    }
    
    
    /*******************************Query Calcul A Base De Circuit***************************************************/
    
    
     public List<Node> getNodeCible(Node s) {
         tx4j = graphDb.beginTx();
        List list_node = new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start n=node("+s.getId()+") match n--b return distinct b");
        Iterator<Node> n_column = result.columnAs("b");
        while (n_column.hasNext()) {
            Node node = n_column.next();
                list_node.add(node);
        }
        tx4j.success();
        tx4j.finish();
        return list_node;
    }
     
     
  public Map<Integer, Integer> getOccurSourceCible(Node s) {
        tx4j = graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start n=node(" + s.getId() + ") match n--c return ID(c) order by ID(c)");
        ExecutionResult result2 = engine.execute("start n=node(" + s.getId() + ") match n-[r:OCCUR]-c return r.OCCUR as occur order by ID(c)");
        Map<Integer, Integer> map = new HashMap();
        Iterator n_column = result.columnAs("ID(c)");
        Iterator occurence = result2.columnAs("occur");
        while (n_column.hasNext() && occurence.hasNext()) {
            int n = (int)(long) n_column.next();
            int nbrnode = (int) occurence.next();
            map.put(n, nbrnode);
            // System.out.println(id+" ==> " +maxoccur);
        }
        tx4j.success();
        tx4j.finish();
        return map;
    }
  
  public Map<Node, Integer> getOccurSC(Node s) {
        tx4j = graphDb.beginTx();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start n=node(" + s.getId() + ") match n--c return c order by ID(c)");
        ExecutionResult result2 = engine.execute("start n=node(" + s.getId() + ") match n-[r:OCCUR]-c return r.OCCUR as occur order by ID(c)");
        Map<Node, Integer> map = new HashMap();
        Iterator<Node> n_column = result.columnAs("c");
        Iterator occurence = result2.columnAs("occur");
        while (n_column.hasNext() && occurence.hasNext()) {
            Node n = n_column.next();
            int nbrnode = (int) occurence.next();
            map.put(n, nbrnode);
            // System.out.println(id+" ==> " +maxoccur);
        }
        tx4j.success();
        tx4j.finish();
        return map;
    }
 public int getOccurCC(int c1,int c2) {
        tx4j = graphDb.beginTx();
        int occur=0;
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start c1=node(" + c1 + "),c2=node(" + c2 + ") match c1-[r:OCCUR]-c2 return r.OCCUR as occur");
        Iterator n_column = result.columnAs("occur");
        if (n_column.hasNext()){
          occur = (int) n_column.next(); 
        }
        tx4j.success();
        tx4j.finish();
        return occur;
    }
    //methode permet de recupere le nombre des noeuds source dans un cycle
    public int nombreNodeSourceInCycle(List<String> listcycle, List<Node> listsource) {
        tx4j = graphDb.beginTx();
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

   
}
