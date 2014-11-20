/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.fund.entities.TransferRequest;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class TransferRequestDAO extends AbstractDAO<TransferRequest> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public TransferRequestDAO() {
        super(TransferRequest.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 根据状态列出所有TransferRequest
     *
     * @param status
     * @return
     */
    public List<TransferRequest> listByStatus(FundRecordStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }

        return getEntityManager()
                .createNamedQuery("TransferRequest.listByStatus", TransferRequest.class)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();

    }

    /**
     * 审核转账申请
     *
     * @param auditEmployee
     * @param requestId
     * @param orderId
     * @param status
     */
    public void auditRequest(String auditEmployee, String requestId, String orderId, FundRecordStatus status) {
        TransferRequest request = find(requestId);
        if (request != null) {
            request.setAuditEmployee(auditEmployee);
            request.setStatus(status);
            request.setOrderId(orderId);
            edit(request);
        }
    }

    /**
     * 只能删除待审核状态的申请,成功和驳回状态的需要保留对账用
     *
     * @param requestId
     */
    public void deleteRequest(String requestId) {
        TransferRequest request = find(requestId);
        if (request != null && request.getStatus().equals(FundRecordStatus.AUDITING)) {
            removeById(requestId);
            return;
        }
        if (request != null) {
            logger.debug("can not remove request {} with status {}, only AUDITING request is allowed to remove", requestId, request.getStatus());
        }
    }
}
