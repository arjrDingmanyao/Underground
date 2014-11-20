/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.user.corporation.CorporationType;
import com.creditcloud.user.entity.CorporationUser;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean; 
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class CorporationUserDAO extends AbstractDAO<CorporationUser> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public CorporationUserDAO() {
        super(CorporationUser.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CorporationUser getByName(String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("CorporationUser.getByName", CorporationUser.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("fail to find corporation with name {}", name);
            return null;
        }
    }

    public CorporationUser getByBusiCode(String busiCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("CorporationUser.getByBusiCode", CorporationUser.class)
                    .setParameter("busiCode", busiCode)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("fail to find corporation with busiCode {}", busiCode);
            return null;
        }
    }

    public CorporationUser getByOrgCode(String orgCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("CorporationUser.getByOrgCode", CorporationUser.class)
                    .setParameter("orgCode", orgCode)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("fail to find corporation with orgCode {}", orgCode);
            return null;
        }
    }

    public CorporationUser getByTaxCode(String taxCode) {
        try {
            return getEntityManager()
                    .createNamedQuery("CorporationUser.getByTaxCode", CorporationUser.class)
                    .setParameter("taxCode", taxCode)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.debug("fail to find corporation with taxCode {}", taxCode);
            return null;
        }
    }

    public int countByType(CorporationType... type) {
        if (type == null || type.length == 0) {
            return 0;
        }

        Long result = getEntityManager()
                .createNamedQuery("CorporationUser.countByType", Long.class)
                .setParameter("typeList", Arrays.asList(type))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public int countByLegalPerson(String legalPersonId) {
        Long result = getEntityManager()
                .createNamedQuery("CorporationUser.countByLegalPerson", Long.class)
                .setParameter("legalPersonId", legalPersonId)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<CorporationUser> listByType(PageInfo info, CorporationType... type) {
        if (type == null || type.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("CorporationUser.listByType", CorporationUser.class)
                .setParameter("typeList", Arrays.asList(type));
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());

        return new PagedResult<>(query.getResultList(), countByType(type));
    }

    public List<String> listLegalPerson() {
        return getEntityManager()
                .createNamedQuery("CorporationUser.listLegalPerson", String.class)
                .getResultList();
    }

    public List<CorporationUser> listByLegalPerson(String legalPersonId) {
        return getEntityManager()
                .createNamedQuery("CorporationUser.listByLegalPerson", CorporationUser.class)
                .setParameter("legalPersonId", legalPersonId)
                .getResultList();
    }
    public List<CorporationUser> listByRtpo(Boolean rtpo) {
        return getEntityManager()
                .createNamedQuery("CorporationUser.listByRtpo", CorporationUser.class)
                .setParameter("rtpo", rtpo)
                .getResultList();
    }
}
