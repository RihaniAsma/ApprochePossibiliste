/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import commun.CommunQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Rihani Asma
 */
public class CreateCycleWithPathFichier {
   private CommunQuery cq;

    public CreateCycleWithPathFichier(CommunQuery cq) {
        this.cq = cq;
    }
  
    private enum RelTypes implements RelationshipType {

        OCCUR
    }

  
    public void cycleSize2_3(Node s, Node c, List<Node> aband, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        //algorithme permet de recuperer tous les chemins possible entre 2 noeuds sans
        //depasser un longueur max de profendeur au niveau de recherche
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        //methode permet de recupere tous les chemins trouver en utlisant l'algo
        Iterable<Path> paths = finder.findAllPaths(s, c);
        Iterator<Path> p = paths.iterator();
        while (p.hasNext()) {
            //chemin recuperer
            path = p.next();
            //methode permet de recupere les noeuds formant le chemin
            Itnodes = path.nodes();
            //transformer l'Iterable à une list
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if (Collections.disjoint(aband, nodes)) {
//test sur le nombre des noeuds de chemins
                if (nodes.size() == 2) {
//construction de cycle s--c--s
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId())};
                    writer.writeNext(cycle);
                }
                if (nodes.size() == 3) {
                    //construction de cycle s--c1--c2--s
                    String[] cycle = {String.valueOf(s.getId()), String.valueOf(c.getId()), String.valueOf(nodes.get(1).getId())};
                    writer.writeNext(cycle);
                }

            }

        }
    }

    public void cycleSize4(Node s, Node c1, Node c2, CSVWriter writer) {
        Path path;
        Iterable<Node> Itnodes;
        PathFinder<Path> finder = GraphAlgoFactory.allPaths(
                Traversal.expanderForTypes(RelTypes.OCCUR, Direction.BOTH), 2);
        List<Node> aband = new ArrayList();
        aband.add(s);
        Iterable<Path> paths = finder.findAllPaths(c1, c2);
        Iterator<Path> p = paths.iterator();
        while (p.hasNext()) {
            path = p.next();
            Itnodes = path.nodes();
            List<Node> nodes = new ArrayList();
            CollectionUtils.addAll(nodes, Itnodes.iterator());
            if (Collections.disjoint(aband, nodes) && nodes.size() >= 3) {
                String[] cycle = {String.valueOf(s.getId()), String.valueOf(c1.getId()), String.valueOf(nodes.get(1).getId()), String.valueOf(c2.getId())};
                writer.writeNext(cycle);

            }
        }
    }
    public void creationFichier(String file,Node s) throws UnsupportedEncodingException, FileNotFoundException, IOException {

        Node c1, c2;
        List<Node> aband = new ArrayList();
        List<Node> cibles = cq.getNodeCible(s);
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/cycle2_3.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
        for (int i = 0; i < cibles.size(); i++) {
            c1 = cibles.get(i);
            CSVWriter writer1 = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/"+c1.getId()+"cycle4.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
            System.out.println(c1.getId() + " cibles");
            cycleSize2_3(s, c1, aband,writer);
             aband.add(c1);
            for (int j = i + 1; j < cibles.size(); j++) {
                c2 = cibles.get(j);
                cycleSize4(s, c1, c2, writer1);
            }
            writer1.close();

        }
        writer.close();


    }
    
    
     public void calculDataCycle2_3(String file,Node s,List<Node> sources) throws FileNotFoundException, IOException{
     Map<Integer, Integer> occurSC=cq.getOccurSourceCible(s);
     float mai;
     int nsi,idc1,idc2,occsc1,occc1c2;
     List<String> idnode ;
         //get calcul cycle2_3
          CSVReader reader = new CSVReader(new FileReader(file+"/cycle2_3.csv"), '\t');
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/cycle2_3Calcul.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
         String[] nextLine;
     while((nextLine=reader.readNext())!=null){
          idc1=Integer.parseInt(nextLine[1]);
          occsc1=occurSC.get(idc1);
     if(nextLine.length==2){
        idnode = Arrays.asList(nextLine);
        nsi=cq.nombreNodeSourceInCycle(idnode, sources);
       // System.out.println(nsi);
        mai=(float)occsc1;
        //System.out.println(mai);
        String[] cyclecal={nextLine[0],nextLine[1],String.valueOf(mai),String.valueOf(nsi)};
        writer.writeNext(cyclecal);
        
     }
     if(nextLine.length==3){
          idc2=Integer.parseInt(nextLine[2]);
      idnode = Arrays.asList(nextLine);
        nsi=cq.nombreNodeSourceInCycle(idnode, sources);
       // System.out.println(nsi);
        occc1c2=cq.getOccurCC(idc1,idc2);
         mai=(float)(occsc1+occurSC.get(idc2)+occc1c2)/3;
       // System.out.println(mai);
        String[] cyclecal={nextLine[0],nextLine[1],nextLine[2],String.valueOf(mai),String.valueOf(nsi)};
        writer.writeNext(cyclecal);
     }
     }
     writer.close();
     reader.close();
      File MyFile = new File(file+"/cycle2_3.csv"); 
     if(MyFile.delete())
         System.out.println(file+"/cycle2_3.csv est supprimer");
     }
    public void calculDataCycle4(String file,Node s,List<Node> sources) throws FileNotFoundException, IOException{
     Map<Integer, Integer> occurSC=cq.getOccurSourceCible(s);
     float mai;
     int nsi,idc1,idc2,idx,occsc1,occsc2,occc1x,occc2x;
     File MyFile;
     List<String> idnode;
      CSVReader reader ;
      CSVWriter writer ;
         //get calcul cycle4
      List<Node> cibels = cq.getNodeCible(s);
            for (Node c1 : cibels) {
            reader = new CSVReader(new FileReader(file + "/" + c1.getId() + "cycle4.csv"), '\t');
           writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file+"/" + c1.getId() +"cycle4Calcul.csv"), "UTF-8"), '\t', CSVWriter.NO_QUOTE_CHARACTER);
         String[] nextLine;
     while((nextLine=reader.readNext())!=null){
          idc1=Integer.parseInt(nextLine[1]);
          idc2=Integer.parseInt(nextLine[3]);
          idx=Integer.parseInt(nextLine[2]);
          occsc1=occurSC.get(idc1);
          occsc2=occurSC.get(idc2);
          occc1x=cq.getOccurCC(idc1,idx);
          occc2x=cq.getOccurCC(idc2,idx);
          idnode = Arrays.asList(nextLine);
         nsi=cq.nombreNodeSourceInCycle(idnode, sources);
         mai=(float)(occsc1+occsc2+occc1x+occc2x)/4;
        String[] cyclecal={nextLine[0],nextLine[1],nextLine[2],nextLine[3],String.valueOf(mai),String.valueOf(nsi)};
        writer.writeNext(cyclecal);
     }
     reader.close();
     writer.close();
      MyFile = new File(file + "/" + c1.getId() + "cycle4.csv"); 
     if(MyFile.delete())
         System.out.println(file + "/" + c1.getId() + "cycle4.csv est supprimer");
            }
     }
    
}
