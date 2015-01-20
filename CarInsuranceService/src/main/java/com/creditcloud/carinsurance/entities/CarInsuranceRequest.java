package com.creditcloud.carinsurance.entities;

import com.creditcloud.carinsurance.model.enums.CarInsuranceDurationType;
import com.creditcloud.carinsurance.model.enums.CarInsurancePayStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceRequestStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceType;
import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.model.constraints.IncrementalInteger;
import java.math.BigDecimal;
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
import javax.ws.rs.FormParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 车险分期申请
 *
 * @author Administrator
 */
@NoArgsConstructor
@Entity
@Table(name = "TB_CAR_INSURANCE_REQUEST")
@NamedQueries({
    @NamedQuery(name = "CarInsuranceRequest.findByNum", query = "select c from CarInsuranceRequest c where c.insuranceNum =:insuranceNum")}
)

public class CarInsuranceRequest extends UUIDEntity {

    /**
     * 保险合同名称
     */
    @Column(nullable = true)
    @Getter
    @Setter
    private String title;
    /**
     * 保单号
     */
    @Column(nullable = false)
    @Getter
    @Setter
    private String insuranceNum;

    /**
     * 用户姓名
     */
    @Column(nullable = true)
    @Getter
    @Setter
    private String name;

    /**
     * 身份证号码
     */
    @Column(nullable = true, length = 18)
    @Getter
    @Setter
    private String idNumber;

    /**
     * 用户手机号
     */
    @Column(nullable = false)
    @Getter
    @Setter
    private String mobile;

    /**
     * 车险总金额
     */
    @Column(nullable = true, precision = 15, scale = 2)
    @Getter
    @Setter
    private BigDecimal totalAmount;

    /**
     * 借款金额
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @Getter
    @Setter
    private BigDecimal amount;

    /**
     * 手续费率 手续费率写为整数 然后代表是千分值 6 是代表0.6%
     */
    @Column(nullable = false)
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_RATE,
	    increment = 1,
	    max = LoanConstant.MAX_LOAN_RATE)
    @Getter
    @Setter
    private int rate;

    /**
     * 首付款
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @Getter
    @Setter
    private BigDecimal firstPayment;

    /**
     * 期限
     */
    @Column(nullable = false)
    @Getter
    @Setter
    private int duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private CarInsuranceDurationType durationType;

    /**
     * 手续费支付方式
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private CarInsuranceType carInsuranceType;

    /**
     * 支付状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private CarInsurancePayStatus carInsurancePayStatus = CarInsurancePayStatus.NOTPAY;

    /**
     * 受理时间
     */
    // @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 50)
    @Getter
    @Setter
    private String acceptanceDate;

    @Column(nullable = false, length = 50)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createDate;

    /**
     * 状态 用于表示该车险的状态
     */
    @FormParam("carInsuranceRequestStatus")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Getter
    @Setter
    private CarInsuranceRequestStatus carInsuranceRequestStatus;

    /**
     * 审核评论
     */
    @Column(name = "REVIEWCOMMENT", length = LoanConstant.MAX_LOAN_DESCRIPTION)
    @Getter
    @Setter
    private String reviewComment;
    /**
     * 借款状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private CarInsuranceStatus carInsuranceStatus;

    public CarInsuranceRequest(String insuranceNum, String name, String idNumber, String mobile, BigDecimal amount, int rate, CarInsuranceType carInsuranceType, BigDecimal firstPayment, int duration, CarInsuranceDurationType durationType, String acceptanceDate, Date createDate, CarInsuranceRequestStatus carInsuranceRequestStatus, CarInsuranceStatus carInsuranceStatus, String title, BigDecimal totalAmount) {
	this.insuranceNum = insuranceNum;
	this.name = name;
	this.idNumber = idNumber;
	this.mobile = mobile;
	this.amount = amount;
	this.rate = rate;
	this.carInsuranceType = carInsuranceType;
	this.firstPayment = firstPayment;
	this.duration = duration;
	this.durationType = durationType;
	this.acceptanceDate = acceptanceDate;
	this.createDate = createDate;
	this.carInsuranceRequestStatus = carInsuranceRequestStatus;
	this.carInsuranceStatus = carInsuranceStatus;
	this.title = title;
	this.totalAmount = totalAmount;
    }
}
