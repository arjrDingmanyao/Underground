/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.activity.entities;

import com.creditcloud.activity.ActivityType;
import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_ACTIVITY")
@NamedQueries({
    /**
     * list query
     */
    @NamedQuery(name = "Activity.listByTarget",
                query = "select a from Activity a where a.target = :target order by a.timeRecorded"),
    @NamedQuery(name = "Activity.listByPerformer",
                query = "select a from Activity a where a.performer = :performer order by a.timeRecorded"),
    /**
     * count query
     */
    @NamedQuery(name = "Activity.countByTarge",
                query = "select count(a) from Activity a where a.target = :target"),
    @NamedQuery(name = "Activity.countByPerformer",
                query = "select count(a) from Activity a where a.performer = :performer")
})
@Data
public class Activity extends RecordScopeEntity {

    /**
     * 活动发起人,只能是User或者Employee,如果是null则为系统管理员
     */
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column =
                                           @Column(name = "PERFORMER_REALM")),
        @AttributeOverride(name = "entityId", column =
                                              @Column(name = "PERFORMER_ID"))
    })
    private RealmEntity performer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    /**
     * 活动的关联对象，例如贷款申请、贷款等
     */
    @Column(nullable = false)
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column =
                                           @Column(name = "TARGET_REALM")),
        @AttributeOverride(name = "entityId", column =
                                              @Column(name = "TARGET_ID"))
    })
    private RealmEntity target;

    private String description;

    /**
     * 可能存储更改后对象的json string
     */
    private String content;

    public Activity(RealmEntity performer,
                    ActivityType type,
                    RealmEntity target,
                    String description,
                    String content) {
        this.performer = performer;
        this.type = type;
        this.target = target;
        this.description = description;
        this.content = content;
    }

    public Activity() {
    }
}
