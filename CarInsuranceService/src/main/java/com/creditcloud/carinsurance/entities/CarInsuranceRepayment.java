/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车险还款计划实体类
 *
 * @author wangwei
 */
@Data
@NoArgsConstructor
@Table(name = "TB_CAR_INSURANCE_REPAYMENT")
@Entity
@NamedQueries({
    @NamedQuery(name = "CarInsuranceRepayment.listCarInsuranceByNum", query = "select c from CarInsuranceRepayment c where c.carInsurance.insuranceNum=:insuranceNum order by c.currentPeriod asc"),
    @NamedQuery(name = "CarInsuranceRepayment.countCarInsuranceByNum", query = "select count(c) from CarInsuranceRepayment c where c.carInsurance.insuranceNum=:insuranceNum"),

    @NamedQuery(name = "CarInsuranceRepayment.listCarInsuranceByCarInsurance", query = "select c from CarInsuranceRepayment c where c.carInsurance=:carInsurance order by c.currentPeriod asc"),

    @NamedQuery(name = "CarInsuranceRepayment.listCarInsuranceDueRepay",
	    query = "select o from CarInsuranceRepayment o where o.dueDate between :from and :to and o.status in :statusList order by o.dueDate ASC"),
    @NamedQuery(name = "CarInsuranceRepayment.listCarInsuranceDueRepayByUser",
	    query = "select o from CarInsuranceRepayment o where o.carInsurance.userId = :userId and o.dueDate between :from and :to and o.status in :statusList order by o.dueDate ASC"),
    @NamedQuery(name = "CarInsuranceRepayment.findByOrderId",
	    query = "select O from CarInsuranceRepayment O where O.orderId= :orderId"),

    @NamedQuery(name = "CarInsuranceRepayment.findById",
	    query = "select c from CarInsuranceRepayment c where c.id =:id"),
    @NamedQuery(name = "CarInsuranceRepayment.countCarInsuranceDueRepay",
	    query = "select count(o) from CarInsuranceRepayment o where o.dueDate between :from and :to and o.status in :statusList"),
    @NamedQuery(name = "CarInsuranceRepayment.countCarInsuranceDueRepayByUser",
	    query = "select count(o) from CarInsuranceRepayment o where o.carInsurance.userId = :userId and o.dueDate between :from and :to and o.status in :statusList"),

    /**
     * update
     */
    @NamedQuery(name = "CarInsuranceRepayment.markStatus",
	    query = "update CarInsuranceRepayment o set o.status = :status where o.id in :ids"),
    /**
     * 逾期
     */
    @NamedQuery(name = "CarInsuranceRepayment.listDueRepay",
	    query = "select o from CarInsuranceRepayment o where o.dueDate between :from and :to and o.status in :statusList order by o.dueDate ASC"),
    @NamedQuery(name = "CarInsuranceRepayment.countDueRepay",
	    query = "select count(o) from CarInsuranceRepayment o where o.dueDate between :from and :to and o.status in :statusList"),})
public class CarInsuranceRepayment extends UUIDEntity {

    @ManyToOne
    @JoinColumn(name = "INSURANCE_NUM", nullable = false)
    private CarInsurance carInsurance;

    //当前还款期数
    @Column(nullable = false)
    @Min(LoanConstant.MIN_LOAN_DURATION)
    @Max(LoanConstant.MAX_LOAN_DURATION)
    private int currentPeriod;

    //应还日期
//    @Converter(name = "localDateConverter",
//               converterClass = LocalDateConverter.class)
//    @Convert("localDateConverter")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date dueDate;

    //当期应还金额
    @Min(0)
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    private BigDecimal amountPrincipal;

    //当期罚息
    @Min(0)
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    private BigDecimal amountInterest;

    //提还违约金
    @Min(0)
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    private BigDecimal amountBreach=BigDecimal.ZERO;

    //还款状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarInsuranceStatus status;

    //实际还款
    @Min(0)
    @Column(nullable = true,
	    precision = 15,
	    scale = 2)
    private BigDecimal repayAmount;

    //实际还款日期
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date repayDate;

    /*
     * 交易订单号, 对应汇付接口中的OrdId
     */
    @Column(nullable = true, unique = true)
    private String orderId;

    public CarInsuranceRepayment(BigDecimal amountInterest, CarInsurance carInsurance, int currentPeriod, Date dueDate, BigDecimal amountPrincipal, CarInsuranceStatus status, BigDecimal repayAmount) {
	this.amountInterest = amountInterest;
	this.carInsurance = carInsurance;
	this.currentPeriod = currentPeriod;
	this.dueDate = dueDate;
	this.amountPrincipal = amountPrincipal;
	this.status = status;
	this.repayAmount = repayAmount;
    }
}
