/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.common.entities.embedded.BankAccount;
import com.creditcloud.fund.api.FundAccountService;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.dao.FundAccountDAO;
import com.creditcloud.fund.entities.dao.UserFundDAO;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.enums.misc.Bank;
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
public class FundAccountServiceBean implements FundAccountService {
    
    @Inject
    Logger logger;
    
    @EJB
    ApplicationBean appBean;
    
    @EJB
    FundAccountDAO accountDAO;
    
    @EJB
    UserFundDAO fundDAO;
    
    @Override
    public boolean setDefaultAccountByUser(String clientCode, String userId, String account) {
        appBean.checkClientCode(clientCode);
        accountDAO.setDefaultAccountByUser(userId, account);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }
    
    @Override
    public boolean deleteByUserAndAccount(String clientCode, String userId, String account) {
        appBean.checkClientCode(clientCode);
        accountDAO.deleteByUserAndAccount(userId, account);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return true;
    }
    
    @Override
    public List<com.creditcloud.fund.model.FundAccount> listAccountByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        List<FundAccount> accounts = accountDAO.listAccountsByUser(userId);
        List<com.creditcloud.fund.model.FundAccount> result = new ArrayList<>(accounts.size());
        for (FundAccount account : accounts) {
            result.add(DTOUtils.getFundAccount(account));
        }
        return result;
    }
    
    @Override
    public boolean addBankCard(String clientCode, String userId, String userName, Bank bank, String account, boolean valid, boolean isDefault) {
        appBean.checkClientCode(clientCode);
        boolean result = false;
        if (accountDAO.checkExistByUserAndAccount(userId, account)) {
            FundAccount fa = accountDAO.getByUserAndAccount(userId, account);
            fa.setDefaultAccount(isDefault);
            fa.setValid(valid);
            logger.debug("FundAccount already exists.");
            return true;
        }
        UserFund userFund = fundDAO.find(userId);
        BankAccount bankAccount = new BankAccount(userName,
                                                  bank,
                                                  null,
                                                  null,
                                                  account);
        try {
            accountDAO.addNew(userFund, bankAccount, valid, isDefault);
            result = true;
        } catch (Exception ex) {
            logger.warn("Error happend when add bank card: {} for user: {}", account, userId, ex);
        }
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return result;
    }
    
    @Override
    public boolean addBankCard(String clientCode, String userId, com.creditcloud.model.user.fund.BankAccount bankAccount, boolean valid, boolean isDefault) {
        appBean.checkClientCode(clientCode);
        boolean result = false;
        if (accountDAO.checkExistByUserAndAccount(userId, bankAccount.getAccount())) {
            FundAccount fa = accountDAO.getByUserAndAccount(userId, bankAccount.getAccount());
            fa.setDefaultAccount(isDefault);
            fa.setValid(valid);
            logger.debug("FundAccount already exists.");
            return true;
        }
        UserFund userFund = fundDAO.find(userId);
        try {
            accountDAO.addNew(userFund, com.creditcloud.common.utils.DTOUtils.convertBankAccountDTO(bankAccount), valid, isDefault);
            result = true;
        } catch (Exception ex) {
            logger.warn("Error happend when add bank card: {} for user: {}", bankAccount, userId, ex);
        }
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
        
        return result;
    }
    
    @Override
    public com.creditcloud.fund.model.FundAccount getByUserAndAccount(String clientCode, String userId, String account) {
        appBean.checkClientCode(clientCode);
        FundAccount result = accountDAO.getByUserAndAccount(userId, account);
        return DTOUtils.getFundAccount(result);
    }
    
    @Override
    public com.creditcloud.fund.model.FundAccount getByAccount(String clientCode, String account) {
        appBean.checkClientCode(clientCode);
        FundAccount result = accountDAO.getByAccount(account);
        return DTOUtils.getFundAccount(result);
    }
    
    @Override
    public com.creditcloud.fund.model.FundAccount findById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        FundAccount result = accountDAO.find(id);
        return DTOUtils.getFundAccount(result);
    }
    
    @Override
    public com.creditcloud.fund.model.FundAccount getDefaultByUser(String clientCode, String userId) {
        List<com.creditcloud.fund.model.FundAccount> accounts = listAccountByUser(clientCode, userId);
        for (com.creditcloud.fund.model.FundAccount fundAccount : accounts) {
            if (fundAccount.isDefaultAccount()) {
                return fundAccount;
            } 
        }
        if (!accounts.isEmpty()) {
            return accounts.get(0);
        }
        logger.warn("No FundAccount found for user.[userId={}]", userId);
        return null;
    }
}
