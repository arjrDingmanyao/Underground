/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.UserAuthenticate;
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
public class UserAuthenticateDAO extends AbstractDAO<UserAuthenticate> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public UserAuthenticateDAO() {
        super(UserAuthenticate.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /**
     * 我们的注册形式可以保证用户的手机是直接认证过的
     * 
     * @param user 
     */
    public void addNew(User user){
        UserAuthenticate result = new UserAuthenticate(user, false, true, false);
        create(result);
    }
}
