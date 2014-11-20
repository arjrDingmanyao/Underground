/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.Repayment;
import com.creditcloud.model.enums.loan.RepaymentStatus;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Table(name="TB_WEALTHPRODUCT_PURCHASE_REPAYMENT")

public class PurchaseRepayment extends UUIDEntity {

    private static final long serialVersionUID = 20140924L;

    @ManyToOne
    @JoinColumn(name = "PURCHASE_ID")
    private Purchase purchase;

    @Column(nullable = false)
    private int period;

    @Enumerated(EnumType.STRING)
    private RepaymentStatus status;

    @Column(nullable = false)
    private Repayment repayment;

    @Column(nullable = true,
            precision = 15,
            scale = 2)
    private BigDecimal repayAmount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date repayDate;

    public PurchaseRepayment(Purchase purchase,
                             int period,
                             Repayment repayment,
                             BigDecimal repayAmount,
                             Date repayDate,
                             RepaymentStatus status) {
        this.purchase = purchase;
        this.period = period;
        this.repayment = repayment;
        this.repayAmount = repayAmount;
        this.repayDate = repayDate;
        this.status = status;
    }
}
