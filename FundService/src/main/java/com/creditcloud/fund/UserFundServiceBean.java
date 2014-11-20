/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.fund.api.UserFundService;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.dao.FundRecordDAO;
import com.creditcloud.fund.entities.dao.UserFundDAO;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.local.UserFundLocalBean;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class UserFundServiceBean implements UserFundService {

    @EJB
    ApplicationBean appBean;

    @EJB
    UserFundDAO fundDAO;

    @EJB
    UserFundLocalBean fundLocalBean;

    @EJB
    FundRecordDAO recordDAO;

    @Inject
    Logger logger;

    @Override
    public com.creditcloud.fund.model.UserFund getByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        UserFund fund = fundLocalBean.getUserFund(userId);
        return DTOUtils.getUserFund(fund);
    }

    @Override
    public void create(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        logger.debug("create user fund for user {}", userId);
        fundLocalBean.createUserFund(userId);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public boolean freeze(String clientCode, String userId, BigDecimal amount) {
        appBean.checkClientCode(clientCode);
        logger.debug("freeze fund.[userId={}][amount={}]", userId, amount);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for freeze can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.freeze(userId, amount);
                
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean release(String clientCode, String userId, BigDecimal amount) {
        appBean.checkClientCode(clientCode);
        logger.debug("release fund.[userId={}][amount={}]", userId, amount);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for release can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.release(userId, amount);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean deposit(String clientCode, String userId, BigDecimal amount) {
        appBean.checkClientCode(clientCode);
        logger.debug("deposit fund.[userId={}][amount={}]", userId, amount);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for deposit can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.deposit(userId, amount);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean withdraw(String clientCode, String userId, BigDecimal outAmount, BigDecimal withdrawAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("withdraw.[userId={}][outAmount={}][withdrawAmount={}]", userId, outAmount, withdrawAmount);
        if (outAmount.signum() == -1) {
            logger.debug("outAmount for withdraw can not be negative!");
            return false;
        }
        if (withdrawAmount.signum() == -1) {
            logger.debug("withdrawAmout for withdraw can not be negative!");
            return false;
        }
        //all zero just return true
        if (outAmount.signum() == 0 && withdrawAmount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.withdraw(userId, outAmount, withdrawAmount);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean calibrate(String clientCode, String userId, BigDecimal diffAvailable, BigDecimal diffFreeze) {
        appBean.checkClientCode(clientCode);
        logger.debug("calibrate.[userId={}][diffAvailable={}][diffFreeze={}]", userId, diffAvailable, diffFreeze);
        fundDAO.calibrate(userId, diffAvailable, diffFreeze);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }

    @Override
    public boolean settleInvest(String clientCode, BigDecimal repayAmount, String investUserId, BigDecimal investAmount, String loanUserId, BigDecimal loanAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("settle invest.[repayAmount={}][investUserId={}][investAmount={}][loanUserId={}][loanAmount={}]",
                     repayAmount, investUserId, investAmount, loanUserId, loanAmount);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for settleInvest can not be negative!");
            return false;
        }
        if (investAmount.signum() == -1) {
            logger.debug("investAmount for settleInvest can not be negative!");
            return false;
        }
        if (loanAmount.signum() == -1) {
            logger.debug("loanAmount for settleInvest can not be negative!");
            return false;
        }
        //all zero just return true
        if (repayAmount.signum() == 0 && loanAmount.signum() == 0 && investAmount.signum() == 0) {
            return true;
        }
        fundDAO.settleInvest(repayAmount, investUserId, investAmount, loanUserId, loanAmount);
        
        // delete cache
        appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }
    
    @Override
    public boolean settleInvestUmp(String clientCode, BigDecimal repayAmount, String investUserId, BigDecimal investAmount, String loanUserId) {
        appBean.checkClientCode(clientCode);
        logger.debug("settle invest ump.[repayAmount={}][investUserId={}][investAmount={}][loanUserId={}]",
                     repayAmount, investUserId, investAmount, loanUserId);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for settleInvestUmp can not be negative!");
            return false;
        }
        if (investAmount.signum() == -1) {
            logger.debug("investAmount for settleInvestUmp can not be negative!");
            return false;
        }
        //all zero just return true
        if (repayAmount.signum() == 0 && investAmount.signum() == 0) {
            return true;
        }
        fundDAO.settleInvestUmp(repayAmount, investUserId, investAmount, loanUserId);
        
        // delete cache
        appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }

    @Override
    public boolean settleInvestUmpRefund(String clientCode, String loanUserId, BigDecimal loanAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("settle invest ump refund.[loanUserId={}][loanAmount={}]",
                     loanUserId, loanAmount);
        //negative return false
        if (loanAmount.signum() == -1) {
            logger.debug("loanAmount for settleInvestUmpRefund can not be negative!");
            return false;
        }
        //all zero just return true
        if (loanAmount.signum() == 0) {
            return true;
        }
        fundDAO.settleInvestUmpRefund(loanUserId, loanAmount);
        
        // delete cache
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }
    
    @Override
    public boolean repayInvest(String clientCode, String investUserId, BigDecimal repayAmount,
                               BigDecimal inAmount, String loanUserId, BigDecimal outAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("repay invest.[investUserId={}][repayAmount={}][inAmount={}][loanUserId={}][outAmount={}]",
                     investUserId, repayAmount, inAmount, loanUserId, outAmount);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for repayInvest can not be negative!");
            return false;
        }
        if (inAmount.signum() == -1) {
            logger.debug("inAmount for repayInvest can not be negative!");
            return false;
        }
        if (outAmount.signum() == -1) {
            logger.debug("outAmount for repayInvest can not be negative!");
            return false;
        }
        //all zero just return true
        if (repayAmount.signum() == 0 && inAmount.signum() == 0 && outAmount.signum() == 0) {
            return true;
        }
        fundDAO.repayInvest(investUserId, repayAmount, inAmount, loanUserId, outAmount);
        
        // delete cache
        appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }

    @Override
    public boolean repayOnly(String clientCode, String loanUserId, BigDecimal repayAmount) {
            appBean.checkClientCode(clientCode);
        logger.debug("repay only.[loanUserId={}][repayAmount={}]",
                     loanUserId, repayAmount);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for repayInvest can not be negative!");
            return false;
        }
        //all zero just return true
        if (repayAmount.signum() == 0) {
            return true;
        }
        fundDAO.repayOnly(loanUserId, repayAmount);
        
        // delete cache
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        return true;
    }
    
    @Override
    public boolean repayInvestOnly(String clientCode, String investUserId, BigDecimal repayAmount,
                               BigDecimal inAmount, String loanUserId) {
        appBean.checkClientCode(clientCode);
        logger.debug("repay invest only.[investUserId={}][repayAmount={}][inAmount={}][loanUserId={}]",
                     investUserId, repayAmount, inAmount, loanUserId);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for repayInvest can not be negative!");
            return false;
        }
        if (inAmount.signum() == -1) {
            logger.debug("inAmount for repayInvest can not be negative!");
            return false;
        }
        //all zero just return true
        if (repayAmount.signum() == 0 && inAmount.signum() == 0) {
            return true;
        }
        fundDAO.repayInvestOnly(investUserId, repayAmount, inAmount, loanUserId);
        
        // delete cache
        appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }
    
    @Override
    public boolean creditAssign(String clientCode, BigDecimal dueAmount, String inUserId, BigDecimal inAmount, String outUserId, BigDecimal outAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("credit assign.[dueAmount={}][inUserId={}][inAmount={}][outUserId={}][outAmount={}]",
                     dueAmount, inUserId, inAmount, outUserId, outAmount);
        //negative return false
        if (dueAmount.signum() == -1) {
            logger.debug("dueAmount for creditAssign can not be negative!");
            return false;
        }
        if (inAmount.signum() == -1) {
            logger.debug("inAmount for creditAssign can not be negative!");
            return false;
        }
        if (outAmount.signum() == -1) {
            logger.debug("outAmount for creditAssign can not be negative!");
            return false;
        }
        //both zero just return true
        if (inAmount.signum() == 0 && outAmount.signum() == 0) {
            return true;
        }
        fundDAO.creditAssign(dueAmount, inUserId, inAmount, outUserId, outAmount);
        
        // delete cache
        appBean.deleteCache(inUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        appBean.deleteCache(outUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }

    @Override
    public boolean disburseInvest(String clientCode, String investUserId,
                                  BigDecimal repayAmount, BigDecimal inAmount) {
        appBean.checkClientCode(clientCode);
        logger.debug("disburse.[investUserId={}][repayAmount={}][inAmount={}]", investUserId, repayAmount, inAmount);
        //negative return false
        if (repayAmount.signum() == -1) {
            logger.debug("repayAmount for disburseInvest can not be negative!");
            return false;
        }
        if (inAmount.signum() == -1) {
            logger.debug("inAmount for disburseInvest can not be negative!");
            return false;
        }
        //both zero just return true
        if (repayAmount.signum() == 0 && inAmount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.disburseInvest(investUserId, repayAmount, inAmount);
        
        // delete cache
        appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean transfer(String clientCode, String userId, BigDecimal amount, boolean income) {
        appBean.checkClientCode(clientCode);
        logger.debug("transfer.[userId={}][amount={}][income={}]", userId, amount, income);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for transfer can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.transfer(userId, amount, income);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public boolean charge(String clientCode, String userId, BigDecimal amount) {
        appBean.checkClientCode(clientCode);
        logger.debug("charge.[userId={}][amount={}]", userId, amount);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for charge can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.available(userId, amount, false);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }
    
    @Override
    public boolean incomeFee(String clientCode, String userId, BigDecimal amount) {
        appBean.checkClientCode(clientCode);
        logger.debug("incomeFee.[userId={}][amount={}]", userId, amount);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for incomeFee can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }
        
        boolean operation = fundDAO.available(userId, amount, true);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }
    
    @Override
    public BigDecimal getTotalAvailable(String clientCode) {
        appBean.checkClientCode(clientCode);
        return fundDAO.sumAvailable();
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.UserFund> listUserFunds(String clientCode, CriteriaInfo criteriaInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<UserFund> ufs = fundDAO.list(criteriaInfo);
        List<com.creditcloud.fund.model.UserFund> result = new ArrayList<>(ufs.getResults().size());
        for (UserFund uf : ufs.getResults()) {
            result.add(DTOUtils.getUserFund(uf));
        }
        return new PagedResult<>(result, ufs.getTotalSize());
    }

    @Override
    public boolean fssTransfer(String clientCode, String userId, BigDecimal amount, boolean in) {
        appBean.checkClientCode(clientCode);
        logger.debug("fssTransfer.[userId={}][amount={}][in={}]", userId, amount, in);
        //negative return false
        if (amount.signum() == -1) {
            logger.debug("amount for fssTransfer can not be negative!");
            return false;
        }
        //zero just return true
        if (amount.signum() == 0) {
            return true;
        }

        boolean operation = fundDAO.available(userId, amount, in);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return operation;
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.UserFund> listUserFunds(String clientCode, PageInfo pageInfo, String... userIds) {
        appBean.checkClientCode(clientCode);
        PagedResult<UserFund> funds = fundDAO.listUserFunds(pageInfo, userIds);
        List<com.creditcloud.fund.model.UserFund> result = new ArrayList<>(funds.getResults().size());
        for (UserFund fund : funds.getResults()) {
            result.add(DTOUtils.getUserFund(fund));
        }
        return new PagedResult<>(result, funds.getTotalSize());
    }
}
