/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ApplicationBean {
     @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    @EJB
    ClientService clientService;

    private Client client;

    @PostConstruct
    void init() {
        ClientConfig clientConfig = configManager.getClientConfig();
        client = clientService.getClient(clientConfig.getCode());
        logger.info("ApplicationBean in TaskService initialized with ClientConfig:\n{}", clientConfig);
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
}
