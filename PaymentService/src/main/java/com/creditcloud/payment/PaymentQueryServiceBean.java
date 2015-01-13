package com.creditcloud.payment;

import com.creditcloud.payment.api.PaymentQueryService;
import com.creditcloud.payment.entities.dao.AssignReconciliationDAO;
import com.creditcloud.payment.model.chinapnr.enums.CmdIdType;
import com.creditcloud.payment.model.chinapnr.enums.FeeObj;
import com.creditcloud.payment.model.chinapnr.enums.QueryTransType;
import com.creditcloud.payment.model.chinapnr.reconciliation.CashReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.CashReconciliationResponse2;
import com.creditcloud.payment.model.chinapnr.reconciliation.CashReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.CashReconciliationResult2;
import com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation;
import com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliationRequest;
import com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.FssPurchaseReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.FssPurchaseReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.FssRedeemReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.FssRedeemReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.ReconciliationRequest;
import com.creditcloud.payment.model.chinapnr.reconciliation.ReconciliationRequest2;
import com.creditcloud.payment.model.chinapnr.reconciliation.SaveReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.SaveReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.TenderReconciliationRequest;
import com.creditcloud.payment.model.chinapnr.reconciliation.TenderReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.TenderReconciliationResult;
import com.creditcloud.payment.model.chinapnr.reconciliation.TransferReconciliationResponse;
import com.creditcloud.payment.model.chinapnr.reconciliation.TransferReconciliationResult;
import com.creditcloud.payment.utils.DTOUtils;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.SslConfigurator;
import org.joda.time.LocalDate;

@Stateless
@Remote
public class PaymentQueryServiceBean
        extends BasePaymentBean
        implements PaymentQueryService {

    @EJB
    AssignReconciliationDAO assignReconciliationDAO;

    @PostConstruct
    void init() {
        this.paymentConfig = this.appBean.getPaymentConfig();

        SslConfigurator sslConfig = SslConfigurator.newInstance();
        Client client = ClientBuilder.newBuilder().sslContext(sslConfig.createSSLContext()).build();
        try {
            this.target = client.target(this.paymentConfig.getUrl().toURI()).path(this.paymentConfig.getPath());
        } catch (URISyntaxException ex) {
            this.logger.error("PaymentConfig may have error.\n{}", this.paymentConfig);
        }
    }

    public FssPurchaseReconciliationResult fssPurchaseReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest request = new ReconciliationRequest(CmdIdType.FssPurchaseReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        FssPurchaseReconciliationResponse response = (FssPurchaseReconciliationResponse) getResponse(request, FssPurchaseReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            FssPurchaseReconciliationResult result = new FssPurchaseReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getFssReconciliationList());

            return result;
        }
        return null;
    }

    public FssRedeemReconciliationResult fssRedeemReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest request = new ReconciliationRequest(CmdIdType.FssRedeemReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        FssRedeemReconciliationResponse response = (FssRedeemReconciliationResponse) getResponse(request, FssRedeemReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            FssRedeemReconciliationResult result = new FssRedeemReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getFssReconciliationList());

            return result;
        }
        return null;
    }

    public CashReconciliationResult2 cashReconciliation2(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest2 request = new ReconciliationRequest2(CmdIdType.CashReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        CashReconciliationResponse2 response = (CashReconciliationResponse2) getResponse(request, CashReconciliationResponse2.class);
        if (verifyResponse(clientCode, response) == 0) {
            CashReconciliationResult2 result = new CashReconciliationResult2(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getCashReconciliationDtoList(), FeeObj.valueOf(response.getFeeObj()));

            return result;
        }
        return null;
    }

    public SaveReconciliationResult saveReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest request = new ReconciliationRequest(CmdIdType.SaveReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        SaveReconciliationResponse response = (SaveReconciliationResponse) getResponse(request, SaveReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            SaveReconciliationResult result = new SaveReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getSaveReconciliationDtoList());

            return result;
        }
        return null;
    }

    public CashReconciliationResult cashReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest request = new ReconciliationRequest(CmdIdType.CashReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        CashReconciliationResponse response = (CashReconciliationResponse) getResponse(request, CashReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            CashReconciliationResult result = new CashReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getCashReconciliationDtoList());

            return result;
        }
        return null;
    }

    public TransferReconciliationResult transferReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize) {
        this.appBean.checkClientCode(clientCode);
        ReconciliationRequest request = new ReconciliationRequest(CmdIdType.TrfReconciliation, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        TransferReconciliationResponse response = (TransferReconciliationResponse) getResponse(request, TransferReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            TransferReconciliationResult result = new TransferReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), response.getTrfReconciliationDtoList());

            return result;
        }
        return null;
    }

    public CreditAssignReconciliationResult creditAssignReconciliation(String clientCode, CreditAssignReconciliationRequest request) {
        this.appBean.checkClientCode(clientCode);
        long startTime = System.currentTimeMillis();
        List<CreditAssignReconciliation> result = Collections.EMPTY_LIST;

        request.setChkValue(getChkValue(clientCode, request));
        CreditAssignReconciliationResponse response = (CreditAssignReconciliationResponse) getResponse(request, CreditAssignReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            result = response.getBidCaReconciliationList();
            if (!result.isEmpty()) {
                cacheAssignReconciliation(result);
            }
            return new CreditAssignReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), result);
        }
        this.logger.debug("fetch credit assign reconciliations from chinapnr.[items={}][time={}ms]", Integer.valueOf(result.size()), Long.valueOf(System.currentTimeMillis() - startTime));
        return null;
    }

    @Asynchronous
    private void cacheAssignReconciliation(List<CreditAssignReconciliation> result) {
        long startTime = System.currentTimeMillis();
        this.assignReconciliationDAO.addAll(DTOUtils.convertCreditAssignReconciliation(result));
        this.logger.debug("cache credit assign reconciliations.[items={}][time={}ms]", Integer.valueOf(result.size()), Long.valueOf(System.currentTimeMillis() - startTime));
    }

    public TenderReconciliationResult tenderReconciliation(String clientCode, LocalDate beginDate, LocalDate endDate, int pageNum, int pageSize, QueryTransType type) {
        this.appBean.checkClientCode(clientCode);
        TenderReconciliationRequest request = new TenderReconciliationRequest(type, this.paymentConfig.getMerCustId(), beginDate.toString("yyyyMMdd"), endDate.toString("yyyyMMdd"), pageNum, pageSize);

        request.setChkValue(getChkValue(clientCode, request));
        TenderReconciliationResponse response = (TenderReconciliationResponse) getResponse(request, TenderReconciliationResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            TenderReconciliationResult result = new TenderReconciliationResult(response.getBeginDate(), response.getEndDate(), response.getPageNum(), response.getPageSize(), response.getTotalItems(), QueryTransType.valueOf(response.getQueryTransType()), response.getReconciliationDtoList());

            return result;
        }
        return null;
    }
}
