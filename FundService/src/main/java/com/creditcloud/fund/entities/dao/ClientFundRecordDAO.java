/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.fund.entities.ClientFundRecord;
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
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class ClientFundRecordDAO extends AbstractDAO<ClientFundRecord> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public ClientFundRecordDAO() {
        super(ClientFundRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ClientFundRecord create(ClientFundRecord record) {
        try {
            getValidatorWrapper().tryValidate(record);
            return super.create(record);
        } catch (InvalidException ex) {
            logger.warn("client fund record  {} is not valid!", record.toString(), ex);
        } catch (ConstraintViolationException ex) {
            logger.warn("Add new client fund record  failed!!!\n{}", record.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new client fund record  failed!!!\n{}", record.toString(), ex);
        }
        return null;
    }

    public int countByAccountAndType(List<String> accountList, Date from, Date to, FundRecordType... type) {
        if (type == null || type.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("ClientFundRecord.countByAccountAndType", Long.class)
                .setParameter("accountList", accountList)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", Arrays.asList(type)).getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<ClientFundRecord> listByAccountAndType(List<String> accountList, Date from, Date to, PageInfo info, FundRecordType... type) {
        if (type == null || type.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("ClientFundRecord.listByAccountAndType", ClientFundRecord.class)
                .setParameter("accountList", accountList)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("typeList", Arrays.asList(type))
                .setFirstResult(info.getOffset())
                .setMaxResults(info.getSize());
        int totalSize = countByAccountAndType(accountList, from, to, type);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    public ClientFundRecord getByAccountAndTypeAndOrderId(String account, String orderId, FundRecordType type) {
        try {
            return getEntityManager()
                    .createNamedQuery("ClientFundRecord.getByAccountAndTypeAndOrderId", ClientFundRecord.class)
                    .setParameter("account", account)
                    .setParameter("orderId", orderId)
                    .setParameter("type", type)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
            logger.debug("fail to find ClientFundRecord by account {}, orderId {}, type {}", account, orderId, type);
            return null;
        }
    }

    public ClientFundRecord getWithdrawByOperationAndStatus(String withdrawId, FundRecordOperation operation, FundRecordStatus status) {
        return getByEntity(new RealmEntity(Realm.WITHDRAW, withdrawId),
                           FundRecordType.WITHDRAW,
                           operation,
                           status);
    }

    private ClientFundRecord getByEntity(RealmEntity entity, FundRecordType type, FundRecordOperation operation, FundRecordStatus status) {
        try {
            return getEntityManager()
                    .createNamedQuery("ClientFundRecord.getByEntity", ClientFundRecord.class)
                    .setParameter("entity", entity)
                    .setParameter("type", type)
                    .setParameter("status", status)
                    .setParameter("operation", operation)
                    .getSingleResult();
        } catch (NoResultException ex) {
            //do nothing
            logger.debug("fail to find ClientFundRecord.[realm={}][entityId={}][type={}][operation={}][status={}]",
                         entity == null ? "" : entity.getRealm(), entity == null ? "" : entity.getEntityId(), type, operation, status);
            return null;
        }
    }

    public List<ClientFundRecord> listWithdrawRequest(FundRecordStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("ClientFundRecord.listWithdrawRequest", ClientFundRecord.class)
                .setParameter("type", FundRecordType.WITHDRAW)
                .setParameter("operation", FundRecordOperation.OUT)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        
    }
}
