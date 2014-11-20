/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.fund.entities.record.FundCreditAssign;
import com.creditcloud.fund.entities.record.FundDeposit;
import com.creditcloud.fund.entities.record.FundFSS;
import com.creditcloud.fund.entities.record.FundFee;
import com.creditcloud.fund.entities.record.FundInvest;
import com.creditcloud.fund.entities.record.FundInvestRepay;
import com.creditcloud.fund.entities.record.FundLoan;
import com.creditcloud.fund.entities.record.FundRecord;
import com.creditcloud.fund.entities.record.FundTransfer;
import com.creditcloud.fund.entities.record.FundWithdraw;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.PagedResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class FundRecordDAO extends AbstractDAO<FundRecord> {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public FundRecordDAO() {
        super(FundRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FundRecord create(FundRecord record) {
        try {
            getValidatorWrapper().tryValidate(record);
            return super.create(record);
        } catch (InvalidException ex) {
            logger.warn("fund record  {} is not valid!", record.toString(), ex);
        } catch (ConstraintViolationException ex) {
            logger.warn("Add new fund record  failed!!!\n{}", record.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new fund record  failed!!!\n{}", record.toString(), ex);
        }
        return null;
    }

    @Override
    public void edit(FundRecord record) {
        super.edit(record);
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
    }

    /**
     * 根据OrderId获取FundRecord
     *
     * @param userId
     * @param type
     * @param orderId
     * @return
     */
    public FundRecord getByOrderId(String userId, FundRecordType type, String orderId) {
        FundRecord result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundRecord.getByOrderId", FundRecord.class)
                    .setParameter("userId", userId)
                    .setParameter("type", type)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            //do nothing is OK
        }
        return result;
    }

    /**
     * 根据OrderId和Fund类型获取FundRecord
     */
    public FundRecord getByOrderIdAndType(FundRecordType type, String orderId) {
        FundRecord result = null;
        List<FundRecord> results = getEntityManager()
                .createNamedQuery("FundRecord.getByOrderIdAndType", FundRecord.class)
                .setParameter("type", type)
                .setParameter("orderId", orderId)
                .getResultList();
        if (results != null && results.size() > 0) {
            result = results.get(0);
        }

        return result;
    }

    public int countByTypes(FundRecordType[] recordType) {
        if (recordType == null || recordType.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("FundRecord.countByType", Long.class)
                .setParameter("typeList", Arrays.asList(recordType))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<FundRecord> listByTypes(PageInfo pageInfo, FundRecordType[] recordType) {
        if (recordType == null || recordType.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<FundRecord> result = getEntityManager().createNamedQuery("FundRecord.listByType", FundRecord.class)
                .setParameter("typeList", Arrays.asList(recordType))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        int totalSize = countByTypes(recordType);
        return new PagedResult<>(result, totalSize);
    }

    public int countByUserAndTypeAndOperationAndStatus(String userId, List<FundRecordType> typeList, List<FundRecordOperation> operationList, List<FundRecordStatus> statusList) {
        if (typeList == null || typeList.isEmpty()) {
            return 0;
        }
        if (operationList == null || operationList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("FundRecord.countByUserAndTypeAndOperationAndStatus", Long.class)
                .setParameter("userId", userId)
                .setParameter("typeList", typeList)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<FundRecord> listByUserAndTypeAndOperationAndStatus(String userId, PageInfo pageInfo,
                                                                          List<FundRecordType> typeList, List<FundRecordOperation> operationList, List<FundRecordStatus> statusList) {
        if (typeList == null || typeList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        if (operationList == null || operationList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        //get results
        List<FundRecord> result = getEntityManager()
                .createNamedQuery("FundRecord.listByUserAndTypeAndOperationAndStatus", FundRecord.class)
                .setParameter("userId", userId)
                .setParameter("typeList", typeList)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        //get total size
        int totalSize = countByUserAndTypeAndOperationAndStatus(userId, typeList, operationList, statusList);
        return new PagedResult<>(result, totalSize);
    }

    /**
     * 获取特定类型的FundWithdraw
     *
     * @param status
     * @return
     */
    public List<FundWithdraw> listWithdrawByStatus(FundRecordStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("FundWithdraw.listByStatus", FundWithdraw.class)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
    }

    /**
     * 在给定时间范围内获取特定类型的FundWithdraw
     *
     * @param from
     * @param to
     * @param pageInfo
     * @param operationList
     * @param statusList
     * @return
     */
    public List<FundWithdraw> listWithdrawByStatus(Date from,
                                                   Date to,
                                                   PageInfo pageInfo,
                                                   List<FundRecordOperation> operationList,
                                                   List<FundRecordStatus> statusList) {
        if (operationList == null || operationList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (statusList == null || statusList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("FundWithdraw.listByOperationAndStatusInRange", FundWithdraw.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
    }

    public List<FundDeposit> listDepositByStatus(Date from,
                                                 Date to,
                                                 PageInfo pageInfo,
                                                 List<FundRecordOperation> operationList,
                                                 List<FundRecordStatus> statusList) {
        if (operationList == null || operationList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (statusList == null || statusList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("FundDeposit.listByOperationAndStatusInRange", FundDeposit.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
    }

    /**
     * 根据OrderId获取唯一的FundDeposit
     *
     * @param userId
     * @param orderId
     * @return
     */
    public FundDeposit getFundDepositByOrderId(String userId, String orderId) {
        FundDeposit result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundDeposit.getByOrderId", FundDeposit.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundDeposit with orderId: {}", orderId);
        }
        return result;
    }

    public FundWithdraw getFundWithdrawByOrderId(String userId, String orderId) {
        FundWithdraw result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundWithdraw.getByOrderId", FundWithdraw.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundWithdraw with orderId: {}", orderId);
        }
        return result;
    }

    public FundFee getFundFeeByOrderId(String userId, String orderId) {
        FundFee result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundFee.getByOrderId", FundFee.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundFee with orderId: {}", orderId);
        }
        return result;
    }

    public FundInvest getFundInvestByOrderId(String userId, String orderId) {
        FundInvest result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundInvest.getByOrderId", FundInvest.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundInvest with orderId: {}", orderId);
        }
        return result;

    }

    public FundTransfer getFundTransferByOrderId(String userId, String orderId) {
        FundTransfer result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundTransfer.getByOrderId", FundTransfer.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundTransfer with orderId: {}", orderId);
        }
        return result;
    }

    public FundFSS getFundFSSByOrderId(String userId, String orderId) {
        FundFSS result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundFSS.getByOrderId", FundFSS.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundFSS with orderId: {}", orderId);
        }
        return result;
    }

    public FundCreditAssign getFundCreditAssignByOrderId(String userId, String orderId) {
        FundCreditAssign result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundCreditAssign.getByOrderId", FundCreditAssign.class)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundCreditAssign with userId {}, orderId: {}", userId, orderId);
        }
        return result;
    }

    public FundInvest getFundInvestByOperationAndStatus(String userId,
                                                        RealmEntity entity,
                                                        FundRecordOperation operation,
                                                        FundRecordStatus status) {
        FundInvest result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundInvest.getByOperationAndStatus", FundInvest.class)
                    .setParameter("userId", userId)
                    .setParameter("realm", entity.getRealm())
                    .setParameter("entityId", entity.getEntityId())
                    .setParameter("operation", operation)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundInvest.[userId={}][entity={}][operation={}][status={}]", userId, entity, operation, status);
        }
        return result;
    }

    public FundLoan getFundLoanByOperationAndStatus(String userId,
                                                    RealmEntity entity,
                                                    FundRecordOperation operation,
                                                    FundRecordStatus status) {
        try {
            return getEntityManager()
                    .createNamedQuery("FundLoan.getByOperationAndStatus", FundLoan.class)
                    .setParameter("userId", userId)
                    .setParameter("entity", entity)
                    .setParameter("operation", operation)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundLoan.[userId={}][entity={}][operation={}][status={}]", userId, entity, operation, status);
            return null;
        }
    }

    public FundCreditAssign getFundCreditAssignByOperationAndStatus(String userId,
                                                                    String investId,
                                                                    FundRecordOperation operation,
                                                                    FundRecordStatus status) {
        FundCreditAssign result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundCreditAssign.getByOperationAndStatus", FundCreditAssign.class)
                    .setParameter("userId", userId)
                    .setParameter("realm", Realm.INVEST)
                    .setParameter("entityId", investId)
                    .setParameter("operation", operation)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundCreditAssign.[userId={}][entityId={}][operation={}][status={}]", userId, investId, operation, status);
        }
        return result;
    }

    public FundWithdraw getWithdrawByOperationAndStatus(String userId,
                                                        String withdrawId,
                                                        FundRecordOperation operation,
                                                        FundRecordStatus status) {
        FundWithdraw result = null;
        try {
            result = getEntityManager()
                    .createNamedQuery("FundWithdraw.getByOperationAndStatus", FundWithdraw.class)
                    .setParameter("userId", userId)
                    .setParameter("realm", Realm.WITHDRAW)
                    .setParameter("entityId", withdrawId)
                    .setParameter("operation", operation)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundWithdraw.[userId={}][entityId={}][operation={}][status={}]", userId, withdrawId, operation, status);
        }
        return result;
    }

    public FundInvestRepay getInvestRepayByOperationAndStatus(String userId,
                                                              String investId,
                                                              FundRecordStatus status,
                                                              FundRecordOperation operation,
                                                              int period) {
        try {
            return getEntityManager()
                    .createNamedQuery("FundInvestRepay.getByOperationAndStatus", FundInvestRepay.class)
                    .setParameter("userId", userId)
                    .setParameter("entity", new RealmEntity(Realm.INVEST, investId))
                    .setParameter("operation", operation)
                    .setParameter("status", status)
                    .setParameter("period", period + "")
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.error("Can't find FundInvestRepay.[userId={}][investId={}][operation={}][status={}][period={}]", userId, investId, operation, status, period);
            return null;
        }
    }

    public int countByUser(String userId, Date from, Date to, FundRecordType... type) {
        if (type == null || type.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("FundRecord.countByUserAndTypeAndTime", Long.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", Arrays.asList(type))
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<FundRecord> listByUser(String userId, Date from, Date to, PageInfo pageInfo, FundRecordType... type) {
        if (type == null || type.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<FundRecord> result = getEntityManager()
                .createNamedQuery("FundRecord.listByUserAndTypeAndTime", FundRecord.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", Arrays.asList(type))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();

        int totalSize = countByUser(userId, from, to, type);
        return new PagedResult<>(result, totalSize);
    }

    public int countByUserAndTypeAndOperationAndStatusAndTime(String userId, Date from, Date to,
                                                              List<FundRecordType> typeList, List<FundRecordOperation> operationList, List<FundRecordStatus> statusList) {
        if (typeList == null || typeList.isEmpty()) {
            return 0;
        }
        if (operationList == null || operationList.isEmpty()) {
            return 0;
        }
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("FundRecord.countByUserAndTypeAndOperationAndStatusAndTime", Long.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", typeList)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<FundRecord> listByUserAndTypeAndOperationAndStatusAndTime(String userId, Date from, Date to, PageInfo pageInfo, List<FundRecordType> typeList, List<FundRecordOperation> operationList, List<FundRecordStatus> statusList) {
        if (typeList == null || typeList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        if (operationList == null || operationList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        if (statusList == null || statusList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("FundRecord.listByUserAndTypeAndOperationAndStatusAndTime", FundRecord.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", typeList)
                .setParameter("operationList", operationList)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = countByUserAndTypeAndOperationAndStatusAndTime(userId, from, to, typeList, operationList, statusList);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    public int countReward(RealmEntity target, FundRecordType type, FundRecordStatus status) {
        Long result = getEntityManager()
                .createNamedQuery("FundReward.countByTypeAndStatus", Long.class)
                .setParameter("target", target)
                .setParameter("type", type)
                .setParameter("status", status)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public boolean markStatus(FundRecordStatus status, String id) {
        int result = getEntityManager()
                .createNamedQuery("FundRecord.markStatus")
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
        if (appBean.isEnableManualFlush()) {
            getEntityManager().flush();
        }
        return result > 0;
    }
}
