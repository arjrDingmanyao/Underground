/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 账户提现记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundWithdraw")
@NamedQueries({
    /**
     * count query
     */
    @NamedQuery(name = "FundWithdraw.countyByFundWithdrawHistoryStatus",
	    query = "select count(fw) from FundWithdraw fw where fw.status in :statusList"),

    @NamedQuery(name = "FundWithdraw.listFundWithdrawHistoryByStatus",
	    query = "select fw from FundWithdraw fw where fw.status in :statusList"),

    @NamedQuery(name = "FundWithdraw.listByStatus",
	    query = "select fw from FundWithdraw fw where fw.status in :statusList"),
    @NamedQuery(name = "FundWithdraw.listByOperationAndStatusInRange",
	    query = "select fw from FundWithdraw fw where fw.operation in :operationList and fw.status in :statusList and fw.timeRecorded between :from and :to order by fw.timeRecorded DESC"),
    @NamedQuery(name = "FundWithdraw.getByOrderId",
	    query = "select fw from FundWithdraw fw where fw.fund.userId = :userId and fw.orderId = :orderId"),
    @NamedQuery(name = "FundWithdraw.getByOperationAndStatus",
	    query = "select fw from FundWithdraw fw where fw.fund.userId = :userId and fw.operation = :operation and fw.status = :status and fw.entity.realm = :realm and fw.entity.entityId = :entityId")
})
public class FundWithdraw extends FundRecord {

    public FundWithdraw() {
    }

    public FundWithdraw(UserFund fund,
	    FundAccount account,
	    RealmEntity entity,
	    FundRecordStatus status,
	    FundRecordOperation operation,
	    BigDecimal amount,
	    String orderId,
	    String transactionId,
	    String description, BigDecimal withdrawFee, Date approveDateTime) {
	super(fund,
		account,
		entity,
		FundRecordType.WITHDRAW,
		status,
		operation,
		amount,
		orderId,
		transactionId,
		description, withdrawFee, approveDateTime);
    }

    public FundWithdraw(UserFund fund,
	    FundAccount account,
	    RealmEntity entity,
	    FundRecordStatus status,
	    FundRecordOperation operation,
	    BigDecimal amount,
	    String orderId,
	    String transactionId,
	    BigDecimal availableAmount,
	    String description) {
	super(fund,
		account,
		entity,
		FundRecordType.WITHDRAW,
		status,
		operation,
		amount,
		orderId,
		transactionId,
		availableAmount,
		description);
    }
}
