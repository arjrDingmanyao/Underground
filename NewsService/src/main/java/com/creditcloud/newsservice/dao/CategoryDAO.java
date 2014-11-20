/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.newsservice.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.newsservice.model.Category;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class CategoryDAO extends AbstractDAO<Category> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "InfoPU")
    private EntityManager em;

    public CategoryDAO() {
        super(Category.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void update(String name) {
        getEntityManager().createNamedQuery("Category.update").setParameter("name", name).executeUpdate();
    }

    public void delete(String name) {
        getEntityManager().createNamedQuery("Category.delete").setParameter("name", name).executeUpdate();
    }

    public Category getByName(String name) {
        return getEntityManager().createNamedQuery("Category.findByName", Category.class)
                .setParameter("name", name)
                .getSingleResult();
    }

}
