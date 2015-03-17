/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.dao;

import com.creditcloud.cms.entities.Banner;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class BannerDAO extends AbstractDAO<Banner> {

    @Inject 
    Logger logger;

    @PersistenceContext(unitName = "NewsPU")
    private EntityManager em;

    public BannerDAO() {
        super(Banner.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Banner save(Banner banner) {
        if (banner.getId() == null || find(banner.getId()) == null) {
            return create(banner);
        }
        /**
         * TODO 只能修改下列项目
         */
        Banner result = find(banner.getId());
        result.setAuthor(banner.getAuthor());
        result.setImgUrl(banner.getImgUrl());
        result.setName(banner.getName());
        result.setNumber(banner.getNumber());
        result.setStatus(banner.getStatus());
        result.setUrl(banner.getUrl());
        result.setUpdateBy(banner.getUpdateBy());
        result.setUpdateTime(banner.getUpdateTime());
        edit(result);
        return result;
    }

    /**
     * @param name
     * @param info
     * @return
     */
    public PagedResult<Banner> listByName(String name, PageInfo info) {
        List<Banner> result = getEntityManager()
                .createNamedQuery("Banner.listByName", Banner.class)
                .setParameter("name",  "%" + name + "%")
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize())
                .getResultList();
        return new PagedResult(result, countByName(name));
    }


    /**
     *
     * @param info
     * @return
     */
    public PagedResult<Banner> listAll(PageInfo info) {
        List<Banner> result = getEntityManager()
                .createNamedQuery("Banner.listAll", Banner.class)
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize())
                .getResultList();
        return new PagedResult(result, countAll());
    }
    
        /**
     *
     * @param info
     * @return
     */
    public PagedResult<Banner> listActive(PageInfo info) {
        List<Banner> result = getEntityManager()
                .createNamedQuery("Banner.listActive", Banner.class)
                .setParameter("status", 1)
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize())
                .getResultList();
        return new PagedResult(result, countActive());
    }

    /**
     *
     * @param name
     * @return
     */
    public int countByName(String name) {
        Long result = getEntityManager()
                .createNamedQuery("Banner.countByName", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    
    /**
     *
     * @return
     */
    public int countAll() {
        Long result = getEntityManager()
                .createNamedQuery("Banner.countAll", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
     /**
     *
     * @return
     */
    public int countActive() {
        Long result = getEntityManager()
                .createNamedQuery("Banner.countActive", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    
    

    public void deleteByChannel(String id) {
        getEntityManager()
                .createNamedQuery("Banner.deleteById")
                .setParameter("id", id)
                .executeUpdate();
    }
}
