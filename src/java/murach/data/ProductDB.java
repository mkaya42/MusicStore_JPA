/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.data;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import murach.business.Product;
import murach.util.DBUtil;

/**
 *
 * @author 247940
 */
public class ProductDB {

    public static Product selectProduct(String productCode) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String query = "Select p from Product p where p.code =:productCode";
        TypedQuery<Product> q = em.createQuery(query, Product.class);
        q.setParameter("productCode", productCode);
        Product product = null;
        try {
            product = q.getSingleResult();
        } catch (NoResultException e) {
            System.out.println(e);

        } finally {
            em.close();
        }
        return product;
    }

    public static Product selectProduct(long productId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        return em.find(Product.class, productId);
    }

    public static List<Product> selectProducts() {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String query = "Select p from Product p";
        TypedQuery<Product> q = em.createQuery(query, Product.class);
        List<Product> products = null;
        try {
            products = q.getResultList();
        } catch (NoResultException e) {
            System.out.println(e);
            return null;
        } finally {
            em.close();
        }
        return products;

    }
}
