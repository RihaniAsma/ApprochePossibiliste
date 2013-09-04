/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commun;

import org.neo4j.graphdb.Node;

/**
 *
 * @author acer
 */
public class ResultBean implements Comparable<ResultBean> {
    //resulat DPP d'un noeud cible j
    float score;
    
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

// utiliser pour facilité la comparaison
    @Override
    public int compareTo(ResultBean o) {
    int resultat = 0;
      if (this.score > o.score)
         resultat = 1;
      if (this.score < o.score)
         resultat = -1;
      if (this.score == o.score)
         resultat = 0;
      return resultat;}
}
