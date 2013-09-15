/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import commun.Neo4jQuery;
import commun.ResultBean;
import distanceAbaseCircuits.opeartions.Entity.CycleDataBean;
import distanceAbaseCircuits.opeartions.Entity.MotReq;
import distanceAbaseCircuits.opeartions.Entity.SqlQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CalculAbaseDeCircuitWithFileInDB {

    private Neo4jQuery cq;//=new Neo4jQuery() ;

    public CalculAbaseDeCircuitWithFileInDB(Neo4jQuery cq) {
        this.cq = cq;
    }

   
    

  /*  public CalculAbaseDeCircuitWithFileInDB(Neo4jQuery cq) {
        this.cq = cq;
    }*/

    public CalculAbaseDeCircuitWithFileInDB() {
    }

    private enum RelTypes implements RelationshipType {

        OCCUR
    }

    public void cycleSize2_3WithData(Node s, Node c, List<Node> source, Map<Node, Integer> occurSC, List<Node> aband, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        int occsc1, occsc2, occc1c2, Nsi;
        Node c2;
        float Mai;
        Iterable<Relationship> Itrels;
        float psi;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterable<Path> paths = finder.findAllPaths(s, c);
        Iterator<Path> p = paths.iterator();
        occsc1 = occurSC.get(c);
        String chemin;
        while (p.hasNext()) {
            //chemin recuperer
            path = p.next();
            //methode permet de recupere les noeuds formant le chemin
            Itnodes = path.nodes();
            //transformer l'Iterable à une list
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            //methode permet de recupere les relations de chemin
            Itrels = path.relationships();
            //transformer l'Iterable à une list
            List<Relationship> rels = new ArrayList();
            CollectionUtils.addAll(rels, Itrels.iterator());
            //eliminer les chemins qui se repete
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (occsc1 / Nsi);
                    chemin = "#" + s.getId() + "#" + c.getId() + "#";
                    //String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(psi)};
                    String[] cycle = {chemin, String.valueOf(psi)};
                    writer.writeNext(cycle);
                }
                if (nodes.size() == 3) {
                    //construction de cycle s--c1--c2--s
                    c2 = nodes.get(1);
                    //System.out.println("this is c2 "+c2.getId());
                    occsc2 = occurSC.get(c2);
                    occc1c2 = (int) rels.get(1).getProperty("OCCUR");
                    Mai = (float) (occsc1 + occsc2 + occc1c2) / 3;
                    Nsi = CollectionUtils.intersection(nodes, source).size();
                    psi = (float) (Mai / Nsi);
                    chemin = "#" + s.getId() + "#" + c.getId() + "#" + c2.getId() + "#";
                    String[] cycle = {chemin, String.valueOf(psi)};
                    // String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(c2.getId()), String.valueOf(psi)};
                    writer.writeNext(cycle);
                }

            }

        }
    }

    public void cycleSize4WithData(Node s, Node c1, Node c2, List<Node> source, Map<Node, Integer> occurSc, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        Iterable<Relationship> Itrels;
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        /*List<Node> aband = new ArrayList<>();
         aband.add(s);*/
        Iterable<Path> paths = finder.findAllPaths(c1, c2);
        Iterator<Path> p = paths.iterator();
        int occsc1 = occurSc.get(c1);
        // System.out.println("s "+s.getId() +" c1 "+c1.getId()+" occur==> "+occsc1);
        int occsc2 = occurSc.get(c2);
        // System.out.println("s "+s.getId() +" c2 "+c2.getId()+" occur==> "+occsc2);
        int occc1x, occc2x, Nsi;
        float Mai, psi;
        Node x;
        String chemin;
        while (p.hasNext()) {
            path = p.next();
            Itnodes = path.nodes();
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if ((!nodes.contains(s)) && nodes.size() >= 3) {
                Itrels = path.relationships();
                //transformer l'Iterable à une list
                List<Relationship> rels = new ArrayList();
                CollectionUtils.addAll(rels, Itrels.iterator());
                x = nodes.get(1);
                occc1x = (int) rels.get(0).getProperty("OCCUR");
                // System.out.println("c1 "+c1.getId() +" x "+nodes.get(1).getId()+" occur==> "+occc1x);
                occc2x = (int) rels.get(1).getProperty("OCCUR");
                //System.out.println("c2 "+c2.getId() +" x "+nodes.get(1).getId()+" occur==> "+occc2x);
                Mai = (float) (occsc1 + occsc2 + occc1x + occc2x) / 4;
                nodes.add(s);
                Nsi = CollectionUtils.intersection(nodes, source).size();
                psi = (float) (Mai / Nsi);
                chemin = "#" + s.getId() + "#" + c1.getId() + "#" + x.getId() + "#" + c2.getId() + "#";
                String[] cycle = {chemin, String.valueOf(psi)};
                // String[] cycle = {String.valueOf(s.getId()), String.valueOf(c1.getId()), String.valueOf(x.getId()),String.valueOf(c2.getId()),String.valueOf(psi)};
                writer.writeNext(cycle);

            }
        }
    }

    public void creationFichierWithData(Node s, List<Node> source) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        //  Neo4jQuery cq =new Neo4jQuery();
        Node c1, c2;
        List<Node> aband = new ArrayList();
        Map<Node, Integer> occurSC = cq.getOccurSC(s);
        List<Node> cibles = cq.getNodeCible(s);
        File File = new File("cycle/" + s.getProperty("texte"));
        File.mkdir();
        String path = File.getPath();
        CSVWriter writer;
        for (int i = 0; i < cibles.size(); i++) {
            c1 = cibles.get(i);
            writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(path + "/" + c1.getId() + ".csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
            // System.out.println(c1.getId());
            cycleSize2_3WithData(s, c1, source, occurSC, aband, writer);
            aband.add(c1);
            System.out.println(c1.getId() + " cibles");
            for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                cycleSize4WithData(s, c1, c2, source, occurSC, writer);
            }
            writer.close();

        }

        //  cq.shutdowndb();

    }

    public void persistDataInDB(MotReq mq, List<Node> cibles, String path, SqlQuery sq) throws FileNotFoundException, IOException {
        CSVReader reader;
        String[] nextLine;
        CycleDataBean cb;
        float psi;
        String file;
        for (Node c : cibles) {
            System.out.println(c.getId());
            file = path + "/" + c.getId() + ".csv";
            reader = new CSVReader(new FileReader(file), '\t');
            while ((nextLine = reader.readNext()) != null) {
                cb = new CycleDataBean();
                cb.setChemin(nextLine[0]);
                psi = Float.valueOf(nextLine[1]);
                cb.setPsi(psi);
                cb.setMot_id(mq);
                sq.saveCycle(cb);
            }

            reader.close();

        }
    }

    public List<ResultBean> calculScore(String req) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("approchePossibilistePU");
        EntityManager em = emf.createEntityManager();
        List<Node> sources = cq.getAllNodeSource(req);
        List<Node> cibles,cibleOfs;
        List<Long> sources_id;
        String mot,path;
        MotReq mq;
        float sumPsiTotal,sumPsiNode;
        SqlQuery sq = new SqlQuery(em);
        for (Node s : sources) {
            mot = (String) s.getProperty("texte");
            if (!sq.exist(mot)) {
                mq = new MotReq();
                mot = (String) s.getProperty("texte");
                mq.setId(s.getId());
                mq.setMot(mot);
                sq.save(mq);
                path = "cycle/" + mot;
                cibleOfs = cq.getNodeCible(s);
                creationFichierWithData(s, sources);
                persistDataInDB(mq, cibleOfs, path, sq);

            } else {
                System.out.println("le mot exixte déja");
            }
        }
        em.close();
        em = emf.createEntityManager();
        sq = new SqlQuery(em);
        sources_id = cq.getIDNode(sources);
        System.out.println("id node source trouver");
        sumPsiTotal = sq.tatalPSIDeReq(sources_id);
        System.out.println("supPsi total calculer" + sumPsiTotal);
        cibles = cq.getAllNodeCible(sources);
        System.out.println(cibles.size());
        List<ResultBean> list_rb = new ArrayList();
        ResultBean rb;
        for (Node c : cibles) {
            System.out.println("calcul des valeur de " + c.getId());
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
        return list_rb;

    }
    public static void main(String[] args) throws IOException {
        Neo4jQuery cq = new Neo4jQuery();
        cq.run();
        CalculAbaseDeCircuitWithFileInDB cc = new CalculAbaseDeCircuitWithFileInDB(cq);
        List<ResultBean> res = cc.calculScore("biologique");
        for (ResultBean r : res) {
            System.out.println(r.getNodeC().getId() + "==>" + r.getScore());
        }
cq.shutdowndb();
    }
}
