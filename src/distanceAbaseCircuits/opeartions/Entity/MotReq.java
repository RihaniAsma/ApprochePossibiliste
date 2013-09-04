/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Rihani Asma
 */
@Entity
public class MotReq implements Serializable {
   private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    
    private String mot;
    
    // relation inverse Motreq (one) -> CycleDataBean (many) de la relation CycleDataBean (many) -> Motreq(one)
    // cascade insertion Motreq -> insertion CycleDataBean
    // cascade maj Motreq -> maj CycleDataBean
    // cascade suppression Motreq -> suppression CycleDataBean
    @OneToMany(mappedBy = "mot_id", cascade = { CascadeType.ALL })
    List<CycleDataBean> CycleList=new ArrayList<CycleDataBean>();
    
    public MotReq() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public List<CycleDataBean> getCycleDataBeanList() {
        return CycleList;
    }

    public void setCycleDataBeanList(List<CycleDataBean> CycleDataBeanList) {
        this.CycleList = CycleDataBeanList;
    }

    
}
