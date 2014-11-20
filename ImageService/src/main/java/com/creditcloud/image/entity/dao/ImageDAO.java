/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.image.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.image.entity.Image;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
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
public class ImageDAO extends AbstractDAO<Image> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ImagePU")
    private EntityManager em;

    public ImageDAO() {
        super(Image.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * count image by owner
     *
     * @param clientCode
     * @param owner
     * @return
     */
    public int countByOwner(String clientCode, RealmEntity owner) {
        Long result = getEntityManager()
                .createNamedQuery("Image.countByOwner", Long.class)
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * list image by owner
     *
     * @param clientCode
     * @param owner
     * @param pageInfo
     * @return
     */
    public PagedResult<Image> listByOwner(String clientCode, RealmEntity owner, PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("Image.listByOwner", Image.class)
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId());
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());

        int totalSize = countByOwner(clientCode, owner);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    /**
     * get image by owner and name
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public Image getByOwnerAndName(String clientCode, RealmEntity owner, String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("Image.getByOwnerAndName", Image.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("realm", owner.getRealm())
                    .setParameter("entityId", owner.getEntityId())
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
        }
        return null;
    }

    /**
     * check whether a specific image exist
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public boolean checkExist(String clientCode, RealmEntity owner, String name) {
        Image result = getByOwnerAndName(clientCode, owner, name);
        return result == null ? false : true;
    }

    /**
     * delete all images of an owner
     *
     * @param clientCode
     * @param owner
     * @return
     */
    public boolean deleteByOwner(String clientCode, RealmEntity owner) {
        getEntityManager()
                .createNamedQuery("Image.deleteByOwner")
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .executeUpdate();
        return true;
    }

    /**
     * delete image by owner and name
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public boolean deleteByOwnerAndName(String clientCode, RealmEntity owner, String name) {
        getEntityManager()
                .createNamedQuery("Image.deleteByOwnerAndName")
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .setParameter("name", name)
                .executeUpdate();
        return true;
    }
}
