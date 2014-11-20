/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.common.utils.OrderGenerator;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.ClientFeatures;
import com.creditcloud.config.Features;
import com.creditcloud.config.PaymentConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.enums.misc.CacheType;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import com.creditcloud.redis.api.SentinelService;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ApplicationBean {

    @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    @EJB
    ClientService clientService;

    @EJB
    SentinelService sentinelService;
    
    private Client client;

    private PaymentConfig paymentConfig;

    private Features features;

    private ClientFeatures clientFeatures;
    
    /**
     * 用于缓存已经生成的ordId，防止重复</p>
     * TODO 但不能防止跟其他地方如market中生成order重复</p>
     * 将订单生成做成单独service,根据传入参数生成并返回一定数目无重复orderId
     */
    private final Set<String> orderIds = new ConcurrentSkipListSet<>();
    

    @PostConstruct
    void init() {
        ClientConfig clientConfig = configManager.getClientConfig();
        features = clientConfig.getFeatures();
        clientFeatures = clientConfig.getClientFeatures();
        
        client = clientService.getClient(clientConfig.getCode());
        paymentConfig = configManager.getPaymentConfig();
        logger.info("ApplicationBean in UserService initialized with ClientConfig:\n{}", clientConfig);
    }

    public Client getClient() {
        return client;
    }

    public String getClientCode() {
        return client == null ? null : client.getCode();
    }

    /**
     *
     * check incoming client code, but do not throw exception
     *
     * @param clientCode
     * @return true if clientCode is null or equal to local client code
     */
    public boolean isValid(String clientCode) {
        if (clientCode != null && !clientCode.equals(getClientCode())) {
            return false;
        }
        return true;
    }

    /**
     * check incoming client code, throw exception if invalid
     *
     * @param clientCode
     * @throws ClientCodeNotMatchException
     */
    public void checkClientCode(String clientCode) {
        if (!isValid(clientCode)) {
            String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", clientCode, getClientCode());
            logger.warn(cause);
            throw new ClientCodeNotMatchException(cause);
        }
    }

    public PaymentConfig getPaymentConfig() {
        return paymentConfig;
    }

    /**
     * 返回Client内唯一的订单号
     *
     * @return
     */
    public synchronized String orderId() {
        String orderId = OrderGenerator.order();
        if (!orderIds.add(orderId)) {
            logger.warn("OrderId already placed.[orderId={}]", orderId);
            return orderId();
        } else {
            return orderId;
        }
    }

    public boolean isEnableManualFlush() {
        return features == null ? true : features.isEnableManualFlush();
    }
    
    public void deleteCache(String userId, String format) {
        try {
            if (clientFeatures.isEnableServiceSentinel() && !StringUtils.isEmpty(userId)) {
                String cacheKey = String.format(format, userId);
                long tick = sentinelService.delete(CacheType.COMMON, cacheKey);
                logger.info("cache key {} delete {}", cacheKey, tick);
                sentinelService.publish(CacheType.COMMON, "invalidate", cacheKey);
            }
        } catch (Exception e) {
            logger.error("sentinel cache exception {}", e);
        }
    }
}
