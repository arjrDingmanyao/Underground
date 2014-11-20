/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.file.entity;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
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
@Table(name = "TB_FILE_INFO",
       uniqueConstraints = {
    /**
     * 每个entity下面文件名唯一
     */
    @UniqueConstraint(columnNames = {"CLIENT_CODE", "ENTITY_ID", "REALM", "NAME"})
})
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "FileInfo.listByOwner",
                query = "select fi from FileInfo fi where fi.clientCode = :clientCode and fi.owner.realm = :realm and fi.owner.entityId = :entityId order by fi.timeUpload DESC"),
    /**
     * count
     */
    @NamedQuery(name = "FileInfo.countByOwner",
                query = "select count(fi) from FileInfo fi where fi.clientCode = :clientCode and fi.owner.realm = :realm and fi.owner.entityId = :entityId"),
    /**
     * delete
     */
    @NamedQuery(name = "FileInfo.deleteByOwner",
                query = "delete from FileInfo fi where fi.clientCode = :clientCode and fi.owner.realm = :realm and fi.owner.entityId = :entityId"),
    @NamedQuery(name = "FileInfo.deleteByOwnerAndName",
                query = "delete from FileInfo fi where fi.clientCode = :clientCode and fi.owner.realm = :realm and fi.owner.entityId = :entityId and fi.name = :name"),
    /**
     * get
     */
    @NamedQuery(name = "FileInfo.getByOwnerAndName",
                query = "select fi from FileInfo fi where fi.clientCode = :clientCode and fi.owner.realm = :realm and fi.owner.entityId = :entityId and fi.name = :name")
})
public class FileInfo extends UUIDEntity {

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
     * 图片名，对于每个owner图片名是唯一的
     */
    @Column(nullable = false)
    private String name;

    /**
     * 上传时间
     */
    @Column(nullable = false, name = "TIME_UPLOAD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpload;

    @PrePersist
    void setup() {
        timeUpload = new Date();
    }

    public FileInfo() {
    }

    public FileInfo(String clientCode,
                    RealmEntity owner,
                    String name) {
        this.clientCode = clientCode;
        this.owner = owner;
        this.name = name;
    }

    public RealmEntity getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Date getTimeUpload() {
        return timeUpload;
    }

    public void setOwner(RealmEntity owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeUpload(Date timeUpload) {
        this.timeUpload = timeUpload;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientCode() {
        return clientCode;
    }
}
