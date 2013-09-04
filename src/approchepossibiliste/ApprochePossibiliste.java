/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package approchepossibiliste;

import approchepossibiliste.operations.CalculPossibiliste;
import commun.ResultBean;
import java.util.List;

/**
 *
 * @author acer
 */
public class ApprochePossibiliste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CalculPossibiliste op = new CalculPossibiliste();
    // List<ResultBean> list_rsl = 
        //op.calculPossibiliste("discussion politique décision négociation préalable partir politique groupe social résultat final consultation Referendum constitution Afrique Sud ");
         List<ResultBean> list_rsl =  op.calculScore("biologique clair");
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.###");
       for (ResultBean r : list_rsl) {
            //j'ai changé le ResultBean en ajoutant le param Node n comme ça vous pouvez récupérer tous les propriétés
            //d'un noeud sans faire recours au requete
           System.out.println(r.getNodeC().getProperty("texte") + "  " + df.format(r.getScore()));

        }
      op.cq.shutdowndb();
    }
}
