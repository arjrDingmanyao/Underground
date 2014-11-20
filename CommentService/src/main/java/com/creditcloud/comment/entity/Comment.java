/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.comment.entity;

import com.creditcloud.comment.CommentStatus;
import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.enums.Realm;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author rooseek
 */
@NamedQueries({
    /**
     * list query
     */
    @NamedQuery(name = "Comment.listByOwner",
                query = "select c from Comment c where c.owner = :owner and c.status in :statusList order by c.timeRecorded DESC"),
    @NamedQuery(name = "Comment.listByReceiver",
                query = "select c from Comment c where c.realm = :realm and c.receiver = :receiver and c.status in :statusList order by c.timeRecorded DESC"),
    /**
     * count query
     */
    @NamedQuery(name = "Comment.countByOwner",
                query = "select count(c) from Comment c where c.owner = :owner and c.status in :statusList"),
    @NamedQuery(name = "Comment.countByReceiver",
                query = "select count(c) from Comment c where c.realm = :realm and c.receiver = :receiver and c.status in :statusList"),
    /**
     * update query
     */
    @NamedQuery(name = "Comment.markStatus",
                query = "update Comment c set c.status = :status where c.id in :commentIds ")
})
@Entity
@Table(name = "TB_COMMENT")
public class Comment extends RecordScopeEntity {

    @Column(nullable = false)
    private String clientCode;

    /**
     * 此评论回复的其他评论id，从而支持评论的树状显示<p>
     * TODO 目前为简化流程，CommentService接口中暂不暴露此id
     */
    @Column(nullable = true)
    private String parentId;

    /**
     * 评论对应的实体,如贷款申请，认证项，图片等
     */
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column =
                                           @Column(name = "OWNER_REALM")),
        @AttributeOverride(name = "entityId", column =
                                              @Column(name = "OWNER_ID"))
    })
    @Column(nullable = false)
    private RealmEntity owner;

    /**
     * 评论内容
     */
    @Column(nullable = false)
    private String content;

    /**
     * 评论对应实体的owner的域,必须是employee/user<p>
     * 且receiver和sender必须是同一个域
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Realm realm;

    /**
     * 评论发送人
     */
    @Column(nullable = false)
    private String sender;

    /**
     * 评论对应实体的owner，可以为空
     */
    @Column(nullable = true)
    private String receiver;

    /**
     * 评论状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    public Comment() {
    }

    public Comment(String clientCode,
                   String parentId,
                   RealmEntity owner,
                   String content,
                   Realm realm,
                   String sender,
                   String receiver,
                   CommentStatus status) {
        this.clientCode = clientCode;
        this.parentId = parentId;
        this.owner = owner;
        this.content = content;
        this.realm = realm;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public String getParentId() {
        return parentId;
    }

    public RealmEntity getOwner() {
        return owner;
    }

    public Realm getRealm() {
        return realm;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setOwner(RealmEntity owner) {
        this.owner = owner;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientCode() {
        return clientCode;
    }
}
