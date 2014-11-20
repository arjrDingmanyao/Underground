/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Entity
@Data
@Table(name = "TB_TAG_ENTITY", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"REALM", "ENTITYID"})})
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    /**
     * get query
     */
    @NamedQuery(name = "TagEntity.getByRealmEntity",
                query = "select te from TagEntity te where te.entity = :entity"),
    /**
     * count query
     */
    @NamedQuery(name = "TagEntity.countByTag",
                query = "select count(te) from TagEntity te where EXISTS(select tag from te.tags tag where tag = :inTag)"),
    @NamedQuery(name = "TagEntity.countByTagAndRealm",
                query = "select count(te) from TagEntity te where te.entity.realm = :realm and EXISTS(select tag from te.tags tag where tag = :inTag)"),
    @NamedQuery(name = "TagEntity.countByEntityAndTag",
                query = "select count(te) from TagEntity te where te.entity = :entity and EXISTS (select tag from te.tags tag where tag = :inTag)"),
    @NamedQuery(name = "TagEntity.checkCommontTag",
                query = "select count(te) from TagEntity te where te.id in :ids and EXISTS(select tag from te.tags tag where tag = :commonTag)"),
    /**
     * list query
     */
    @NamedQuery(name = "TagEntity.listByTag",
                query = "select te.entity from TagEntity te where EXISTS (select tag from te.tags tag where tag = :inTag) order by te.timeRecorded"),
    @NamedQuery(name = "TagEntity.listByTagAndRealm",
                query = "select te.entity from TagEntity te where te.entity.realm = :realm and EXISTS(select tag from te.tags tag where tag = :inTag) order by te.timeRecorded"),
    @NamedQuery(name = "TagEntity.listTagByRealm",
                query = "select tag from TagEntity te join te.tags tag where te.entity = :entity and tag.realm = :realm order by te.timeRecorded")
})
public class TagEntity extends RecordScopeEntity {

    @AttributeOverrides({
        @AttributeOverride(name = "realm", column =
                @Column(name = "REALM")),
        @AttributeOverride(name = "entityId", column =
                @Column(name = "ENTITYID"))
    })
    @Column(nullable = false)
    private RealmEntity entity;

    @OneToMany()
    @JoinTable(name = "RF_ENTITY_TAG",
               joinColumns =
            @JoinColumn(name = "ENTITY_ID"),
               inverseJoinColumns =
            @JoinColumn(name = "TAG_ID"))
    private Collection<Tag> tags;

}
