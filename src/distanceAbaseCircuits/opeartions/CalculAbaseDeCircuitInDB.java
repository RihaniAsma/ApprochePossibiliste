/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import commun.Neo4jQuery;
import commun.ResultBean;
import distanceAbaseCircuits.opeartions.Entity.CycleDataBean;
import distanceAbaseCircuits.opeartions.Entity.MotReq;
import distanceAbaseCircuits.opeartions.Entity.SqlQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CalculAbaseDeCircuitInDB {
    
      public Neo4jQuery cq ;//= new Neo4jQuery();

    public CalculAbaseDeCircuitInDB(Neo4jQuery cq) {
        this.cq = cq;
    }
    

    public CalculAbaseDeCircuitInDB() {
    }

    private enum RelTypes implements RelationshipType {

        OCCUR
    }
    public void cycleSize2_3WithData(Node s, Node c, List<Node> source, Map<Node, Integer> occurSC, List<Node> aband, EntityManager em, MotReq mq) {
        Transaction tx4j = cq.getGraphDb().beginTx();
        Path path;
        int occsc1, occsc2, occc1c2, Nsi;
        Node c2;
        float Mai;
        float psi;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterator<Path> p = finder.findAllPaths(s, c).iterator();//paths.iterator();
        occsc1 = occurSC.get(c);
        while (p.hasNext()) {
            //chemin recuperer
            path = p.next();
            //methode permet de recupere les noeuds formant le chemin
            //transformer l'Iterable à une list
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, path.nodes().iterator());
            //methode permet de recupere les relations de chemin
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList();
            CollectionUtils.addAll(rels, path.relationships().iterator());
            //eliminer les chemins qui se repete
            CycleDataBean cd = new CycleDataBean();
            String chemin;
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (occsc1 / Nsi);
                    chemin = "#" + s.getId() + "#" + c.getId() + "#";
                    cd.setChemin(chemin);
                    cd.setPsi(psi);
                    cd.setMot_id(mq);
                    em.persist(cd);
                }
                if (nodes.size() == 3) {
                    c2 = nodes.get(1);
                    occsc2 = occurSC.get(c2);
                    occc1c2 = (int) rels.get(1).getProperty("OCCUR");
                    Mai = (float) (occsc1 + occsc2 + occc1c2) / 3;
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (Mai / Nsi);
                    chemin = "#" + s.getId() + "#" + c.getId() + "#" + c2.getId() + "#";
                    cd.setChemin(chemin);
                    cd.setPsi(psi);
                    cd.setMot_id(mq);
                    em.persist(cd);
                }

            }

        }
        tx4j.success();
        tx4j.finish();
    }

    public void cycleSize4WithData(Node s, Node c1, Node c2, List<Node> source, Map<Node, Integer> occurSc, EntityManager em, MotReq mq) {
        Transaction tx4j = cq.getGraphDb().beginTx();
        Path path;

        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        Iterator<Path> p = finder.findAllPaths(c1, c2).iterator();
        int occsc1 = occurSc.get(c1);
        int occsc2 = occurSc.get(c2);
        int occc1x, occc2x, Nsi;
        float Mai, psi;
        Node x;
        String chemin;
        while (p.hasNext()) {
            CycleDataBean cd = new CycleDataBean();
            path = p.next();
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, path.nodes().iterator());
            if ((!nodes.contains(s)) && nodes.size() >= 3) {
                //transformer l'Iterable à une list
                List<Relationship> rels = new ArrayList();
                CollectionUtils.addAll(rels, path.relationships().iterator());//Itrels.iterator());
                x = nodes.get(1);
                occc1x = (int) rels.get(0).getProperty("OCCUR");
                occc2x = (int) rels.get(1).getProperty("OCCUR");
                Mai = (float) (occsc1 + occsc2 + occc1x + occc2x) / 4;
                nodes.add(s);
                Nsi = CollectionUtils.intersection(nodes, source).size();
                psi = (float) (Mai / Nsi);
                chemin = "#" + s.getId() + "#" + c1.getId() + "#" + x.getId() + "#" + c2.getId() + "#";
                cd.setChemin(chemin);
                cd.setPsi(psi);
                cd.setMot_id(mq);
                em.persist(cd);

            }
        }
        tx4j.success();
        tx4j.finish();
    }

    public void create(Node s, List<Node> source, Neo4jQuery cq, EntityManager em, MotReq mq) {
        EntityTransaction tx = em.getTransaction();
        Node c1, c2;
        List<Node> aband = new ArrayList();
        Map<Node, Integer> occurSC = cq.getOccurSC(s);
        List<Node> cibles = cq.getNodeCible(s);
        for (int i = 0; i < cibles.size(); i++) {
            tx.begin();
            c1 = cibles.get(i);
            cycleSize2_3WithData(s, c1, source, occurSC, aband, em, mq);
            aband.add(c1);
            System.out.println(c1.getId() + " cibles");
            for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                cycleSize4WithData(s, c1, c2, source, occurSC, em, mq);
            }
            tx.commit();

        }
        aband.clear();
        occurSC.clear();
        cibles.clear();
    }

    public List<ResultBean> calculScore(String req) {

        EntityManagerFactory emf;
        EntityManager em;
        emf = Persistence.createEntityManagerFactory("approchePossibilistePU");
        String mot;
        float sumPsiTotal, sumPsiNode;
        // on récupère un EntityManager à  partir de l'EntityManagerFactory précédent
        em = emf.createEntityManager();
        SqlQuery sq = new SqlQuery(em);
      //  cq.run();
        List<Node> sources = cq.getAllNodeSource(req);
        List<ResultBean> list_rb;//=new ArrayList();
        ResultBean rb;
        MotReq mq;
       for (Node s : sources) {
            mot = (String) s.getProperty("texte");
            if (!sq.exist(mot)) {
                System.out.println("Creation des cycles de noeud " + mot);
                mq = new MotReq();
                mq.setId(s.getId());
                mq.setMot(mot);
                sq.save(mq);
                create(s, sources, cq, em, mq);
            } else {
                System.out.println("les cycles de " + mot + " sont déja enregistrer");
            }

        }
        //calcul de score des noeuds cibles
        List<Long> sources_id = cq.getIDNode(sources);
        sumPsiTotal = sq.tatalPSIDeReq(sources_id);
        //System.out.println(sumPsiTotal);
        List<Node> cibles = cq.getAllNodeCible(sources);
      //  System.out.println(cibles.size());
        list_rb = new ArrayList();
        for (Node c : cibles) {
            rb = new ResultBean();
            rb.setNodeC(c);
            sumPsiNode = sq.tatalPSIDeNodeC(sources_id, c.getId());
            rb.setScore(sumPsiNode / sumPsiTotal);
            list_rb.add(rb);
        }
        cibles.clear();
        Collections.sort(list_rb);
        //reverser l'ordre pour avoir un ordre décroissant
        Collections.reverse(list_rb);
        em.close();
        emf.close();
     //  cq.shutdowndb();
        return list_rb;
    }

   public static void main(String[] args) {
        Neo4jQuery cq = new Neo4jQuery();
        cq.run();
        CalculAbaseDeCircuitInDB cc = new CalculAbaseDeCircuitInDB(cq);
        List<ResultBean> res = cc.calculScore("biologique");
        for (ResultBean r : res) {
            System.out.println(r.getNodeC().getId() + "==>" + r.getScore());
        }
cq.shutdowndb();
    }
}
