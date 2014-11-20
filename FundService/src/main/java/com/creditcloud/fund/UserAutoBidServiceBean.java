/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.fund.api.UserAutoBidService;
import com.creditcloud.fund.entities.UserAutoBid;
import com.creditcloud.fund.entities.dao.UserAutoBidDAO;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.AutoBidRange;
import com.creditcloud.fund.model.AutoBidRank;
import com.creditcloud.fund.model.AutoBidStatistics;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.loan.RepaymentMethod;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.model.loan.InvestRule;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class UserAutoBidServiceBean implements UserAutoBidService {

    @EJB
    ApplicationBean appBean;

    @EJB
    UserAutoBidDAO autoBidDAO;

    @Inject
    Logger logger;

    @Override
    public AutoBidStatistics getStatistics(String clientCode) {
        appBean.checkClientCode(clientCode);
        int active = autoBidDAO.countByActive();
        long activeAmount = autoBidDAO.sumByActive();
        int valid = autoBidDAO.countByValidActive();
        long validAmount = autoBidDAO.sumByValidActive();
        AutoBidStatistics result = new AutoBidStatistics(active,
                                                         activeAmount,
                                                         valid,
                                                         validAmount);
        return result;
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.UserAutoBid> listByStrategy(String clientCode,
                                                                              AutoBidRange range,
                                                                              boolean mortgaged,
                                                                              PageInfo info,
                                                                              RepaymentMethod... methods) {
        appBean.checkClientCode(clientCode);
        if (methods == null || methods.length == 0) {
            logger.error("listByStrategy at least one method should be selected.");
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        PagedResult<UserAutoBid> bids = autoBidDAO.listByStrategy(range, mortgaged, info, methods);
        List<com.creditcloud.fund.model.UserAutoBid> result = new ArrayList<>(bids.getResults().size());
        for (UserAutoBid bid : bids.getResults()) {
            result.add(DTOUtils.getUserAutoBid(bid));
        }
        return new PagedResult<>(result, bids.getTotalSize());
    }

    @Override
    public int countByStrategy(String clientCode,
                               AutoBidRange range,
                               boolean mortgaged,
                               RepaymentMethod... methods) {
        appBean.checkClientCode(clientCode);
        if (methods == null || methods.length == 0) {
            logger.error("countByStrategy at least one method should be selected.");
            return 0;
        }
        return autoBidDAO.countByStrategy(range, mortgaged, methods);
    }

    @Override
    public long sumByStrategy(String clientCode,
                              AutoBidRange range,
                              boolean mortgaged,
                              RepaymentMethod... methods) {
        appBean.checkClientCode(clientCode);
        if (methods == null || methods.length == 0) {
            logger.error("sumByStrategy at least one method should be selected.");
            return 0;
        }
        return autoBidDAO.sumByStrategy(range, mortgaged, methods);
    }

    @Override
    public AutoBidRank getAutoBidRank(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        AutoBidRank rank = autoBidDAO.getAutoBidRank(userId);
        return rank;
    }

    @Override
    public com.creditcloud.fund.model.UserAutoBid getByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getUserAutoBid(autoBidDAO.find(userId));
    }

    @Override
    public void saveConfig(String clientCode, com.creditcloud.fund.model.UserAutoBid autoBid) {
        appBean.checkClientCode(clientCode);
        logger.debug("save auto bid config for user.[autoBid={}]", autoBid);
        autoBidDAO.saveConfig(autoBid);
    }

    @Override
    public boolean activate(String clientCode, String userId, boolean activate) {
        appBean.checkClientCode(clientCode);
        logger.debug("activate auto bid for user {}, activate {}.", userId, activate);
        if (activate) {
            return autoBidDAO.activate(userId);
        } else {
            return autoBidDAO.deactivate(userId);
        }
    }

    @Override
    public boolean updateLastBidTime(String clientCode, String userId, Date bidTime) {
        appBean.checkClientCode(clientCode);
        logger.debug("update user {} last bid time {}.", userId, bidTime);
        autoBidDAO.updateLastBidTime(userId, bidTime);
        return true;
    }

    @Override
    public List<com.creditcloud.fund.model.UserAutoBid> listByLoanAndActivedTime(String clientCode, Loan loan, CreditRank creditRank) {
        appBean.checkClientCode(clientCode);
        List<UserAutoBid> result = autoBidDAO.listByLoanAndActivatedTime(loan.getRate(),
                                                                         loan.getAmount(),
                                                                         loan.getDuration(),
                                                                         creditRank,
                                                                         loan.getMethod(),
                                                                         loan.isMortgaged(),
                                                                         loan.getLoanRequest().getInvestRule());
        List<com.creditcloud.fund.model.UserAutoBid> autoBids = new ArrayList<>(result.size());
        InvestRule investRule = loan.getLoanRequest().getInvestRule();
        for (UserAutoBid autoBid : result) {
            if (investRule != null) {
                int amount = InvestRule.normalize(investRule, autoBid.getSingleAmount());
                if (amount > 0) {
                    com.creditcloud.fund.model.UserAutoBid actual = DTOUtils.getUserAutoBid(autoBid);
                    actual.setSingleAmount(amount);
                    autoBids.add(actual);
                }
            } else {
                autoBids.add(DTOUtils.getUserAutoBid(autoBid));
            }
        }
        return autoBids;
    }

    @Override
    public List<com.creditcloud.fund.model.UserAutoBid> listByLoanAndLastBidTime(String clientCode, Loan loan, CreditRank creditRank) {
        appBean.checkClientCode(clientCode);
        List<UserAutoBid> result = autoBidDAO.listByLoanAndLastBidTime(loan.getRate(),
                                                                       loan.getAmount(),
                                                                       loan.getDuration(),
                                                                       creditRank,
                                                                       loan.getMethod(),
                                                                       loan.isMortgaged(),
                                                                       loan.getLoanRequest().getInvestRule());
        List<com.creditcloud.fund.model.UserAutoBid> autoBids = new ArrayList<>(result.size());
        InvestRule investRule = loan.getLoanRequest().getInvestRule();
        for (UserAutoBid autoBid : result) {
            if (investRule != null) {
                int amount = InvestRule.normalize(investRule, autoBid.getSingleAmount());
                if (amount > 0) {
                    com.creditcloud.fund.model.UserAutoBid actual = DTOUtils.getUserAutoBid(autoBid);
                    actual.setSingleAmount(amount);
                    autoBids.add(actual);
                }
            } else {
                autoBids.add(DTOUtils.getUserAutoBid(autoBid));
            }
        }
        return autoBids;
    }

    @Override
    public void enableAutoBid(String clientCode, String userId, boolean enable) {
        appBean.checkClientCode(clientCode);
        UserAutoBid autoBid = autoBidDAO.find(userId);
        if (autoBid != null) {
            autoBid.setEnable(enable);
            autoBidDAO.edit(autoBid);
            logger.debug("set auto bid for user {} to {}", userId, enable);
        }
    }
}
