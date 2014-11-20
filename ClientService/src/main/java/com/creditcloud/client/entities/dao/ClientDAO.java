/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.Client;
import com.creditcloud.common.entities.dao.AbstractDAO;
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
public class ClientDAO extends AbstractDAO<Client> {

    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public ClientDAO() {
        super(Client.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Client getClientByCode(String clientCode) {
        Client result = null;
        try {
            result = (Client) em.createNamedQuery("Client.findByCode")
                    .setParameter("code", clientCode)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.warn("Client: {} not exists.", clientCode);
        } catch (Exception ex) {
            log.error("Exception happend when query Client by Code: " + clientCode, ex);
        }
        return result;
    }
}
