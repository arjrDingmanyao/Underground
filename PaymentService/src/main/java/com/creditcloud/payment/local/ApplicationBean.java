package com.creditcloud.payment.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.common.bean.AbstractClientApplicationBean;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.PaymentConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import com.creditcloud.payment.api.PaymentService;
import com.creditcloud.payment.entities.dao.FssAccountDAO;
import com.creditcloud.payment.utils.DTOUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;

@LocalBean
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ApplicationBean
        extends AbstractClientApplicationBean {

    @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    @EJB
    ClientService clientService;

    @EJB
    PaymentService paymentService;

    @EJB
    FssAccountDAO fssAccountDAO;

    private PaymentConfig paymentConfig;

    private static final Map<String, com.creditcloud.payment.model.FssAccount> fssCache = new ConcurrentHashMap();

    @PostConstruct
    void init() {
        ClientConfig clientConfig = this.configManager.getClientConfig();
        this.client = this.clientService.getClient(clientConfig.getCode());
        this.paymentConfig = this.configManager.getPaymentConfig();
        this.logger.info("Payment Service started for Client: {} with PaymentConfig: \n{}", clientConfig.getCode(), this.paymentConfig.toString());
    }

    public PaymentConfig getPaymentConfig() {
        return this.paymentConfig;
    }

    public Map<String, com.creditcloud.payment.model.FssAccount> getFssCache(boolean fetch) {
        if ((fetch) && (fssCache.isEmpty())) {
            updateAsset();
        }
        return fssCache;
    }

    public void checkClientCode(String clientCode) {
        if (!isValid(clientCode)) {
            String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", new Object[]{clientCode, getClientCode()});
            this.logger.warn(cause);
            throw new ClientCodeNotMatchException(cause);
        }
    }

    @Schedule(persistent = false, second = "0", minute = "0", hour = "3")
    private void updateAsset() {
        if (fssCache.isEmpty()) {
            for (com.creditcloud.payment.entities.FssAccount fa : this.fssAccountDAO.findAll()) {
                fssCache.put(fa.getUserId(), DTOUtils.getFssAccountDTO(fa));
            }
            this.logger.debug("FssCache loaded {} items.", Integer.valueOf(fssCache.size()));
        }
        List<String> userIds = this.fssAccountDAO.outOfDateFssAccounts(getClientCode());
        if (!userIds.isEmpty()) {
            this.logger.info("PaymentService start to update {} FssAccounts.", Integer.valueOf(userIds.size()));
            long timeStart = System.currentTimeMillis();
            for (String userId : userIds) {
                com.creditcloud.payment.model.FssAccount fa = this.paymentService.queryFssAccount(getClientCode(), userId);
                if (fa != null) {
                    fssCache.put(fa.getUserId(), fa);
                }
            }
            this.logger.info("PaymentService updated {} FssAccounts.[time={}]", Integer.valueOf(userIds.size()), Long.valueOf(System.currentTimeMillis() - timeStart));
        }
    }
}
