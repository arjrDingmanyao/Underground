/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.Branch;
import com.creditcloud.common.entities.dao.AbstractDAO;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author sobranie
 */
@Slf4j
@Stateless
@LocalBean
public class BranchDAO extends AbstractDAO<Branch> {

    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public BranchDAO() {
        super(Branch.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Branch> listByClient(String clientCode) {
        return getEntityManager()
                .createNamedQuery("Branch.listByClient", Branch.class)
                .setParameter("clientCode", clientCode)
                .getResultList();
    }

    public Branch getByCode(String clientCode, String code) {
        try {
            return getEntityManager()
                    .createNamedQuery("Branch.getByCode", Branch.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("code", code)
                    .getSingleResult();

        } catch (NoResultException ex) {
            log.warn("Branch not found.[clientCode={}][code={}]", clientCode, code);
            return null;
        }
    }

    public Branch getByName(String clientCode, String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("Branch.getByName", Branch.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException ex) {
            log.warn("Branch not found.[clientCode={}][name={}]", clientCode, name);
            return null;
        }
    }

    public List<Branch> listByPrincipal(String principalId) {
        return getEntityManager()
                .createNamedQuery("Branch.listByPrincipal", Branch.class)
                .setParameter("id", principalId)
                .getResultList();
    }

    public List<Branch> listByContact(String contactId) {
        return getEntityManager()
                .createNamedQuery("Branch.listByContact", Branch.class)
                .setParameter("id", contactId)
                .getResultList();
    }
}
