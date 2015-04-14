/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.common.entities.embedded.RealmEntity;
import static com.creditcloud.common.utils.DTOUtils.convertRealmEntity;
import com.creditcloud.fund.api.FundRecordService;
import com.creditcloud.fund.entities.ClientFundRecord;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.entities.dao.ClientFundRecordDAO;
import com.creditcloud.fund.entities.dao.FundAccountDAO;
import com.creditcloud.fund.entities.dao.FundRecordDAO;
import com.creditcloud.fund.entities.dao.UserFundDAO;
import com.creditcloud.fund.entities.record.FundCreditAssign;
import com.creditcloud.fund.entities.record.FundDeposit;
import com.creditcloud.fund.entities.record.FundFSS;
import com.creditcloud.fund.entities.record.FundFee;
import com.creditcloud.fund.entities.record.FundInvest;
import com.creditcloud.fund.entities.record.FundInvestRepay;
import com.creditcloud.fund.entities.record.FundLoan;
import com.creditcloud.fund.entities.record.FundLoanRepay;
import com.creditcloud.fund.entities.record.FundRecord;
import com.creditcloud.fund.entities.record.FundReward;
import com.creditcloud.fund.entities.record.FundTransfer;
import com.creditcloud.fund.entities.record.FundWithdraw;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import static com.creditcloud.fund.model.enums.FundRecordType.FEE_AUTHENTICATE;
import static com.creditcloud.fund.model.enums.FundRecordType.FEE_INVEST_INTEREST;
import static com.creditcloud.fund.model.enums.FundRecordType.FEE_LOAN_GUARANTEE;
import static com.creditcloud.fund.model.enums.FundRecordType.FEE_LOAN_INTEREST;
import static com.creditcloud.fund.model.enums.FundRecordType.FEE_LOAN_SERVICE;
import com.creditcloud.fund.utils.DTOUtils;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.constant.NumberConstant;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.PagedResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class FundRecordServiceBean implements FundRecordService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    FundRecordDAO recordDAO;

    @EJB
    UserFundDAO fundDAO;

    @EJB
    FundAccountDAO accountDAO;

    @EJB
    ClientFundRecordDAO clientRecordDAO;

    @Override
    public com.creditcloud.fund.model.record.FundRecord getById(String clientCode, String id) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getFundRecord(recordDAO.find(id));
    }

    @Override
    public com.creditcloud.fund.model.record.FundRecord getByUserAndTypeAndOrderId(String clientCode, String userId, FundRecordType type, String orderId) {
	appBean.checkClientCode(clientCode);
	FundRecord record = recordDAO.getByOrderId(userId, type, orderId);
	return DTOUtils.getFundRecord(record);
    }

    @Override
    public com.creditcloud.fund.model.record.FundRecord getByTypeAndOrderId(String clientCode, FundRecordType type, String orderId) {
	appBean.checkClientCode(clientCode);
	FundRecord record = recordDAO.getByOrderIdAndType(type, orderId);
	return DTOUtils.getFundRecord(record);
    }

    @Override
    public com.creditcloud.fund.model.record.FundRecord addNew(String clientCode, com.creditcloud.fund.model.record.FundRecord record) {
	appBean.checkClientCode(clientCode);
	logger.debug("Adding new fundRecord:\n{}", record);
	UserFund fund = fundDAO.find(record.getUserId());
	FundAccount account = null;
	if (record.getAccount() != null) {
	    account = accountDAO.getByUserAndAccount(record.getUserId(), record.getAccount().getAccount());
	}
	FundRecord result = recordDAO.create(convertFundRecord(fund, account, record));

	// delete cache
	appBean.deleteCache(record.getUserId(), CacheConstant.KEY_PREFIX_USER_FUND);

	return DTOUtils.getFundRecord(result);
    }

    @Override
    public void update(String clientCode, com.creditcloud.fund.model.record.FundRecord record) {
	appBean.checkClientCode(clientCode);
        logger.info("传入update方法的record对象：{}",record);
	FundRecord result = recordDAO.find(record.getId());
        logger.info("find方法查询缓存result对象：{}",result);
	if (result != null) {
	    //TODO暂时只有下列field可以修改
	    if (record.getAccount() != null) {
		FundAccount account = accountDAO.getByUserAndAccount(record.getUserId(), record.getAccount().getAccount());
		result.setAccount(account);
	    }
	    result.setAmount(record.getAmount());
	    result.setDescription(record.getDescription());
	    result.setStatus(record.getStatus());
	    result.setTransactionId(record.getTransactionId());
	    recordDAO.edit(result);

	    // delete cache
	    appBean.deleteCache(record.getUserId(), CacheConstant.KEY_PREFIX_USER_FUND);
            logger.info("调用edit方法后的record对象：{}",record);
            logger.info("缓存result对象：{}",result);
	}
    }

    @Override
    public void updateStatus(String clientCode, String recordId, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	boolean result = recordDAO.markStatus(status, recordId);
	if (!result) {
	    logger.debug("fail to find record {} to update status {}.", recordId, status);
	} else {

	    FundRecord record = recordDAO.find(recordId);
	    // delete cache
	    appBean.deleteCache(record.getFund().getUserId(), CacheConstant.KEY_PREFIX_USER_FUND);

	}
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.record.FundRecord> listByUser(String clientCode, String userId, Date startDate, Date endDate, PageInfo pageInfo, FundRecordType... type) {
	appBean.checkClientCode(clientCode);
	logger.debug("listByUser called.[userId={}][startDate={}][endDate={}][pageInfo={}][type={}]", userId, startDate, endDate, pageInfo, Arrays.asList(type));
	PagedResult<FundRecord> result = recordDAO.listByUser(userId, startDate, endDate, pageInfo, type);
	List<com.creditcloud.fund.model.record.FundRecord> records = new ArrayList<>(result.getResults().size());
	for (FundRecord record : result.getResults()) {
	    records.add(DTOUtils.getFundRecord(record));
	}
	return new PagedResult<>(records, result.getTotalSize());
    }

    @Override
    public int countByUser(String clientCode, String userId, List<FundRecordType> type, List<FundRecordOperation> operation, List<FundRecordStatus> status) {
	appBean.checkClientCode(clientCode);
	return recordDAO.countByUserAndTypeAndOperationAndStatus(userId, type, operation, status);
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.record.FundRecord> listByUser(String clientCode, String userId, PageInfo pageInfo, List<FundRecordType> type, List<FundRecordOperation> operation, List<FundRecordStatus> status) {
	appBean.checkClientCode(clientCode);
	PagedResult<FundRecord> result = recordDAO.listByUserAndTypeAndOperationAndStatus(userId, pageInfo, type, operation, status);
	List<com.creditcloud.fund.model.record.FundRecord> records = new ArrayList<>(result.getResults().size());
	for (FundRecord record : result.getResults()) {
	    records.add(DTOUtils.getFundRecord(record));
	}
	return new PagedResult<>(records, result.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.fund.model.record.FundRecord> listByUser(String clientCode, String userId, Date startDate, Date endDate, PageInfo pageInfo, List<FundRecordType> type, List<FundRecordOperation> operation, List<FundRecordStatus> status) {
	appBean.checkClientCode(clientCode);
	PagedResult<FundRecord> result = recordDAO.listByUserAndTypeAndOperationAndStatusAndTime(userId, startDate, endDate, pageInfo, type, operation, status);
	List<com.creditcloud.fund.model.record.FundRecord> records = new ArrayList<>(result.getResults().size());
	for (FundRecord record : result.getResults()) {
	    records.add(DTOUtils.getFundRecord(record));
	}
	return new PagedResult<>(records, result.getTotalSize());
    }

    @Override
    public List<com.creditcloud.fund.model.record.FundWithdraw> listWithdrawRequest(String clientCode, FundRecordStatus... status) {
	appBean.checkClientCode(clientCode);
	logger.debug("List FundWithdraw request for client.[client={}]", clientCode);
	List<com.creditcloud.fund.model.record.FundWithdraw> result;
	if (status == null || status.length == 0) {
	    List<FundWithdraw> requests = recordDAO.listWithdrawByStatus(FundRecordStatus.AUDITING);
	    result = new ArrayList<>(requests.size());
	    for (FundWithdraw fundWithdraw : requests) {
		result.add(DTOUtils.getFundWithdrawDTO(fundWithdraw));
	    }
	} else {
	    List<FundWithdraw> requests = recordDAO.listWithdrawByStatus(status);
	    result = new ArrayList<>(requests.size());
	    for (FundWithdraw fundWithdraw : requests) {
		result.add(DTOUtils.getFundWithdrawDTO(fundWithdraw));
	    }
	}
	return result;
    }

    @Override
    public List<com.creditcloud.fund.model.record.FundWithdraw> listWithdraw(String clientCode,
	    Date startDate,
	    Date endDate,
	    PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	List<FundRecordOperation> operationList = new ArrayList<>();
	operationList.add(FundRecordOperation.OUT);
	List<FundRecordStatus> statusList = new ArrayList<>();
	statusList.add(FundRecordStatus.SUCCESSFUL);
	logger.debug("List withdraw for client.[client={}][startDate={}][endDate={}]", clientCode, startDate, endDate);

	List<FundWithdraw> records = recordDAO.listWithdrawByStatus(startDate,
		endDate,
		pageInfo,
		operationList,
		statusList);
	List<com.creditcloud.fund.model.record.FundWithdraw> result = new ArrayList<>(records.size());
	for (FundWithdraw fundWithdraw : records) {
	    result.add(DTOUtils.getFundWithdrawDTO(fundWithdraw));
	}
	return result;
    }

    /**
     * 体现历史记录
     *
     * @param clientCode
     * @param startDate
     * @param endDate
     * @param pageInfo
     * @return
     */
    @Override
    public PagedResult<com.creditcloud.fund.model.record.FundWithdraw> listWithdrawAndFeeHistory(String clientCode, Date startDate, Date endDate, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	List<FundRecordStatus> statusList = new ArrayList<>();
	for (FundRecordStatus status : FundRecordStatus.values()) {
	    statusList.add(status);
	}
	logger.debug("List withdraw for client.[client={}][startDate={}][endDate={}]", clientCode, startDate, endDate);
	PagedResult<FundWithdraw> result = recordDAO.listWithdrawByStatus(pageInfo, statusList);
	//获取取现费用
	List<com.creditcloud.fund.model.record.FundWithdraw> records = new ArrayList<>(result.getResults().size());
	for (FundWithdraw fundWithdraw : result.getResults()) {
	    com.creditcloud.fund.model.record.FundWithdraw dto = DTOUtils.getFundWithdrawDTO(fundWithdraw);
	    //添加手续费
	    dto.setWithdrawFee(fundWithdraw.getWithdrawFee());
	    dto.setApproveDateTime(fundWithdraw.getApproveDateTime());
	    records.add(dto);
	}

	return new PagedResult<>(records, result.getTotalSize());
    }

    @Override
    public List<com.creditcloud.fund.model.record.FundDeposit> listDeposit(String clientCode, Date startDate, Date endDate, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	List<FundRecordOperation> operationList = new ArrayList<>();
	operationList.add(FundRecordOperation.IN);
	List<FundRecordStatus> statusList = new ArrayList<>();
	statusList.add(FundRecordStatus.SUCCESSFUL);
	logger.debug("List deposit for client.[client={}][startDate={}][endDate={}]", clientCode, startDate, endDate);
	List<FundDeposit> records = recordDAO.listDepositByStatus(startDate,
		endDate,
		pageInfo,
		operationList,
		statusList);
	List<com.creditcloud.fund.model.record.FundDeposit> result = new ArrayList<>(records.size());
	for (FundDeposit fundDeposit : records) {
	    result.add(DTOUtils.getFundDepositDTO(fundDeposit));
	}
	return result;
    }

    @Override
    public com.creditcloud.fund.model.record.FundInvest getFundInvestByOrderId(String clientCode, String userId, String pnrOrderId) {
	appBean.checkClientCode(clientCode);
	FundInvest result = recordDAO.getFundInvestByOrderId(userId, pnrOrderId);
	return DTOUtils.getFundInvestDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundDeposit getFundDepositByOrderId(String clientCode, String userId, String pnrOrderId) {
	appBean.checkClientCode(clientCode);
	FundDeposit result = recordDAO.getFundDepositByOrderId(userId, pnrOrderId);
	return DTOUtils.getFundDepositDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundWithdraw getFundWithdrawByOrderId(String clientCode, String userId, String pnrOrderId) {
	appBean.checkClientCode(clientCode);
	FundWithdraw result = recordDAO.getFundWithdrawByOrderId(userId, pnrOrderId);
	return DTOUtils.getFundWithdrawDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundInvest getFundInvestByOperationAndStatus(String clientCode, String userId, String investId, FundRecordOperation operation, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	FundInvest result = recordDAO.getFundInvestByOperationAndStatus(userId, new RealmEntity(Realm.INVEST, investId), operation, status);
	return DTOUtils.getFundInvestDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundInvest getFundInvestByOperationAndStatus(String clientCode, String userId, com.creditcloud.model.misc.RealmEntity investEntity, FundRecordOperation operation, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	FundInvest result = recordDAO.getFundInvestByOperationAndStatus(userId, com.creditcloud.common.utils.DTOUtils.convertRealmEntity(investEntity), operation, status);
	return DTOUtils.getFundInvestDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundCreditAssign getFundCreditAssignByOperationAndStatus(String clientCode, String userId, String investId, FundRecordOperation operation, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	FundCreditAssign result = recordDAO.getFundCreditAssignByOperationAndStatus(userId, investId, operation, status);
	return DTOUtils.getFundCreditAssignDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundTransfer getFundTransferByOrderId(String clientCode, String userId, String orderId) {
	appBean.checkClientCode(clientCode);
	FundTransfer result = recordDAO.getFundTransferByOrderId(userId, orderId);
	return DTOUtils.getFundTransferDTO(result);
    }

    @Override
    public com.creditcloud.fund.model.record.FundCreditAssign getFundCreditAssignByOrderId(String clientCode, String userId, String orderId) {
	appBean.checkClientCode(clientCode);
	FundCreditAssign result = recordDAO.getFundCreditAssignByOrderId(userId, orderId);
	return DTOUtils.getFundCreditAssignDTO(result);
    }

    @Override
    public Pair<FundRecordStatus, com.creditcloud.fund.model.record.FundRecord> checkInvest(String clientCode, String userId, String investId) {
	appBean.checkClientCode(clientCode);
	//首先查看是否投标结算记录
	FundRecord investRecord = recordDAO.getFundInvestByOperationAndStatus(userId, new RealmEntity(Realm.INVEST, investId), FundRecordOperation.OUT, FundRecordStatus.SUCCESSFUL);
	if (investRecord != null) {
	    return new ImmutablePair<>(FundRecordStatus.SUCCESSFUL, DTOUtils.getFundRecord(investRecord));
	}
	//查看是否已经取消
	investRecord = recordDAO.getFundInvestByOperationAndStatus(userId, new RealmEntity(Realm.INVEST, investId), FundRecordOperation.OUT, FundRecordStatus.CANCELED);
	if (investRecord != null) {
	    return new ImmutablePair<>(FundRecordStatus.CANCELED, DTOUtils.getFundRecord(investRecord));
	}
	//查看是否有成功投标记录
	investRecord = recordDAO.getFundInvestByOperationAndStatus(userId, new RealmEntity(Realm.INVEST, investId), FundRecordOperation.OUT, FundRecordStatus.INITIALIZED);
	if (investRecord != null) {
	    return new ImmutablePair<>(FundRecordStatus.INITIALIZED, DTOUtils.getFundRecord(investRecord));
	}
	return null;
    }

    @Override
    public com.creditcloud.fund.model.record.FundWithdraw getWithdrawByOperationAndStatus(String clientCode, String userId, String withdrawId, FundRecordOperation operation, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	FundWithdraw result = recordDAO.getWithdrawByOperationAndStatus(userId, withdrawId, operation, status);
	return DTOUtils.getFundWithdrawDTO(result);
    }

    @Override
    public boolean checkInvestRepay(String clientCode, String userId, String investId, int period) {
	appBean.checkClientCode(clientCode);
	FundInvestRepay repay = recordDAO.getInvestRepayByOperationAndStatus(userId, investId, FundRecordStatus.SUCCESSFUL, FundRecordOperation.IN, period);
	return repay != null;
    }

    @Override
    public void settleInvestRecord(String clientCode, String investUserId, String investId, BigDecimal investAmount,
	    String loanUserId, String loanId, Map<FundRecordType, BigDecimal> feeDetails, String orderId) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	UserFund loanFund = fundDAO.find(loanUserId);
	//投标放款记录
	recordDAO.create(new FundInvest(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.OUT,
		investAmount, orderId, null,investFund.getAvailableAmount(), null));
	//贷款放款记录
	recordDAO.create(new FundLoan(loanFund,
		new RealmEntity(Realm.LOAN, loanId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		investAmount, orderId, null,loanFund.getAvailableAmount(), null));

	for (Map.Entry<FundRecordType, BigDecimal> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		//贷款服务费
		case FEE_LOAN_SERVICE:
		//贷款管理费
		case FEE_LOAN_MANAGE:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人服务费收费记录
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee, orderId, null,loanFund.getAvailableAmount(), null));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getFeeAccount().getAccountId(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		//担保费
		case FEE_LOAN_GUARANTEE:
		//风险管理費
		case FEE_LOAN_RISK:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人风险保证金记录
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee, orderId, null,loanFund.getAvailableAmount(), null));
			//商户保证金记录
			clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getGuaranteeAccount().getAccountId(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void settleInvestRecord2(String clientCode, String investUserId, String investId, BigDecimal investAmount,
	    String loanUserId, String loanId, Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeDetails, String orderId) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	UserFund loanFund = fundDAO.find(loanUserId);
	//投标放款记录
	recordDAO.create(new FundInvest(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.OUT,
		investAmount, orderId, null,investFund.getAvailableAmount(), null));
	//贷款放款记录
	recordDAO.create(new FundLoan(loanFund,
		new RealmEntity(Realm.LOAN, loanId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		investAmount, orderId, null,loanFund.getAvailableAmount(), null));

	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		//贷款服务费
		case FEE_LOAN_SERVICE:
		//贷款管理费
		case FEE_LOAN_MANAGE:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人服务费收费记录
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee, orderId, null,loanFund.getAvailableAmount(), null));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		//担保费
		case FEE_LOAN_GUARANTEE:
		//风险管理費
		case FEE_LOAN_RISK:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人风险保证金记录
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee, orderId, null,loanFund.getAvailableAmount(), null));
			//商户保证金记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void settleInvestRecord2Ump(String clientCode, String investUserId, String investId, BigDecimal investAmount,
	    String loanUserId, String loanId, Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeDetails, String orderId) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	//投标放款记录
	recordDAO.create(new FundInvest(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.OUT,
		investAmount,
		orderId,
		null,
		investFund.getAvailableAmount(),
		null));
	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		//贷款服务费
		case FEE_LOAN_SERVICE:
		//贷款管理费
		case FEE_LOAN_MANAGE:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		//担保费
		case FEE_LOAN_GUARANTEE:
		//风险管理費
		case FEE_LOAN_RISK:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//商户保证金记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, null));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void settleInvestRecord2UmpRefund(String clientCode,
	    String loanUserId,
	    String loanId,
	    BigDecimal refundAmount,
	    String refundOrderId,
	    String account,
	    Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeToMerchantDetails,
	    String userId,
	    Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeToGuaranteeDetails) {
	appBean.checkClientCode(clientCode);
	UserFund loanFund = fundDAO.find(loanUserId);
	//贷款放款记录
	if (refundAmount.compareTo(BigDecimal.ZERO) == 1) {
	    recordDAO.create(new FundLoan(loanFund,
		    new RealmEntity(Realm.LOAN, loanId),
		    FundRecordStatus.SUCCESSFUL,
		    FundRecordOperation.IN,
		    refundAmount,
		    refundOrderId,
		    null,
		    loanFund.getAvailableAmount(),
		    null));
	}

	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeToMerchantDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    String orderId = entry.getValue().getLeft();
	    FundRecordType type = entry.getKey();
	    switch (entry.getKey()) {
		//贷款服务费
		case FEE_LOAN_SERVICE:
		//贷款管理费
		case FEE_LOAN_MANAGE:
		//担保费
		case FEE_LOAN_GUARANTEE:
		//风险管理費
		case FEE_LOAN_RISK:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				type,
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId,
				null,loanFund.getAvailableAmount(),
				null));
			clientRecordDAO.create(new ClientFundRecord(account,
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				type,
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee,
				orderId,
				null,
				null));
		    }
		    break;
		default:
		//do nothing
	    }
	}
	if (userId == null) {
	    return;
	}
	UserFund investFund = fundDAO.find(userId);
	if (userId == null) {
	    logger.error("not find user fund for userId:{}", userId);
	    return;
	}
	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeToGuaranteeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    String orderId = entry.getValue().getLeft();
	    FundRecordType type = entry.getKey();
	    switch (entry.getKey()) {
		//贷款服务费
		case FEE_LOAN_SERVICE:
		//贷款管理费
		case FEE_LOAN_MANAGE:
		//担保费
		case FEE_LOAN_GUARANTEE:
		//风险管理費
		case FEE_LOAN_RISK:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//担保人人风险保证金记录
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.LOAN, loanId),
				type,
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.IN,
				fee,
				orderId,
				null,investFund.getAvailableAmount(),
				null));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void repayInvestRecord(String clientCode, String investUserId, String investId, BigDecimal repayAmount, String loanUserId, String loanId, Map<FundRecordType, BigDecimal> feeDetails, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	UserFund loanFund = fundDAO.find(loanUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "第" + period + "期还款";
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount,
		orderId, null,investFund.getAvailableAmount(), periodDesc));
	recordDAO.create(new FundLoanRepay(loanFund,
		new RealmEntity(Realm.LOAN, loanId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.OUT,
		repayAmount,
		orderId, null,loanFund.getAvailableAmount(), periodDesc));

	for (Map.Entry<FundRecordType, BigDecimal> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		/**
		 * 平台跟借款人收费项目
		 */
		case FEE_LOAN_INTEREST:
		case FEE_LOAN_OVERDUE:
		case FEE_LOAN_PENALTY:
		case FEE_LOAN_MANAGE:
		case FEE_ADVANCE_REPAY:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人支出
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,loanFund.getAvailableAmount(),
				periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getFeeAccount().getAccountId(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 平台跟投资人收费项目
		 */
		case FEE_INVEST_INTEREST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//投资人支出
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getFeeAccount().getAccountId(),
				new RealmEntity(Realm.INVEST, investId),
				investUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 借款人給投资人费用
		 */
		case FEE_LOAN_PENALTY_INVEST:
		case FEE_ADVANCE_REPAY_INVEST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人支出
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,loanFund.getAvailableAmount(),
				periodDesc));
			//投资人收入
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.IN,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void repayInvestRecord2(String clientCode, String investUserId, String investId, BigDecimal repayAmount, String loanUserId, String loanId,
	    Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeDetails, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	UserFund loanFund = fundDAO.find(loanUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "" + period;
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount,
		orderId, null,investFund.getAvailableAmount(), periodDesc));
	recordDAO.create(new FundLoanRepay(loanFund,
		new RealmEntity(Realm.LOAN, loanId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.OUT,
		repayAmount,
		orderId, null,loanFund.getAvailableAmount(), periodDesc));

	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		/**
		 * 平台跟借款人收费项目
		 */
		case FEE_LOAN_INTEREST:
		case FEE_LOAN_OVERDUE:
		case FEE_LOAN_PENALTY:
		case FEE_LOAN_MANAGE:
		case FEE_ADVANCE_REPAY:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人支出
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,loanFund.getAvailableAmount(),
				periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 平台跟投资人收费项目
		 */
		case FEE_INVEST_INTEREST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//投资人支出
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.INVEST, investId),
				investUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 借款人給投资人费用
		 */
		case FEE_LOAN_PENALTY_INVEST:
		case FEE_ADVANCE_REPAY_INVEST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人支出
			recordDAO.create(new FundFee(loanFund,
				new RealmEntity(Realm.LOAN, loanId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,loanFund.getAvailableAmount(),
				periodDesc));
			//投资人收入
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.IN,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void repayInvestRecord2Ump(String clientCode, String investUserId, String investId, BigDecimal repayAmount, String loanUserId, String loanId,
	    Map<FundRecordType, ImmutablePair<String, BigDecimal>> feeDetails, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	UserFund loanFund = fundDAO.find(loanUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "" + period;
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount,
		orderId, null,investFund.getAvailableAmount() ,periodDesc));
	//不记录还款者的记录，已经在其还款至标的时记录
//        recordDAO.create(new FundLoanRepay(loanFund,
//                                           new RealmEntity(Realm.LOAN, loanId),
//                                           FundRecordStatus.SUCCESSFUL,
//                                           FundRecordOperation.OUT,
//                                           repayAmount,
//                                           orderId, null, periodDesc));

	for (Map.Entry<FundRecordType, ImmutablePair<String, BigDecimal>> entry : feeDetails.entrySet()) {
	    BigDecimal fee = entry.getValue().getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	    switch (entry.getKey()) {
		/**
		 * 平台跟借款人收费项目
		 */
		case FEE_LOAN_INTEREST:
		case FEE_LOAN_OVERDUE:
		case FEE_LOAN_PENALTY:
		case FEE_LOAN_MANAGE:
		case FEE_ADVANCE_REPAY:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//不记录借款人的记录，已经在其还款至标的时记录
//                        //借款人支出
//                        recordDAO.create(new FundFee(loanFund,
//                                                     new RealmEntity(Realm.LOAN, loanId),
//                                                     entry.getKey(),
//                                                     FundRecordStatus.SUCCESSFUL,
//                                                     FundRecordOperation.OUT,
//                                                     fee,
//                                                     orderId, null,
//                                                     periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.LOAN, loanId),
				loanUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 平台跟投资人收费项目
		 */
		case FEE_INVEST_INTEREST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//投资人支出
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.OUT,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
			//商户收费记录
			clientRecordDAO.create(new ClientFundRecord(entry.getValue().getLeft(),
				new RealmEntity(Realm.INVEST, investId),
				investUserId,
				entry.getKey(),
				FundRecordOperation.IN,
				FundRecordStatus.SUCCESSFUL,
				fee, orderId, null, periodDesc));
		    }
		    break;
		/**
		 * 借款人給投资人费用
		 */
		case FEE_LOAN_PENALTY_INVEST:
		case FEE_ADVANCE_REPAY_INVEST:
		    //大于0.01元才收
		    if (fee.compareTo(BigDecimal.ZERO) == 1) {
			//借款人支出
			//不记录还款者的记录，已经在其还款至标的时记录
//                        recordDAO.create(new FundFee(loanFund,
//                                                     new RealmEntity(Realm.LOAN, loanId),
//                                                     entry.getKey(),
//                                                     FundRecordStatus.SUCCESSFUL,
//                                                     FundRecordOperation.OUT,
//                                                     fee,
//                                                     orderId, null,
//                                                     periodDesc));
			//投资人收入
			recordDAO.create(new FundFee(investFund,
				new RealmEntity(Realm.INVEST, investId),
				entry.getKey(),
				FundRecordStatus.SUCCESSFUL,
				FundRecordOperation.IN,
				fee,
				orderId, null,investFund.getAvailableAmount(),
				periodDesc));
		    }
		    break;
		default:
		//do nothing
	    }
	}

	// delete cache
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void disburseInvestRecord(String clientCode, String investUserId, String investId, BigDecimal repayAmount,
	    String loanId, BigDecimal investFee, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "" + period;
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount,
		orderId, null,investFund.getAvailableAmount(), periodDesc));
	//商户风险金账户垫付还款
	clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getGuaranteeAccount().getAccountId(),
		new RealmEntity(Realm.INVEST, investId),
		investUserId,
		FundRecordType.DISBURSE,
		FundRecordOperation.OUT,
		FundRecordStatus.SUCCESSFUL,
		repayAmount,
		orderId, null, periodDesc));

	//大于0.01元才收，垫付只记录投资人费用，不用记录借款人费用
	investFee = investFee.setScale(2, NumberConstant.ROUNDING_MODE);
	if (investFee.compareTo(BigDecimal.ZERO) == 1) {
	    //投资人利息管理费
	    recordDAO.create(new FundFee(investFund,
		    new RealmEntity(Realm.INVEST, investId),
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordStatus.SUCCESSFUL,
		    FundRecordOperation.OUT,
		    investFee,
		    orderId, null,investFund.getAvailableAmount(),
		    periodDesc));
	    //收费账户入账
	    clientRecordDAO.create(new ClientFundRecord(appBean.getPaymentConfig().getFeeAccount().getAccountId(),
		    new RealmEntity(Realm.INVEST, investId),
		    investUserId,
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordOperation.IN,
		    FundRecordStatus.SUCCESSFUL,
		    investFee,
		    orderId, null, periodDesc));
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void disburseInvestRecord2(String clientCode, String investUserId, String investId, ImmutablePair<String, BigDecimal> repayAmount,
	    String loanId, ImmutablePair<String, BigDecimal> investAmount, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "" + period;
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount.getRight(),
		orderId, null,investFund.getAvailableAmount(), periodDesc));
	//商户风险金账户垫付还款
	clientRecordDAO.create(new ClientFundRecord(repayAmount.getLeft(),
		new RealmEntity(Realm.INVEST, investId),
		investUserId,
		FundRecordType.DISBURSE,
		FundRecordOperation.OUT,
		FundRecordStatus.SUCCESSFUL,
		repayAmount.getRight(),
		orderId, null, periodDesc));

	//大于0.01元才收，垫付只记录投资人费用，不用记录借款人费用
	BigDecimal investFee = investAmount.getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	if (investFee.compareTo(BigDecimal.ZERO) == 1) {
	    //投资人利息管理费
	    recordDAO.create(new FundFee(investFund,
		    new RealmEntity(Realm.INVEST, investId),
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordStatus.SUCCESSFUL,
		    FundRecordOperation.OUT,
		    investFee,
		    orderId, null,investFund.getAvailableAmount(),
		    periodDesc));
	    //收费账户入账
	    clientRecordDAO.create(new ClientFundRecord(investAmount.getLeft(),
		    new RealmEntity(Realm.INVEST, investId),
		    investUserId,
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordOperation.IN,
		    FundRecordStatus.SUCCESSFUL,
		    investFee,
		    orderId, null, periodDesc));
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void disburseInvestRecord2Ump(String clientCode, String investUserId, String investId, ImmutablePair<String, BigDecimal> repayAmount,
	    String loanId, ImmutablePair<String, BigDecimal> investAmount, String orderId, int period) {
	appBean.checkClientCode(clientCode);
	UserFund investFund = fundDAO.find(investUserId);
	//不要更改periodDesc格式，查找还款的时候需要对照period
	String periodDesc = "" + period;
	recordDAO.create(new FundInvestRepay(investFund,
		new RealmEntity(Realm.INVEST, investId),
		FundRecordStatus.SUCCESSFUL,
		FundRecordOperation.IN,
		repayAmount.getRight(),
		orderId, null,investFund.getAvailableAmount(), periodDesc));

	//大于0.01元才收，垫付只记录投资人费用，不用记录借款人费用
	BigDecimal investFee = investAmount.getRight().setScale(2, NumberConstant.ROUNDING_MODE);
	if (investFee.compareTo(BigDecimal.ZERO) == 1) {
	    //投资人利息管理费
	    recordDAO.create(new FundFee(investFund,
		    new RealmEntity(Realm.INVEST, investId),
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordStatus.SUCCESSFUL,
		    FundRecordOperation.OUT,
		    investFee,
		    orderId, null,investFund.getAvailableAmount(),
		    periodDesc));
	    //收费账户入账
	    clientRecordDAO.create(new ClientFundRecord(investAmount.getLeft(),
		    new RealmEntity(Realm.INVEST, investId),
		    investUserId,
		    FundRecordType.FEE_INVEST_INTEREST,
		    FundRecordOperation.IN,
		    FundRecordStatus.SUCCESSFUL,
		    investFee,
		    orderId, null, periodDesc));
	}

	// delete cache
	appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void clientTransfer(String clientCode, String inAccount, BigDecimal amount, String outAccount, String orderId) {
	appBean.checkClientCode(clientCode);
	//转入记录
	clientRecordDAO.create(new ClientFundRecord(inAccount, null, null,
		FundRecordType.TRANSFER,
		FundRecordOperation.IN,
		FundRecordStatus.SUCCESSFUL,
		amount, orderId, null, null));
	//转出记录
	clientRecordDAO.create(new ClientFundRecord(outAccount, null, null,
		FundRecordType.TRANSFER,
		FundRecordOperation.OUT,
		FundRecordStatus.SUCCESSFUL,
		amount, orderId, null, null));
    }

    @Override
    public void userTransfer(String clientCode, String account, BigDecimal amount, String userId, boolean transferIn, String orderId, String description) {
	appBean.checkClientCode(clientCode);
	//商户记录
	clientRecordDAO.create(new ClientFundRecord(account, null,
		userId,
		FundRecordType.TRANSFER,
		transferIn ? FundRecordOperation.OUT : FundRecordOperation.IN,
		FundRecordStatus.SUCCESSFUL,
		amount, orderId, null, description));
	//用户记录
	UserFund fund = fundDAO.find(userId);
	recordDAO.create(new FundTransfer(fund, null,
		FundRecordStatus.SUCCESSFUL,
		transferIn ? FundRecordOperation.IN : FundRecordOperation.OUT,
		amount,
		orderId,
		null,fund.getAvailableAmount(), description));

	// delete cache
	appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public void userReward(String clientCode, String account, BigDecimal amount, String userId, FundRecordType type, com.creditcloud.model.misc.RealmEntity target, String orderId) {
	appBean.checkClientCode(clientCode);
	//商户记录
	clientRecordDAO.create(new ClientFundRecord(account,
		com.creditcloud.common.utils.DTOUtils.convertRealmEntity(target),
		userId,
		type,
		FundRecordOperation.OUT,
		FundRecordStatus.SUCCESSFUL,
		amount, orderId, null, null));
	//用户记录
	UserFund fund = fundDAO.find(userId);
	recordDAO.create(new FundReward(fund, null,
		com.creditcloud.common.utils.DTOUtils.convertRealmEntity(target),
		type,
		FundRecordStatus.SUCCESSFUL,
		amount, orderId, null,fund.getAvailableAmount(), account));

	// delete cache
	appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_FUND);
    }

    @Override
    public boolean checkReward(String clientCode, com.creditcloud.model.misc.RealmEntity target, FundRecordType rewardType, FundRecordStatus status) {
	appBean.checkClientCode(clientCode);
	int count = recordDAO.countReward(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(target), rewardType, status);
	return count > 0;
    }

    @Override
    public boolean checkFss(String clientCode, String userId, String orderId) {
	appBean.checkClientCode(clientCode);
	return recordDAO.getFundFSSByOrderId(userId, orderId) != null;
    }

    /**
     * conversion from model to entity for FundRecord and its sub classes
     *
     * @param fund
     * @param account
     * @param record
     * @return
     */
    //TODO need refactor
    @Deprecated
    private static FundRecord convertFundRecord(UserFund fund, FundAccount account, com.creditcloud.fund.model.record.FundRecord record) {
	FundRecord result = null;
	if (record != null) {
	    switch (record.getType()) {
		case DEPOSIT:
		    result = new FundDeposit(fund,
			    account,
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case WITHDRAW:
		    result = new FundWithdraw(fund,
			    account,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case INVEST:
		    result = new FundInvest(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case INVEST_REPAY:
		    result = new FundInvestRepay(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case LOAN:
		    result = new FundLoan(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case LOAN_REPAY:
		    result = new FundLoanRepay(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case TRANSFER:
		    result = new FundTransfer(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case FSS:
		    result = new FundFSS(fund,
			    record.getType(),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    fund.getAvailableAmount());
		    break;
		case CREDIT_ASSIGN:
		    result = new FundCreditAssign(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    break;
		case FEE_WITHDRAW:
		case FEE_AUTHENTICATE:
		case FEE_INVEST_INTEREST:
		case FEE_LOAN_SERVICE:
		case FEE_LOAN_MANAGE:
		case FEE_LOAN_INTEREST:
		case FEE_LOAN_VISIT:
		case FEE_LOAN_GUARANTEE:
		case FEE_LOAN_RISK:
		case FEE_LOAN_OVERDUE:
		case FEE_LOAN_PENALTY:
		case FEE_LOAN_PENALTY_INVEST:
		case FEE_DEPOSIT:
		case FEE_ADVANCE_REPAY:
		case FEE_ADVANCE_REPAY_INVEST:
		case FEE_CREDIT_ASSIGN:
		case FEE_BIND_CARD:
		    result = new FundFee(fund,
			    convertRealmEntity(record.getEntity()),
			    record.getType(),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		case REWARD_REGISTER:
		case REWARD_REFERRAL:
		case REWARD_INVEST:
		case REWARD_DEPOSIT:
		    result = new FundReward(fund,
			    account,
			    convertRealmEntity(record.getEntity()),
			    record.getType(),
			    record.getStatus(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
		    break;
		default:
		    result = new FundRecord(fund,
			    account,
			    convertRealmEntity(record.getEntity()),
			    record.getType(),
			    record.getStatus(),
			    record.getOperation(),
			    record.getAmount(),
			    record.getOrderId(),
			    record.getTransactionId(),
			    fund.getAvailableAmount(),
			    record.getDescription());
		    result.setId(record.getId());
	    }
	}

	return result;
    }

    @Override
    public boolean finishProjectLoan(String clientCode,
	    List<Pair<String, String>> investList,
	    String loanId,
	    String loanUserId,
	    BigDecimal loanAmount,
	    String orderId) {
	appBean.checkClientCode(clientCode);
	/**
	 * modify for invest
	 */
	for (Pair<String, String> invest : investList) {
	    //check unsettled record
	    String investUserId = invest.getLeft();
	    String investId = invest.getRight();
	    FundRecord investRecord = recordDAO.getFundInvestByOperationAndStatus(investUserId,
		    new RealmEntity(Realm.FUNDINGINVEST, investId),
		    FundRecordOperation.OUT,
		    FundRecordStatus.INITIALIZED);
	    if (investRecord == null) {
		logger.debug("FundInvest record not exist.[investUserId={}][investId={}]", investUserId, investId);
		continue;
	    }

	    //check freeze record
	    FundRecord freezeRecord = recordDAO.getFundInvestByOperationAndStatus(investUserId,
		    new RealmEntity(Realm.FUNDINGINVEST, investId),
		    FundRecordOperation.FREEZE,
		    FundRecordStatus.SUCCESSFUL);
	    if (freezeRecord == null) {
		logger.warn("fail to find freeze record for invest {}.", investId);
		continue;
	    }

	    //check release record
	    FundInvest releaseRecord = recordDAO.getFundInvestByOperationAndStatus(investUserId,
		    new RealmEntity(Realm.FUNDINGINVEST, investId),
		    FundRecordOperation.RELEASE,
		    FundRecordStatus.SUCCESSFUL);

	    if (releaseRecord == null) {
		//TODO for ump just generate release record
		UserFund investUserFund=fundDAO.find(investUserId);
		releaseRecord = new FundInvest(investUserFund,
			new RealmEntity(Realm.FUNDINGINVEST, investId),
			FundRecordStatus.SUCCESSFUL,
			FundRecordOperation.RELEASE,
			freezeRecord.getAmount(),
			//TODO just forge this orderId, this is ok as ump has no freeze/release 
			appBean.orderId(), null, investUserFund.getAvailableAmount(), null);
		recordDAO.create(releaseRecord);

		fundDAO.directRelease(investUserId, freezeRecord.getAmount());

		//do not add new successful record, just update original record
		investRecord.setStatus(FundRecordStatus.SUCCESSFUL);
		recordDAO.edit(investRecord);

		// delete cache
		appBean.deleteCache(investUserId, CacheConstant.KEY_PREFIX_USER_FUND);
	    }
	}

	/**
	 * modify for loan
	 */
	FundRecord loanRecord = recordDAO.getFundLoanByOperationAndStatus(loanUserId,
		new RealmEntity(Realm.PROJECTLOAN, loanId),
		FundRecordOperation.IN,
		FundRecordStatus.INITIALIZED);
	if (loanRecord == null) {
	    logger.debug("fail to add FundRecord for loan {}, userId {}, orderId {}", loanId, loanUserId, orderId);
	    return false;
	}

	loanRecord.setStatus(FundRecordStatus.SUCCESSFUL);
	recordDAO.edit(loanRecord);
	//add for available
	fundDAO.available(loanUserId, loanAmount, true);
	logger.info("set status for ProjectLoan {} to SUCCESSFUL", loanId);

	// delete cache
	appBean.deleteCache(loanUserId, CacheConstant.KEY_PREFIX_USER_FUND);

	return true;
    }
}
