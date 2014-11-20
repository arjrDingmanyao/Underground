/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.file.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.file.entity.FileInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class FileInfoDAO extends AbstractDAO<FileInfo> {

    @PersistenceContext(unitName = "FilePU")
    private EntityManager em;

    public FileInfoDAO() {
        super(FileInfo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * count file by owner
     *
     * @param clientCode
     * @param owner
     * @return
     */
    public int countByOwner(String clientCode, RealmEntity owner) {
        Long result = getEntityManager()
                .createNamedQuery("FileInfo.countByOwner", Long.class)
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * list file by owner
     *
     * @param clientCode
     * @param owner
     * @param pageInfo
     * @return
     */
    public PagedResult<FileInfo> listByOwner(String clientCode, RealmEntity owner, PageInfo pageInfo) {
        Query query = getEntityManager()
                .createNamedQuery("FileInfo.listByOwner", FileInfo.class)
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId());
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());

        int totalSize = countByOwner(clientCode, owner);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    /**
     * get file by owner and name
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public FileInfo getByOwnerAndName(String clientCode, RealmEntity owner, String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("FileInfo.getByOwnerAndName", FileInfo.class)
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
     * check whether a specific file exist
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public boolean checkExist(String clientCode, RealmEntity owner, String name) {
        FileInfo result = getByOwnerAndName(clientCode, owner, name);
        return result == null ? false : true;
    }

    /**
     * delete all files of an owner
     *
     * @param clientCode
     * @param owner
     * @return
     */
    public boolean deleteByOwner(String clientCode, RealmEntity owner) {
        getEntityManager()
                .createNamedQuery("FileInfo.deleteByOwner")
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .executeUpdate();
        return true;
    }

    /**
     * delete file by owner and name
     *
     * @param clientCode
     * @param owner
     * @param name
     * @return
     */
    public boolean deleteByOwnerAndName(String clientCode, RealmEntity owner, String name) {
        getEntityManager()
                .createNamedQuery("FileInfo.deleteByOwnerAndName")
                .setParameter("clientCode", clientCode)
                .setParameter("realm", owner.getRealm())
                .setParameter("entityId", owner.getEntityId())
                .setParameter("name", name)
                .executeUpdate();
        return true;
    }
}
