/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities.dao;

import com.creditcloud.appoint.entities.AppointUser;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
@LocalBean
@Stateless
public class AppointUserDAO extends AbstractDAO<AppointUser> {
    
    @Inject
    Logger logger;

    @PersistenceContext(unitName = "AppointPU")
    private EntityManager em;

    public AppointUserDAO() {
        super(AppointUser.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagedResult<AppointUser> listAll(PageInfo pageInfo) {
        List<AppointUser> result = getEntityManager()
                .createNamedQuery("AppointUser.listAll", AppointUser.class)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, count());
    }

    public int countByBranch(String... branch) {
        if (branch == null || branch.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointUser.countByBranch", Long.class)
                .setParameter("branchList", Arrays.asList(branch))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<AppointUser> listByBranch(PageInfo pageInfo, String... branch) {
        if (branch == null || branch.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<AppointUser> result = getEntityManager()
                .createNamedQuery("AppointUser.listByBranch", AppointUser.class)
                .setParameter("branchList", Arrays.asList(branch))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByBranch(branch));
    }

    public List<ElementCount<String>> countEachByBranch() {
        List<ElementCount<String>> result = new ArrayList();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointUser.countEachByBranch")
                .getResultList();
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(branch, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    public AppointUser findByIdNumber(String idNumber) {
        try {
            return getEntityManager()
                    .createNamedQuery("AppointUser.findByIdNumber", AppointUser.class)
                    .setParameter("idNumber", idNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
            logger.warn("can not find AppointUser with idNumber {}", idNumber);
            return null;
        }
    }
}
