/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.tag.entities.Tag;
import com.creditcloud.tag.entities.TagEntity;
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
@Stateless
@LocalBean
public class TagEntityDAO extends AbstractDAO<TagEntity> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "TagPU")
    private EntityManager em;

    public TagEntityDAO() {
        super(TagEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagEntity getByRealmEntity(RealmEntity entity) {
        try {
            return getEntityManager()
                    .createNamedQuery("TagEntity.getByRealmEntity", TagEntity.class)
                    .setParameter("entity", entity)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.warn("can not find TagEntity by {}", entity);
            return null;
        }
    }

    /**
     * 统计tag标记的entity数目
     *
     * @param tagId
     * @return
     */
    public int countByTag(Tag inTag) {
        Long result = getEntityManager()
                .createNamedQuery("TagEntity.countByTag", Long.class)
                .setParameter("inTag", inTag)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<RealmEntity> listByTag(Tag inTag, PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("TagEntity.listByTag", RealmEntity.class)
                .setParameter("inTag", inTag)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());

        int totalSize = countByTag(inTag);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    public boolean checkTagExist(Tag inTag, RealmEntity entity) {
        Long result = getEntityManager()
                .createNamedQuery("TagEntity.countByEntityAndTag", Long.class)
                .setParameter("inTag", inTag)
                .setParameter("entity", entity)
                .getSingleResult();

        if (result == null || result.intValue() == 0) {
            return false;
        }
        return true;
    }

    public boolean checkCommonTag(List<String> ids, Tag commonTag) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        Long result = getEntityManager()
                .createNamedQuery("TagEntity.checkCommontTag", Long.class)
                .setParameter("commonTag", commonTag)
                .setParameter("ids", ids)
                .getSingleResult();
        int expected = ids.size();
        int actual = result == null ? 0 : result.intValue();
        return expected == actual;
    }

    public int countByTagAndRealm(Realm realm, Tag inTag) {
        Long result = getEntityManager()
                .createNamedQuery("TagEntity.countByTagAndRealm", Long.class)
                .setParameter("realm", realm)
                .setParameter("inTag", inTag)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<RealmEntity> listByTagAndRealm(Realm realm, Tag inTag, PageInfo pageInfo) {
        List<RealmEntity> result = getEntityManager()
                .createNamedQuery("TagEntity.listByTagAndRealm", RealmEntity.class)
                .setParameter("realm", realm)
                .setParameter("inTag", inTag)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();

        return new PagedResult<>(result, countByTagAndRealm(realm, inTag));
    }

    public List<Tag> listTagByRealm(RealmEntity entity, Realm realm) {
        return getEntityManager()
                .createNamedQuery("TagEntity.listTagByRealm", Tag.class)
                .setParameter("entity", entity)
                .setParameter("realm", realm)
                .getResultList();
    }
}
