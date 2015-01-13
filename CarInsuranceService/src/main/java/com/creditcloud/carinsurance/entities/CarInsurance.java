package com.creditcloud.carinsurance.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.model.constraints.IncrementalInteger;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceType;
import com.creditcloud.carinsurance.model.enums.CarInsuranceDurationType;
import com.creditcloud.carinsurance.model.enums.CarInsuranceChagreBackType;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车险实体类
 *
 * @author wangwei
 */
@Data
@NoArgsConstructor
@Table(name = "TB_CAR_INSURANCE")
@Entity
@NamedQueries({
    @NamedQuery(name = "CarInsurance.listCarInsurance", query = "select c from CarInsurance c order by c.createDate"),
    @NamedQuery(name = "CarInsurance.countCarInsurance", query = "select count(c) from CarInsurance c"),
    @NamedQuery(name = "CarInsurance.listByUser",
	    query = "select o from CarInsurance o where o.userId = :userId"),
    @NamedQuery(name = "CarInsurance.findByNum", query = "select c from CarInsurance c where c.insuranceNum =:insuranceNum"),
    /**
     * count
     */
    @NamedQuery(name = "CarInsurance.countByUser",
	    query = "select count(o) from CarInsurance o where o.userId = :userId")
})
public class CarInsurance extends UUIDEntity {

    /**
     * 保单号
     */
    @Column(nullable = false, unique = true)
    private String insuranceNum;

    /**
     * 保险合同名称
     */
    @Column(nullable = true)
    private String title;

    /**
     * 用户ID
     */
    @Column(nullable = true)
    private String userId;

    /**
     * 用户登录名
     */
    @Column(nullable = true)
    private String loginname;

    /**
     * 用户姓名
     */
    @Column(nullable = true)
    private String username;

    /**
     * 用户手机号
     */
    @Column(nullable = false)
    private String mobile;

    /**
     * 车险总金额
     */
    @Column(nullable = true, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 借款金额
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * 手续费率 手续费率写为整数 然后代表是千分值 6 是代表0.6%
     */
    @Column(nullable = false)
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_RATE,
	    increment = 1,
	    max = LoanConstant.MAX_LOAN_RATE)
    private int rate;

    /**
     * 手续费支付方式
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarInsuranceType carInsuranceType;

    /**
     * 首付款
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal firstPayment;

    /**
     * 期限
     */
    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarInsuranceDurationType durationType;

    /**
     * 受理时间
     */
    // @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 50)
    private String createDate;

    /**
     * @ 扣款 通知接口
     * @ 扣款状态 未处理 扣款成功 扣款失败
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarInsuranceChagreBackType chagreBackType = CarInsuranceChagreBackType.UNASSIGNED;

    /**
     * 借款状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarInsuranceStatus carInsuranceStatus;

    public CarInsurance(String insuranceNum, String userId, String loginname, String username, String mobile, BigDecimal amount, int rate, CarInsuranceType carInsuranceType, BigDecimal firstPayment, int duration, CarInsuranceDurationType durationType, String createDate, CarInsuranceStatus carInsuranceStatus, String title, BigDecimal totalAmount) {
	this.insuranceNum = insuranceNum;
	this.userId = userId;
	this.loginname = loginname;
	this.username = username;
	this.mobile = mobile;
	this.amount = amount;
	this.rate = rate;
	this.carInsuranceType = carInsuranceType;
	this.firstPayment = firstPayment;
	this.duration = duration;
	this.durationType = durationType;
	this.createDate = createDate;
	this.carInsuranceStatus = carInsuranceStatus;
	this.title = title;
	this.totalAmount = totalAmount;
    }
}
