/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.dao;

import com.creditcloud.cms.enums.Category;
import com.creditcloud.cms.entities.Article;
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
public class ArticleDAO extends AbstractDAO<Article> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "NewsPU")
    private EntityManager em;

    public ArticleDAO() {
        super(Article.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Article save(Article article) {
        if (article.getId() == null || find(article.getId()) == null) {
            return create(article);
        }
        /**
         * TODO 只能修改下列项目
         */
        Article result = find(article.getId());
        result.setAuthor(article.getAuthor());
        result.setCategory(article.getCategory());
        result.setContent(article.getContent());
        result.setHasImage(article.isHasImage());
        result.setMedia(article.getMedia());
        result.setNewsId(article.getNewsId());
        result.setPriority(article.isPriority());
        result.setPubDate(article.getPubDate());
        result.setTitle(article.getTitle());
        result.setUrl(article.getUrl());
        result.setBgColor(article.getBgColor());
        edit(result);
        return result;
    }

    /**
     *
     * @param category
     * @param info
     * @return
     */
    public PagedResult<Article> listByCategory(Category category, PageInfo info) {
        List<Article> result = getEntityManager()
                .createNamedQuery("Article.listByCategory", Article.class)
                .setParameter("category", category)
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize())
                .getResultList();
        return new PagedResult(result, countByCategory(category));
    }

    /**
     *
     * @param category
     * @return
     */
    public int countByCategory(Category category) {
        Long result = getEntityManager()
                .createNamedQuery("Article.countByCategory", Long.class)
                .setParameter("category", category)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     *
     * @param id
     * @param info
     * @return
     */
    public PagedResult<Article> listByChannel(String id, PageInfo info) {
        List<Article> result = getEntityManager()
                .createNamedQuery("Article.listByChannel", Article.class)
                .setParameter("id", id)
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize())
                .getResultList();
        return new PagedResult(result, countByChannel(id));
    }

    /**
     *
     * @param id
     * @return
     */
    public int countByChannel(String id) {
        Long result = getEntityManager()
                .createNamedQuery("Article.countByChannel", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public void deleteByChannel(String id) {
        getEntityManager()
                .createNamedQuery("Article.deleteByChannel")
                .setParameter("id", id)
                .executeUpdate();
    }
}
