package com.creditcloud.payment;

import chinapnr.SecureLink;
import com.creditcloud.config.PaymentConfig;
import com.creditcloud.payment.local.ApplicationBean;
import com.creditcloud.payment.model.chinapnr.base.BaseRequest;
import com.creditcloud.payment.model.chinapnr.base.BaseResponse;
import com.creditcloud.payment.utils.FormUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BasePaymentBean {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    PaymentConfig paymentConfig;

    WebTarget target;

    protected String getChkValue(String clientCode, BaseRequest request) {
        this.appBean.checkClientCode(clientCode);
        String checkString = request.chkString();
        SecureLink sl = new SecureLink();
        if (this.paymentConfig.getMerId() == null) {
            this.logger.error("MerId can not be null!");
            return null;
        }
        if (this.paymentConfig.getPrivateKeyPath() == null) {
            this.logger.error("PrivateKeyPath can not be null!");
            return null;
        }
        int result = sl.SignMsg(this.paymentConfig.getMerId(), this.paymentConfig.getPrivateKeyPath(), checkString.getBytes());
        if (result != 0) {
            this.logger.error("Invalid sign result {}!", Integer.valueOf(result));
        }
        this.logger.debug("Generate ChkValue: \n{}\n for String: \n{}", sl.getChkValue(), checkString);
        return sl.getChkValue();
    }

    protected int verifyResponse(String clientCode, BaseResponse response) {
        this.appBean.checkClientCode(clientCode);
        if (response == null) {
            this.logger.error("Null response, maybe bad json format.");
            return -1;
        }
        if (response.getCmdId() == null) {
            this.logger.error("Null CmdId found in response!");
            return -1;
        }
        if (!this.paymentConfig.getMerCustId().equalsIgnoreCase(response.getMerCustId())) {
            this.logger.error("Received others response.[response={}]", response.toString());
            return -1;
        }
        if (StringUtils.isBlank(response.chkString())) {
            this.logger.error("ChkString is blank.[response={}]", response.toString());
            return -1;
        }
        String msgData = "";
        try {
            msgData = URLDecoder.decode(response.chkString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            this.logger.error("Can't happen!", ex);
        }
        String chkValue = response.getChkValue();

        SecureLink sl = new SecureLink();
        int result = sl.VeriSignMsg(this.paymentConfig.getPublicKeyPath(), msgData, chkValue);
        if (result != 0) {
            this.logger.error("Verify failed , result={}, for Message: \n{}\n using ChkValue: \n{}", new Object[]{Integer.valueOf(result), msgData, chkValue});
        }
        return result;
    }

    protected <T extends BaseResponse> T getResponse(BaseRequest request, Class<T> clazz) {
        Form form = FormUtils.getForm(request);
        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        String response = (String) this.target.request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE, MediaType.TEXT_HTML_TYPE}).post(entity, String.class);
        Gson gson = new Gson();
        if (ignoreLog(request)) {
            this.logger.debug("log for {} is skipped, please enable 'printReconciliationLog' in 'PaymentConfig' to view log.", request.getCmdId());
        } else {
            this.logger.debug("Payment Response:" + response);
        }
        try {
            return (T) (BaseResponse) gson.fromJson(response, clazz);
        } catch (JsonSyntaxException ex) {
            this.logger.error("ChinaPnR return bad response.[clazz={}][response={}]", new Object[]{clazz.getCanonicalName(), response, ex});
        }
        return null;
    }

    private boolean ignoreLog(BaseRequest request) {
                if (this.appBean.getPaymentConfig().isPrintReconciliationLog()) {
                    return false;
                }
                return true;
    }
}
