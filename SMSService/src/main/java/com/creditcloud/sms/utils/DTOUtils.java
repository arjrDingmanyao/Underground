/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms.utils;

import com.creditcloud.sms.model.SMSMessage;

/**
 * 短信记录DTO
 *
 * @author Administrator
 */
public class DTOUtils {

    public static SMSMessage getSMSMessageDTO(com.creditcloud.sms.entities.SMSMessage smsMessage) {
	SMSMessage result = null;
	if (smsMessage != null) {
	    result = new SMSMessage(smsMessage.getId(), smsMessage.getContent(), smsMessage.getReceiver(), smsMessage.getSentTime());
	}
	return result;
    }
}
