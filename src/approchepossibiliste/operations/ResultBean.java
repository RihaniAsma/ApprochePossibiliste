/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package approchepossibiliste.operations;

import org.neo4j.graphdb.Node;

/**
 *
 * @author acer
 */
public class ResultBean implements Comparable<ResultBean> {
    //resulat DPP d'un noeud cible j
    float DPP;
    
    //noeud cible j
    Node nodeC;

    public Node getNodeC() {
        return nodeC;
    }

    public void setNodeC(Node nodeC) {
        this.nodeC = nodeC;
    }
    
    public ResultBean() {
    }

    public float getDPP() {
        return DPP;
    }

    public void setDPP(float DPP) {
        this.DPP = DPP;
    }
    
// utiliser pour facilité la comparaison
    @Override
    public int compareTo(ResultBean o) {
    int resultat = 0;
      if (this.DPP > o.DPP)
         resultat = 1;
      if (this.DPP < o.DPP)
         resultat = -1;
      if (this.DPP == o.DPP)
         resultat = 0;
      return resultat;}
}
