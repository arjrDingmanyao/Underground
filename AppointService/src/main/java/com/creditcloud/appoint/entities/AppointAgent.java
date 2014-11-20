/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities;

import com.creditcloud.common.entities.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import lombok.Data;

/**
 *
 * @author rooseek
 */
@Data
//@Entity
@Table(name = "TB_APPOINT_AGENT")
public class AppointAgent extends BaseEntity {

    /**
     * 代理人本身也是特殊的user
     */
    @Id
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String description;

    /**
     * 认购配额
     */
    @Min(0)
    private int quota;

    /**
     * 已认购额度
     */
    @Min(0)
    private int amount;

    /**
     * 认购数目
     */
    @Min(0)
    private int count;

    public AppointAgent() {
    }

    public AppointAgent(String userId, String description, int quota, int amount, int count) {
        this.userId = userId;
        this.description = description;
        this.quota = quota;
        this.amount = amount;
        this.count = count;
    }
}
