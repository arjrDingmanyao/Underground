/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message.entity;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.message.MessageType;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_MESSAGE_BODY")
@NamedQueries({
    @NamedQuery(name = "MessageBody.countByRealmAndType",
                query = "select count(mb) from MessageBody mb where mb.realm in :realmList and mb.type in :typeList"),
    @NamedQuery(name = "MessageBody.listByRealmAndType",
                query = "select mb from MessageBody mb where mb.realm in :realmList and mb.type in :typeList order by mb.sentTime DESC")})
public class MessageBody extends UUIDEntity {

    @Column(nullable = true)
    private String title;

    @Column(nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SENT_TIME", nullable = false)
    private Date sentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Realm realm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @OneToMany(mappedBy = "body",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<Message> messages;

    public MessageBody() {
    }

    public MessageBody(String title, String content, Date sentTime, Realm realm, MessageType type) {
        this.title = title;
        this.content = content;
        this.sentTime = sentTime;
        this.realm = realm;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
