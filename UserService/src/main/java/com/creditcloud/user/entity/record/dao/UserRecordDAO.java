/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.record.UserRecord;
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
public class UserRecordDAO extends AbstractDAO<UserRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public UserRecordDAO() {
        super(UserRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
