/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.corporation.entities.Factoring;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class FactoringDAO extends AbstractDAO<Factoring> {

    @PersistenceContext(unitName = "CorporationPU")
    private EntityManager em;

    public FactoringDAO() {
        super(Factoring.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByFactoringCorporation(String factoringCorporation) {
        Long result = getEntityManager()
                .createNamedQuery("Factoring.countByFactoringCorporation", Long.class)
                .setParameter("factoringCorporation", factoringCorporation)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<Factoring> listByFactoringCorporation(String factoringCorporation, PageInfo pageInfo) {
        List<Factoring> result = getEntityManager()
                .createNamedQuery("Factoring.listByFactoringCorporation", Factoring.class)
                .setParameter("factoringCorporation", factoringCorporation)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByFactoringCorporation(factoringCorporation));
    }

    public int countByFinanceCorporation(String financeCorporation) {
        Long result = getEntityManager()
                .createNamedQuery("Factoring.countByFinanceCorporation", Long.class)
                .setParameter("financeCorporation", financeCorporation)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<Factoring> listByFinanceCorporation(String financeCorporation, PageInfo pageInfo) {
        List<Factoring> result = getEntityManager()
                .createNamedQuery("Factoring.listByFinanceCorporation", Factoring.class)
                .setParameter("financeCorporation", financeCorporation)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByFinanceCorporation(financeCorporation));
    }
}
