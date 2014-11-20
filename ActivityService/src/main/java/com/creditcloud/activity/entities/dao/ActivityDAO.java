/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.activity.entities.dao;

import com.creditcloud.activity.entities.Activity;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class ActivityDAO extends AbstractDAO<Activity> {

    @PersistenceContext(unitName = "ActivityPU")
    private EntityManager em;

    public ActivityDAO() {
        super(Activity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByTarge(RealmEntity target) {
        Long result = getEntityManager()
                .createNamedQuery("Activity.countByTarge", Long.class)
                .setParameter("target", target)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<Activity> listByTarge(RealmEntity target, PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("Activity.listByTarget", Activity.class)
                .setParameter("target", target)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());

        return new PagedResult<>(query.getResultList(), countByTarge(target));
    }

    public int countByPerformer(RealmEntity performer) {
        Long result = getEntityManager()
                .createNamedQuery("Activity.countByPerformer", Long.class)
                .setParameter("performer", performer)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public PagedResult<Activity> listByPerformer(RealmEntity performer, PageInfo pageInfo){
        Query query = getEntityManager()
        .createNamedQuery("Activity.listByPerformer", Activity.class)
        .setParameter("performer", performer)
        .setFirstResult(pageInfo.getOffset())
        .setMaxResults(pageInfo.getSize());
        
        return new PagedResult<>(query.getResultList(),countByPerformer(performer));
    }
}
