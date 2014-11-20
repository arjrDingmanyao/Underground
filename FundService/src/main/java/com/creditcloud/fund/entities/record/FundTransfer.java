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
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundTransfer")
@NamedQueries({
    @NamedQuery(name = "FundTransfer.getByOrderId",
                query = "select fi from FundTransfer fi where fi.fund.userId = :userId and fi.orderId = :orderId"),
    @NamedQuery(name = "FundTransfer.getByOperationAndStatus",
                query = "select fi from FundTransfer fi where fi.fund.userId = :userId and fi.operation = :operation and fi.status = :status")
})
public class FundTransfer extends FundRecord {

    private static final long serialVersionUID = 20131203L;

    public FundTransfer() {
    }

    public FundTransfer(UserFund fund,
                        RealmEntity entity,
                        FundRecordStatus status,
                        FundRecordOperation operation,
                        BigDecimal amount,
                        String orderId,
                        String transactionId,
                        String description) {
        super(fund,
              null,
              entity,
              FundRecordType.TRANSFER,
              status,
              operation,
              amount,
              orderId,
              transactionId,
              description);
    }
}
