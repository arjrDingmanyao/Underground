/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.corporation.entities.FinanceCorporation;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class FinanceCorporationDAO extends AbstractDAO<FinanceCorporation> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "CorporationPU")
    private EntityManager em;

    public FinanceCorporationDAO() {
        super(FinanceCorporation.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByFactoringCorporation(String factoringCorporation) {
        Long result = getEntityManager()
                .createNamedQuery("FinanceCorporation.countByFactoringCorporation", Long.class)
                .setParameter("factoringCorporation", factoringCorporation)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<FinanceCorporation> listByFactoringCorporation(String factoringCorporation, PageInfo pageInfo) {
        List<FinanceCorporation> result = getEntityManager()
                .createNamedQuery("FinanceCorporation.listByFactoringCorporation", FinanceCorporation.class)
                .setParameter("factoringCorporation", factoringCorporation)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByFactoringCorporation(factoringCorporation));
    }

    public FinanceCorporation getByName(String factoringCorporation, String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("FinanceCorporation.getByName", FinanceCorporation.class)
                    .setParameter("name", name)
                    .setParameter("factoringCorporation", factoringCorporation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("no finance corporation found for factoringCorporation {}, name {}.", factoringCorporation, name);
            return null;
        }
    }

    public FinanceCorporation getByBusiCode(String factoringCorporation, String busiCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("FinanceCorporation.getByBusiCode", FinanceCorporation.class)
                    .setParameter("busiCode", busiCode)
                    .setParameter("factoringCorporation", factoringCorporation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("no finance corporation found for factoringCorporation {}, busiCode {}.", factoringCorporation, busiCode);
            return null;
        }
    }

    public FinanceCorporation getByTaxCode(String factoringCorporation, String taxCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("FinanceCorporation.getByTaxCode", FinanceCorporation.class)
                    .setParameter("taxCode", taxCode)
                    .setParameter("factoringCorporation", factoringCorporation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("no finance corporation found for factoringCorporation {}, taxCode {}.", factoringCorporation, taxCode);
            return null;
        }
    }

    public FinanceCorporation getByOrgCode(String factoringCorporation, String orgCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("FinanceCorporation.getByOrgCode", FinanceCorporation.class)
                    .setParameter("orgCode", orgCode)
                    .setParameter("factoringCorporation", factoringCorporation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("no finance corporation found for factoringCorporation {}, orgCode {}.", factoringCorporation, orgCode);
            return null;
        }
    }

    public FinanceCorporation getByBankLicense(String factoringCorporation, String bankLicense) {
        try {
            return getEntityManager()
                    .createNamedQuery("FinanceCorporation.getByBankLicense", FinanceCorporation.class)
                    .setParameter("bankLicense", bankLicense)
                    .setParameter("factoringCorporation", factoringCorporation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("no finance corporation found for factoringCorporation {}, bankLicense {}.", factoringCorporation, bankLicense);
            return null;
        }
    }
}
