/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.fund.api.ClientFundService;
import com.creditcloud.fund.entities.ClientFundRecord;
import com.creditcloud.fund.entities.dao.ClientFundRecordDAO;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ClientFundServiceBean implements ClientFundService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    ClientFundRecordDAO clientRecordDAO;

    @Override
    public PagedResult<com.creditcloud.fund.model.ClientFundRecord> listRecord(String clientCode, List<String> accountList,
                                                                               Date from, Date to, PageInfo pageInfo, FundRecordType... type) {
        appBean.checkClientCode(clientCode);
        logger.debug("listClientFundRecord.[account={}][from={}][to={}][pageInfo={}][type={}]", accountList, from, to, pageInfo, Arrays.asList(type));
        PagedResult<ClientFundRecord> records = clientRecordDAO.listByAccountAndType(accountList, from, to, pageInfo, type);
        List<com.creditcloud.fund.model.ClientFundRecord> result = new ArrayList<>(records.getResults().size());
        for (ClientFundRecord record : records.getResults()) {
            result.add(DTOUtils.getClientFundRecord(record));
        }
        return new PagedResult<>(result, records.getTotalSize());
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord addRecord(String clientCode, com.creditcloud.fund.model.ClientFundRecord record) {
        appBean.checkClientCode(clientCode);
        logger.debug("Adding new clientfundRecord:\n{}", record);
        ClientFundRecord result = clientRecordDAO.create(DTOUtils.convertClientFundRecord(record));
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public void updateRecord(String clientCode, com.creditcloud.fund.model.ClientFundRecord record) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.find(record.getId());
        if (result != null) {
            //只有下面几项可以更改
            result.setAmount(record.getAmount());
            result.setDescription(record.getDescription());
            result.setTransactionId(record.getTransactionId());
            result.setStatus(record.getStatus());
            clientRecordDAO.edit(result);
        }
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getDepositRequestByOrderId(String clientCode, String orderId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getByAccountAndTypeAndOrderId(appBean.getPaymentConfig().getBaseAccount().getAccountId(),
                                                                                orderId, FundRecordType.DEPOSIT);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getDepositRequestByOrderIdAndAccount(String clientCode, String account, String orderId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getByAccountAndTypeAndOrderId(account,
                                                                                orderId,
                                                                                FundRecordType.DEPOSIT);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getWithdrawRequestByOrderId(String clientCode, String orderId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getByAccountAndTypeAndOrderId(appBean.getPaymentConfig().getBaseAccount().getAccountId(),
                                                                                orderId, FundRecordType.WITHDRAW);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getWithdrawRequestByOrderIdAndAccount(String clientCode, String account, String orderId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getByAccountAndTypeAndOrderId(account,
                                                                                orderId,
                                                                                FundRecordType.WITHDRAW);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getBindCardFeeRequestByOrderIdAndAccount(String clientCode, String account, String orderId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getByAccountAndTypeAndOrderId(account,
                                                                                orderId,
                                                                                FundRecordType.FEE_BIND_CARD);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getRecordById(String clientCode, String recordId) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.find(recordId);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public com.creditcloud.fund.model.ClientFundRecord getWithdrawByOperationAndStatus(String clientCode, String withdrawId, FundRecordOperation operation, FundRecordStatus status) {
        appBean.checkClientCode(clientCode);
        ClientFundRecord result = clientRecordDAO.getWithdrawByOperationAndStatus(withdrawId, operation, status);
        return DTOUtils.getClientFundRecord(result);
    }

    @Override
    public List<com.creditcloud.fund.model.ClientFundRecord> listWithdrawRequest(String clientCode, FundRecordStatus... status) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.fund.model.ClientFundRecord> result;
        if (status == null || status.length == 0) {
            List<ClientFundRecord> records = clientRecordDAO.listWithdrawRequest(FundRecordStatus.AUDITING);
            result = new ArrayList<>(records.size());
            for (ClientFundRecord record : records) {
                result.add(DTOUtils.getClientFundRecord(record));
            }
        } else {
            List<ClientFundRecord> records = clientRecordDAO.listWithdrawRequest(status);
            result = new ArrayList<>(records.size());
            for (ClientFundRecord record : records) {
                result.add(DTOUtils.getClientFundRecord(record));
            }
        }
        return result;
    }
}
