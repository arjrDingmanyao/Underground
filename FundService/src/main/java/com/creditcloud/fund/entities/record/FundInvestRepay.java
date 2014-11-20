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
 * 投标还款记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundInvestRepay")
@NamedQueries({
    @NamedQuery(name = "FundInvestRepay.getByOrderId",
                query = "select f from FundInvestRepay f where f.fund.userId = :userId and f.orderId = :orderId"),
    @NamedQuery(name = "FundInvestRepay.getByOperationAndStatus",
                query = "select fi from FundInvestRepay fi where fi.fund.userId = :userId and fi.operation = :operation and fi.status = :status and fi.entity = :entity and fi.description = :period")
})
public class FundInvestRepay extends FundRecord {

    public FundInvestRepay() {
    }

    public FundInvestRepay(UserFund fund,
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
              FundRecordType.INVEST_REPAY,
              status,
              operation,
              amount,
              orderId,
              transactionId,
              description);
    }
}
