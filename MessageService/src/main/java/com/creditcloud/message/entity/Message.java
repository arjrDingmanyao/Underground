/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message.entity;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.enums.message.MessageStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_MESSAGE")
@NamedQueries({
    @NamedQuery(name = "Message.markStatus",
                query = "update Message m set m.status = :status where m.id in :messageIds"),
    @NamedQuery(name = "Message.countByReceiver",
                query = "select count(m) from Message m where m.body.realm = :realm AND (m.receiver = :receiver AND m.body.type in :typeList and m.status in :statusList)"),
    @NamedQuery(name = "Message.countBySender",
                query = "select count(m) from Message m where m.body.realm = :realm AND (m.sender = :sender AND m.body.type in :typeList)"),
    @NamedQuery(name = "Message.listByReceiver",
                query = "select m from Message m where m.body.realm = :realm AND (m.receiver = :receiver AND m.body.type in :typeList and m.status in :statusList) order by m.body.sentTime DESC"),
    @NamedQuery(name = "Message.listBySender",
                query = "select m from Message m where m.body.realm = :realm AND (m.sender = :sender AND m.body.type in :typeList) order by m.body.sentTime DESC"),
    @NamedQuery(name = "Message.countConversation",
                query = "select count(m) from Message m where m.body.realm = :realm AND m.status in :statusList AND (m.receiver = :receiver AND m.sender = :sender OR m.receiver = :sender and m.sender = :receiver)"),
    @NamedQuery(name = "Message.listConversation",
                query = "select m from Message m where m.body.realm = :realm AND m.status in :statusList AND (m.receiver = :receiver AND m.sender = :sender OR m.receiver = :sender and m.sender = :receiver) order by m.body.sentTime DESC")
})
public class Message extends UUIDEntity {

    @ManyToOne
    @JoinColumn(name = "BODY_ID", nullable = false)
    private MessageBody body;

    @Index
    @Column(nullable = true)
    private String sender;

    @Index
    @Column(nullable = false)
    private String receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    public Message() {
    }

    public Message(MessageBody body, String sender, String receiver, MessageStatus status) {
        this.body = body;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public MessageBody getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public MessageStatus getStatus() {
        return status;
    }
}
