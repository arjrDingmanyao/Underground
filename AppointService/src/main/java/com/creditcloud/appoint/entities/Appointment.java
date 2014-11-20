/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities;

import com.creditcloud.appoint.AppointConstant;
import com.creditcloud.appoint.enums.AppointmentStatus;
import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.common.entities.embedded.InvestRule;
import com.creditcloud.model.constraints.IncrementalInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author rooseek
 */
@Data
//@Entity
@Table(name = "TB_APPOINTMENT")
@NamedQueries({
    @NamedQuery(name = "Appointment.listByStatus",
                query = "select a from Appointment a where a.status in :statusList order by a.timeOpened DESC")})
public class Appointment extends ClientScopeEntity {

    /**
     * 认购产品标题或名称
     */
    @Column(nullable = false)
    @Size(max = AppointConstant.MAX_APPOINT_TITLE)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    /**
     * 总认购金额限度
     */
    @IncrementalInteger(min = AppointConstant.MIN_QUOTA,
                        increment = AppointConstant.STEP_QUOTA,
                        max = AppointConstant.MAX_QUOTA)
    private int quota;

    /**
     * 已经可认购额度
     */
    @Min(0)
    private int amount;

    /**
     * 认购数额
     */
    @Min(0)
    private int count;

    /**
     * 投资额度控制
     */
    @Valid
    @Column(nullable = false)
    private InvestRule investRule;

    @Size(max = AppointConstant.MAX_APPOINT_DESCRIPTION)
    private String description;

    /**
     * 开放募集时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOpened;

    /**
     * 募集期限(单位小时)
     */
    @Min(AppointConstant.MIN_APPOINT_TIME_OUT)
    private int timeOut;

    /**
     * 结束募集时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeFinished;

    public Appointment() {
    }

    public Appointment(String title,
                       AppointmentStatus status,
                       int quota,
                       int amount,
                       int count,
                       InvestRule investRule,
                       String description,
                       Date timeOpened,
                       int timeOut,
                       Date timeFinished) {
        this.title = title;
        this.quota = quota;
        this.status = status;
        this.amount = amount;
        this.count = count;
        this.investRule = investRule;
        this.description = description;
        this.timeOpened = timeOpened;
        this.timeOut = timeOut;
        this.timeFinished = timeFinished;
    }
}
