/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.entities;

import com.creditcloud.common.entities.BaseEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_FACTORING")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Factoring.listByFactoringCorporation",
                query = "select f from Factoring f where f.factoringCorporation = :factoringCorporation order by f.timeCreated desc"),
    @NamedQuery(name = "Factoring.listByFinanceCorporation",
                query = "select f from Factoring f where f.financeCorporation = :financeCorporation order by f.timeCreated desc"),
    /**
     * count
     */
    @NamedQuery(name = "Factoring.countByFactoringCorporation",
                query = "select count(f) from Factoring f where f.factoringCorporation = :factoringCorporation "),
    @NamedQuery(name = "Factoring.countByFinanceCorporation",
                query = "select count(f) from Factoring f where f.financeCorporation = :financeCorporation ")
})
public class Factoring extends BaseEntity {

    /**
     * 关联借款申请
     */
    @Id
    private String id;

    /**
     * 关联保理公司
     */
    private String factoringCorporation;

    /**
     * 央行登记号
     */
    private String centralBankRegisterNo;

    //优质民营，上市公司，地方国企，大型央企
    private String type;

    /**
     * 应收账款
     */
    @Min(0)
    private int receivables;

    /**
     * 关联融资企业
     */
    private String financeCorporation;

    /**
     * 关联债务企业描述
     */
    private String debtDescription;

    /**
     * 保理项目介绍
     */
    private String description;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timeCreated;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timeLastUpdated;

    /**
     * 反保理措施描述
     */
    private String antiDescription;

    /**
     * 历史交易信息
     */
    private String historyDescription;

    @PrePersist
    private void setup() {
        Date date = new Date();
        this.timeCreated = date;
        this.timeLastUpdated = date;
    }

    public Factoring(String id,
                     String factoringCorporation,
                     String centralBankRegisterNo,
                     String type,
                     int receivables,
                     String financeCorporation,
                     String debtDescription,
                     String description,
                     String antiDescription,
                     String historyDescription) {
        this.id = id;
        this.factoringCorporation = factoringCorporation;
        this.centralBankRegisterNo = centralBankRegisterNo;
        this.type = type;
        this.receivables = receivables;
        this.financeCorporation = financeCorporation;
        this.debtDescription = debtDescription;
        this.description = description;
        this.antiDescription = antiDescription;
        this.historyDescription = historyDescription;
    }
}
