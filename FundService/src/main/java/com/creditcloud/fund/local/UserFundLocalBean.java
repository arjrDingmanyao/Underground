/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.local;

import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.dao.FundAccountDAO;
import com.creditcloud.fund.entities.dao.FundRecordDAO;
import com.creditcloud.fund.entities.dao.UserAutoBidDAO;
import com.creditcloud.fund.entities.dao.UserFundDAO;
import com.creditcloud.fund.entities.dao.UserFundHistoryDAO;
import com.creditcloud.fund.entities.record.UserFundHistory;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class UserFundLocalBean {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    UserFundDAO userFundDAO;

    @EJB
    FundRecordDAO fundRecordDAO;

    @EJB
    UserAutoBidDAO autoBidDAO;

    @EJB
    UserFundHistoryDAO historyDAO;

    @EJB
    FundAccountDAO fundAccountDAO;

    /**
     * create UserFund as well as UserAutoBid
     *
     * @param userId
     * @return
     */
    public UserFund createUserFund( String userId) {
        UserFund fund = userFundDAO.addNew(userId);
        autoBidDAO.addNew(fund);
        return fund;
    }

    /**
     * 获取用户资金账户信息，没有则创建
     *
     * @param userId
     * @return
     */
    public UserFund getUserFund(String userId) {
        UserFund result = userFundDAO.find(userId);
        if (result == null) {
            result = createUserFund(userId);
        }
        return result;
    }

    //@Schedule(persistent = false, second = "0", minute = "0", hour = "0")
    //现在直接从FundRecord计算出资金曲线,UserFundHistory不再需要
    private void dailyUpdate() {
        long start = System.currentTimeMillis();
        logger.debug("start dailyUpdate FundHistory by scheduler. [startTime={}]", new Date(start));
        long todayTime = DateUtils.MILLIS_PER_DAY * (System.currentTimeMillis() / DateUtils.MILLIS_PER_DAY);
        Date today = new Date(todayTime);

        for (UserFund fund : userFundDAO.findAll()) {
            UserFundHistory history = new UserFundHistory(fund,
                                                          today,
                                                          fund.getAvailableAmount(),
                                                          fund.getFrozenAmount(),
                                                          fund.getDueInAmount(),
                                                          fund.getDueOutAmount(),
                                                          fund.getDepositAmount(),
                                                          fund.getWithdrawAmount(),
                                                          fund.getTransferAmount());
            historyDAO.edit(history);
        }
        logger.debug("finish dailyUpdate FundHistory by scheduler.[time={}ms]", System.currentTimeMillis() - start);
    }
}
