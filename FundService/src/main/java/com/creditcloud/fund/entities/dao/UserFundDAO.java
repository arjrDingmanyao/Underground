/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class UserFundDAO extends AbstractDAO<UserFund> {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public UserFundDAO() {
        super(UserFund.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 这里需要获取更新过的
     *
     * @param id
     * @return
     */
    @Override
    public UserFund find(Object id) {
        UserFund userFund = super.find(id);
        if (userFund != null) {
            getEntityManager().refresh(userFund);
        }
        return userFund;
    }

    /**
     * 根据User/Corporation创建UserFund.
     *
     * @param entityId
     * @return
     */
    public UserFund addNew(String entityId) {
        UserFund fund = new UserFund(entityId,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO,
                                     BigDecimal.ZERO);
        try {
            getValidatorWrapper().tryValidate(fund);
            return create(fund);
        } catch (InvalidException ex) {
            logger.warn("userfund {} is not valid!", fund.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new userfund failed!!!\n{}", fund.toString(), ex);
        }
        return null;
    }

    public boolean freeze(String userId, BigDecimal amount) {
        if (find(userId) == null) {
            logger.error("user {} not exist for freeze.", userId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.freeze")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to freeze for user {}", userId);
        }
        return result > 0;
    }

    public boolean release(String userId, BigDecimal amount) {
        UserFund uf = find(userId);
        if (uf == null) {
            logger.error("user {} not exist for release.", userId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.release")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to release.[userId={}][amount={}][frozenAmount={}]", userId, amount, uf.getFrozenAmount());
        }
        return result > 0;
    }

    /**
     * just deduct from frozenAmount, but do not add in availableAmount
     *
     * @param userId
     * @param amount
     * @return
     */
    public boolean directRelease(String userId, BigDecimal amount) {
        UserFund uf = find(userId);
        if (uf == null) {
            logger.error("user {} not exist for directRelease.", userId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.directRelease")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to directRelease.[userId={}][amount={}][frozenAmount={}]", userId, amount, uf.getFrozenAmount());
        }
        return result > 0;
    }

    public boolean deposit(String userId, BigDecimal amount) {
        if (find(userId) == null) {
            logger.error("user {} not exist for deposit.", userId);
            return false;
        }
        getEntityManager()
                .createNamedQuery("UserFund.deposit")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        return true;
    }

    public boolean withdraw(String userId, BigDecimal out, BigDecimal withdraw) {
        if (find(userId) == null) {
            logger.error("user {} not exist for withdraw.", userId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.withdraw")
                .setParameter("userId", userId)
                .setParameter("out", out)
                .setParameter("withdraw", withdraw)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to withdraw for user {}", userId);
        }
        return result > 0;
    }

    public void calibrate(String userId, BigDecimal diffAvailable, BigDecimal diffFreeze) {
        if (find(userId) == null) {
            logger.error("user {} not exist for calibrate.", userId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.calibrate")
                .setParameter("userId", userId)
                .setParameter("diffAvailable", diffAvailable)
                .setParameter("diffFreeze", diffFreeze)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public void settleInvest(BigDecimal dueAmount, String investUserId, BigDecimal investAmount, String loanUserId, BigDecimal loanAmount) {
        if (find(investUserId) == null || find(loanUserId) == null) {
            logger.error("invest user {} or loan user {} not exist for settleInvest", investUserId, loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.settleInvest")
                .setParameter("dueAmount", dueAmount)
                .setParameter("investUserId", investUserId)
                .setParameter("investAmount", investAmount)
                .setParameter("loanUserId", loanUserId)
                .setParameter("loanAmount", loanAmount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }
    
    public void settleInvestUmp(BigDecimal dueAmount, String investUserId, BigDecimal investAmount, String loanUserId) {
        if (find(investUserId) == null || find(loanUserId) == null) {
            logger.error("invest user {} or loan user {} not exist for settleInvestUmp", investUserId, loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.settleInvestUmp")
                .setParameter("dueAmount", dueAmount)
                .setParameter("investUserId", investUserId)
                .setParameter("investAmount", investAmount)
                .setParameter("loanUserId", loanUserId)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }
    
    public void settleInvestUmpRefund(String loanUserId, BigDecimal loanAmount) {
        if (find(loanUserId) == null) {
            logger.error("loan user {} not exist for settleInvestUmpRefund", loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.settleInvestUmpRefund")
                .setParameter("loanUserId", loanUserId)
                .setParameter("loanAmount", loanAmount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public void repayInvest(String investUserId,
                            BigDecimal repayAmount,
                            BigDecimal inAmount,
                            String loanUserId,
                            BigDecimal outAmount) {
        if (find(investUserId) == null || find(loanUserId) == null) {
            logger.error("invest user {} or loan user {} not exist for repayInvest", investUserId, loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.repayInvest")
                .setParameter("investUserId", investUserId)
                .setParameter("repayAmount", repayAmount)
                .setParameter("inAmount", inAmount)
                .setParameter("loanUserId", loanUserId)
                .setParameter("outAmount", outAmount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public void repayOnly(String loanUserId,
                          BigDecimal repayAmount) {
        if (find(loanUserId) == null) {
            logger.error("loan user {} not exist for repayOnly", loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.repayOnly")
                .setParameter("repayAmount", repayAmount)
                .setParameter("loanUserId", loanUserId)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public void repayInvestOnly(String investUserId,
                                BigDecimal repayAmount,
                                BigDecimal inAmount,
                                String loanUserId) {
        if (find(investUserId) == null || find(loanUserId) == null) {
            logger.error("invest user {} or loan user {} not exist for repayInvest", investUserId, loanUserId);
            return;
        }
        getEntityManager()
                .createNamedQuery("UserFund.repayInvestOnly")
                .setParameter("investUserId", investUserId)
                .setParameter("repayAmount", repayAmount)
                .setParameter("inAmount", inAmount)
                .setParameter("loanUserId", loanUserId)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public void creditAssign(BigDecimal dueAmount,
                             String inUserId,
                             BigDecimal inAmount,
                             String outUserId,
                             BigDecimal outAmount) {
        if (find(inUserId) == null || find(outUserId) == null) {
            logger.error("in user {} or out user {} not exist for creditAssign", inUserId, outUserId);
            return;
        }

        getEntityManager()
                .createNamedQuery("UserFund.creditAssign")
                .setParameter("dueAmount", dueAmount)
                .setParameter("inUserId", inUserId)
                .setParameter("inAmount", inAmount)
                .setParameter("outUserId", outUserId)
                .setParameter("outAmount", outAmount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    public boolean disburseInvest(String investUserId,
                                  BigDecimal repayAmount,
                                  BigDecimal inAmount) {
        if (find(investUserId) == null) {
            logger.error("user {} not exist for disburse.", investUserId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.disburseInvest")
                .setParameter("investUserId", investUserId)
                .setParameter("repayAmount", repayAmount)
                .setParameter("inAmount", inAmount)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        return result > 0;
    }

    public boolean transfer(String userId, BigDecimal amount, boolean income) {
        if (find(userId) == null) {
            logger.error("user {} not exist for transfer.", userId);
            return false;
        }
        int result = getEntityManager()
                .createNamedQuery("UserFund.transfer")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .setParameter("income", income)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to transfer for user {}", userId);
        }
        return result > 0;
    }

    /**
     * 更新dueInAmount
     *
     * @param userId
     * @param amount
     * @param add
     */
    private boolean dueIn(String userId, BigDecimal amount, boolean add) {
        int result = getEntityManager()
                .createNamedQuery("UserFund.dueIn")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .setParameter("add", add)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        return result > 0;
    }

    /**
     * 更新dueOutAmount
     *
     * @param userId
     * @param amount
     * @param add
     */
    private boolean dueOut(String userId, BigDecimal amount, boolean add) {
        int result = getEntityManager()
                .createNamedQuery("UserFund.dueOut")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .setParameter("add", add)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        return result > 0;
    }

    /**
     * 更新availableAmount
     *
     * @param userId
     * @param amount
     * @param add
     */
    public boolean available(String userId, BigDecimal amount, boolean add) {
        int result = getEntityManager()
                .createNamedQuery("UserFund.available")
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .setParameter("add", add)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        if (result == 0) {
            logger.debug("not enough amount to sub available for user {}", userId);
        }
        return result > 0;
    }

    /**
     * TODO 考虑用户账号是否可用
     *
     * @return
     */
    public BigDecimal sumAvailable() {
        Object result = getEntityManager()
                .createNamedQuery("UserFund.sumAvailable")
                .getSingleResult();
        return result == null ? BigDecimal.ZERO : (BigDecimal) result;
    }

    public PagedResult<UserFund> listUserFunds(PageInfo pageInfo, String... userIds) {
        if (userIds == null || userIds.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<UserFund> result = getEntityManager()
                .createNamedQuery("UserFund.listByUser", UserFund.class)
                .setParameter("userIds", Arrays.asList(userIds))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countUserFunds(userIds));
    }

    public int countUserFunds(String... userIds) {
        if (userIds == null || userIds.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserFund.countByUser", Long.class)
                .setParameter("userIds", Arrays.asList(userIds))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
}
