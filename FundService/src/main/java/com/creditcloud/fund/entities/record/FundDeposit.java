/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

import com.creditcloud.fund.entities.FundAccount;
import com.creditcloud.fund.entities.UserFund;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import java.math.BigDecimal;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * 账户充值记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundDeposit")
@NamedQueries({
    @NamedQuery(name = "FundDeposit.getByOrderId",
                query = "select fd from FundDeposit fd where fd.fund.userId = :userId and fd.orderId = :orderId"),
    @NamedQuery(name = "FundDeposit.listByOperationAndStatusInRange",
                query = "select fd from FundDeposit fd where fd.operation in :operationList and fd.status in :statusList and fd.timeRecorded between :from and :to order by fd.timeRecorded DESC"),})
public class FundDeposit extends FundRecord {

    public FundDeposit(UserFund fund,
                       FundAccount account,
                       FundRecordStatus status,
                       FundRecordOperation operation,
                       BigDecimal amount,
                       String orderId,
                       String transactionId,
		       BigDecimal availableAmount,
                       String description) {
        super(fund,
              account,
              null,
              FundRecordType.DEPOSIT,
              status,
              operation,
              amount,
              orderId,
              transactionId,
	      availableAmount,
              description);
    }

    public FundDeposit() {
    }
}
