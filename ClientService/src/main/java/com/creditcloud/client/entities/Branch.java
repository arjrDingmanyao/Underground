/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.common.entities.embedded.Location;
import com.creditcloud.model.enums.client.BranchType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author sobranie
 */
@Data
@Entity
@Table(name = "TB_BRANCH", uniqueConstraints = {
    //每个客户分支机构code唯一
    @UniqueConstraint(columnNames = {"ClientCode", "CODE"}),
    //每个客户分支机构name唯一
    @UniqueConstraint(columnNames = {"ClientCode", "NAME"})})
@NamedQueries({
    /**
     * list query
     */
    @NamedQuery(name = "Branch.listByClient",
                query = "select b from Branch b where b.clientCode = :clientCode order by b.timeCreated DESC"),
    @NamedQuery(name = "Branch.listByPrincipal",
                query = "select b from Branch b where b.principal.id = :id order by b.timeCreated DESC"),
    @NamedQuery(name = "Branch.listByContact",
                query = "select b from Branch b where b.contactPerson.id = :id order by b.timeCreated DESC"),
    /**
     * count query
     */
    @NamedQuery(name = "Branch.countByClient",
                query = "select count(b) from Branch b where b.clientCode = :clientCode"),
    @NamedQuery(name = "Branch.countByPrincipal",
                query = "select count(b) from Branch b where b.principal.id = :id"),
    @NamedQuery(name = "Branch.countByContact",
                query = "select count(b) from Branch b where b.contactPerson.id = :id"),
    /**
     * get query
     */
    @NamedQuery(name = "Branch.getByCode",
                query = "select b from Branch b where b.clientCode = :clientCode and b.code = :code"),
    @NamedQuery(name = "Branch.getByName",
                query = "select b from Branch b where b.clientCode = :clientCode and b.name = :name")
})
public class Branch extends ClientScopeEntity {

    @Column(nullable = true)
    private String parentId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BranchType type;

    @Column(nullable = true)
    private Location location;

    /**
     * 负责人
     */
    @ManyToOne
    @JoinColumn(name = "PRINCIPAL_ID", nullable = false)
    private Employee principal;

    /**
     * 联系人
     */
    @ManyToOne
    @JoinColumn(name = "CONTACTPERSON_ID", nullable = false)
    private Employee contactPerson;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeLastUpdated;

    @PrePersist
    private void setup() {
        this.timeCreated = new Date();
    }

    public Branch() {
    }

    public Branch(String parentId,
                  String name,
                  String code,
                  BranchType type,
                  Location location,
                  Employee principal,
                  Employee contactPerson,
                  String description) {
        this.parentId = parentId;
        this.name = name;
        this.code = code;
        this.type = type;
        this.location = location;
        this.principal = principal;
        this.contactPerson = contactPerson;
        this.description = description;
    }
}
