/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits;

import approchepossibiliste.operations.CalculPossibiliste;
import commun.ResultBean;
import distanceAbaseCircuits.opeartions.CalculAbaseDeCircuit;
import java.util.List;

/**
 *
 * @author Rihani Asma
 */
public class DistanceAbaseCircuits {
    public static void main(String[] args){
    
    // TODO code application logic here
        CalculAbaseDeCircuit op = new CalculAbaseDeCircuit();
    // List<ResultBean> list_rsl = 
        //op.calculPossibiliste("discussion politique décision négociation préalable partir politique groupe social résultat final consultation Referendum constitution Afrique Sud ");
         List<ResultBean> list_rsl =  op.calculScore("biologique1 clair");
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.###");
       for (ResultBean r : list_rsl) {
            //j'ai changé le ResultBean en ajoutant le param Node n comme ça vous pouvez récupérer tous les propriétés
            //d'un noeud sans faire recours au requete
           System.out.println(r.getNodeC().getProperty("texte") + "  " + df.format(r.getScore()));

        }
       op.cq.shutdowndb();
    }
    
}
