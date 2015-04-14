/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

import com.creditcloud.common.entities.embedded.RealmEntity;
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
 * 贷款还款记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundLoanRepay")
@NamedQueries({
    @NamedQuery(name = "FundLoanRepay.getByOrderId",
                query = "select f from FundLoanRepay f where f.fund.userId = :userId and f.orderId = :orderId")
})
public class FundLoanRepay extends FundRecord {

    public FundLoanRepay() {
    }

    public FundLoanRepay(UserFund fund,
                         RealmEntity loan,
                         FundRecordStatus status,
                         FundRecordOperation operation,
                         BigDecimal amount,
                         String orderId,
                         String transactionId,
			 BigDecimal availableAmount,
                         String description) {
        super(fund,
              null,
              loan,
              FundRecordType.LOAN_REPAY,
              status,
              operation,
              amount,
              orderId,
              transactionId,
	      availableAmount,
              description);
    }
}
