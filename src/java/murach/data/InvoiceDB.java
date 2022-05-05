/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.data;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import murach.business.Invoice;
import murach.util.DBUtil;

/**
 *
 * @author 247940
 */
public class InvoiceDB {

    public static void insert(Invoice invoice) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            em.persist(invoice);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
            transaction.rollback();
        } finally {
            em.close();
        }

    }

    public static void update(Invoice invoice) {

        invoice.setIsProcessed(true);
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            em.merge(invoice);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
            transaction.rollback();
        } finally {
            em.close();
        }

    }

    public static List<Invoice> selectUnprocessedInvoices() {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String query = "Select i from invoice i where i.isprocessed=false";
        TypedQuery<Invoice> q = em.createQuery(query, Invoice.class);

        List<Invoice> results = null;

        try {
            results = q.getResultList();
        } catch (NoResultException e) {
            System.out.println(e);
            return null;
        } finally {
            em.close();
        }
        return results;
    }
}
