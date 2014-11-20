/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.fund.entities.UserAutoBid;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.embedded.AutoBidRange;
import com.creditcloud.fund.model.AutoBidRank;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.loan.RepaymentMethod;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.model.loan.Duration;
import com.creditcloud.model.loan.InvestRule;
import com.creditcloud.model.misc.PagedResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class UserAutoBidDAO extends AbstractDAO<UserAutoBid> {

    private static final AutoBidRange RANGE = new AutoBidRange(LoanConstant.MIN_LOAN_RATE,
                                                               LoanConstant.MAX_LOAN_RATE,
                                                               LoanConstant.MIN_LOAN_DURATION,
                                                               LoanConstant.MAX_LOAN_DURATION,
                                                               CreditRank.lowest(),
                                                               CreditRank.highest());

    private static final List<RepaymentMethod> METHOD_LIST = new ArrayList<>(Arrays.asList(RepaymentMethod.values()));

    //1970/01/01 08:30:00
    private static final Date DEFAULT_LAST_BID_TIME = new Date(0);

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public UserAutoBidDAO() {
        super(UserAutoBid.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void addNew(UserFund fund) {
        UserAutoBid bid = new UserAutoBid(fund,
                                          false,
                                          LoanConstant.MIN_INVEST_AMOUNT,
                                          0,
                                          METHOD_LIST,
                                          RANGE,
                                          false,
                                          (Date) null,
                                          DEFAULT_LAST_BID_TIME,
                                          true);

        try {
            getValidatorWrapper().tryValidate(bid);
            create(bid);
        } catch (InvalidException ex) {
            logger.warn("user auto bid {} is not valid!", bid.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new user auto bid failed!!!\n{}", bid.toString(), ex);
        }
    }

    /**
     * a successful auto bid ,add record and move auto bid to the last of queue
     *
     * @param autoBid
     */
    public void updateLastBidTime(String userId, Date bidTime) {
        UserAutoBid result = find(userId);
        if (result != null) {
            result.setLastBidTime(bidTime);
            edit(result);
        }
    }

    /**
     * save user configuration of auto bid
     *
     * @param bid
     */
    public void saveConfig(com.creditcloud.fund.model.UserAutoBid bid) {
        UserAutoBid result = find(bid.getUserId());
        if (result != null) {
            result.setActive(bid.isActive());
            result.setActivedTime(bid.getActivedTime());
            result.setLastBidTime(bid.getLastBidTime());
            result.setMortgaged(bid.isMortgaged());
            result.setRange(DTOUtils.convertAutoBidRange(bid.getRange()));
            result.setRepayMethod(bid.getRepayMethod());
            result.setReservedAmount(bid.getReservedAmount());
            result.setSingleAmount(bid.getSingleAmount());
            //用户是没有权限更新enable的
            //result.setEnable(bid.isEnable());
            edit(result);
        }
    }

    /**
     * activate auto bid for user
     *
     * @param userId
     */
    public boolean activate(String userId) {
        UserAutoBid bid = find(userId);
        bid.setActive(true);
        bid.setActivedTime(new Date());
        edit(bid);
        return true;
    }

    /**
     * deactivate auto bid for user
     *
     * @param userId
     * @return
     */
    public boolean deactivate(String userId) {
        UserAutoBid bid = find(userId);
        bid.setActive(false);
        edit(bid);
        return true;
    }

    /**
     * get AutoBidRank for an userId
     * 如果没有activate,那么返回所有自动投标排名情况，否则返回有交集的自动投标排名情况
     *
     * @param userId
     * @return
     */
    public AutoBidRank getAutoBidRank(String userId) {
        UserAutoBid autoBid = find(userId);
        boolean activated = autoBid.isActive();
        int total = activated ? countByAutoBid(userId) : countByActive();
        long totalAmount = activated ? sumByAutoBid(userId) : sumByActive();
        int rank = activated ? countRankOfAutoBid(userId) : 0;
        long rankAmount = activated ? sumRankOfAutoBid(userId) : 0;

        AutoBidRank info = new AutoBidRank(total, totalAmount, rank, rankAmount);
        return info;
    }

    /**
     * list all active user auto bid
     *
     * @return
     */
    public List<UserAutoBid> listByActive() {
        return getEntityManager()
                .createNamedQuery("UserAutoBid.listByActive", UserAutoBid.class)
                .getResultList();
    }

    /**
     * list all qualified auto bid for a loan, order by lastBidTime of auto bid
     * in DESC
     *
     * @param rate
     * @param loanAmount
     * @param duration
     * @param creditRank
     * @param method
     * @param mortgaged
     * @param investRule
     * @return
     */
    public List<UserAutoBid> listByLoanAndLastBidTime(int rate,
                                                      int loanAmount,
                                                      Duration duration,
                                                      CreditRank creditRank,
                                                      RepaymentMethod method,
                                                      boolean mortgaged,
                                                      InvestRule investRule) {
        int minAmount = LoanConstant.MIN_INVEST_AMOUNT;
        int maxAmount = loanAmount;
        if (investRule != null) {
            minAmount = investRule.getMinAmount();
            maxAmount = Math.min(loanAmount, investRule.getMaxAmount());
        }

        Query query = getEntityManager()
                .createNamedQuery("UserAutoBid.listByLoanAndLastBidTime", UserAutoBid.class)
                .setParameter("rate", rate)
                .setParameter("duration", duration.getTotalMonths())
                .setParameter("creditRank", creditRank)
                .setParameter("repayMethod", method)
                .setParameter("mortgaged", mortgaged)
                .setParameter("minAmount", minAmount)
                .setParameter("maxAmount", maxAmount);
        return query.getResultList();
    }

    /**
     * list all qualified auto bid for a loan, order by activated time in ASC
     *
     * @param rate
     * @param loanAmount
     * @param duration
     * @param creditRank
     * @param method
     * @param mortgaged
     * @param investRule
     * @return
     */
    public List<UserAutoBid> listByLoanAndActivatedTime(int rate,
                                                        int loanAmount,
                                                        Duration duration,
                                                        CreditRank creditRank,
                                                        RepaymentMethod method,
                                                        boolean mortgaged,
                                                        InvestRule investRule) {
        int minAmount = LoanConstant.MIN_INVEST_AMOUNT;
        int maxAmount = loanAmount;
        if (investRule != null) {
            minAmount = investRule.getMinAmount();
            maxAmount = Math.min(loanAmount, investRule.getMaxAmount());
        }

        Query query = getEntityManager()
                .createNamedQuery("UserAutoBid.listByLoanAndActivedTime", UserAutoBid.class)
                .setParameter("rate", rate)
                .setParameter("duration", duration.getTotalMonths())
                .setParameter("creditRank", creditRank)
                .setParameter("repayMethod", method)
                .setParameter("mortgaged", mortgaged)
                .setParameter("minAmount", minAmount)
                .setParameter("maxAmount", maxAmount);
        return query.getResultList();
    }

    /**
     * list all active auto bid, but has not sufficient amount in fund
     *
     * @return
     */
    public List<UserAutoBid> listByActiveAndInsufficientAmount() {
        return getEntityManager()
                .createNamedQuery("UserAutoBid.listByInvalidActive", UserAutoBid.class)
                .getResultList();
    }

    /**
     * 统计所有有交集的自动投标总数
     *
     * @param userId
     * @return
     */
    public int countByAutoBid(String userId) {
        UserAutoBid autoBid = find(userId);
        if (autoBid == null) {
            //沒有设置
            return -1;
        }
        if (!autoBid.isActive()) {
            //没有开启投标
            return -1;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.countByAutoBid", Long.class)
                .setParameter("minRate", autoBid.getRange().getMinRate())
                .setParameter("maxRate", autoBid.getRange().getMaxRate())
                .setParameter("minDuration", autoBid.getRange().getMinDuration())
                .setParameter("maxDuration", autoBid.getRange().getMaxDuration())
                .setParameter("minCredit", autoBid.getRange().getMinCredit())
                .setParameter("maxCredit", autoBid.getRange().getMaxCredit())
                .setParameter("repayMethod", autoBid.getRepayMethod())
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 统计所有有交集的自动投标的总额
     *
     * @param userId
     * @return
     */
    public long sumByAutoBid(String userId) {
        UserAutoBid autoBid = find(userId);
        if (autoBid == null) {
            //沒有设置
            return -1;
        }
        if (!autoBid.isActive()) {
            //没有开启投标
            return -1;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.sumByAutoBid", Long.class)
                .setParameter("minRate", autoBid.getRange().getMinRate())
                .setParameter("maxRate", autoBid.getRange().getMaxRate())
                .setParameter("minDuration", autoBid.getRange().getMinDuration())
                .setParameter("maxDuration", autoBid.getRange().getMaxDuration())
                .setParameter("minCredit", autoBid.getRange().getMinCredit())
                .setParameter("maxCredit", autoBid.getRange().getMaxCredit())
                .setParameter("repayMethod", autoBid.getRepayMethod())
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }

    /**
     * 统计自动投标排名，涉及所有有交集的自动投标
     *
     * @param userId
     * @return
     */
    public int countRankOfAutoBid(String userId) {
        UserAutoBid autoBid = find(userId);
        if (autoBid == null) {
            //沒有设置
            logger.warn("auto bid not found for user {}", userId);
            return -1;
        }
        if (!autoBid.isActive()) {
            //没有开启投标
            logger.warn("auto bid not actived for user{}", userId);
            return -1;
        }
        if (BigDecimal.valueOf(autoBid.getSingleAmount() + autoBid.getReservedAmount())
                .compareTo(autoBid.getFund().getAvailableAmount()) > 0) {
            //可投余额不足 
            logger.warn("auto bid insufficient amount for user{}", userId);
            return -1;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.countRankOfAutoBid", Long.class)
                .setParameter("userId", userId)
                .setParameter("activatedTime", autoBid.getActivedTime())
                .setParameter("minRate", autoBid.getRange().getMinRate())
                .setParameter("maxRate", autoBid.getRange().getMaxRate())
                .setParameter("minDuration", autoBid.getRange().getMinDuration())
                .setParameter("maxDuration", autoBid.getRange().getMaxDuration())
                .setParameter("minCredit", autoBid.getRange().getMinCredit())
                .setParameter("maxCredit", autoBid.getRange().getMaxCredit())
                .setParameter("repayMethod", autoBid.getRepayMethod())
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 统计自动投标排名在前的自动投标总额，涉及所有有交集的自动投标
     *
     * @param userId
     * @return
     */
    public long sumRankOfAutoBid(String userId) {
        UserAutoBid autoBid = find(userId);
        if (autoBid == null) {
            //沒有设置
            logger.warn("auto bid not found for user {}", userId);
            return -1;
        }
        if (!autoBid.isActive()) {
            //没有开启投标
            logger.warn("auto bid not actived for user{}", userId);
            return -1;
        }
        if (BigDecimal.valueOf(autoBid.getSingleAmount() + autoBid.getReservedAmount())
                .compareTo(autoBid.getFund().getAvailableAmount()) > 0) {
            //可投余额不足 
            logger.warn("auto bid insufficient amount for user{}", userId);
            return -1;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.sumRankOfAutoBid", Long.class)
                .setParameter("userId", userId)
                .setParameter("activatedTime", autoBid.getActivedTime())
                .setParameter("minRate", autoBid.getRange().getMinRate())
                .setParameter("maxRate", autoBid.getRange().getMaxRate())
                .setParameter("minDuration", autoBid.getRange().getMinDuration())
                .setParameter("maxDuration", autoBid.getRange().getMaxDuration())
                .setParameter("minCredit", autoBid.getRange().getMinCredit())
                .setParameter("maxCredit", autoBid.getRange().getMaxCredit())
                .setParameter("repayMethod", autoBid.getRepayMethod())
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }

    /**
     * 统计所有开启自动投标数
     *
     * @return
     */
    public int countByActive() {
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.countByActive", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 统计所有开启自动投标数,且其账户必须有足够余额
     *
     * @return
     */
    public int countByValidActive() {
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.countByValidActive", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 统计所有开启自动投标总额
     *
     * @return
     */
    public long sumByActive() {
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.sumByActive", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }

    /**
     * 统计所有开启自动投标总额,且其账户必须有足够余额
     *
     * @return
     */
    public long sumByValidActive() {
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.sumByValidActive", Long.class)
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }

    /**
     * 按投标策略来统计自动投标总数
     *
     * @param range
     * @param mortgaged
     * @param methods
     * @return
     */
    public int countByStrategy(com.creditcloud.fund.model.AutoBidRange range,
                               boolean mortgaged,
                               RepaymentMethod... methods) {
        if (methods == null || methods.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.countByStrategy", Long.class)
                .setParameter("minRate", range.getMinRate())
                .setParameter("maxRate", range.getMaxRate())
                .setParameter("minDuration", range.getMinDuration())
                .setParameter("maxDuration", range.getMaxDuration())
                .setParameter("minCredit", range.getMinCredit())
                .setParameter("maxCredit", range.getMaxCredit())
                .setParameter("repayMethod", Arrays.asList(methods))
                .setParameter("mortgaged", mortgaged)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 按投标策略列出所有满足条件自动投标
     *
     * @param range
     * @param mortgaged
     * @param info
     * @param methods
     * @return
     */
    public PagedResult<UserAutoBid> listByStrategy(com.creditcloud.fund.model.AutoBidRange range,
                                                   boolean mortgaged,
                                                   PageInfo info,
                                                   RepaymentMethod... methods) {
        if (methods == null || methods.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("UserAutoBid.listByStrategy", UserAutoBid.class)
                .setParameter("minRate", range.getMinRate())
                .setParameter("maxRate", range.getMaxRate())
                .setParameter("minDuration", range.getMinDuration())
                .setParameter("maxDuration", range.getMaxDuration())
                .setParameter("minCredit", range.getMinCredit())
                .setParameter("maxCredit", range.getMaxCredit())
                .setParameter("repayMethod", Arrays.asList(methods))
                .setParameter("mortgaged", mortgaged);
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());

        int totalSize = countByStrategy(range, mortgaged, methods);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    /**
     * 按投标策略来统计自动投标总金额
     *
     * @param range
     * @param mortgaged
     * @param methods
     * @return
     */
    public long sumByStrategy(com.creditcloud.fund.model.AutoBidRange range,
                              boolean mortgaged,
                              RepaymentMethod... methods) {
        if (methods == null || methods.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("UserAutoBid.sumByStrategy", Long.class)
                .setParameter("minRate", range.getMinRate())
                .setParameter("maxRate", range.getMaxRate())
                .setParameter("minDuration", range.getMinDuration())
                .setParameter("maxDuration", range.getMaxDuration())
                .setParameter("minCredit", range.getMinCredit())
                .setParameter("maxCredit", range.getMaxCredit())
                .setParameter("repayMethod", Arrays.asList(methods))
                .setParameter("mortgaged", mortgaged)
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }
}
