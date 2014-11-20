/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.local;

import com.creditcloud.message.api.MessageService;
import com.creditcloud.model.enums.Realm;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class MessageBridge {
    
    @EJB
    ApplicationBean appBean;

    @EJB
    MessageService messageService;


    /**
     * send a notification to several users
     *
     * @param title
     * @param content
     * @param sendSMS
     * @param receiver
     */
    public void notify(String title,
                       String content,
                       String... receiver) {
        messageService.sendNotification(appBean.getClient(),
                                        Realm.USER,
                                        title,
                                        content,
                                        receiver);
    }
}
