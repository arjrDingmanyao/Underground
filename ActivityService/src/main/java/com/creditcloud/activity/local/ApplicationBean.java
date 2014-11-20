/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.activity.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.common.bean.AbstractClientApplicationBean;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Singleton
public class ApplicationBean extends AbstractClientApplicationBean{

    @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    @EJB
    ClientService clientService;
    
    @PostConstruct
    void init() {
        ClientConfig clientConfig = configManager.getClientConfig();
        client = clientService.getClient(clientConfig.getCode());
        logger.info("ApplicationBean in CommentService initialized.[clientCode={}]", getClientCode());
    }

    @Override
    public void checkClientCode(String clientCode) {
        if (!isValid(clientCode)) {
            String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", clientCode, getClientCode());
            logger.warn(cause);
            throw new ClientCodeNotMatchException(cause);
        }
    }
}
