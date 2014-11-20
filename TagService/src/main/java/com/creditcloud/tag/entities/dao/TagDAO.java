/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.tag.entities.Tag;
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
public class TagDAO extends AbstractDAO<Tag> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "TagPU")
    private EntityManager em;

    public TagDAO() {
        super(Tag.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Tag addNew(Realm realm, String name, String alias, String description) {
        Tag tag = getByName(realm, name);
        if (tag == null) {
            tag = new Tag(realm, name, alias, description);
            try {
                getValidatorWrapper().validate(tag);
                return create(tag);
            } catch (InvalidException ex) {
                logger.warn("tag {} is not valid!", tag.toString(), ex);
            } catch (Exception ex) {
                logger.warn("tag {} is not valid!", tag.toString(), ex);
            }
            return null;
        }
        logger.debug("tag already exist for name {}", name);
        return tag;
    }

    public Tag getByName(Realm realm, String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("Tag.getByName", Tag.class)
                    .setParameter("realm", realm)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.warn("can not find tag with name {}", name);
            return null;
        }
    }

    public int countByRealm(Realm realm) {
        Long result = getEntityManager()
                .createNamedQuery("Tag.countByRealm", Long.class)
                .setParameter("realm", realm)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<Tag> listByRealm(Realm realm, PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("Tag.listByRealm", Tag.class)
                .setParameter("realm", realm)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = countByRealm(realm);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    public PagedResult<Tag> listAll(PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("Tag.listAll", Tag.class)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = count();
        return new PagedResult<>(query.getResultList(), totalSize);
    }
}
