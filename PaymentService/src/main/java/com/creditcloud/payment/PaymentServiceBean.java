package com.creditcloud.payment;

import com.creditcloud.common.utils.NumberUtils;
import com.creditcloud.config.SubAccount;
import com.creditcloud.model.enums.TransStat;
import com.creditcloud.model.enums.misc.Bank;
import com.creditcloud.model.enums.misc.City;
import com.creditcloud.model.enums.misc.Province;
import com.creditcloud.payment.api.PaymentService;
import com.creditcloud.payment.entities.dao.FssAccountDAO;
import com.creditcloud.payment.entities.dao.PaymentAccountDAO;
import com.creditcloud.payment.model.CashAuditResult;
import com.creditcloud.payment.model.CorpRegisterResult;
import com.creditcloud.payment.model.FreezeResult;
import com.creditcloud.payment.model.FssAccount;
import com.creditcloud.payment.model.FssProductInfo;
import com.creditcloud.payment.model.FssStats;
import com.creditcloud.payment.model.LoanResult;
import com.creditcloud.payment.model.MerCashResult;
import com.creditcloud.payment.model.PaymentResult;
import com.creditcloud.payment.model.PnRConstant;
import com.creditcloud.payment.model.TenderResult;
import com.creditcloud.payment.model.TransStatResult;
import com.creditcloud.payment.model.TransferResult;
import com.creditcloud.payment.model.UnFreezeResult;
import com.creditcloud.payment.model.UserBalanceResult;
import com.creditcloud.payment.model.chinapnr.BgBindCardRequest;
import com.creditcloud.payment.model.chinapnr.BgBindCardResponse;
import com.creditcloud.payment.model.chinapnr.BgRegisterRequest;
import com.creditcloud.payment.model.chinapnr.BgRegisterResponse;
import com.creditcloud.payment.model.chinapnr.CashAuditRequest;
import com.creditcloud.payment.model.chinapnr.CashAuditResponse;
import com.creditcloud.payment.model.chinapnr.DelCardRequest;
import com.creditcloud.payment.model.chinapnr.DelCardResponse;
import com.creditcloud.payment.model.chinapnr.MerCashRequest;
import com.creditcloud.payment.model.chinapnr.MerCashResponse;
import com.creditcloud.payment.model.chinapnr.PosWhSaveRequest;
import com.creditcloud.payment.model.chinapnr.PosWhSaveResponse;
import com.creditcloud.payment.model.chinapnr.UserFreezeRequest;
import com.creditcloud.payment.model.chinapnr.UserFreezeResponse;
import com.creditcloud.payment.model.chinapnr.UserUnFreezeRequest;
import com.creditcloud.payment.model.chinapnr.UserUnFreezeResponse;
import com.creditcloud.payment.model.chinapnr.UsrAcctPayRequest;
import com.creditcloud.payment.model.chinapnr.UsrAcctPayResponse;
import com.creditcloud.payment.model.chinapnr.base.BaseRequest;
import com.creditcloud.payment.model.chinapnr.base.BaseResponse;
import com.creditcloud.payment.model.chinapnr.base.UserRequest;
import com.creditcloud.payment.model.chinapnr.enums.AcctType;
import com.creditcloud.payment.model.chinapnr.enums.AuditFlag;
import com.creditcloud.payment.model.chinapnr.enums.AuditStat;
import com.creditcloud.payment.model.chinapnr.enums.CmdIdType;
import com.creditcloud.payment.model.chinapnr.enums.FeeObjFlag;
import com.creditcloud.payment.model.chinapnr.enums.IsDefault;
import com.creditcloud.payment.model.chinapnr.enums.IsFreeze;
import com.creditcloud.payment.model.chinapnr.enums.IsUnFreeze;
import com.creditcloud.payment.model.chinapnr.enums.QueryTransType;
import com.creditcloud.payment.model.chinapnr.query.AccountDetail;
import com.creditcloud.payment.model.chinapnr.query.AcctsQueryRequest;
import com.creditcloud.payment.model.chinapnr.query.AcctsQueryResponse;
import com.creditcloud.payment.model.chinapnr.query.BalanceQueryResponse;
import com.creditcloud.payment.model.chinapnr.query.CardInfo;
import com.creditcloud.payment.model.chinapnr.query.CorpRegisterQueryRequest;
import com.creditcloud.payment.model.chinapnr.query.CorpRegisterQueryResponse;
import com.creditcloud.payment.model.chinapnr.query.FssAccountQueryRequest;
import com.creditcloud.payment.model.chinapnr.query.FssAccountQueryResponse;
import com.creditcloud.payment.model.chinapnr.query.FssProductQueryRequest;
import com.creditcloud.payment.model.chinapnr.query.FssProductQueryResponse;
import com.creditcloud.payment.model.chinapnr.query.QueryCardInfoRequest;
import com.creditcloud.payment.model.chinapnr.query.QueryCardInfoResponse;
import com.creditcloud.payment.model.chinapnr.query.TransStatQueryRequest;
import com.creditcloud.payment.model.chinapnr.query.TransStatQueryResponse;
import com.creditcloud.payment.model.chinapnr.tender.AutoTenderQueryResponse;
import com.creditcloud.payment.model.chinapnr.tender.BorrowerDetail;
import com.creditcloud.payment.model.chinapnr.tender.TenderRequest;
import com.creditcloud.payment.model.chinapnr.tender.TenderRequest2;
import com.creditcloud.payment.model.chinapnr.tender.TenderResponse;
import com.creditcloud.payment.model.chinapnr.tender.TenderResponse2;
import com.creditcloud.payment.model.chinapnr.transfer.CreditAssignRequest;
import com.creditcloud.payment.model.chinapnr.transfer.CreditAssignResponse;
import com.creditcloud.payment.model.chinapnr.transfer.DivDetail;
import com.creditcloud.payment.model.chinapnr.transfer.DivDetail2;
import com.creditcloud.payment.model.chinapnr.transfer.LoansRequest;
import com.creditcloud.payment.model.chinapnr.transfer.LoansRequest2;
import com.creditcloud.payment.model.chinapnr.transfer.LoansResponse;
import com.creditcloud.payment.model.chinapnr.transfer.LoansResponse2;
import com.creditcloud.payment.model.chinapnr.transfer.RepaymentRequest;
import com.creditcloud.payment.model.chinapnr.transfer.RepaymentRequest2;
import com.creditcloud.payment.model.chinapnr.transfer.RepaymentResponse;
import com.creditcloud.payment.model.chinapnr.transfer.RepaymentResponse2;
import com.creditcloud.payment.model.chinapnr.transfer.TransferRequest;
import com.creditcloud.payment.model.chinapnr.transfer.TransferResponse;
import com.creditcloud.payment.model.chinapnr.transfer.UsrTransferRequest;
import com.creditcloud.payment.model.chinapnr.transfer.UsrTransferResponse;
import com.creditcloud.payment.utils.DTOUtils;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.glassfish.jersey.SslConfigurator;
import org.joda.time.LocalDate;

@Stateless
@Remote
public class PaymentServiceBean
        extends BasePaymentBean
        implements PaymentService {

    @EJB
    PaymentAccountDAO paymentAccountDAO;

    @EJB
    FssAccountDAO fssAccountDAO;

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

    public com.creditcloud.payment.model.PaymentAccount getUserPaymentAccount(String clientCode, String userId) {
        this.appBean.checkClientCode(clientCode);
        return DTOUtils.getPaymentAccountDTO(this.paymentAccountDAO.getByUserId(clientCode, userId));
    }

    public com.creditcloud.payment.model.PaymentAccount createUserPaymentAccount(String clientCode, com.creditcloud.payment.model.PaymentAccount paymentAccount) {
        this.appBean.checkClientCode(clientCode);
        String userId = paymentAccount.getUserId().intern();
        synchronized (userId) {
            com.creditcloud.payment.entities.PaymentAccount pa = this.paymentAccountDAO.getByUserId(clientCode, userId);
            if (pa != null) {
                this.logger.debug("User already have a PaymentAccount.[userId={}]", userId);
            } else {
                try {
                    pa = (com.creditcloud.payment.entities.PaymentAccount) this.paymentAccountDAO.create(DTOUtils.convertPaymentAccountDTO(paymentAccount, clientCode));
                } catch (Exception ex) {
                    if (this.paymentAccountDAO.find(paymentAccount.getAccountId()) != null) {
                        this.logger.debug("Duplicate creation of PaymentAccount.");
                    } else {
                        throw ex;
                    }
                }
            }
            return DTOUtils.getPaymentAccountDTO(pa);
        }
    }

    public String getUserIdByAccountId(String clientCode, String accountId) {
        this.appBean.checkClientCode(clientCode);
        String result = null;
        com.creditcloud.payment.entities.PaymentAccount account = (com.creditcloud.payment.entities.PaymentAccount) this.paymentAccountDAO.find(accountId);
        if ((account != null) && (clientCode.equalsIgnoreCase(account.getClientCode()))) {
            result = account.getUserId();
        }
        return result;
    }

    public UserBalanceResult queryBalance(String clientCode, String userId) {
        return queryBalance(clientCode, userId, true);
    }

    /**
     * 入参rollbackOnException的值已经没有意义了
     * @param clientCode
     * @param userId
     * @param rollbackOnException
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public UserBalanceResult queryBalance(String clientCode, String userId, boolean rollbackOnException) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount paymentAccount = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (paymentAccount != null) {
            UserRequest userRequest = new UserRequest(CmdIdType.QueryBalanceBg, this.paymentConfig.getMerCustId(), paymentAccount.getAccountId());

            userRequest.setChkValue(getChkValue(clientCode, userRequest));
            BalanceQueryResponse bqResponse = null;
            try {
                bqResponse = (BalanceQueryResponse) getResponse(userRequest, BalanceQueryResponse.class);
            } catch (RuntimeException ex) {
//                throw (rollbackOnException ? new PaymentException(ex) : new PaymentNotRollbackException(ex));
                logger.error("实时查询汇付余额接口异常：{}", ex);
            }
            logger.debug("实时查询汇付余额返回结果：{}", bqResponse);
            if (verifyResponse(clientCode, bqResponse) == 0) {
                if (bqResponse.success()) {
                    return new UserBalanceResult(bqResponse.getRespCode(), bqResponse.getRespDesc(), NumberUtils.parse(bqResponse.getAcctBal()).setScale(2), NumberUtils.parse(bqResponse.getFrzBal()).setScale(2), NumberUtils.parse(bqResponse.getAvlBal()).setScale(2));
                }
                this.logger.warn("Query balance failed.[clientCode={}][userId={}][RespCode={}][RespDesc={}]", new Object[]{clientCode, userId, bqResponse.getRespCode(), bqResponse.getRespDesc()});

                return new UserBalanceResult(bqResponse.getRespCode(), bqResponse.getRespDesc(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }
        return new UserBalanceResult("account.not.found", "未找到支付账号", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public TransStat queryAutoTender(String clientCode, String userId) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount paymentAccount = this.paymentAccountDAO.getByUserId(clientCode, userId);
        TransStat result = TransStat.NOTEXIST;
        if (paymentAccount != null) {
            UserRequest userRequest = new UserRequest(CmdIdType.QueryTenderPlan, this.paymentConfig.getMerCustId(), paymentAccount.getAccountId());

            userRequest.setChkValue(getChkValue(clientCode, userRequest));
            AutoTenderQueryResponse absResponse = (AutoTenderQueryResponse) getResponse(userRequest, AutoTenderQueryResponse.class);
            if ((verifyResponse(clientCode, absResponse) == 0)
                && (absResponse.getTransStat() != null) && (!"".equals(absResponse.getTransStat()))) {
                result = TransStat.valueOf(absResponse.getTransStat());
            }
        }
        return result;
    }

    @Override
    public PaymentResult deleteCard(String clientCode, String userId, String cardId) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            DelCardRequest request = new DelCardRequest(this.paymentConfig.getMerCustId(), account.getAccountId(), cardId);

            request.setChkValue(getChkValue(clientCode, request));
            DelCardResponse response = (DelCardResponse) getResponse(request, DelCardResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
        }
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }
    
    @Override
    public boolean queryCardIsDefault(String clientCode, String userId, String cardId) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            QueryCardInfoRequest request = new QueryCardInfoRequest(this.paymentConfig.getMerCustId(), account.getAccountId(), cardId);

            request.setChkValue(getChkValue(clientCode, request));
            QueryCardInfoResponse response = (QueryCardInfoResponse) getResponse(request, QueryCardInfoResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return true;
            }
        }
        return false;
    }

    public PaymentResult autoTender(String clientCode, String userId, BigDecimal amount, String orderId, List<BorrowerDetail> borrowerDetails, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            Gson gson = new Gson();
            TenderRequest request = new TenderRequest(CmdIdType.AutoTender, this.paymentConfig.getMerCustId(), orderId, LocalDate.now().toString("yyyyMMdd"), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), account.getAccountId(), this.paymentConfig.getMaxTenderRate(), gson.toJson(borrowerDetails), "", BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            TenderResponse response = (TenderResponse) getResponse(request, TenderResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public CreditAssignResponse autoCreditAssign(String clientCode, CreditAssignRequest assignRequest) {
        this.appBean.checkClientCode(clientCode);
        assignRequest.setChkValue(getChkValue(clientCode, assignRequest));
        CreditAssignResponse response = (CreditAssignResponse) getResponse(assignRequest, CreditAssignResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            return response;
        }
        return null;
    }

    public TenderResult autoTender2(String clientCode, String userId, BigDecimal amount, String orderId, List<BorrowerDetail> borrowerDetails, String FreezeOrdId, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            Gson gson = new Gson();
            TenderRequest2 request = new TenderRequest2(CmdIdType.AutoTender, this.paymentConfig.getMerCustId(), orderId, LocalDate.now().toString("yyyyMMdd"), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), account.getAccountId(), this.paymentConfig.getMaxTenderRate(), gson.toJson(borrowerDetails), IsFreeze.Y.name(), FreezeOrdId, "", BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            TenderResponse2 response = (TenderResponse2) getResponse(request, TenderResponse2.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new TenderResult(response.getRespCode(), response.getRespDesc(), response.getFreezeOrdId(), response.getFreezeTrxId());
            }
            return new TenderResult(PaymentResult.VERIFY_FAILED.getRespCode(), PaymentResult.VERIFY_FAILED.getRespDesc(), null, null);
        }
        return new TenderResult(PaymentResult.ACCOUNT_NOT_FOUND.getRespCode(), PaymentResult.ACCOUNT_NOT_FOUND.getRespDesc(), null, null);
    }

    public PaymentResult bgBindCard(String clientCode, String userId, String openAcctId, Bank openBankId, Province openProvId, City openAreaId, String OpenBranchName, IsDefault isDefault) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            BgBindCardRequest request = new BgBindCardRequest(this.paymentConfig.getMerCustId(), account.getAccountId(), openAcctId, openBankId.name(), openProvId.getKey(), openAreaId.getKey(), OpenBranchName, isDefault.name());

            request.setChkValue(getChkValue(clientCode, request));
            BgBindCardResponse response = (BgBindCardResponse) getResponse(request, BgBindCardResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public com.creditcloud.payment.model.PaymentAccount bgRegister(String clientCode, String userId, String usrName, String loginPwd, String transPwd, String idNo, String usrMp, String usrEmail) {
        this.appBean.checkClientCode(clientCode);

        String accoutName = this.appBean.getClientCode().toLowerCase().concat("_").concat(usrMp);
        BgRegisterRequest request = new BgRegisterRequest(this.paymentConfig.getMerCustId(), accoutName, usrName, loginPwd, transPwd, "00", idNo, usrMp, usrEmail);

        request.setChkValue(getChkValue(clientCode, request));
        BgRegisterResponse response = (BgRegisterResponse) getResponse(request, BgRegisterResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            if (response.success()) {
                com.creditcloud.payment.model.PaymentAccount account = new com.creditcloud.payment.model.PaymentAccount(response.getUsrCustId(), userId, accoutName, new Date());

                return createUserPaymentAccount(clientCode, account);
            }
        }
        return null;
    }

    public void userAcctPay(String clientCode, String userId, BigDecimal amount, String orderId, AcctType inAcctType, String inAcctId, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            UsrAcctPayRequest request = new UsrAcctPayRequest(this.paymentConfig.getMerCustId(), account.getAccountId(), orderId, amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), inAcctId, inAcctType.name(), "", BgRetUrl);

            request.setChkValue(getChkValue(clientCode, request));
            UsrAcctPayResponse response = (UsrAcctPayResponse) getResponse(request, UsrAcctPayResponse.class);
            if (verifyResponse(clientCode, response) != 0) {
            }
        }
    }

    public FreezeResult userFreeze(String clientCode, String userId, BigDecimal amount, String orderId, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount paymentAccount = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (paymentAccount != null) {
            UserFreezeRequest request = new UserFreezeRequest(this.paymentConfig.getMerCustId(), paymentAccount.getAccountId(), orderId, LocalDate.now().toString("yyyyMMdd"), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            UserFreezeResponse response = (UserFreezeResponse) getResponse(request, UserFreezeResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                FreezeResult ta = new FreezeResult(response.getRespCode(), response.getRespDesc(), response.getSubAcctType(), response.getSubAcctId(), response.getOrdId(), response.getOrdDate(), NumberUtils.parse(response.getTransAmt()).setScale(2), response.getTrxId());

                return ta;
            }
        }
        return null;
    }

    public FreezeResult clientWithdrawFreeze(String clientCode, BigDecimal amount, String orderId, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        UserFreezeRequest request = new UserFreezeRequest(this.paymentConfig.getMerCustId(), this.paymentConfig.getMerCustId(), orderId, LocalDate.now().toString("yyyyMMdd"), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), BgRetUrl);

        SubAccount account = this.paymentConfig.getBaseAccount();
        request.setSubAcctType(account.getAccountType());
        request.setSubAcctId(account.getAccountId());
        request.setMerPriv(merPriv);
        request.setChkValue(getChkValue(clientCode, request));
        UserFreezeResponse response = (UserFreezeResponse) getResponse(request, UserFreezeResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            FreezeResult ta = new FreezeResult(response.getRespCode(), response.getRespDesc(), response.getSubAcctType(), response.getSubAcctId(), response.getOrdId(), response.getOrdDate(), NumberUtils.parse(response.getTransAmt()).setScale(2), response.getTrxId());

            return ta;
        }
        return null;
    }

    public UnFreezeResult userUnFreeze(String clientCode, String orderId, String trxId, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        UserUnFreezeRequest request = new UserUnFreezeRequest(this.paymentConfig.getMerCustId(), orderId, LocalDate.now().toString("yyyyMMdd"), trxId, BgRetUrl);

        request.setMerPriv(merPriv);
        request.setChkValue(getChkValue(clientCode, request));
        UserUnFreezeResponse response = (UserUnFreezeResponse) getResponse(request, UserUnFreezeResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            UnFreezeResult ta = new UnFreezeResult(response.getRespCode(), response.getRespDesc(), response.getOrdId(), response.getOrdDate(), response.getTrxId());

            return ta;
        }
        return null;
    }

    public CashAuditResult cashAudit(String clientCode, String userId, BigDecimal amount, String OrdId, AuditFlag auditFlag, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            CashAuditRequest request = new CashAuditRequest(this.paymentConfig.getMerCustId(), OrdId, account.getAccountId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), auditFlag.name(), BgRetUrl);

            request.setChkValue(getChkValue(clientCode, request));
            CashAuditResponse response = (CashAuditResponse) getResponse(request, CashAuditResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new CashAuditResult(response.getRespCode(), response.getRespDesc(), StringUtils.isEmpty(response.getTransAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getTransAmt()), StringUtils.isEmpty(response.getFeeAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getFeeAmt()), response.getFeeCustId(), response.getFeeAcctId(),response.getOpenAcctId());
            }
        }
        return null;
    }

    public CashAuditResult clientCashAudit(String clientCode, BigDecimal amount, String OrdId, AuditFlag auditFlag, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        CashAuditRequest request = new CashAuditRequest(this.paymentConfig.getMerCustId(), OrdId, this.paymentConfig.getMerCustId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), auditFlag.name(), BgRetUrl);

        request.setChkValue(getChkValue(clientCode, request));
        CashAuditResponse response = (CashAuditResponse) getResponse(request, CashAuditResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            return new CashAuditResult(response.getRespCode(), response.getRespDesc(), StringUtils.isEmpty(response.getTransAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getTransAmt()), StringUtils.isEmpty(response.getFeeAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getFeeAmt()), response.getFeeCustId(), response.getFeeAcctId(),response.getOpenAcctId());
        }
        return null;
    }

    public MerCashResult merCash(String clientCode, String userId, BigDecimal amount, String orderId, String BgRetUrl, boolean forUser) {
        this.appBean.checkClientCode(clientCode);
        if (forUser) {
            com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
            if (account != null) {
                MerCashRequest request = new MerCashRequest(this.paymentConfig.getMerCustId(), orderId, account.getAccountId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), "", BgRetUrl, "");

                request.setChkValue(getChkValue(clientCode, request));
                MerCashResponse response = (MerCashResponse) getResponse(request, MerCashResponse.class);
                if (verifyResponse(clientCode, response) == 0) {
                    return new MerCashResult(response.getRespCode(), response.getRespDesc(), StringUtils.isEmpty(response.getTransAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getTransAmt()), StringUtils.isEmpty(response.getFeeAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getFeeAmt()), response.getFeeCustId(), response.getFeeAcctId());
                }
            }
            return null;
        }
        MerCashRequest request = new MerCashRequest(this.paymentConfig.getMerCustId(), orderId, this.paymentConfig.getMerCustId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), "", BgRetUrl, "");

        request.setChkValue(getChkValue(clientCode, request));
        MerCashResponse response = (MerCashResponse) getResponse(request, MerCashResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            return new MerCashResult(response.getRespCode(), response.getRespDesc(), StringUtils.isEmpty(response.getTransAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getTransAmt()), StringUtils.isEmpty(response.getFeeAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getFeeAmt()), response.getFeeCustId(), response.getFeeAcctId());
        }
        return null;
    }

    public PaymentResult posWhSave(String clientCode, String userId, String openAcctId, BigDecimal amount, String orderId, String checkDate, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            PosWhSaveRequest request = new PosWhSaveRequest(this.paymentConfig.getMerCustId(), account.getAccountId(), openAcctId, amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), orderId, LocalDate.now().toString("yyyyMMdd"), checkDate, "", BgRetUrl);

            request.setChkValue(getChkValue(clientCode, request));
            PosWhSaveResponse response = (PosWhSaveResponse) getResponse(request, PosWhSaveResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public PaymentResult loan(String clientCode, String ordId, LocalDate ordDate, String investUserId, BigDecimal amount, BigDecimal fee, String subOrdId, LocalDate subOrdDate, String loanUserId, List<DivDetail> details, IsDefault isDefault, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);
        com.creditcloud.payment.entities.PaymentAccount loanAccount = this.paymentAccountDAO.getByUserId(clientCode, loanUserId);
        if ((investAccount != null) && (loanAccount != null)) {
            Gson gson = new Gson();
            LoansRequest request = new LoansRequest(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), investAccount.getAccountId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), subOrdId, subOrdDate.toString("yyyyMMdd"), loanAccount.getAccountId(), details.isEmpty() ? "" : gson.toJson(details), isDefault.name(), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            LoansResponse response = (LoansResponse) getResponse(request, LoansResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both invest account and loan account can not be null.[investUserId={}][loanUserId={}]", investUserId, loanUserId);
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public LoanResult loan2(String clientCode, String ordId, String investUserId, BigDecimal amount, BigDecimal fee, String subOrdId, LocalDate subOrdDate, String loanUserId, List<DivDetail2> details, FeeObjFlag feeObjFlag, IsDefault isDefault, IsUnFreeze isUnFreeze, String UnFreezeOrdId, String FreezeTrxId, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);
        com.creditcloud.payment.entities.PaymentAccount loanAccount = this.paymentAccountDAO.getByUserId(clientCode, loanUserId);
        if ((investAccount != null) && (loanAccount != null)) {
            Gson gson = new Gson();
            LoansRequest2 request = new LoansRequest2(this.paymentConfig.getMerCustId(), ordId, LocalDate.now().toString("yyyyMMdd"), investAccount.getAccountId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), subOrdId, subOrdDate.toString("yyyyMMdd"), loanAccount.getAccountId(), details.isEmpty() ? "" : gson.toJson(details), feeObjFlag.name(), isDefault.name(), isUnFreeze.name(), UnFreezeOrdId, FreezeTrxId, BgRetUrl, null);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            LoansResponse2 response = (LoansResponse2) getResponse(request, LoansResponse2.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new LoanResult(response.getUnFreezeOrdId(), response.getFreezeTrxId(), response.getRespCode(), response.getRespDesc());
            }
            return new LoanResult(null, null, PaymentResult.VERIFY_FAILED.getRespCode(), PaymentResult.VERIFY_FAILED.getRespDesc());
        }
        this.logger.warn("both invest account and loan account can not be null.[investUserId={}][loanUserId={}]", investUserId, loanUserId);
        return new LoanResult(null, null, PaymentResult.ACCOUNT_NOT_FOUND.getRespCode(), PaymentResult.ACCOUNT_NOT_FOUND.getRespDesc());
    }

    public PaymentResult repay(String clientCode, String ordId, LocalDate ordDate, String loanUserId, String subOrdId, LocalDate subOrdDate, BigDecimal transAmt, BigDecimal fee, String investUserId, List<DivDetail> details, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);
        com.creditcloud.payment.entities.PaymentAccount loanAccount = this.paymentAccountDAO.getByUserId(clientCode, loanUserId);
        if ((investAccount != null) && (loanAccount != null)) {
            Gson gson = new Gson();
            RepaymentRequest request = new RepaymentRequest(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), loanAccount.getAccountId(), subOrdId, subOrdDate.toString("yyyyMMdd"), "", transAmt.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), investAccount.getAccountId(), "", details.isEmpty() ? "" : gson.toJson(details), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            RepaymentResponse response = (RepaymentResponse) getResponse(request, RepaymentResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both invest account and loan account can not be null.[investUserId={}][loanUserId={}]", investUserId, loanUserId);
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public PaymentResult repay2(String clientCode, String ordId, LocalDate ordDate, String loanUserId, String subOrdId, LocalDate subOrdDate, BigDecimal transAmt, BigDecimal fee, String investUserId, List<DivDetail2> details, FeeObjFlag feeObjFlag, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);
        com.creditcloud.payment.entities.PaymentAccount loanAccount = this.paymentAccountDAO.getByUserId(clientCode, loanUserId);
        if ((investAccount != null) && (loanAccount != null)) {
            Gson gson = new Gson();
            RepaymentRequest2 request = new RepaymentRequest2(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), loanAccount.getAccountId(), subOrdId, subOrdDate.toString("yyyyMMdd"), "", transAmt.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), investAccount.getAccountId(), "", details.isEmpty() ? "" : gson.toJson(details), feeObjFlag.name(), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            RepaymentResponse2 response = (RepaymentResponse2) getResponse(request, RepaymentResponse2.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both invest account and loan account can not be null.[investUserId={}][loanUserId={}]", investUserId, loanUserId);
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public PaymentResult disburse(String clientCode, String ordId, LocalDate ordDate, String subOrdId, LocalDate subOrdDate, BigDecimal transAmt, BigDecimal fee, String investUserId, List<DivDetail> details, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);

        String riskAccountId = this.paymentConfig.getGuaranteeAccount().getAccountId();
        if ((investAccount != null) && (riskAccountId != null)) {
            Gson gson = new Gson();
            RepaymentRequest request = new RepaymentRequest(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), this.paymentConfig.getMerCustId(), subOrdId, subOrdDate.toString("yyyyMMdd"), riskAccountId, transAmt.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), investAccount.getAccountId(), "", details.isEmpty() ? "" : gson.toJson(details), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            RepaymentResponse response = (RepaymentResponse) getResponse(request, RepaymentResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both invest account and riskAccountId can not be null.[investUserId={}]", investUserId);
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public PaymentResult disburse2(String clientCode, String ordId, LocalDate ordDate, String subOrdId, LocalDate subOrdDate, BigDecimal transAmt, String OutAcctId, BigDecimal fee, String investUserId, List<DivDetail> details, String BgRetUrl, String merPriv) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount investAccount = this.paymentAccountDAO.getByUserId(clientCode, investUserId);
        if ((investAccount != null) && (OutAcctId != null)) {
            Gson gson = new Gson();
            RepaymentRequest request = new RepaymentRequest(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), this.paymentConfig.getMerCustId(), subOrdId, subOrdDate.toString("yyyyMMdd"), OutAcctId, transAmt.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), fee.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), investAccount.getAccountId(), "", details.isEmpty() ? "" : gson.toJson(details), BgRetUrl);

            request.setMerPriv(merPriv);
            request.setChkValue(getChkValue(clientCode, request));
            RepaymentResponse response = (RepaymentResponse) getResponse(request, RepaymentResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both invest account and riskAccountId can not be null.[investUserId={}]", investUserId);
        return PaymentResult.ACCOUNT_NOT_FOUND;
    }

    public TransferResult transfer(String clientCode, String ordId, String outCustId, String outAcctId, BigDecimal amount, String inCustId, String inAcctId, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        if ((outCustId != null) && (inCustId != null)) {
            TransferRequest request = new TransferRequest(this.paymentConfig.getMerCustId(), ordId, outCustId, outAcctId, amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), inCustId, inAcctId, "", BgRetUrl);

            request.setChkValue(getChkValue(clientCode, request));
            TransferResponse response = (TransferResponse) getResponse(request, TransferResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new TransferResult(response.getOrdId(), response.getOutAcctId(), StringUtils.isEmpty(response.getTransAmt()) ? BigDecimal.ZERO : new BigDecimal(response.getTransAmt()), response.getRespCode(), response.getRespDesc());
            }
        }
        this.logger.warn("outCustId or inCustId is null![outCustId={}][inCustId={}]", outCustId, inCustId);
        return null;
    }

    public PaymentResult userTransfer(String clientCode, String outUserId, String inUserId, String orderId, BigDecimal amount, String BgRetUrl) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount outAccount = this.paymentAccountDAO.getByUserId(this.appBean.getClientCode(), outUserId);
        com.creditcloud.payment.entities.PaymentAccount inAccount = this.paymentAccountDAO.getByUserId(this.appBean.getClientCode(), inUserId);
        if ((outAccount != null) && (inAccount != null)) {
            UsrTransferRequest request = new UsrTransferRequest(this.paymentConfig.getMerCustId(), orderId, outAccount.getAccountId(), inAccount.getAccountId(), amount.setScale(2, PnRConstant.PNR_ROUNDING_MODE).toPlainString(), BgRetUrl, BgRetUrl, "");

            request.setChkValue(getChkValue(clientCode, request));
            UsrTransferResponse response = (UsrTransferResponse) getResponse(request, UsrTransferResponse.class);
            if (verifyResponse(clientCode, response) == 0) {
                return new PaymentResult(response.getRespCode(), response.getRespDesc());
            }
            return PaymentResult.VERIFY_FAILED;
        }
        this.logger.warn("both outAccount and inAccount can not be null![outAccount={}][inAccount={}]", outAccount, inAccount);
        return null;
    }

    public TransStatResult queryTransStat(String clientCode, LocalDate ordDate, String ordId, QueryTransType type) {
        this.appBean.checkClientCode(clientCode);
        TransStatQueryRequest request = new TransStatQueryRequest(this.paymentConfig.getMerCustId(), ordId, ordDate.toString("yyyyMMdd"), type.name());

        request.setChkValue(getChkValue(clientCode, request));
        TransStatQueryResponse response = (TransStatQueryResponse) getResponse(request, TransStatQueryResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            TransStat stat = TransStat.NOTEXIST;
            if ((response.getTransStat() != null) && (!"".equals(response.getTransStat()))) {
                stat = TransStat.valueOf(response.getTransStat());
            }
            TransStatResult result = new TransStatResult(response.getOrdId(), response.getOrdDate(), QueryTransType.valueOf(response.getQueryTransType()), stat);

            return result;
        }
        return null;
    }

    public List<AccountDetail> queryAccounts(String clientCode) {
        this.appBean.checkClientCode(clientCode);
        AcctsQueryRequest request = new AcctsQueryRequest(this.paymentConfig.getMerCustId());
        request.setChkValue(getChkValue(clientCode, request));
        AcctsQueryResponse response = (AcctsQueryResponse) getResponse(request, AcctsQueryResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            return response.getAcctDetails();
        }
        return new ArrayList();
    }

    public CorpRegisterResult queryCorpRegister(String clientCode, String busiCode) {
        this.appBean.checkClientCode(clientCode);
        CorpRegisterQueryRequest request = new CorpRegisterQueryRequest(this.paymentConfig.getMerCustId(), busiCode, "");

        request.setChkValue(getChkValue(clientCode, request));
        CorpRegisterQueryResponse response = (CorpRegisterQueryResponse) getResponse(request, CorpRegisterQueryResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            CorpRegisterResult result = new CorpRegisterResult(response.getUsrCustId(), response.getUsrId(), response.getAuditStat() == null ? AuditStat.NOTEXIST : AuditStat.valueOf(response.getAuditStat()), response.getBusiCode());

            return result;
        }
        return null;
    }

    public FssProductInfo queryFssProduct(String clientCode) {
        this.appBean.checkClientCode(clientCode);
        FssProductQueryRequest request = new FssProductQueryRequest(this.paymentConfig.getMerCustId());
        request.setChkValue(getChkValue(clientCode, request));
        FssProductQueryResponse response = (FssProductQueryResponse) getResponse(request, FssProductQueryResponse.class);
        if (verifyResponse(clientCode, response) == 0) {
            return new FssProductInfo(response.getAnnuRate(), response.getPrdRate());
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FssAccount queryFssAccount(String clientCode, String userId) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.payment.entities.PaymentAccount account = this.paymentAccountDAO.getByUserId(clientCode, userId);
        if (account != null) {
            FssAccount result = DTOUtils.getFssAccountDTO(this.fssAccountDAO.getByUserId(clientCode, userId));
            if ((result != null) && (DateUtils.isSameDay(result.getTimeUpdated(), new Date()))) {
                return result;
            }
            FssAccountQueryRequest request = new FssAccountQueryRequest(this.paymentConfig.getMerCustId(), account.getAccountId());
            request.setChkValue(getChkValue(clientCode, request));
            FssAccountQueryResponse response = (FssAccountQueryResponse) getResponse(request, FssAccountQueryResponse.class);
            if ((verifyResponse(clientCode, response) == 0) && (response.success())) {
                BigDecimal totalAsset = NumberUtils.parse(response.getTotalAsset());
                BigDecimal totalProfit = NumberUtils.parse(response.getTotalProfit());
                if (result == null) {
                    if ((totalAsset.compareTo(BigDecimal.ZERO) > 0) && (totalProfit.compareTo(BigDecimal.ZERO) > 0)) {
                        this.fssAccountDAO.newFssAccount(clientCode, account.getAccountId(), userId, totalAsset, totalProfit);
                    }
                    return new FssAccount(account.getUserId(), account.getAccountId(), response.getTotalAsset(), response.getTotalProfit(), BigDecimal.ZERO, BigDecimal.ZERO, new Date(), new Date());
                }
                result.setTotalAsset(response.getTotalAsset());
                result.setTotalProfit(response.getTotalProfit());
                this.fssAccountDAO.updateAsset(clientCode, userId, totalAsset, totalProfit);
                return result;
            }
        }
        return null;
    }

    public FssAccount queryFssAccount(String clientCode) {
        this.appBean.checkClientCode(clientCode);
        FssAccountQueryRequest request = new FssAccountQueryRequest(this.paymentConfig.getMerCustId(), this.paymentConfig.getMerCustId());
        request.setChkValue(getChkValue(clientCode, request));
        FssAccountQueryResponse response = (FssAccountQueryResponse) getResponse(request, FssAccountQueryResponse.class);
        if ((verifyResponse(clientCode, response) == 0) && (response.success())) {
            return new FssAccount(response.getTotalAsset(), response.getTotalProfit());
        }
        return null;
    }

    public FssStats fssStats(String clientCode) {
        this.appBean.checkClientCode(clientCode);
        return this.fssAccountDAO.fssStats(clientCode);
    }

    public Map<String, FssAccount> getAllFssAccounts(String clientCode) {
        this.appBean.checkClientCode(clientCode);
        return this.appBean.getFssCache(true);
    }

    public String getChkValue(String clientCode, BaseRequest request) {
        return super.getChkValue(clientCode, request);
    }

    public int verifyResponse(String clientCode, BaseResponse response) {
        return super.verifyResponse(clientCode, response);
    }
}
