package com.creditcloud.payment.utils;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DTOUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");

    public static com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation getCreditAssignReconciliation(com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation reconciliation) {
        com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation result = null;
        if (reconciliation != null) {
            result = new com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation(reconciliation.getOrdId(), reconciliation.getOrdDate().toString("yyyyMMdd"), reconciliation.getSellCustId(), reconciliation.getCreditAmt(), reconciliation.getCreditDealAmt(), reconciliation.getFee(), reconciliation.getBuyCustId(), reconciliation.getTransStat(), reconciliation.getPnrDate(), reconciliation.getPnrSeqId());
        }
        return result;
    }

    public static com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation convertCreditAssignReconciliation(com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation reconciliation) {
        com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation result = null;
        if (reconciliation != null) {
            result = new com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation(reconciliation.getOrdId(), LocalDate.parse(reconciliation.getOrdDate(), DATE_TIME_FORMATTER), reconciliation.getSellCustId(), reconciliation.getCreditAmt(), reconciliation.getCreditDealAmt(), reconciliation.getFee(), reconciliation.getBuyCustId(), reconciliation.getTransStat(), reconciliation.getPnrDate(), reconciliation.getPnrSeqId());
        }
        return result;
    }

    public static List<com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation> getCreditAssignReconciliation(List<com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation> reconciliationList) {
        List<com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation> result = new ArrayList();
        for (com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation reconciliation : reconciliationList) {
            result.add(getCreditAssignReconciliation(reconciliation));
        }
        return result;
    }

    public static List<com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation> convertCreditAssignReconciliation(List<com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation> reconciliationList) {
        List<com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation> result = new ArrayList();
        for (com.creditcloud.payment.model.chinapnr.reconciliation.CreditAssignReconciliation reconciliation : reconciliationList) {
            result.add(convertCreditAssignReconciliation(reconciliation));
        }
        return result;
    }

    public static com.creditcloud.payment.model.PaymentAccount getPaymentAccountDTO(com.creditcloud.payment.entities.PaymentAccount paymentAccount) {
        com.creditcloud.payment.model.PaymentAccount result = null;
        if (paymentAccount != null) {
            result = new com.creditcloud.payment.model.PaymentAccount();
            result.setUserId(paymentAccount.getUserId());
            result.setAccountId(paymentAccount.getAccountId());
            result.setAccountName(paymentAccount.getAccountName());
            result.setTimeCreate(paymentAccount.getTimeCreate());
        }
        return result;
    }

    public static com.creditcloud.payment.entities.PaymentAccount convertPaymentAccountDTO(com.creditcloud.payment.model.PaymentAccount paymentAccount, String clientCode) {
        com.creditcloud.payment.entities.PaymentAccount result = null;
        if (paymentAccount != null) {
            result = new com.creditcloud.payment.entities.PaymentAccount();
            result.setUserId(paymentAccount.getUserId());
            result.setAccountId(paymentAccount.getAccountId());
            result.setAccountName(paymentAccount.getAccountName());
            result.setTimeCreate(paymentAccount.getTimeCreate());
            result.setClientCode(clientCode);
        }
        return result;
    }

    public static com.creditcloud.payment.model.FssAccount getFssAccountDTO(com.creditcloud.payment.entities.FssAccount fssAccount) {
        com.creditcloud.payment.model.FssAccount result = null;
        if (fssAccount != null) {
            result = new com.creditcloud.payment.model.FssAccount(fssAccount.getUserId(), fssAccount.getAccountId(), fssAccount.getBalance().toPlainString(), fssAccount.getTotalProfit().toPlainString(), fssAccount.getTotalDeposit(), fssAccount.getTotalWithdraw(), fssAccount.getTimeCreated(), fssAccount.getTimeUpdated());
        }
        return result;
    }
}
