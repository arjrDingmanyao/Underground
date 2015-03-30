/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.fund.model.enums.FundRecordStatus;
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
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.Index;

/**
 * 用户提现历史记录
 *
 * @author Administrator
 */
@Entity
@Table(name = "TB_FUND_WITHDRAW_HISTORY",
	uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"USER_ID", "orderId"})})
@NamedQueries({
    @NamedQuery(name = "FundWithdrawHistory.listByPageInfoByStatus",
	    query = "select O from FundWithdrawHistory O where O.status in :statusList order by O.approveDateTime DESC"),
    @NamedQuery(name = "FundWithdrawHistory.findByOrderId",
	    query = "select O from FundWithdrawHistory O where O.orderId= :orderId order by O.timeRecorded ASC"),

    /**
     * count
     */
    @NamedQuery(name = "FundWithdrawHistory.countByStatus",
	    query = "select count(O) from FundWithdrawHistory O where O.status in :statusList order by O.approveDateTime DESC"),

    @NamedQuery(name = "FundWithdrawHistory.markStatus",
	    query = "update FundWithdrawHistory O set O.status = :status where O.id = :id")

})
public class FundWithdrawHistory extends UUIDEntity {

    /**
     * 用户资金账号
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @Getter
    @Setter
    private UserFund fund;

    /**
     * 资金涉及的银行账户
     */
    @ManyToOne
    @JoinColumn(name = "FUND_ACCOUNT_ID")
    @Getter
    @Setter
    private FundAccount account;
    /**
     * 金额
     */
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private String employeeId;
    /*
     * 交易订单号, 对应汇付接口中的OrdId
     */
    @Index
    @Column(nullable = false)
    @Getter
    @Setter
    private String orderId;

    /**
     * 交易流水号, 对应汇付接口中的TrxId
     */
    @Getter
    @Setter
    private String transactionId;

    /**
     * 资金状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private FundRecordStatus status;

    //提现手续费
    @Getter
    @Setter
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    private BigDecimal transferAmount = BigDecimal.ZERO;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    @Getter
    @Setter
    private Date approveDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    @Getter
    @Setter
    private Date timeRecorded;

    /**
     * 可能为失败的提示信息
     */
    @Getter
    @Setter
    private String description;

    public FundWithdrawHistory() {

    }

    public FundWithdrawHistory(UserFund fund,
	    FundAccount account,
	    String employeeId,
	    BigDecimal amount,
	    String orderId,
	    String transactionId,
	    FundRecordStatus status,
	    Date timeRecorded) {
	this.fund = fund;
	this.account = account;
	this.employeeId = employeeId;
	this.amount = amount;
	this.orderId = orderId;
	this.transactionId = transactionId;
	this.status = status;
	this.timeRecorded = timeRecorded;
    }

}
