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
 * 贷款放款记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundLoan")
@NamedQueries({
    @NamedQuery(name = "FundLoan.getByOrderId",
                query = "select fl from FundLoan fl where fl.fund.userId = :userId and fl.orderId = :orderId"),
    @NamedQuery(name = "FundLoan.getByOperationAndStatus",
                query = "select fl from FundLoan fl where fl.fund.userId = :userId and fl.operation = :operation and fl.status = :status and fl.entity = :entity")
})
public class FundLoan extends FundRecord {

    public FundLoan() {
    }

    public FundLoan(UserFund fund,
                    RealmEntity entity,
                    FundRecordStatus status,
                    FundRecordOperation operation,
                    BigDecimal amount,
                    String orderId,
                    String transactionId,
		     BigDecimal availableAmount,
                    String description) {
        super(fund,
              null,
              entity,
              FundRecordType.LOAN,
              status,
              operation,
              amount,
              orderId,
              transactionId,
	      availableAmount,
              description);
    }
}
