/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund;

import com.creditcloud.fund.api.TransferRequestService;
import com.creditcloud.fund.entities.dao.TransferRequestDAO;
import com.creditcloud.fund.model.TransferRequest;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.utils.DTOUtils;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class TransferRequestServiceBean implements TransferRequestService {

    @EJB
    TransferRequestDAO requestDAO;

    @Override
    public TransferRequest addNew(TransferRequest request) {
        return DTOUtils.getTransferRequest(requestDAO.create(DTOUtils.convertTransferRequest(request)));
    }

    @Override
    public TransferRequest find(String id) {
        return DTOUtils.getTransferRequest(requestDAO.find(id));
    }

    @Override
    public List<TransferRequest> listByStatus(FundRecordStatus... status) {
        return DTOUtils.getTransferRequest(requestDAO.listByStatus(status));
    }

    @Override
    public void auditRequest(String auditEmployee, String requestId, String orderId, FundRecordStatus status) {
        requestDAO.auditRequest(auditEmployee, requestId, orderId, status);
    }

    @Override
    public void deleteRequest(String requestId) {
        requestDAO.deleteRequest(requestId);
    }

}
