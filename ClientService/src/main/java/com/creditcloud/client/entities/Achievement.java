/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.enums.client.PerformanceType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * 用于考核员工绩效和业绩的具体成绩,如某年某月某日借款人开户一个,或者实地调查贷款请求一个
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_ACHIVEMENT")
public class Achievement extends UUIDEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    public Achievement() {
    }

    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
        this.type = type;
    }

}
