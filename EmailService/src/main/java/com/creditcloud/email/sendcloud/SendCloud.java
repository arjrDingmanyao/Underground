/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.sendcloud;

import com.creditcloud.email.template.EmailTemplate;
import com.creditcloud.email.types.Email;
import com.creditcloud.model.client.Client;
import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.exception.BlankException;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class SendCloud {

    @Inject
    Logger logger;

    //TODO 不同的用户配置加在EmailConfig中
    private static final String account = "postmaster@creditcloud.sendcloud.org";

    private static final String password = "FzjnJ83o";

    /**
     * get text/html template for email
     */
    private EmailTemplate template;

    @PostConstruct
    void init() {
        try {
            template = new EmailTemplate();
        } catch (IOException ex) {
            logger.error("Failed to initialize the EmailTemplate", ex);
        }
    }

    @Asynchronous
    public Future<Boolean> send(Client client, Email mail) {
        try {
            Message message = new Message(client.getSupportEmail(), client.getShortName());
            ResourceBundle bundle = ResourceBundle.getBundle("com.creditcloud.email.email", client.getLocale());
            String subject = bundle.getString(mail.getType().getKey());
            String body = template.getContent(client, mail);
            message.setBody(body);
            message.setSubject(subject);
            message.addRecipient(mail.getAddress());
            com.sohu.sendcloud.SendCloud sendCloud = new com.sohu.sendcloud.SendCloud(account, password);
            sendCloud.setMessage(message);
            sendCloud.send();
            logger.debug("send email to {}", mail.getAddress());
            return new AsyncResult<>(true);
        } catch (BlankException ex) {
            //just ignore 
            logger.debug("get BlankException", ex);
        } catch (Exception ex) {
            //just ignore 
            logger.debug("get Exception", ex);
        }
        return new AsyncResult<>(false);
    }

    @Asynchronous
    public Future<Boolean> send(Client client, String subject, String content, String... emailAddress) {
        if (emailAddress == null || emailAddress.length == 0) {
            return new AsyncResult<>(false);
        }
        try {
            String senderEmail = client.getSupportEmail();
            if (senderEmail == null || senderEmail.isEmpty()) {
                senderEmail = "donotreply@creditcloud.com";
            }
            String title = client.getShortName();
            if (title == null || title.isEmpty()) {
                title = "新邮件";
            }
            Message message = new Message(senderEmail, title);
            message.setBody(content);
            message.setSubject(subject);
            message.addRecipients(Arrays.asList(emailAddress));
            com.sohu.sendcloud.SendCloud sendCloud = new com.sohu.sendcloud.SendCloud(account, password);

            sendCloud.setMessage(message);
            sendCloud.send();
            return new AsyncResult<>(true);
        } catch (BlankException ex) {
            //just ignore 
            logger.debug("get BlankException", ex);
        } catch (Exception ex) {
            //just ignore 
            logger.debug("get Exception", ex);
        }
        return new AsyncResult<>(false);
    }
}
