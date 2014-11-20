/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.image.entity;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.constraints.ClientCode;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_IMAGE",
       uniqueConstraints = {
    /**
     * 每个entity下面图片名唯一
     */
    @UniqueConstraint(columnNames = {"CLIENT_CODE", "REALM", "ENTITY_ID", "IMAGE_NAME"})
})
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Image.listByOwner",
                query = "select i from Image i where i.clientCode = :clientCode and i.owner.realm = :realm and i.owner.entityId = :entityId order by i.timeUpload DESC"),
    /**
     * count
     */
    @NamedQuery(name = "Image.countByOwner",
                query = "select count(i) from Image i where i.clientCode = :clientCode and i.owner.realm = :realm and i.owner.entityId = :entityId"),
    /**
     * delete
     */
    @NamedQuery(name = "Image.deleteByOwner",
                query = "delete from Image i where i.clientCode = :clientCode and i.owner.realm = :realm and i.owner.entityId = :entityId"),
    @NamedQuery(name = "Image.deleteByOwnerAndName",
                query = "delete from Image i where i.clientCode = :clientCode and i.owner.realm = :realm and i.owner.entityId = :entityId and i.name = :name"),
    /**
     * get
     */
    @NamedQuery(name = "Image.getByOwnerAndName",
                query = "select i from Image i where i.clientCode = :clientCode and i.owner.realm = :realm and i.owner.entityId = :entityId and i.name = :name")
})
public class Image extends UUIDEntity {

    /**
     * 客户唯一代号
     */
    @ClientCode
    @Column(name = "CLIENT_CODE", nullable = false)
    private String clientCode;

    /**
     * 文件拥有者或关联者,可以为客户、员工、用户等
     */
    @Column(nullable = false)
    @AttributeOverrides({
        @AttributeOverride(name = "entityId", column =
                @Column(name = "ENTITY_ID"))
    })
    private RealmEntity owner;

    /**
     * 上传时间
     */
    @Column(nullable = false, name = "TIME_UPLOAD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpload;

    /**
     * 图片名，对于每个实体图片名是唯一的
     */
    @Column(name = "IMAGE_NAME", nullable = false)
    private String name;

    @PrePersist
    void setup() {
        timeUpload = new Date();
    }

    public Image() {
    }

    public Image(String clientCode,
                 RealmEntity owner,
                 String name) {
        this.clientCode = clientCode;
        this.owner = owner;
        this.name = name;
    }

    public String getClientCode() {
        return clientCode;
    }

    public Date getTimeUpload() {
        return timeUpload;
    }

    public String getName() {
        return name;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public void setTimeUpload(Date timeUpload) {
        this.timeUpload = timeUpload;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmEntity getOwner() {
        return owner;
    }

    public void setOwner(RealmEntity owner) {
        this.owner = owner;
    }
}
