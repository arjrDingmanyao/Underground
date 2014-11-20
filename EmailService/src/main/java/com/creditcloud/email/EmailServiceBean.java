/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email;

import com.creditcloud.config.EmailConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.email.api.EmailService;
import com.creditcloud.email.entities.dao.EmailConfirmDAO;
import com.creditcloud.email.sendcloud.SendCloud;
import com.creditcloud.email.template.EmailTemplate;
import com.creditcloud.email.types.ActivationEmail;
import com.creditcloud.email.types.AuthenticationEmail;
import com.creditcloud.email.types.ConfirmResult;
import com.creditcloud.email.types.Email;
import com.creditcloud.email.types.RegistrationEmail;
import com.creditcloud.email.utils.EmailUtils;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.constant.EmailConstant;
import com.creditcloud.model.user.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@Remote
public class EmailServiceBean implements EmailService {

    @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    @EJB
    EmailConfirmDAO confirmDAO;

    @EJB
    SendCloud sendcloud;

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
    @Override
    public Future<Boolean> send(Client client, Email mail) {
        if (EmailConstant.DEFAULT_EMAIL.equalsIgnoreCase(mail.getAddress())) {
            logger.info("skip user default email address {}", EmailConstant.DEFAULT_EMAIL);
            return new AsyncResult<>(false);
        }

        boolean result = false;
        switch (mail.getType()) {
            case CONFIRM_CREDITMARKET_REGISTRATION:
                logger.info("Confirm Registration. Client: {}, to: {}", client.getCode(), mail.getAddress());
                result = sendEmail(client, mail);
                break;
            case CONFIRM_CREDITMARKET_ACTIVATION:
                logger.info("Confirm Activation. Client: {}, to: {}", client.getCode(), mail.getAddress());
                if (confirmDAO.isActivated(client, mail.getAddress())) {
                    /**
                     * This email is activated already,no need to add
                     * EmailConfirm in storage
                     */
                    logger.info("This email is already activated, activation mail will not be sent.[client={}][mail={}]", client.getCode(), mail.getAddress());
                    result = true;
                } else {
                    if (sendEmail(client, mail)) {
                        ActivationEmail amail = (ActivationEmail) mail;
                        if (confirmDAO.addNew(client, mail.getAddress(), amail.getConfirmCode(), null)) {
                            result = true;
                        }
                    }
                }
                break;

            case CONFIRM_CREDITMARKET_AUTHENTICATION:
                logger.info("Confirm Authentication. Client: {}, to: {}", client.getCode(), mail.getAddress());
                AuthenticationEmail amail = (AuthenticationEmail) mail;
                if (confirmDAO.isActivated(client, amail.getAddress(), amail.getUserId())) {
                    /**
                     * This email is authenticated already,no need to add
                     * EmailConfirm in storage
                     */
                    logger.info("This email is already authenticated, authentication mail will not be sent.[client={}][mail={}]", client.getCode(), mail.getAddress());
                    result = true;
                } else {
                    if (sendEmail(client, mail)) {
                        if (confirmDAO.addNew(client, mail.getAddress(), amail.getConfirmCode(), amail.getUserId())) {
                            result = true;
                        }
                    }
                }
                break;
            default:
            //do nothing
        }
        return new AsyncResult<>(result);
    }

    @Override
    public ConfirmResult confirmEmail(Client client, String emailAddress, String confirmCode) {
        return confirmEmail(client, emailAddress, confirmCode, null);
    }

    @Override
    public ConfirmResult confirmEmail(Client client, String emailAddress, String confirmCode, String userId) {
        return confirmDAO.confirmEmail(client, emailAddress, confirmCode, userId);
    }

    private boolean sendEmail(Client client, Email mail) {
        final EmailConfig config = configManager.getEmailConfig();
        /**
         * send email
         */
        ResourceBundle bundle = ResourceBundle.getBundle("com.creditcloud.email.email", client.getLocale());
        String subject = bundle.getString(mail.getType().getKey());
        String charset = bundle.getString("charset");
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", config.getHost());
            props.put("mail.smtp.socketFactory.port", config.getPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", config.getPort());

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUser(), config.getPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getUser(), client.getShortName()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.getAddress(), mail.getPersonal(), charset));
            message.setSubject(subject);

            String text = template.getContent(client, mail);

            message.setContent(text, "text/html;charset=UTF-8");

            message.setHeader("X-Mailer", "smtpsend");
            message.setSentDate(new Date());

            Transport.send(message);
        } catch (UnsupportedEncodingException | MessagingException ex) {
            logger.error("Exception happend when sending email.[client={}][mail={}]", client, mail, ex);
            return false;
        }
        return true;
    }

    @Asynchronous
    @Override
    public Future<Boolean> sendRegistration(Client client, String emailAddress) {
        return sendRegistration(client, emailAddress, emailAddress);
    }

    @Asynchronous
    @Override
    public Future<Boolean> sendRegistration(Client client, String emailAddress, String personal) {
        return send(client, new RegistrationEmail(emailAddress, personal));
    }

    @Asynchronous
    @Override
    public Future<Boolean> sendActivation(Client client, String emailAddress) {
        return sendActivation(client, emailAddress, emailAddress);
    }

    @Asynchronous
    @Override
    public Future<Boolean> sendActivation(Client client, String emailAddress, String personal) {
        String confirmCode = EmailUtils.getConfirmCode(client.getCode(), emailAddress);
        return send(client, new ActivationEmail(emailAddress, personal, confirmCode));
    }

    @Asynchronous
    @Override
    public Future<Boolean> sendAuthentication(Client client, String emailAddress, User user) {
        String confirmCode = EmailUtils.getConfirmCode(client.getCode(), emailAddress);
        return send(client, new AuthenticationEmail(emailAddress, user, confirmCode));
    }

    @Override
    public boolean isActivated(Client client, String emailAddress) {
        return confirmDAO.isActivated(client, emailAddress);
    }

    @Asynchronous
    @Override
    public Future<Boolean> send(Client client, String subject, String content, String... emailAddress) {
        return sendcloud.send(client, subject, content, emailAddress);
    }
}
