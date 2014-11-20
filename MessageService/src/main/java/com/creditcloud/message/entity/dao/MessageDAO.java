/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.message.entity.Message;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.message.MessageStatus;
import static com.creditcloud.model.enums.message.MessageStatus.ARCHIVED;
import static com.creditcloud.model.enums.message.MessageStatus.NEW;
import static com.creditcloud.model.enums.message.MessageStatus.READ;
import com.creditcloud.model.enums.message.MessageType;
import com.creditcloud.model.misc.PagedResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class MessageDAO extends AbstractDAO<Message> {

    @PersistenceContext(unitName = "MessagePU")
    private EntityManager em;

    private static final List<MessageStatus> NOT_DELETED = Arrays.asList(NEW, READ, ARCHIVED);

    public MessageDAO() {
        super(Message.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByReceiver(Realm realm,
                               String receiver,
                               List<MessageType> typeList,
                               List<MessageStatus> statusList) {
        if (typeList == null || typeList.isEmpty()) {
            return 0;
        }
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Message.countByReceiver", Long.class)
                .setParameter("realm", realm)
                .setParameter("receiver", receiver)
                .setParameter("typeList", typeList)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public int countBySender(Realm realm,
                             String sender,
                             List<MessageType> typeList) {
        if (typeList == null || typeList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Message.countBySender", Long.class)
                .setParameter("realm", realm)
                .setParameter("sender", sender)
                .setParameter("typeList", typeList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 列出对话，注意不要把已删除的消息也显示出来
     *
     * @param realm
     * @param sender
     * @param receiver
     * @return
     */
    public PagedResult<Message> listConversation(Realm realm, String sender, String receiver, PageInfo info) {
        //get results
        Query query = getEntityManager()
                .createNamedQuery("Message.listConversation", Message.class)
                .setParameter("realm", realm)
                .setParameter("sender", sender)
                .setParameter("receiver", receiver)
                .setParameter("statusList", NOT_DELETED);
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());
        List<Message> messages = query.getResultList();
        //get total size
        int totalSize = countConversation(realm, sender, receiver);

        return new PagedResult(messages, totalSize);
    }

    public int countConversation(Realm realm, String sender, String receiver) {
        Long result = getEntityManager()
                .createNamedQuery("Message.countConversation", Long.class)
                .setParameter("realm", realm)
                .setParameter("sender", sender)
                .setParameter("receiver", receiver)
                .setParameter("statusList", NOT_DELETED)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public void markStatus(MessageStatus status, String... messageIds) {
        if (messageIds == null || messageIds.length == 0) {
            return;
        }
        getEntityManager()
                .createNamedQuery("Message.markStatus")
                .setParameter("status", status)
                .setParameter("messageIds", Arrays.asList(messageIds))
                .executeUpdate();
    }

    public PagedResult<Message> listByReceiver(Realm realm,
                                               String receiver,
                                               List<MessageType> typeList,
                                               List<MessageStatus> statusList,
                                               PageInfo pageInfo) {
        if (typeList == null || typeList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        if (statusList == null || statusList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        //get results
        Query query = getEntityManager()
                .createNamedQuery("Message.listByReceiver", Message.class)
                .setParameter("realm", realm)
                .setParameter("receiver", receiver)
                .setParameter("typeList", typeList)
                .setParameter("statusList", statusList);
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());
        List<Message> messages = query.getResultList();
        //get total size
        int totalSize = countByReceiver(realm, receiver, typeList, statusList);

        return new PagedResult(messages, totalSize);
    }

    public PagedResult<Message> listBySender(Realm realm,
                                             String sender,
                                             List<MessageType> typeList,
                                             PageInfo pageInfo) {
        if (typeList == null || typeList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        //get results
        Query query = getEntityManager()
                .createNamedQuery("Message.listBySender", Message.class)
                .setParameter("realm", realm)
                .setParameter("sender", sender)
                .setParameter("typeList", typeList);
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());
        List<Message> messages = query.getResultList();
        //get total size
        int totalSize = countBySender(realm, sender, typeList);

        return new PagedResult(messages, totalSize);
    }
}
