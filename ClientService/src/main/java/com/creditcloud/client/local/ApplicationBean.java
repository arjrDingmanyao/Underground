/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.local;

import com.creditcloud.common.bean.AbstractClientApplicationBean;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author rooseek
 */
@Slf4j
@LocalBean
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ApplicationBean extends AbstractClientApplicationBean {

    @EJB
    ConfigManager configManager;
    
    private String clientCode;

    @PostConstruct
    void init() {
        ClientConfig clientConfig = configManager.getClientConfig();
        clientCode = clientConfig.getCode();
    }
    
    @Override
    public String getClientCode(){
        return clientCode;
    }

    /**
     * check incoming client code, throw exception if invalid
     *
     * @param clientCode
     * @throws ClientCodeNotMatchException
     */
    @Override
    public void checkClientCode(String clientCode) {
        if (!isValid(clientCode)) {
            String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", clientCode, getClientCode());
            log.warn(cause);
            throw new ClientCodeNotMatchException(cause);
        }
    }
}
