/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message;

import com.creditcloud.user.api.UserService;
import com.creditcloud.client.api.EmployeeService;
import com.creditcloud.message.api.MessageService;
import com.creditcloud.message.entity.Message;
import com.creditcloud.message.entity.MessageBody;
import com.creditcloud.message.entity.dao.MessageBodyDAO;
import com.creditcloud.message.entity.dao.MessageDAO;
import com.creditcloud.message.utils.DTOUtils;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.client.Employee;
import com.creditcloud.model.user.User;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import static com.creditcloud.model.enums.Realm.EMPLOYEE;
import static com.creditcloud.model.enums.Realm.USER;
import com.creditcloud.model.enums.message.MessageStatus;
import com.creditcloud.model.enums.message.MessageType;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

/**
 *
 * @author martin
 */
@Stateless
@Remote
public class MessageServiceBean implements MessageService {

    @Inject
    Logger logger;

    @EJB
    UserService userService;

    @EJB
    EmployeeService employeeService;

    @EJB
    MessageDAO messageDAO;

    @EJB
    MessageBodyDAO bodyDAO;

    @Asynchronous
    @Override
    public void sendMessage(Client client,
                            Realm realm,
                            String title,
                            String content,
                            String sender,
                            String... receivers) {
        logger.debug("send message.[client={}][realm={}][sender={}][receiver={}]", client.getCode(), realm, sender, ArrayUtils.toString(receivers));

        if (receivers == null || receivers.length == 0) {
            logger.warn("no receiver found");
            //no receiver just ignore
            return;
        }
        Set<Message> messages = new HashSet();
        MessageBody body = new MessageBody(title, content, new Date(), realm, MessageType.MESSAGE);
        switch (realm) {
            case USER:
                for (String receiver : receivers) {
                    messages.add(new Message(body, sender, receiver, MessageStatus.NEW));
                }
                /**
                 * do not send short message for messages between users
                 */
                break;
            case EMPLOYEE:
                for (String receiver : receivers) {
                    messages.add(new Message(body, sender, receiver, MessageStatus.NEW));
                }
                break;
            default:
            //do nothing
        }
        body.setMessages(messages);
        bodyDAO.create(body);
    }

    @Asynchronous
    @Override
    public Future<com.creditcloud.model.Message> reply(Client client,
                                                       Realm realm,
                                                       String title,
                                                       String content,
                                                       String sender,
                                                       String receiver) {
        logger.debug("reply.[client={}][realm={}][sender={}][receiver={}]", client.getCode(), realm, sender, receiver);
        if (receiver == null) {
            logger.warn("no receiver found");
            //no receiver just ignore
            return new AsyncResult<>(null);
        }
        Set<Message> messages = new HashSet();
        MessageBody body = new MessageBody(title, content, new Date(), realm, MessageType.MESSAGE);
        switch (realm) {
            case USER:
                messages.add(new Message(body, sender, receiver, MessageStatus.NEW));
                /**
                 * do not send short message for messages between users
                 */
                break;
            case EMPLOYEE:
                messages.add(new Message(body, sender, receiver, MessageStatus.NEW));
                break;
            default:
            //donothing
        }
        body.setMessages(messages);
        MessageBody result = bodyDAO.create(body);

        if (result.getMessages().size() != 1) {
            throw new IllegalStateException("only one message could exist in message body for reply");
        }
        for (Message message : result.getMessages()) {
            /**
             * messagebody中只会有这一个message,所以直接返回
             */
            return new AsyncResult<>(DTOUtils.getMessage(message));
        }

        return new AsyncResult<>(null);
    }

    @Asynchronous
    @Override
    public void sendNotification(Client client,
                                 Realm realm,
                                 String title,
                                 String content,
                                 String... receivers) {
        logger.debug("send notification.[client={}][realm={}][receiver={}]", client.getCode(), realm, ArrayUtils.toString(receivers));
        if (receivers == null || receivers.length == 0) {
            logger.warn("no receiver found");
            //no receiver just ignore
            return;
        }
        Set<Message> messages = new HashSet();
        MessageBody body = new MessageBody(title, content, new Date(), realm, MessageType.NOTIFICATION);
        switch (realm) {
            case USER:
                for (String receiver : receivers) {
                    messages.add(new Message(body, null, receiver, MessageStatus.NEW));
                }
                break;
            case EMPLOYEE:
                for (String receiver : receivers) {
                    messages.add(new Message(body, null, receiver, MessageStatus.NEW));
                }
                break;
            default:
            //do nothing
        }
        body.setMessages(messages);
        bodyDAO.create(body);
    }

    @Asynchronous
    @Override
    public void broadcast(Client client,
                          Realm realm,
                          String title,
                          String content) {
        logger.debug("broadcast.[client={}][realm={}]", client.getCode(), realm);
        switch (realm) {
            case USER:
                if (userService.countByClient(client.getCode()) == 0) {
                    logger.warn("no user exist for client {}", client.getCode());
                    return;
                }
                break;
            case EMPLOYEE:
                //管理员是默认有的，只有管理员时候不发
                if (employeeService.countByClient(client.getCode()) < 2) {
                    logger.warn("no employee exist besides admin for client {}", client.getCode());
                    return;
                }
                break;
            default:
                logger.debug("illegal realm {} to broadcast, only USER and EMPLOYEE supported now.", realm);
                return;
        }
        Set<Message> messages = new HashSet();
        MessageBody body = new MessageBody(title, content, new Date(), realm, MessageType.NOTIFICATION);
        switch (realm) {
            case USER:
                for (User receiver : userService.listAllUsers(client.getCode())) {
                    messages.add(new Message(body, null, receiver.getId(), MessageStatus.NEW));
                }
                break;
            case EMPLOYEE:
                for (Employee receiver : employeeService.listByClient(client.getCode())) {
                    messages.add(new Message(body, null, receiver.getId(), MessageStatus.NEW));
                }
                break;
            default:
            //do nothing
        }
        body.setMessages(messages);
        bodyDAO.create(body);
    }

    @Override
    public PagedResult<com.creditcloud.model.Message> listConversation(Client client, Realm realm, String sender, String receiver, PageInfo pageInfo) {
        logger.debug("listConversation.[client={}][realm={}][sender={}][receiver={}]", client.getCode(), realm, sender, receiver);
        if (sender == null || receiver == null) {
            logger.warn("both sender and receiver must not be null.[sender={}][receiver={}]", sender, receiver);
            return new PagedResult(Collections.EMPTY_LIST, 0);
        }
        if (sender.equals(receiver)) {
            logger.warn("can not list conversation for one self.[sender={}][receiver={}]", sender, receiver);
            return new PagedResult(Collections.EMPTY_LIST, 0);
        }

        PagedResult<Message> messages = messageDAO.listConversation(realm, sender, receiver, pageInfo);
        List<com.creditcloud.model.Message> result = new ArrayList(messages.getResults().size());
        for (Message message : messages.getResults()) {
            result.add(DTOUtils.getMessage(message));
        }
        return new PagedResult(result, messages.getTotalSize());
    }

    @Asynchronous
    @Override
    public void markStatus(Client client, MessageStatus status, String... messageIds) {
        messageDAO.markStatus(status, messageIds);
    }

    @Override
    public int countByReceiver(Client client,
                               Realm realm,
                               String receiver,
                               MessageType[] type,
                               MessageStatus[] status) {
        logger.debug("countByReceiver.[client={}][realm={}][receiver={}][type={}][status={}]",
                     client.getCode(), realm, receiver, ArrayUtils.toString(type), ArrayUtils.toString(status));
        return messageDAO.countByReceiver(realm, receiver, Arrays.asList(type), Arrays.asList(status));
    }

    @Override
    public PagedResult<com.creditcloud.model.Message> listByReceiver(Client client,
                                                                     Realm realm,
                                                                     String receiver,
                                                                     MessageType[] type,
                                                                     MessageStatus[] status,
                                                                     PageInfo pageInfo) {
        logger.debug("listByReceiver.[client={}][realm={}][receiver={}][type={}][status={}]",
                     client.getCode(), realm, receiver, ArrayUtils.toString(type), ArrayUtils.toString(status));
        PagedResult<Message> pagedResult = messageDAO.listByReceiver(realm, receiver, Arrays.asList(type), Arrays.asList(status), pageInfo);
        List<com.creditcloud.model.Message> result = new ArrayList<>(pagedResult.getResults().size());
        for (Message message : pagedResult.getResults()) {
            result.add(DTOUtils.getMessage(message));
        }
        return new PagedResult(result, pagedResult.getTotalSize());
    }

    @Override
    public int countBySender(Client client, Realm realm, String sender, MessageType... type) {
        logger.debug("countBySender.[client={}][realm={}][sender={}][type={}]",
                     client.getCode(), realm, sender, ArrayUtils.toString(type));
        return messageDAO.countBySender(realm, sender, Arrays.asList(type));
    }

    @Override
    public PagedResult<com.creditcloud.model.Message> listBySender(Client client, Realm realm, String sender, PageInfo pageInfo, MessageType... type) {
        logger.debug("listBySender.[client={}][realm={}][sender={}][type={}]",
                     client.getCode(), realm, sender, ArrayUtils.toString(type));
        PagedResult<Message> pagedResult = messageDAO.listBySender(realm, sender, Arrays.asList(type), pageInfo);
        List<com.creditcloud.model.Message> result = new ArrayList<>(pagedResult.getResults().size());
        for (Message message : pagedResult.getResults()) {
            result.add(DTOUtils.getMessage(message));
        }
        return new PagedResult(result, pagedResult.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.model.MessageBody> listNotification(Client client, PageInfo pageInfo, Realm... realms) {
        logger.debug("list notification called.[client={}][realm={}]", client.getCode(), Arrays.asList(realms));
        PagedResult<MessageBody> bodys = bodyDAO.listByRealmAndType(pageInfo, Arrays.asList(realms), Arrays.asList(MessageType.NOTIFICATION));
        List<com.creditcloud.model.MessageBody> result = new ArrayList<>(bodys.getResults().size());
        for (MessageBody body : bodys.getResults()) {
            result.add(DTOUtils.getMessageBody(body));
        }
        return new PagedResult<>(result, bodys.getTotalSize());
    }
}
