/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email;

import com.creditcloud.email.template.EmailTemplate;
import com.creditcloud.email.types.ActivationEmail;
import com.creditcloud.model.client.Client;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rooseek
 */
public class EmailTest {

    @Ignore
    @Test
    public void simpleEmail() throws MessagingException, UnsupportedEncodingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hao.chen@creditcloud.com", "chenhao1985");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("hao.chen@creditcloud.com", "云中信"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("rooseek@gmail.com", "陈浩", "GBK"));
        msg.setSubject("邮箱验证");

        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("云中信");
        mp.addBodyPart(mbp1);
        msg.setContent(mp);

        msg.setHeader("X-Mailer", "smtpsend");
        msg.setSentDate(new Date());


        Transport.send(msg);
    }

    @Ignore
    @Test
    public void textEmail() throws MessagingException, UnsupportedEncodingException, IOException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hao.chen@creditcloud.com", "chenhao1985");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("hao.chen@creditcloud.com", "云中信"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("rooseek@gmail.com", "陈浩", "GBK"));
        msg.setSubject("邮箱验证");

        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("Hello Creditcloud");
        mp.addBodyPart(mbp1);
        msg.setContent("<h1>This is actual message</h1>",
                       "text/html;charset=gb2312");

        msg.setHeader("X-Mailer", "smtpsend");
        msg.setSentDate(new Date());

        msg.writeTo(new FileOutputStream("textMail.eml"));

        Transport.send(msg);
    }

    @Ignore
    @Test
    public void testFreeMaker() throws MessagingException, UnsupportedEncodingException, IOException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hao.chen@creditcloud.com", "chenhao1985");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("hao.chen@creditcloud.com", "云中信"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("rooseek@gmail.com", "陈浩", "UTF-8"));
        msg.setSubject("邮箱验证");


        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("Hello Creditcloud");
        mp.addBodyPart(mbp1);



        EmailTemplate template = new EmailTemplate();
        Client client = new Client();
        client.setName("云中信");
        client.setUrl("www.creditlcoud.com");
//        msg.setContent(template.getContent(client, new RegistrationEmail("rooseek@gmail.com", "陈浩")),
//                       "text/html;charset=gb2312");
        msg.setContent(template.getContent(client, new ActivationEmail("rooseek@gmail.com", "陈浩", "23423sgadgaetqw45")),
                       "text/html;charset=gb2312");

        msg.setHeader("X-Mailer", "smtpsend");
        msg.setSentDate(new Date());


        Transport.send(msg);
    }
}
