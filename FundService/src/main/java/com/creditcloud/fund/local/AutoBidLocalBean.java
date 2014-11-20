/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.local;

import com.creditcloud.fund.entities.UserAutoBid;
import com.creditcloud.fund.entities.dao.UserAutoBidDAO;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.user.api.UserCreditService;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class AutoBidLocalBean {

    @Inject
    Logger logger;

    @EJB
    UserAutoBidDAO userAutoBidDAO;

    @EJB
    MessageBridge messageBridge;

    @EJB
    ApplicationBean appBean;

    @EJB
    UserCreditService creditService;

    /**
     * list all qualified auto bid for a loan
     *
     * @param loan
     * @return
     */
    public List<UserAutoBid> listByLoanAndLastBidTime(Loan loan) {
        return userAutoBidDAO.listByLoanAndActivatedTime(loan.getRate(),
                                                         loan.getAmount(),
                                                         loan.getDuration(),
                                                         creditService.getUserCreditByUserId(appBean.getClientCode(),
                                                                                             loan.getLoanRequest().getUser().getId()).getCreditRank(),
                                                         loan.getMethod(),
                                                         loan.isMortgaged(),
                                                         loan.getLoanRequest().getInvestRule());
    }

    /**
     * list all qualified auto bid for a loan
     *
     * @param loan
     * @return
     */
    public List<UserAutoBid> listByLoanAndActivedTime(Loan loan) {
        return userAutoBidDAO.listByLoanAndActivatedTime(loan.getRate(),
                                                         loan.getAmount(),
                                                         loan.getDuration(),
                                                         creditService.getUserCreditByUserId(appBean.getClientCode(),
                                                                                             loan.getLoanRequest().getUser().getId()).getCreditRank(),
                                                         loan.getMethod(),
                                                         loan.isMortgaged(),
                                                         loan.getLoanRequest().getInvestRule());
    }

    /**
     * 对于账户余额不足的用户，系统自动关闭自动投标并且发送通知
     */
    //TODO 绝大部分客户不开启自动投标 暂时关闭
    //@Schedule(persistent = false, second = "0", minute = "0/10", hour = "*")
    private void updateActive() {
        long start = System.currentTimeMillis();
        logger.debug("start updating active user auto bid by scheduler. [startTime={}]", new Date(start));
        Set<String> receivers = new HashSet<>();
        for (UserAutoBid bid : userAutoBidDAO.listByActiveAndInsufficientAmount()) {
            bid.setActive(false);
            userAutoBidDAO.edit(bid);
            receivers.add(bid.getUserId());
        }
        if (receivers.size() > 0) {
            messageBridge.notify("账户余额不足,自动投标已关闭,请充值",
                                 "账户余额不足,自动投标已关闭,请充值",
                                 receivers.toArray(new String[receivers.size()]));
        }
        logger.debug("finish updating active user auto bid by scheduler.[time={}ms]", System.currentTimeMillis() - start);
    }
}
