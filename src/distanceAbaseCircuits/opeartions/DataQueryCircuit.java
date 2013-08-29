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
import java.util.Set;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Rihani Asma
 */
public class DataQueryCircuit {

    public DataQueryCircuit() {
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

    public Map<Node, Integer> getOccurCibleCible(Node s, Node c, List<Node> listcible) {

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cible", listcible);
        ExecutionResult result = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c.getId() + "),c2=node({cible}) match c1--c2 where c2<>s return c2 order by ID(c2)", params);
        ExecutionResult result2 = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c.getId() + "),c2=node({cible}) match c1-[r:OCCUR]-c2 where c2<>s return r.OCCUR as occur order by ID(c2)", params);
        Map<Node, Integer> map = new HashMap();
        Iterator<Node> n_column = result.columnAs("c2");
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

    public List<CycleData> cycleSize2(Node s, Map<Node, Integer> occurSourceCible) {
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        List<Node> source = new ArrayList<>();
        source.add(s);
        Set listKey = occurSourceCible.keySet();  // Obtenir la liste des clés
        Iterator iterateurs = listKey.iterator();
        // Parcourir les clés et afficher les entrées de chaque clé;
        while (iterateurs.hasNext()) {
            Node n = (Node) iterateurs.next();
            //cycle de format s--c1--s
            cd = new CycleData();
            cd.addNode(s);
            cd.addNode(n);
            cd.setMai(occurSourceCible.get(n));
            cd.setListnode(cd.getListnode());
            //calcul nombre soucre dans cycle
            int nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
            cd.setNbsource(nbrsource);
            // System.out.println("taille cycle "+cd.getListnode().size());
            listcycle.add(cd);
          System.out.println("ce cycle est crée " + s.getId() + "==>" + n.getId());
        }
       // System.out.println("nombre des cycles taille 2 " + listcycle.size());
        return listcycle;

    }

    public List<CycleData> cycleSize3(Node s, Map<Node, Integer> occurSourceCible) {
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        List<Node> source = new ArrayList<>();
        source.add(s);
        //list noeud cible
        List<Node> list = new ArrayList(occurSourceCible.keySet());
        //pour chaque noeud cible recupere les noeuds cibles qui occure avec
        //pas juste le remove change le compte de la liste donc modifier la methode
        //modidier et reste à verifier
        List<Node> list2 = new ArrayList();
        list2.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            Node c1 = list.get(i);
            list2.remove(list2.indexOf(c1));
            //Map contient les occurences entre noeud cible c1 et les noueds cible qui occure avec
            Map<Node, Integer> occurcible = getOccurCibleCible(s, c1, list2);
            Set listKey = occurcible.keySet();  // Obtenir la liste des clés
            Iterator iterateurs = listKey.iterator();
            while (iterateurs.hasNext()) {
                Node c2 = (Node) iterateurs.next();
                //cycle de format s--c1--c2--s
                cd = new CycleData();
                cd.addNode(s);
                cd.addNode(c1);
                cd.addNode(c2);
                //calcul Mai occur(s,c1)+occur(c1,c2)+occur(s,c2)
                int oc1 = occurSourceCible.get(c1);//occurence entre s et c1
                //  System.out.println(oc1);
                int oc2 = occurcible.get(c2);// occurence c1 et c2
                // System.out.println(oc2);
                int oc3 = occurSourceCible.get(c2);//occurence entre c2 et s
                // System.out.println(oc3);
                float Mai = (float) (oc1 + oc2 + oc3) / 3;
                // System.out.println(Mai);
                cd.setMai(Mai);
                cd.setListnode(cd.getListnode());
                //calcul nombre soucre dans cycle
                int nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                cd.setNbsource(nbrsource);

                //  System.out.println("taille cycle "+cd.getListnode().size());
                listcycle.add(cd);
              System.out.println("ce cycle est crée " + s.getId() + "==>" + c1.getId() + "==>" + c2.getId() + " de mai " + cd.getMai());
            }
           // System.out.println("nbr cycle taille 3 " + listcycle.size());
        }
       // System.out.println("nbr cycle taille 3 " + listcycle.size());

        return listcycle;
    }

    public List<CycleData> cycleSize4(Node s, Map<Node, Integer> occurSourceCible) {
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        List<CycleData> listcycle = new ArrayList();
        CycleData cd;
        //list des noeud cible de s
        List<Node> list = new ArrayList(occurSourceCible.keySet());
        //liste noeud source à utliser lors de l'utilisation des requetes
        List<Node> source = new ArrayList<>();
        source.add(s);

        //pour chaque noeud cible recup les noeuds commun quelle a avec le reste des noeuds cible
        for (int i = 0; i < list.size(); i++) {
            //noeud cible c1
            Node c1 = list.get(i);
            //le reste des noeud cible
            list.remove(list.indexOf(c1));
            //parametre de requette
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("cible", list);
            //requete pour requpere les val necessaire pour la construction de circuits s--c1--x--c2--s avec x noeud cible commun
            //entre c1 et c2
            ExecutionResult result = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c1.getId() + "),"
                    + "c2=node({cible}) match c1-[r:OCCUR]-x,c2--x where x<>s and x<>c1 and x<>c2 return r.OCCUR as c1x order by ID(x)", params);
            ExecutionResult result2 = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c1.getId() + "),"
                    + "c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return x order by ID(x)", params);
            ExecutionResult result3 = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c1.getId() + "),"
                    + "c2=node({cible}) match c1--x,c2-[r2:OCCUR]-x "
                    + "where x<>s and x<>c1 and x<>c2 return r2.OCCUR as c2x  order by ID(x)", params);
            ExecutionResult result4 = engine.execute("start s=node(" + s.getId() + "),c1=node(" + c1.getId() + "),"
                    + "c2=node({cible}) match c1--x,c2--x where x<>s and x<>c1 and x<>c2 return c2 order by ID(x)", params);
            Iterator occurc1x = result.columnAs("c1x");
            Iterator<Node> noeudCommx = result2.columnAs("x");
            Iterator occurc2x = result3.columnAs("c2x");
            Iterator<Node> noeudC2 = result4.columnAs("c2");
            while (occurc1x.hasNext() && noeudCommx.hasNext() && occurc2x.hasNext() && noeudC2.hasNext()) {
                Node c2 = noeudC2.next();
                Node x = noeudCommx.next();
                //cycle de format s--c1--x--c2--s   
                cd = new CycleData();
                cd.addNode(s);
                cd.addNode(c1);
                cd.addNode(c2);
                cd.addNode(x);
                cd.setListnode(cd.getListnode());
                //calcul Mai
                //occurence s--c1
                int ocSC1 = occurSourceCible.get(c1);
               // System.out.println(s.getId()+"occur "+c1.getId()+"= "+ocSC1);
                //occurence s--c2
                int ocSC2 = occurSourceCible.get(c2);
               // System.out.println(s.getId()+"occur "+c2.getId()+"= "+ocSC2);
                //occurence c1--x
                int ocC1X = (int) occurc1x.next();
                // System.out.println(c1.getId()+"occur "+x.getId()+"= "+ocC1X);
                //occurence c2--x
                int ocC2X = (int) occurc2x.next();
                //System.out.println(c2.getId()+"occur "+x.getId()+"= "+ocC2X);

                float Mai = (float)(ocSC1 + ocSC2 + ocC1X + ocC2X) / 4;
                cd.setMai(Mai);
                int nbrsource = nombreNodeSourceInCycle(cd.getListnode(), source);
                cd.setNbsource(nbrsource);

                //  System.out.println("taille cycle "+cd.getListnode().size());
                listcycle.add(cd);
               System.out.println("ce cycle est crée " + s.getId() + "==>" + c1.getId() + "==>" + x.getId() + "==>" + c2.getId() + " de mai " + cd.getMai());

            }
           // System.out.println("nbr cycle taille 4 " + listcycle.size());

        }

       // System.out.println("nbr cycle taille 4 " + listcycle.size());
        return listcycle;
    }
    /*   public void cycle2Node() {
     List<CycleData> listcycle = new ArrayList();
     Node c1, c2;
     CycleData cd;
     List<Node> source = cq.getAllNodeSource("biologique1");
     List<Node> cibles = cq.getAllNodeCible(source);
     Map<Long, Integer> occur = getOccurCibleOfSource(source.get(0));
     System.out.println(cibles.size());
     ExecutionEngine engine = new ExecutionEngine(graphDb);
     for (int i = 0; i < cibles.size(); i++) {
     c1 = cibles.get(i);
     //cycle de format s--c1--s
     cd = new CycleData();
     cd.addNode(source.get(0));
     cd.addNode(c1);
     cd.setMai(occur.get(c1.getId()));
     cd.setNbsource(1);
     cd.setListnode(cd.getListnode());
     System.out.println("ce cycle est crée " + source.get(0).getId() + "==>" + c1.getId());
     listcycle.add(cd);
     for(int j=i+1;j<cibles.size();j++){
     c2=cibles.get(j);
     //cycle de format s--c1--c2--s
     ExecutionResult result = engine.execute("start c1=node("+c1.getId()+"),c2=node(" + c2.getId() + ") match c1-[r:OCCUR]-c2 return r.OCCUR as r");
     Iterator occur_column = result.columnAs("r");
     if (occur_column.hasNext()) {
     cd=new CycleData();
     //ajout des noeud de cycle
     cd.addNode(source.get(0));
     cd.addNode(c1);
     cd.addNode(c2);
     cd.setListnode(cd.getListnode());
     //calcul de Mai=( occur(s,c1)+occur(s,c2)+occur(c1,c2))/3
     int occurSC1=occur.get(c1.getId());
     int occurSC2=occur.get(c2.getId());
     int occurC1C2=(int) occur_column.next();
     int Mai=(occurSC1+occurSC2+occurC1C2)/3;
     cd.setMai(Mai);
     //calcul de nombre des neouds source dans le cycle
     int nbrsource=nombreNodeSourceInCycle(cd.getListnode(),source);
     System.out.println(nbrsource);
     System.out.println("ce cycle est crée " + source.get(0).getId() + "==>" + c1.getId()+"==>"+c2.getId());
     }
     }
     }
     System.out.println(listcycle.size());
  
     }
     /*       private enum RelTypes implements RelationshipType {
     OCCUR
     }*/

    public static void main(String[] args) {
         System.out.println(new Date());
        DataQueryCircuit dqc = new DataQueryCircuit();
        Node s = dqc.cq.FindNode("biologique");
        // Node c=dqc.cq.FindNode("biologique1");
        Map<Node, Integer> occurSourceCible = dqc.getOccurSourceCible(s);
     /*  List<CycleData> cycle2= dqc.cycleSize2(s, occurSourceCible);
       System.out.println("nbr cycle taille 2 " + cycle2.size());
        System.out.println(new Date());
      List<CycleData> cycle3=  dqc.cycleSize3(s, occurSourceCible);
       System.out.println("nbr cycle taille 3 " + cycle3.size());
        System.out.println(new Date());*/
        List<CycleData> cycle4=dqc.cycleSize4(s, occurSourceCible);
         System.out.println("nbr cycle taille 4 " + cycle4.size());
          System.out.println(new Date());
        /* List<CycleData> allcycle=new ArrayList<>();
         allcycle.addAll(cycle2);
         allcycle.addAll(cycle3);
         allcycle.addAll(cycle4);
         System.out.println("nbr totale des cycles avec 4 noeuds max pour noeud " + s.getId()+" est "+allcycle.size());
         */
        
        //dqc.cycle2Node();
        dqc.cq.shutdowndb();
    }
}
/*  TraversalDescription td = Traversal.description();
 //td.uniqueness(Uniqueness.NONE);
 //td.evaluator(Evaluators.toDepth(3));
 // td.breadthFirst();
 // td.evaluator(Evaluators.endNodeIs(Evaluation.INCLUDE_AND_PRUNE, Evaluation.INCLUDE_AND_PRUNE,s));
 // Traverser t= ;
 for (Path path:td.evaluator(Evaluators.toDepth(3)).traverse(s)){
 System.out.println(path.length());
 System.out.println(path);
 }
 /* PathFinder<Path> finder = GraphAlgoFactory.allPaths(
 Traversal.expanderForTypes(ExampleTypes.OCCUR,Direction.BOTH ), 3 );
 Iterable<Path> paths = finder.findAllPaths(s, c );
 Iterator<Path> p=paths.iterator();
 int i=1;
 while(p.hasNext()){
 Path path=p.next();
 Iterable<Node> nodes = path.nodes();
 System.out.println("node of path "+i);
 System.out.println(path.length());
 System.out.println(path);
 /*  while(nodes.iterator().hasNext()){
 Node n=nodes.iterator().next();
 System.out.println(n.getId()+" ,");
 }
 i++;
 }*/
