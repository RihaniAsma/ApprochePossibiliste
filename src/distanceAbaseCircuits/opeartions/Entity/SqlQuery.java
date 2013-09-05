/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distanceAbaseCircuits.opeartions.Entity;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Rihani Asma
 */
public class SqlQuery {
    private EntityManager em;

    public SqlQuery(EntityManager em) {
        this.em = em;
    }
    
  
    
     public boolean exist(String mot){
        boolean exist = false;
        Query resultat= em.createQuery("select count(*) from MotReq p where p.mot= :mot");
        resultat.setParameter("mot", mot);
        long x=(long) resultat.getSingleResult();
        if(x!=0)
            exist=true;
        return exist;
        }
   public void save(MotReq motReq) {
       em.getTransaction().begin();
		em.persist(motReq);
       em.getTransaction().commit();
}
    public void saveCycle(CycleDataBean cdb) {
       em.getTransaction().begin();
		em.persist(cdb);
       em.getTransaction().commit();
}
   public float tatalPSIDeReq(List<Long> listmot_id ){
   float sum=0;
   em.getTransaction().begin();
    Query resultat= em.createQuery("select sum(psi) from CycleDataBean p where p.mot_id.id in (:listmot_id)");
       resultat.setParameter("listmot_id", listmot_id);
         sum=(float)(double) resultat.getSingleResult();
         em.getTransaction().commit();
   return sum;
   }
   public float tatalPSIDeNodeC(List<Long> listmot_id,Long node_id ){
   float sum=0;
   em.getTransaction().begin();
    Query resultat= em.createQuery("select sum(psi) from CycleDataBean p where p.mot_id.id in (:listmot_id) and p.chemin like :node_id ");
       resultat.setParameter("listmot_id", listmot_id);
       resultat.setParameter("node_id", "%#"+node_id+"#%");
         sum=(float)(double) resultat.getSingleResult();
         em.getTransaction().commit();
   return sum;
   }
   
  /* public static void main(String[] argv) {
             EntityManagerFactory emf;
       EntityManager em;
       EntityTransaction tx;
       emf = Persistence.createEntityManagerFactory("approchePossibilistePU");
       em = null;
     // on récupère un EntityManager à  partir de l'EntityManagerFactory précédent
      em = emf.createEntityManager();
     SqlQuery sq=new SqlQuery(em);
     List<Long> listmot_id=new ArrayList();
     Long a=new Long(1);
     Long b=new Long(5);
     listmot_id.add(a);
     listmot_id.add(b);
     Long node_id=new Long(4);
     //Long node_id="14743";
     float x=sq.tatalPSIDeNodeC(listmot_id, node_id);
     System.out.println(x);
     
      em.close();
      emf.close();
      }*/
}
