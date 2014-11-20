/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.wealthproduct.entities.Purchase;
import com.creditcloud.wealthproduct.enums.PurchaseStatus;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class PurchaseDAO extends AbstractDAO<Purchase> {
    
    @Inject
    Logger logger;
    
    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;
    
    public PurchaseDAO() {
        super(Purchase.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public int countByProduct(String productId, List<PurchaseStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Purchase.countByProduct", Long.class)
                .setParameter("productId", productId)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public PagedResult<Purchase> listByProduct(String productId, List<PurchaseStatus> statusList, PageInfo pageInfo) {
        if (statusList == null || statusList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<Purchase> result = getEntityManager()
                .createNamedQuery("Purchase.listByProduct", Purchase.class)
                .setParameter("productId", productId)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        int totalSize = countByProduct(productId, statusList);
        return new PagedResult<>(result, totalSize);
    }
    
    public int sumByProduct(String productId, List<PurchaseStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Purchase.sumByProduct", Long.class)
                .setParameter("productId", productId)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public int countByUserAndProduct(String userId, String productId, List<PurchaseStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Purchase.countByProduct", Long.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public List<Purchase> listByUserAndProduct(String userId, String productId, List<PurchaseStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Purchase.listByProduct", Purchase.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .setParameter("statusList", statusList)
                .getResultList();
    }
}
