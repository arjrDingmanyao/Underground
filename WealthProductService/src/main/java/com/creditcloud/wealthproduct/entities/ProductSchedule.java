/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities;

import com.creditcloud.common.entities.BaseEntity;
import java.util.Date;
import javax.persistence.Embeddable;
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
@Embeddable
public class ProductSchedule extends BaseEntity {

    private static final long serialVersionUID = 20140925L;

    /**
     * appoint stage
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date appointEndTime;

    /**
     * purchase stage
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseEndTime;

    /**
     * wealth management stage
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date manageStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date manageEndTime;

    public ProductSchedule(Date appointStartTime,
                           Date appointEndTime,
                           Date purchaseStartTime,
                           Date purchaseEndTime,
                           Date manageStartTime,
                           Date manageEndTime) {
        this.appointStartTime = appointStartTime;
        this.appointEndTime = appointEndTime;
        this.purchaseStartTime = purchaseStartTime;
        this.purchaseEndTime = purchaseEndTime;
        this.manageStartTime = manageStartTime;
        this.manageEndTime = manageEndTime;
    }
}
