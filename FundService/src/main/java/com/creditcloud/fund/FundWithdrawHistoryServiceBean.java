/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.fund.api.FundWithdrawHistoryService;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.dao.FundAccountDAO;
import com.creditcloud.fund.entities.dao.FundWithdrawHistoryDAO;
import com.creditcloud.fund.entities.dao.UserFundDAO;
import com.creditcloud.fund.entities.FundWithdrawHistory;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * 提现记录
 *
 * @author Administrator
 */
@Remote
@Stateless
public class FundWithdrawHistoryServiceBean implements FundWithdrawHistoryService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    UserFundDAO fundDAO;

    @EJB
    FundAccountDAO accountDAO;

    @EJB
    FundWithdrawHistoryDAO fundWithdrawHistoryDAO;

    @Override
    public com.creditcloud.fund.model.FundWithdrawHistory addNew(String clientCode, com.creditcloud.fund.model.FundWithdrawHistory record) {
	appBean.checkClientCode(clientCode);
	logger.debug("Adding new FundWithdrawHistory:\n{}", record);
	UserFund fund = fundDAO.find(record.getUserId());
	FundAccount account = null;
	if (record.getAccount() != null) {
	    account = accountDAO.getByUserAndAccount(record.getUserId(), record.getAccount().getAccount());
	}
	FundWithdrawHistory persist = new FundWithdrawHistory(fund,
		account,
		record.getEmployeeId(),
		record.getAmount(),
		record.getOrderId(),
		record.getTransactionId(),
		record.getStatus(),
		new Date());
	FundWithdrawHistory result = fundWithdrawHistoryDAO.create(persist);
	return DTOUtils.getFundWithdrawHistoryDTO(result);
    }

    @Override
    public void updateStatus(String clientCode, String fundwithdrawId, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	boolean result = fundWithdrawHistoryDAO.markStatus(status, fundwithdrawId);
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.FundWithdrawHistory> listFundWithdrawHistory(String clientCode, Date startDate, Date endDate, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);

	PagedResult<FundWithdrawHistory> result = fundWithdrawHistoryDAO.listByPageInfo(pageInfo);

	//获取取现费用
	List<com.creditcloud.fund.model.FundWithdrawHistory> records = new ArrayList<>(result.getResults().size());
	for (FundWithdrawHistory temp : result.getResults()) {
	    com.creditcloud.fund.model.FundWithdrawHistory dto = DTOUtils.getFundWithdrawHistoryDTO(temp);
	    records.add(dto);
	}
	return new PagedResult<>(records, result.getTotalSize());

    }

    @Override
    public com.creditcloud.fund.model.FundWithdrawHistory getFundWithdrawHistoryByorderId(String orderId) {
	FundWithdrawHistory temp = fundWithdrawHistoryDAO.findByOrderId(orderId);
	com.creditcloud.fund.model.FundWithdrawHistory result = DTOUtils.getFundWithdrawHistoryDTO(temp);
	return result;
    }

    @Override
    public void update(com.creditcloud.fund.model.FundWithdrawHistory history) {
	logger.debug("update FundWithdrawHistory:\n{}", history);
	FundWithdrawHistory persist = fundWithdrawHistoryDAO.find(history.getId());
	if (persist != null) {
	    persist.setTransferAmount(history.getTransferAmount());
	    persist.setStatus(history.getStatus());
	    persist.setTransactionId(history.getTransactionId());
	    //更新
	    logger.debug("FundWithdrawHistory edit :{}", persist);
	    fundWithdrawHistoryDAO.edit(persist);
	} else {
	    logger.debug("FundWithdrawHistory edit failed ,Object is not exist:{}", persist);
	}

    }

    @Override
    public void updateStatusToSuccess(com.creditcloud.fund.model.FundWithdrawHistory history) {
	logger.debug("update FundWithdrawHistory:\n{}", history);
	FundWithdrawHistory persist = fundWithdrawHistoryDAO.find(history.getId());
	if (persist != null) {
	    persist.setApproveDateTime(history.getApproveDateTime());
	    persist.setStatus(history.getStatus());
	    persist.setEmployeeId(history.getEmployeeId());
	    //更新
	    logger.debug("FundWithdrawHistory edit :{}", persist);
	    fundWithdrawHistoryDAO.edit(persist);
	} else {
	    logger.debug("FundWithdrawHistory edit failed ,Object is not exist:{}", persist);
	}

    }

}
