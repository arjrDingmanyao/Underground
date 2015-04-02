/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.sms.entities.SMSBlackList;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class SMSBlackListDAO extends AbstractDAO<SMSBlackList> {

    @Inject 
    Logger logger;

    @PersistenceContext(unitName = "SmsPU")
    private EntityManager em;

    public SMSBlackListDAO() {
        super(SMSBlackList.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SMSBlackList save(SMSBlackList smsBlack) {
        if (smsBlack.getId() == null || find(smsBlack.getId()) == null) {
            return create(smsBlack);
        }
        /**
         * TODO 只能修改下列项目
         */
        SMSBlackList result = find(smsBlack.getId());
        result.setNumber(smsBlack.getNumber());
        edit(result);
        return result;
    }

    /**
     *
     * @return
     */
    public int countAll() {
        Long result = getEntityManager()
                .createNamedQuery("SMSBlackList.countAll", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public void deleteByChannel(String id) {
        getEntityManager()
                .createNamedQuery("SMSBlackList.deleteById")
                .setParameter("id", id)
                .executeUpdate();
    }
}
