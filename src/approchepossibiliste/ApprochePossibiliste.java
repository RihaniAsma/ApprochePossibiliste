/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package approchepossibiliste;

import approchepossibiliste.operations.DataQuery;
import approchepossibiliste.operations.ResultBean;
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
        DataQuery op = new DataQuery();
     List<ResultBean> list_rsl = 
        op.calculPossibiliste("discussion politique décision négociation préalable partir politique groupe social résultat final consultation Referendum constitution Afrique Sud ");
        // List<ResultBean> list_rsl =  op.calculPossibiliste("biologique clair");
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.###");
       for (ResultBean r : list_rsl) {
            //j'ai changé le ResultBean en ajoutant le param Node n comme ça vous pouvez récupérer tous les propriétés
            //d'un noeud sans faire recours au requete
           System.out.println(r.getNodeC().getProperty("texte") + "  " + df.format(r.getDPP()));//pour récupérer l'id d'un noeud
            //System.out.println(r.getNodeC().getProperty("type"));//pour récupérer les propriétés d'un noeud (type,texte,langue)
            // System.out.println(df.format(r.getDPP()));//pour récupérer DPP de chaque noeud
            //System.out.println(list_rsl.size());

        }
    }
}
