/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
public class CommunQuery {
    
     GraphDatabaseService graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder("C:/Users/acer/Desktop/NEO4J-HOME/data/graph.db").
            setConfig(GraphDatabaseSettings.node_keys_indexable, "texte").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
             setConfig(GraphDatabaseSettings.mapped_memory_page_size, "20M").
            newGraphDatabase();
    Transaction tx4j;// = graphDb.beginTx();

    public CommunQuery() {
    }

    /**
     * verif si un noeud existe dans la base ou pas
     *
     * @param label un lemma de requete
     * @return le noeud correspondant s'il existe dans la base
     */
    public Node FindNode(String label) {
        Node find = null;
        ReadableIndex<Node> autoNodeIndex = graphDb.index()
                .getNodeAutoIndexer()
                .getAutoIndex();
        find = autoNodeIndex.get("texte", label).getSingle();
        return find;
    }
   
    /**
     * recup les id des noeuds
     *
     * @param req requete entre par l'user
     * @return id node
     */
    public List getNodeId(String req) {

        List list_id = new ArrayList();
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
        return list_node;
    }

    /**
     * recup les noeuds cible d'une requete
     *
     * @param source list des noueds sources
     * @return les noeuds correspondant au lemma qui occure avec les lemma de
     * requete dans la base s'il existe
     */
    public List<Node> getAllNodeCible(List<Node> source) {
        List list_node = new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
        ExecutionResult result = engine.execute("start n=node({node}) match n--b return distinct b", params);
        //ExecutionResult result = engine.execute("start n=node({node}) match n-->b return distinct b", params);
        Iterator<Node> n_column = result.columnAs("b");
        while (n_column.hasNext()) {
            Node node = n_column.next();
                list_node.add(node);
        }
        return list_node;
    }
    
    
     public List<Node> getNodeCible(Node s) {
        List list_node = new ArrayList();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("start n=node("+s.getId()+") match n--b return distinct b order by ID(b)");
        //ExecutionResult result = engine.execute("start n=node({node}) match n-->b return distinct b", params);
        Iterator<Node> n_column = result.columnAs("b");
        while (n_column.hasNext()) {
            Node node = n_column.next();
                list_node.add(node);
        }
        return list_node;
    }

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

     public void shutdowndb() {
        graphDb.shutdown();
    } 
}
