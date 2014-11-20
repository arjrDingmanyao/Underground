/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.Role;
import com.creditcloud.common.entities.dao.AbstractDAO;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author sobranie
 */
@Slf4j
@Stateless
@LocalBean
public class RoleDAO extends AbstractDAO<Role> {

    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public RoleDAO() {
        super(Role.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Role find(String roleId, boolean includeMembers) {
        Role role = find(roleId);
        if (includeMembers) {
            role.getMembers().size();
        }
        return role;
    }
}
