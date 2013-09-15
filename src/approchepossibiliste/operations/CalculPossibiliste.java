/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package approchepossibiliste.operations;

import commun.ResultBean;
import commun.Neo4jQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.neo4j.graphdb.Node;

/**
 *
 * @author acer
 */
public class CalculPossibiliste {

    public CalculPossibiliste() {
    }

     public Neo4jQuery cq;//=new Neo4jQuery();

    public CalculPossibiliste(Neo4jQuery cq) {
        this.cq = cq;
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
        
         Map<Integer,Float> ft=cq.Frequency(c,source);
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
     public  List<ResultBean> calculScore(String req) {
        //liste des noeuds sources de la requete
        List<Node> node_s = cq.getAllNodeSource(req);
        System.out.println("source "+node_s.size());
        //liste des noeuds cibles de tous les neouds sources
        List<Node> node_c = cq.getAllNodeCible(node_s);
         System.out.println("cible "+node_c.size());
          List<ResultBean> list_rb = new ArrayList();
      // int i=1;
       int nCa=node_c.size();
       float dpp;
        ResultBean rb;
       Map<Integer, Float> log=cq.log(node_s,nCa);
        for(Node c:node_c){
            rb=new ResultBean();
             rb.setNodeC(c);
           dpp=degreDePertinencePossibiliste(c, node_s,log);
             rb.setScore(dpp);
        list_rb.add(rb);
           //System.out.println(i);
           //i++;
        }
         Collections.sort(list_rb);
         //reverser l'ordre pour avoir un ordre décroissant
        Collections.reverse(list_rb);
       return list_rb;
    }
    
 public static void main(String[] args) {
       Neo4jQuery cq=new Neo4jQuery() ;
      cq.run();
     CalculPossibiliste dq = new CalculPossibiliste(cq);
     List<ResultBean> rs=dq.calculScore("biologique mentionner");
     for(ResultBean r:rs){
     System.out.println(r.getNodeC().getProperty("texte")+"==>"+r.getScore());
     }
 cq.shutdowndb();
     }
 }
