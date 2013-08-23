/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package approchepossibiliste.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.neo4j.cypher.javacompat.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.ReadableIndex;

/**
 *
 * @author acer
 */
public class DataQuery {

    GraphDatabaseService graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder("C:/Users/acer/Desktop/NEO4J-HOME/data/graph.db").
            setConfig(GraphDatabaseSettings.node_keys_indexable, "texte").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
            newGraphDatabase();
    Transaction tx4j;// = graphDb.beginTx();

    public DataQuery() {
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
        //ExecutionResult result = engine.execute("start n=node({node}) match n--b return distinct b", params);
        ExecutionResult result = engine.execute("start n=node({node}) match n-->b return distinct b", params);
        Iterator<Node> n_column = result.columnAs("b");
        while (n_column.hasNext()) {
            Node node = n_column.next();
                list_node.add(node);
        }
        return list_node;
    }
   
    public Map<Integer,Float> Frequency(Node c,List<Node> source){
   
        ExecutionEngine engine = new ExecutionEngine(graphDb);
         Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
       // ExecutionResult result = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]-b return r.OCCUR order by ID(n)",params);
        ExecutionResult result = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]->b return r.OCCUR order by ID(n)",params);
        Iterator occur_column = result.columnAs("r.OCCUR");
       // ExecutionResult result2 = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]-b return ID(n) order by ID(n)",params);
        ExecutionResult result2 = engine.execute("start n=node({node}),b=node(" + c.getId() + ") match n-[r:OCCUR]->b return ID(n) order by ID(n)",params);
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
                return mapft;
    }
    
      public Map<Integer, Float> log(List<Node> source,int nCa) {
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("node", source);
       // ExecutionResult result = engine.execute("start n=node({node}) match n--c return n,count(distinct c) as b", params);
       // ExecutionResult result2 = engine.execute("start n=node({node}) match n--c return n,count(distinct c) as b", params);
        ExecutionResult result = engine.execute("start n=node({node}) match n-->c return n,count(distinct c) as b", params);
        ExecutionResult result2 = engine.execute("start n=node({node}) match n-->c return n,count(distinct c) as b", params);
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
       
        return map;
    }
       /**
     * @param ft map des fréquences d'un noeud cible 
     * @return le resulat de degré de possibilité d'un noeud cible
     */
    public float degreDePossibilitee(Map<Integer,Float> ft)
     {
        float dp=1;
        Set listKey=ft.keySet();  // Obtenir la liste des clés
    		Iterator iterateurs=listKey.iterator();
    		// Parcourir les clés et afficher les entrées de chaque clé;
    		while(iterateurs.hasNext())
    		{
    			Object key= iterateurs.next();
    			//System.out.println ("source "+key+" ==>ft "+ft.get(key));
                         dp = dp * ft.get(key);
    		}
     return dp;
     }
    
    /**
     * 
     * @param log map des logs des noeuds source
     * @return la valeur N necessaire pour le calcul de DPP
     */
     public float degreDeNecessite( Map<Integer, Float> log,
         Map<Integer,Float> ft) {
        float vf;
        float necessityValue = 1;
        Set listKey=log.keySet();  // Obtenir la liste des clés
    		Iterator iterateurs=listKey.iterator();
    		// Parcourir les clés et afficher les entrées de chaque clé;
    		while(iterateurs.hasNext())
    		{
    			Object key= iterateurs.next();
    			vf=(float) log.get(key)* ft.get(key);
            necessityValue = necessityValue * (1 - vf);
        }
        necessityValue = 1 - necessityValue;
        return necessityValue;
    }
      
    public float degreDePertinencePossibiliste(Node c,List<Node> source ,Map<Integer, Float> log){
        
         Map<Integer,Float> ft=Frequency(c,source);
         float dpp,dp,n;
         //degre possibiliste
         if(ft.containsValue(0))
             dpp=degreDeNecessite(log, ft);
         else
         { dp=degreDePossibilitee(ft);
         //nessecite
          n=degreDeNecessite(log, ft);
          dpp=dp+n;
         }
         return dpp;
    
    }
    
    /** 
     * @param req requete de l'user
     * @return liste des id des noeuds avec les DPP corespondants par ordre decroissant
     */
     public  List<ResultBean> calculPossibiliste(String req) {
        //liste des noeuds sources de la requete
        List<Node> node_s = getAllNodeSource(req);
        System.out.println("source "+node_s.size());
        //liste des noeuds cibles de tous les neouds sources
        List<Node> node_c = getAllNodeCible(node_s);
         System.out.println("cible "+node_c.size());
          List<ResultBean> list_rb = new ArrayList<ResultBean>();
      // int i=1;
       int nCa=node_c.size();
       float dpp;
        ResultBean rb;
       Map<Integer, Float> log=log(node_s,nCa);
        for(Node c:node_c){
            rb=new ResultBean();
             rb.setNodeC(c);
           dpp=degreDePertinencePossibiliste(c, node_s,log);
             rb.setDPP(dpp);
        list_rb.add(rb);
           //System.out.println(i);
           //i++;
        }
         Collections.sort(list_rb);
        Collections.reverse(list_rb);
       return list_rb;
    }
    
     public void shutdowndb() {
        graphDb.shutdown();
    }
  public static void main(String[] args) {
     DataQuery dq = new DataQuery();
 //List<Node> lst= dq.getAllNodeSource("discussion politique décision négociation préalable partir politique groupe social résultat final consultation Referendum constitution Afrique Sud "); 
 List<Node> lst= dq.getAllNodeSource ("biologique1");
 //Node c=dq.FindNode("biologique3");
 // System.out.println(c.getId());
/* Map<Integer, Float> m1= dq.essai(c, lst);
 Map<Integer, Float> m2=dq.log(lst);
 System.out.println(m1.size());
 System.out.println(m2.size());*/
  //System.out.println("source "+lst.size());
 List<Node> cible=dq.getAllNodeCible(lst);
 //System.out.println("cible "+cible.size());
 //System.out.println(dq.getNombreNodeCible(lst));
for(Node n:cible)
    System.out.print(n.getId()+",");
   //Node c=dq.FindNode("politique");
 //System.out.println("source "+dq.getNombMaxOccur(c, lst));
       //for(Node n:lst)
          //System.out.println(dq.getNmbrCibleDeSource2(n));
  /* Map<Integer, Integer> m=dq.getNombreNodeCjdeSi(lst);
     Set listKeys=m.keySet();  // Obtenir la liste des clés
    		Iterator iterateur=listKeys.iterator();
    		// Parcourir les clés et afficher les entrées de chaque clé;
    		while(iterateur.hasNext())
    		{
    			Object key= iterateur.next();
    			System.out.println (key+" ==>"+m.get(key));
    		}
 dq.shutdowndb();*/
     }
}
