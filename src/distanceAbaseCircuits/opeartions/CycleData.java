/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions;

import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Rihani Asma
 */
public class CycleData {
    
    //list des noeuds qui forme le cycle
    List<Node> listnode=new ArrayList<>();
    
    //moyenne des poids des arcs de cycle
    float  Mai;
    
    //nombre des noeuds source qui exite dans le cycle
    int nbsource;

    public CycleData() {
    }

    public List<Node> getListnode() {
        return listnode;
    }

    public void setListnode(List<Node> listnode) {
        this.listnode = listnode;
    }

    public float getMai() {
        return Mai;
    }

    public void setMai(float Mai) {
        this.Mai = Mai;
    }

    public int getNbsource() {
        return nbsource;
    }

    public void setNbsource(int nbsource) {
        this.nbsource = nbsource;
    }
    
    public void addNode(Node n) {
		listnode.add(n);
               setListnode(listnode);
	}
    
    
}
