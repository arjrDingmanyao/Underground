/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities.dao;

import com.creditcloud.appoint.entities.AppointAgent;
import com.creditcloud.common.entities.dao.AbstractDAO;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class AppointAgentDAO extends AbstractDAO<AppointAgent> {

    @PersistenceContext(unitName = "AppointPU")
    private EntityManager em;

    public AppointAgentDAO() {
        super(AppointAgent.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
