/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.Index;

/**
 * 资金历史记录
 *
 * @author rooseek
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("FundRecord")
@DiscriminatorColumn(name = "DTYPE")
@Table(name = "TB_FUND_RECORD",
	uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"USER_ID", "type", "orderId"})})
@NamedQueries({
    /**
     * get query
     */
    @NamedQuery(name = "FundRecord.getByOrderId",
	    query = "select fr from FundRecord fr where fr.fund.userId = :userId and fr.type = :type and fr.orderId = :orderId"),
    @NamedQuery(name = "FundRecord.getByOrderIdAndType",
	    query = "select fr from FundRecord fr where fr.type = :type and fr.orderId = :orderId order by fr.timeRecorded DESC"),
    /**
     * count query
     */
    @NamedQuery(name = "FundRecord.countByUserAndTypeAndOperationAndStatus",
	    query = "select count(fr) from FundRecord fr where fr.fund.userId = :userId and fr.type in :typeList and fr.operation in :operationList and fr.status in :statusList"),
    @NamedQuery(name = "FundRecord.countByType",
	    query = "select count(fr) from FundRecord fr where fr.type in :typeList"),
    @NamedQuery(name = "FundRecord.countByUserAndTypeAndTime",
	    query = "select count(fr) from FundRecord fr where fr.fund.userId = :userId and fr.timeRecorded between :from and :to and fr.type in :typeList"),
    @NamedQuery(name = "FundRecord.countByUserAndTypeAndOperationAndStatusAndTime",
	    query = "select count(fr) from FundRecord fr where fr.fund.userId = :userId and fr.timeRecorded between :from and :to and fr.type in :typeList and fr.operation in :operationList and fr.status in :statusList"),
    /**
     * list query
     */
    @NamedQuery(name = "FundRecord.listByUserAndTypeAndTime",
	    query = "select fr from FundRecord fr where fr.fund.userId = :userId and fr.timeRecorded between :from and :to and fr.type in :typeList order by fr.timeRecorded DESC"),
    @NamedQuery(name = "FundRecord.listByType",
	    query = "select fr from FundRecord fr where fr.type in :typeList order by fr.timeRecorded ASC"),
    @NamedQuery(name = "FundRecord.listByUserAndTypeAndOperationAndStatus",
	    query = "select fr from FundRecord fr where fr.fund.userId = :userId and fr.type in :typeList and fr.operation in :operationList and fr.status in :statusList order by fr.timeRecorded DESC"),
    @NamedQuery(name = "FundRecord.listByUserAndTypeAndOperationAndStatusAndTime",
	    query = "select fr from FundRecord fr where fr.fund.userId = :userId and fr.timeRecorded between :from and :to and fr.type in :typeList and fr.operation in :operationList and fr.status in :statusList order by fr.timeRecorded DESC"),
    /**
     * update
     */
    @NamedQuery(name = "FundRecord.markStatus",
	    query = "update FundRecord fr set fr.status = :status where fr.id = :id")
})
public class FundRecord extends RecordScopeEntity {

    /**
     * 用户资金账号
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    protected UserFund fund;

    /**
     * 资金涉及的银行账户
     */
    @ManyToOne
    @JoinColumn(name = "FUND_ACCOUNT_ID")
    protected FundAccount account;

    /*
     * 资金记录对应的实体，例如投标就对应InvestId
     */
    protected RealmEntity entity;

    /**
     * 资金记录类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected FundRecordType type;

    /**
     * 资金状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected FundRecordStatus status;

    /**
     * 资金操作
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected FundRecordOperation operation;

    /**
     * 金额
     */
    @Column(nullable = false,
	    precision = 15,
	    scale = 2)
    protected BigDecimal amount;

    /*
     * 交易订单号, 对应汇付接口中的OrdId
     */
    @Index
    @Column(nullable = false)
    protected String orderId;

    /**
     * 交易流水号, 对应汇付接口中的TrxId
     */
    protected String transactionId;

    /**
     * 可能为失败的提示信息
     */
    protected String description;

    /**
     * 给fundWithdraw（提现使用）
     */
    @Transient
    @Getter
    @Setter
    protected BigDecimal withdrawFee;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    @Getter
    @Setter
    protected Date approveDateTime;

    public FundRecord(UserFund fund,
	    FundAccount account,
	    RealmEntity entity,
	    FundRecordType type,
	    FundRecordStatus status,
	    FundRecordOperation operation,
	    BigDecimal amount,
	    String orderId,
	    String transactionId,
	    String description) {
	this.fund = fund;
	this.account = account;
	this.entity = entity;
	this.type = type;
	this.status = status;
	this.operation = operation;
	this.amount = amount;
	this.orderId = orderId;
	this.transactionId = transactionId;
	this.description = description;
    }

    public FundRecord(UserFund fund,
	    FundAccount account,
	    RealmEntity entity,
	    FundRecordType type,
	    FundRecordStatus status,
	    FundRecordOperation operation,
	    BigDecimal amount,
	    String orderId,
	    String transactionId,
	    String description, BigDecimal withdrawFee, Date approveDateTime) {
	this.fund = fund;
	this.account = account;
	this.entity = entity;
	this.type = type;
	this.status = status;
	this.operation = operation;
	this.amount = amount;
	this.orderId = orderId;
	this.transactionId = transactionId;
	this.description = description;
	this.withdrawFee = withdrawFee;
	this.approveDateTime = approveDateTime;
    }

    public FundRecord() {
    }

    public UserFund getFund() {
	return fund;
    }

    public RealmEntity getEntity() {
	return entity;
    }

    public FundRecordType getType() {
	return type;
    }

    public FundRecordStatus getStatus() {
	return status;
    }

    public FundRecordOperation getOperation() {
	return operation;
    }

    public BigDecimal getAmount() {
	return amount;
    }

    public String getOrderId() {
	return orderId;
    }

    public String getTransactionId() {
	return transactionId;
    }

    public String getDescription() {
	return description;
    }

    public void setFund(UserFund fund) {
	this.fund = fund;
    }

    public void setEntity(RealmEntity entity) {
	this.entity = entity;
    }

    public void setType(FundRecordType type) {
	this.type = type;
    }

    public void setStatus(FundRecordStatus status) {
	this.status = status;
    }

    public void setOperation(FundRecordOperation operation) {
	this.operation = operation;
    }

    public void setAmount(BigDecimal amount) {
	this.amount = amount;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

    public void setTransactionId(String transactionId) {
	this.transactionId = transactionId;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public FundAccount getAccount() {
	return account;
    }

    public void setAccount(FundAccount account) {
	this.account = account;
    }

}
