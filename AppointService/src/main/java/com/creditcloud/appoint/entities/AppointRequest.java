/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.appoint.enums.AppointRequestStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import lombok.Data;

/**
 *
 * @author rooseek
 */
@Data
//@Entity
@Table(name = "TB_APPOINT_REQUEST")
@NamedQueries({
    /**
     * update
     */
    @NamedQuery(name = "AppointRequest.markStatus",
                query = "update AppointRequest ar set ar.status = :status where ar.id in :ids"),
    /**
     * list
     */
    @NamedQuery(name = "AppointRequest.listByAppointment",
                query = "select ar from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList order by ar.timeRecorded DESC"),
    @NamedQuery(name = "AppointRequest.listByUser",
                query = "select ar from AppointRequest ar where ar.userId = :userId and ar.status in :statusList order by ar.timeRecorded DESC"),
    @NamedQuery(name = "AppointRequest.listByAppointmentAndUser",
                query = "select ar from AppointRequest ar where ar.appointment.id = :appointmentId and ar.userId = :userId and ar.status in :statusList order by ar.timeRecorded DESC"),
    /**
     * count
     */
    @NamedQuery(name = "AppointRequest.countByAppointment",
                query = "select count(ar) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList"),
    @NamedQuery(name = "AppointRequest.countByUser",
                query = "select count(ar) from AppointRequest ar where ar.userId = :userId and ar.status in :statusList"),
    @NamedQuery(name = "AppointRequest.countByAppointmentAndUser",
                query = "select count(ar) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.userId = :userId and ar.status in :statusList "),
    @NamedQuery(name = "AppointRequest.countUser",
                query = "select count(DISTINCT ar.userId) from AppointRequest ar where ar.status in :statusList"),
    @NamedQuery(name = "AppointRequest.countUserByAppointment",
                query = "select count(DISTINCT ar.userId) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList"),
    /**
     * sum
     */
    @NamedQuery(name = "AppointRequest.sumByAppointment",
                query = "select sum(ar.amount) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList"),
    @NamedQuery(name = "AppointRequest.sumByUser",
                query = "select sum(ar.amount) from AppointRequest ar where ar.userId = :userId and ar.status in :statusList"),
    @NamedQuery(name = "AppointRequest.sumByAppointmentAndUser",
                query = "select sum(ar.amount) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.userId = :userId and ar.status in :statusList"),
    /**
     * count/sum and group
     */
    @NamedQuery(name = "AppointRequest.countUserByBranch",
                query = "select ar.branchId as branch ,count(DISTINCT ar.userId) as userCount from AppointRequest ar where ar.status in :statusList group by branch order by userCount desc"),
    @NamedQuery(name = "AppointRequest.countUserByBranchAndAppointment",
                query = "select ar.branchId as branch ,count(DISTINCT ar.userId) as userCount from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList group by branch order by userCount desc"),
    @NamedQuery(name = "AppointRequest.countEachByBranchAndAppointment",
                query = "select ar.branchId as branch, count(ar) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList group by branch "),
    @NamedQuery(name = "AppointRequest.sumEachByBranchAndAppointment",
                query = "select ar.branchId as branch, sum(ar.amount) from AppointRequest ar where ar.appointment.id = :appointmentId  and ar.status in :statusList group by branch "),
    @NamedQuery(name = "AppointRequest.getBranchStatByAppointment",
                query = "select ar.branchId as branch, sum(ar.amount) as branchSum , count(ar)from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList group by branch  order by branchSum desc"),
    @NamedQuery(name = "AppointRequest.getBranchStat",
                query = "select ar.branchId as branch, sum(ar.amount) as branchSum  , count(ar) from AppointRequest ar where ar.status in :statusList group by branch order by branchSum desc "),
    @NamedQuery(name = "AppointRequest.getUserStat",
                query = "select ar.userId as user, sum(ar.amount) as branchSum , count(ar) from AppointRequest ar where ar.status in :statusList group by user  order by branchSum desc"),
    @NamedQuery(name = "AppointRequest.getUserStatByAppointment",
                query = "select ar.userId as user, sum(ar.amount) as branchSum , count(ar) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.status in :statusList group by user order by branchSum desc"),
    @NamedQuery(name = "AppointRequest.getDailyStat",
                query = "select cast(ar.timeRecorded as date) date, sum(ar.amount), count(ar) from AppointRequest ar where ar.timeRecorded between :from and :to and ar.status in :statusList group by date order by date"),
    @NamedQuery(name = "AppointRequest.getDailyStatByAppointment",
                query = "select cast(ar.timeRecorded as date) date, sum(ar.amount), count(ar) from AppointRequest ar where ar.appointment.id = :appointmentId and ar.timeRecorded between :from and :to and ar.status in :statusList group by date order by date")
})
public class AppointRequest extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(name = "APPOINTMENT_ID")
    private Appointment appointment;

    /**
     * 投资者userId
     */
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointRequestStatus status;

    /**
     * 认购金额
     */
    @Min(0)
    private int amount;

    /**
     * 对应机构Id
     */
    private String branchId;

    public AppointRequest() {
    }

    public AppointRequest(Appointment appointment,
                          String userId,
                          AppointRequestStatus status,
                          int amount,
                          String branchId) {
        this.appointment = appointment;
        this.userId = userId;
        this.status = status;
        this.amount = amount;
        this.branchId = branchId;
    }
}
