/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.Duration;
import com.creditcloud.model.enums.loan.RepaymentMethod;
import com.creditcloud.wealthproduct.WealthProductConstant;
import com.creditcloud.wealthproduct.enums.ReturnMethod;
import com.creditcloud.wealthproduct.enums.WealthProductStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_WEALTHPRODUCT")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "WealthProduct.listByStatus",
                query = "select wp from WealthProduct wp where wp.status in :statusList order by wp.schedule.appointStartTime desc"),
    /**
     * count
     */
    @NamedQuery(name = "WealthProduct.countByStatus",
                query = "select count(wp) from WealthProduct wp where wp.status in :statusList"),
    /**
     *
     */
    @NamedQuery(name = "WealthProduct.sumByStatus",
                query = "select sum(wp.amount) from WealthProduct wp where wp.status in :statusList"),
    /**
     * update status
     */
    @NamedQuery(name = "WealthProduct.markStatus",
                query = "update WealthProduct wp set wp.status = :status where wp.id = :productId")
})
public class WealthProduct extends UUIDEntity {

    @Column(nullable = false)
    @Size(max = WealthProductConstant.MAX_TITLE_LENGHT)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnMethod returnMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentMethod repayMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WealthProductStatus status;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int rate;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = true)
    private int purchaseAmount;

    private int purchaseNumber;

    private ProductSchedule schedule;

    @Size(max = WealthProductConstant.MAX_DESCRIPTION_LENGTH)
    private String description;

    public WealthProduct(String title,
                         ReturnMethod returnMethod,
                         RepaymentMethod repayMethod,
                         WealthProductStatus status,
                         String userId,
                         int rate,
                         Duration duration,
                         int amount,
                         int purchaseAmount,
                         int purchaseNumber,
                         ProductSchedule schedule,
                         String description) {
        this.title = title;
        this.returnMethod = returnMethod;
        this.repayMethod = repayMethod;
        this.status = status;
        this.userId = userId;
        this.rate = rate;
        this.duration = duration;
        this.amount = amount;
        this.purchaseAmount = purchaseAmount;
        this.purchaseNumber = purchaseNumber;
        this.schedule = schedule;
        this.description = description;
    }

}
