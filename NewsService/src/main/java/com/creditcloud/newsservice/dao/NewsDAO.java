/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.newsservice.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.newsservice.model.News;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class NewsDAO extends AbstractDAO<News> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "InfoPU")
    private EntityManager em;

    public NewsDAO() {
        super(News.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<News> getNewsByMedia(String media) {
        List<News> result = null;
        try {
            result = getEntityManager().createNamedQuery("News.findByMedia")
                    .setParameter("media", media)
                    .getResultList();
        } catch (NoResultException ex) {
            logger.warn("News: {} not exists.", media);
        } catch (Exception ex) {
            logger.error("Exception happend when query News by Media: " + media, ex);
        }
        return result;
    }

    public List<News> findNewsByKeyword(String keyWord) {
        List<News> information = null;
        try {
            information = getEntityManager()
                    .createNamedQuery("News.getByKeyword")
                    .setParameter("keyWord", "%" + keyWord + "%")
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException ex) {
            logger.warn("Can't find information by content keyWord: {}", keyWord, ex);
        }
        return information;
    }
}
