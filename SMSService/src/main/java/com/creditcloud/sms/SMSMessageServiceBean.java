/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.sms.api.SMSMessageService;
import com.creditcloud.sms.dao.SMSMessageDAO;
import com.creditcloud.sms.model.SMSMessage;
import com.creditcloud.sms.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * 短信日志-将现有短信内容入库并增加界面查询
 *
 * @author Administrator
 */
@Remote
@Stateless
public class SMSMessageServiceBean implements SMSMessageService {

    private Client client;

    private ClientConfig clientConfig;

    @Inject
    Logger logger;

    @EJB
    private SMSMessageDAO sMSMessageDAO;

    @EJB
    ConfigManager configManager;

    @EJB
    ClientService clientService;

    @PostConstruct
    void init() {
	clientConfig = configManager.getClientConfig();
	client = clientService.getClient(clientConfig.getCode());
    }

    @Override
    public PagedResult<SMSMessage> findByPage(PageInfo pageInfo) {
	PagedResult<com.creditcloud.sms.entities.SMSMessage> pagedResult = sMSMessageDAO.findByPage(pageInfo);
	List<SMSMessage> lists = new ArrayList<>(pagedResult.getResults().size());
	for (com.creditcloud.sms.entities.SMSMessage message : pagedResult.getResults()) {
	    lists.add(DTOUtils.getSMSMessageDTO(message));
	}
	return new PagedResult<>(lists, pagedResult.getTotalSize());
    }

    @Override
    public PagedResult<SMSMessage> findByPageAndDateRange(PageInfo pageInfo, Date from, Date to, String receiver) {
	PagedResult<com.creditcloud.sms.entities.SMSMessage> pagedResult = sMSMessageDAO.findByPageAndDateRange(pageInfo, from, to, "%" + receiver + "%");
	List<SMSMessage> lists = new ArrayList<>(pagedResult.getResults().size());
	for (com.creditcloud.sms.entities.SMSMessage message : pagedResult.getResults()) {
	    lists.add(DTOUtils.getSMSMessageDTO(message));
	}
	return new PagedResult<>(lists, pagedResult.getTotalSize());
    }

    /**
     * 保存到数据库 记录日志
     *
     * @param type
     * @param mobile
     * @param contents
     */
    @Override
    public void addRecord(SMSType type, String mobile, String... contents) {
	logger.debug("add SMSMessage record:mobile {} ",mobile);
	ResourceBundle resourceBundle = ResourceBundle.getBundle("com.creditcloud.sms.messages", Locale.getDefault());
	String contentTemplate = resourceBundle.getString(type.getKey());
	String content = "";
	switch (type) {
	    case CONFIRM_CREDITMARKET_REGISTER:
		content = String.format(contentTemplate,client.getShortName(), contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content,mobile, new Date()));
		break;
	    case CONFIRM_CREDITMARKET_CHANGE_LOGIN_PASSWORD:
		content = String.format(contentTemplate, contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content,mobile, new Date()));
		break;
	    case CREDITMARKET_RESET_PASSWORD:
		content = String.format(contentTemplate, contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case CONFIRM_CREDITMARKET_AUTHENTICATE_MOBILE:
		content = String.format(contentTemplate, contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case CREDITMANAGER_MESSAGE:
		content = String.format(contentTemplate, contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case CREDITMANAGER_RESET_PASSWORD:
		content = String.format(contentTemplate,client.getShortName(), contents[0]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED:
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_CREDITMARKET_USER_CREATED:
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;

	    case NOTIFICATION_LOANREQUEST_STATUS:
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;

	    case NOTIFICATION_LOAN_STATUS:
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;

	    case NOTIFICATION_LOAN_REPAY:
		content = String.format(contentTemplate, contents[0], contents[1], contents[2], client.getShortName(), contents[3]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_LOAN_CLEARED:
		content = String.format(contentTemplate, contents[0], contents[1]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_WITHDRAW_APPLY:
		content = String.format(contentTemplate, client.getShortName(), contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_REPAYMENT_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_REPAYMENT_NOTICE");
		content = String.format(contentTemplate, client.getShortName(), contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_REPAYMENT_NOTICE_NEW:
		logger.debug("SMSTye NOTIFICATION_AUTO_REPAYMENT_NOTICE_NEW");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2], contents[3]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_LOANS_SUCCESS_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_SUCCESS_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_LOANS_FAILURE_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_FAILURE_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_LOANS_SETTLE_NOTICE:
		logger.debug("SMSTye NOTIFICATION_LOANS_SETTLE_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_LOAN_OVERDUE_REPAYMENT_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOAN_OVERDUE_REPAYMENT_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2], contents[3], contents[4], contents[5], contents[6]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_LOANS_OVERDUE_SUCCESS_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_OVERDUE_SUCCESS_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2], contents[3], contents[4], contents[5], contents[6]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    case NOTIFICATION_AUTO_LOANS_OVERDUE_FAILURE_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_OVERDUE_FAILURE_NOTICE");
		content = String.format(contentTemplate, contents[0], contents[1], contents[2], contents[3], contents[4], contents[5]);
		sMSMessageDAO.create(new com.creditcloud.sms.entities.SMSMessage(content ,mobile, new Date()));
		break;
	    default:
		logger.debug("SMSTye not set");
		break;
	}

    }
}
