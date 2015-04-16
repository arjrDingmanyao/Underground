/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.utils;

import static com.creditcloud.common.utils.DTOUtils.getBankAccountDTO;
import static com.creditcloud.common.utils.DTOUtils.getRealmEntity;
import com.creditcloud.fund.entities.ClientFundRecord;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.TransferRequest;
import com.creditcloud.fund.entities.UserAutoBid;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.embedded.AutoBidRange;
import com.creditcloud.fund.entities.record.FundCreditAssign;
import com.creditcloud.fund.entities.record.FundDeposit;
import com.creditcloud.fund.entities.record.FundInvest;
import com.creditcloud.fund.entities.record.FundRecord;
import com.creditcloud.fund.entities.record.FundTransfer;
import com.creditcloud.fund.entities.record.FundWithdraw;
import com.creditcloud.fund.entities.FundWithdrawHistory;
import com.creditcloud.fund.entities.record.UserFundHistory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rooseek
 */
public class DTOUtils extends com.creditcloud.common.utils.DTOUtils {

    /**
     * handle user fund
     *
     * @param fund
     * @return
     */
    public static com.creditcloud.fund.model.UserFund getUserFund(UserFund fund) {
        com.creditcloud.fund.model.UserFund result = null;
        if (fund != null) {
            result = new com.creditcloud.fund.model.UserFund(fund.getUserId(),
                                                             fund.getAvailableAmount(),
                                                             fund.getFrozenAmount(),
                                                             fund.getDueInAmount(),
                                                             fund.getDueOutAmount(),
                                                             fund.getDepositAmount(),
                                                             fund.getWithdrawAmount(),
                                                             fund.getTransferAmount());
        }
        return result;
    }

    /**
     * handle FundRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.record.FundRecord getFundRecord(FundRecord record) {
        com.creditcloud.fund.model.record.FundRecord result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.record.FundRecord(record.getId(),
                                                                      record.getFund().getUserId(),
                                                                      record.getAccount() == null ? null : getBankAccountDTO(record.getAccount().getAccount()),
                                                                      getRealmEntity(record.getEntity()),
                                                                      record.getType(),
                                                                      record.getStatus(),
                                                                      record.getOperation(),
                                                                      record.getAmount(),
                                                                      record.getOrderId(),
                                                                      record.getTransactionId(),
								      record.getAvailableAmount(),
                                                                      record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        
        return result;
    }

    /**
     * handle UserAutoBid
     *
     * @param bid
     * @return
     */
    public static com.creditcloud.fund.model.UserAutoBid getUserAutoBid(UserAutoBid bid) {
        com.creditcloud.fund.model.UserAutoBid result = null;
        if (bid != null) {
            result = new com.creditcloud.fund.model.UserAutoBid(bid.getUserId(),
                                                                bid.isActive(),
                                                                bid.getSingleAmount(),
                                                                bid.getReservedAmount(),
                                                                bid.getRepayMethod(),
                                                                getAutoBidRange(bid.getRange()),
                                                                bid.isMortgaged(),
                                                                bid.isAllIn(),
                                                                bid.getActivedTime(),
                                                                bid.getLastBidTime());
            result.setEnable(bid.isEnable());
        }
        return result;
    }
    
    public static UserAutoBid convertUserAutoBid(UserFund fund, com.creditcloud.fund.model.UserAutoBid bid) {
        UserAutoBid result = null;
        if (bid != null) {
            result = new UserAutoBid(fund,
                                     bid.isActive(),
                                     bid.getSingleAmount(),
                                     bid.getReservedAmount(),
                                     bid.getRepayMethod(),
                                     convertAutoBidRange(bid.getRange()),
                                     bid.isMortgaged(),
                                     bid.isAllIn(),
                                     bid.getActivedTime(),
                                     bid.getLastBidTime(),
                                     bid.isEnable());
        }
        return result;
    }

    /**
     * Handle AutoBidRange
     *
     * @param bid
     * @return
     */
    public static com.creditcloud.fund.model.AutoBidRange getAutoBidRange(AutoBidRange bid) {
        com.creditcloud.fund.model.AutoBidRange result = null;
        if (bid != null) {
            result = new com.creditcloud.fund.model.AutoBidRange(bid.getMinRate(),
                                                                 bid.getMaxRate(),
                                                                 bid.getMinDuration(),
                                                                 bid.getMaxDuration(),
                                                                 bid.getMinCredit(),
                                                                 bid.getMaxCredit());
        }
        return result;
    }
    
    public static AutoBidRange convertAutoBidRange(com.creditcloud.fund.model.AutoBidRange bid) {
        AutoBidRange result = null;
        if (bid != null) {
            result = new AutoBidRange(bid.getMinRate(),
                                      bid.getMaxRate(),
                                      bid.getMinDuration(),
                                      bid.getMaxDuration(),
                                      bid.getMinCredit(),
                                      bid.getMaxCredit());
        }
        return result;
    }

    /**
     * handle FundHistory
     *
     * @param history
     * @return
     */
    public static com.creditcloud.fund.model.UserFundHistory getUserFundHistory(UserFundHistory history) {
        com.creditcloud.fund.model.UserFundHistory result = null;
        if (history != null) {
            result = new com.creditcloud.fund.model.UserFundHistory(history.getUserId(),
                                                                    history.getAsOfDate(),
                                                                    history.getAvailableAmount(),
                                                                    history.getFrozenAmount(),
                                                                    history.getDueInAmount(),
                                                                    history.getDueOutAmount(),
                                                                    history.getDepositAmount(),
                                                                    history.getWithdrawAmount(),
                                                                    history.getTransferAmount());
        }
        
        return result;
    }

    /**
     * handle FundWithdraw
     *
     * @param fundWithdraw
     * @return
     */
    public static com.creditcloud.fund.model.record.FundWithdraw getFundWithdrawDTO(FundWithdraw fundWithdraw) {
        com.creditcloud.fund.model.record.FundWithdraw result = null;
        if (fundWithdraw != null) {
            result = new com.creditcloud.fund.model.record.FundWithdraw(fundWithdraw.getId(),
                                                                        fundWithdraw.getFund().getUserId(),
                                                                        fundWithdraw.getAccount() == null ? null : getBankAccountDTO(fundWithdraw.getAccount().getAccount()),
                                                                        getRealmEntity(fundWithdraw.getEntity()),
                                                                        fundWithdraw.getStatus(),
                                                                        fundWithdraw.getOperation(),
                                                                        fundWithdraw.getAmount(),
                                                                        fundWithdraw.getOrderId(),
                                                                        fundWithdraw.getTransactionId(),
									fundWithdraw.getAvailableAmount(),
                                                                        fundWithdraw.getDescription());
            result.setTimeRecorded(fundWithdraw.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle FundInvest
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.record.FundInvest getFundInvestDTO(FundInvest record) {
        com.creditcloud.fund.model.record.FundInvest result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.record.FundInvest(record.getId(),
                                                                      record.getFund().getUserId(),
                                                                      getRealmEntity(record.getEntity()),
                                                                      record.getStatus(),
                                                                      record.getOperation(),
                                                                      record.getAmount(),
                                                                      record.getOrderId(),
                                                                      record.getTransactionId(),
									record.getAvailableAmount(),
                                                                      record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle FundDeposit
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.record.FundDeposit getFundDepositDTO(FundDeposit record) {
        com.creditcloud.fund.model.record.FundDeposit result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.record.FundDeposit(record.getId(),
                                                                       record.getFund().getUserId(),
                                                                       record.getAccount() == null ? null : getBankAccountDTO(record.getAccount().getAccount()),
                                                                       record.getStatus(),
                                                                       record.getAmount(),
                                                                       record.getOrderId(),
                                                                       record.getTransactionId(),
								       record.getAvailableAmount(),
                                                                       record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle FundTransfer
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.record.FundTransfer getFundTransferDTO(FundTransfer record) {
        com.creditcloud.fund.model.record.FundTransfer result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.record.FundTransfer(record.getId(),
                                                                        record.getFund().getUserId(),
                                                                        getRealmEntity(record.getEntity()),
                                                                        record.getStatus(),
                                                                        record.getOperation(),
                                                                        record.getAmount(),
                                                                        record.getOrderId(),
                                                                        record.getTransactionId(),
									record.getAvailableAmount(),
                                                                        record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle FundCreditAssign
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.record.FundCreditAssign getFundCreditAssignDTO(FundCreditAssign record) {
        com.creditcloud.fund.model.record.FundCreditAssign result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.record.FundCreditAssign(record.getId(),
                                                                            record.getFund().getUserId(),
                                                                            getRealmEntity(record.getEntity()),
                                                                            record.getStatus(),
                                                                            record.getOperation(),
                                                                            record.getAmount(),
                                                                            record.getOrderId(),
                                                                            record.getTransactionId(),
									    record.getAvailableAmount(),
                                                                            record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle FundAccount
     *
     * @param account
     * @return
     */
    public static com.creditcloud.fund.model.FundAccount getFundAccount(FundAccount account) {
        com.creditcloud.fund.model.FundAccount result = null;
        if (account != null) {
            result = new com.creditcloud.fund.model.FundAccount(account.getId(),
                                                                account.getFund().getUserId(),
                                                                DTOUtils.getBankAccountDTO(account.getAccount()),
                                                                account.isValid(),
                                                                account.isDefaultAccount(),
                                                                account.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle ClientFundRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.ClientFundRecord getClientFundRecord(ClientFundRecord record) {
        com.creditcloud.fund.model.ClientFundRecord result = null;
        if (record != null) {
            result = new com.creditcloud.fund.model.ClientFundRecord(record.getId(),
                                                                     record.getAccount(),
                                                                     DTOUtils.getRealmEntity(record.getEntity()),
                                                                     record.getUserId(),
                                                                     record.getType(),
                                                                     record.getOperation(),
                                                                     record.getStatus(),
                                                                     record.getAmount(),
                                                                     record.getOrderId(),
                                                                     record.getTransactionId(),
                                                                     record.getDescription());
            result.setTimeRecorded(record.getTimeRecorded());
        }
        return result;
    }
    
    public static ClientFundRecord convertClientFundRecord(com.creditcloud.fund.model.ClientFundRecord record) {
        ClientFundRecord result = null;
        if (record != null) {
            result = new ClientFundRecord(record.getAccount(),
                                          DTOUtils.convertRealmEntity(record.getEntity()),
                                          record.getUserId(),
                                          record.getType(),
                                          record.getOperation(),
                                          record.getStatus(),
                                          record.getAmount(),
                                          record.getOrderId(),
                                          record.getTransactionId(),
                                          record.getDescription());
        }
        return result;
    }

    /**
     * handle TransferRequest
     *
     * @param request
     * @return
     */
    public static com.creditcloud.fund.model.TransferRequest getTransferRequest(TransferRequest request) {
        com.creditcloud.fund.model.TransferRequest result = null;
        if (request != null) {
            result = new com.creditcloud.fund.model.TransferRequest(request.getId(),
                                                                    request.getUserId(),
                                                                    request.getAmount(),
                                                                    request.getAccount(),
                                                                    request.getStatus(),
                                                                    request.getOrderId(),
                                                                    request.getRequestEmployee(),
                                                                    request.getAuditEmployee(),
                                                                    request.getDescription());
            result.setTimeRecorded(request.getTimeRecorded());
        }
        return result;
    }
    
    public static List<com.creditcloud.fund.model.TransferRequest> getTransferRequest(List<TransferRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<com.creditcloud.fund.model.TransferRequest> result = new ArrayList<>(requests.size());
        for (TransferRequest request : requests) {
            result.add(getTransferRequest(request));
        }
        return result;
    }
    
    public static TransferRequest convertTransferRequest(com.creditcloud.fund.model.TransferRequest request) {
        TransferRequest result = null;
        if (request != null) {
            result = new TransferRequest(request.getUserId(),
                                         request.getAmount(),
                                         request.getAccount(),
                                         request.getStatus(),
                                         request.getOrderId(),
                                         request.getRequestEmployee(),
                                         request.getAuditEmployee(),
                                         request.getDescription());
            result.setId(request.getId());
        }
        return result;
    }
    
     /**
     * 提现历史
     *
     * @param record
     * @return
     */
    public static com.creditcloud.fund.model.FundWithdrawHistory getFundWithdrawHistoryDTO(FundWithdrawHistory record) {
	com.creditcloud.fund.model.FundWithdrawHistory result = null;
	if (record != null) {
	    result = new com.creditcloud.fund.model.FundWithdrawHistory(record.getId(),
		    record.getFund().getUserId(),
		    record.getBankName(),
		    record.getBankAccount(),
		    record.getAmount(),
		    record.getEmployeeId(),
		    record.getOrderId(),
		    record.getTransactionId(),
		    record.getStatus(),
		    record.getTransferAmount(),
		    record.getApproveDateTime(),
		    record.getTimeRecorded(),
		    record.getDescription());
	    result.setTimeRecorded(record.getTimeRecorded());
	}
	return result;
    }
}
