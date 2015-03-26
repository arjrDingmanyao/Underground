/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.BankAccount;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class FundAccountDAO extends AbstractDAO<FundAccount> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public FundAccountDAO() {
        super(FundAccount.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 列出用户的FundAccount.
     *
     * 包括UserFund和银行卡信息，银行卡号被隐藏
     *
     * @param userId
     * @return
     */
    public List<FundAccount> listAccountsByUser(String userId) {
        List<FundAccount> results = getEntityManager()
                .createNamedQuery("FundAccount.listByUser", FundAccount.class)
                .setParameter("userId", userId)
                .getResultList();
        return results;
    }

    /**
     * 增加一条银行卡记录
     *
     * @param userFund
     * @param bankAccount
     * @return
     */
    public FundAccount addNew(UserFund userFund, BankAccount bankAccount, boolean valid, boolean isDefault) {
        if (userFund != null && bankAccount != null) {
            return create(new FundAccount(userFund, bankAccount, valid, isDefault));
        } else {
            logger.error("UserFund or BankAccount is null.");
            return null;
        }
    }

    /**
     * 设置绑定的银行卡为通过验证
     *
     * @param userId
     * @param account
     * @return
     */
    public boolean setValid(String userId, String account) {
        FundAccount fundAccount = getByUserAndAccount(userId, account);
        fundAccount.setValid(true);
        edit(fundAccount);
        return true;
    }

    /**
     * 设置首选默认银行卡号,同时将其他卡号取消首选默认
     *
     * @param userId
     * @param account
     * @return
     */
    public boolean setDefaultAccountByUser(String userId, String account) {
        FundAccount fundAccount = getDefaultAccount(userId);
        if (fundAccount != null) {
            fundAccount.setDefaultAccount(false);
            edit(fundAccount);
        }
        FundAccount newDefault = getByUserAndAccount(userId, account);
        if (newDefault != null) {
            newDefault.setDefaultAccount(true);
            edit(newDefault);
        }
        return true;
    }

    /**
     * 获取默认的银行卡号
     *
     * @param userId
     * @param account
     * @return
     */
    public FundAccount getDefaultAccount(String userId) {
        try {
            return getEntityManager()
                    .createNamedQuery("FundAccount.getDefaultByUser", FundAccount.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing;
        }
        return null;
    }

    /**
     * 检查用户名下账户号唯一性
     *
     * @param userId
     * @param account
     * @return
     */
    public boolean checkExistByUserAndAccount(String userId, String account) {
        return getByUserAndAccount(userId, account) != null;
    }

    /**
     * 根据用户的银行账号获得fundaccount
     *
     * @param userId
     * @param account
     * @return
     */
    public FundAccount getByUserAndAccount(String userId, String account) {
        try {
            return getEntityManager()
                    .createNamedQuery("FundAccount.getByUserAndAccount", FundAccount.class)
                    .setParameter("userId", userId)
                    .setParameter("account", account)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
        }
        return null;
    }
    
     /**
     * 根据银行账号获得fundaccount
     *
     * @param account
     * @return
     */
    public FundAccount getByAccount(String account) {
        try {
            return getEntityManager()
                    .createNamedQuery("FundAccount.getByAccount", FundAccount.class)
                    .setParameter("account", account)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
        }
        return null;
    }

    /**
     * 根据userId和银行卡号删除
     *
     * @param userId
     * @param account
     * @return
     */
    public boolean deleteByUserAndAccount(String userId, String account) {
        getEntityManager()
                .createNamedQuery("FundAccount.deleteByUserAndAccount")
                .setParameter("userId", userId)
                .setParameter("account", account)
                .executeUpdate();
        return true;
    }

    /**
     * 删除用户所有银行银行账号
     *
     * @param userId
     * @return
     */
    public boolean deleteByUser(String userId) {
        getEntityManager()
                .createNamedQuery("FundAccount.deleteByUser")
                .setParameter("userId", userId)
                .executeUpdate();
        return true;
    }
}
