/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions.Entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Rihani Asma
 */
@Entity
public class CycleDataBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String chemin;
    private float psi;
    // relation principale CycleDataBean (many) -> MotReq (one)
    // implémentée par une clé étrangère (mot_id) dans CycleDataBean
    // 1 CycleDataBean a nécessairement 1 MotReq (nullable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mot_id",nullable=false)
    private MotReq mot_id;

    public CycleDataBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public float getPsi() {
        return psi;
    }

    public void setPsi(float psi) {
        this.psi = psi;
    }

  public MotReq getMot_id() {
        return mot_id;
    }

    public void setMot_id(MotReq mot_id) {
        this.mot_id = mot_id;
    }
}
