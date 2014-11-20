/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.SocialUser;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sobranie
 */
@Stateless
@LocalBean
public class SocialUserDAO extends AbstractDAO<SocialUser> {
    
    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public SocialUserDAO() {
        super(SocialUser.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<SocialUser> listByUserId(String userId){
        return getEntityManager()
                .createNamedQuery("SocialUser.listByUserId", SocialUser.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
