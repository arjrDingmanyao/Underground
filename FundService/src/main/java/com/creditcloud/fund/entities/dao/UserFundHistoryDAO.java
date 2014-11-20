/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.fund.entities.record.UserFundHistory;
import java.util.Date;
import java.util.List;
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
public class UserFundHistoryDAO extends AbstractDAO<UserFundHistory> {

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public UserFundHistoryDAO() {
        super(UserFundHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * list user fund history between from date and to date
     *
     * @param userId
     * @param from
     * @param to
     * @return
     */
    public List<UserFundHistory> listByUserAndDate(String userId, Date from, Date to) {
        return getEntityManager()
                .createNamedQuery("UserFundHistory.listByUserAndDate", UserFundHistory.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
