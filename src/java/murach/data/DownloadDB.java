/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import murach.business.Download;
import murach.util.DBUtil;

/**
 *
 * @author 247940
 */
public class DownloadDB {

    public static void insert(Download download) {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(download);
        } catch (Exception e) {
            System.out.println();
            transaction.rollback();
        } finally {
            em.close();
            transaction.commit();
        }
    }

}
