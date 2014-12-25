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
 * 服务费，手续费，管理费
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundFee")
@NamedQueries({
    @NamedQuery(name = "FundFee.getByOrderId",
                query = "select ff from FundFee ff where ff.fund.userId = :userId and ff.orderId = :orderId"),
    @NamedQuery(name = "FundFee.getFundFeeByOrderId",
                query = "select ff from FundFee ff where ff.orderId = :orderId")
})
public class FundFee extends FundRecord {

    public FundFee() {
    }

    public FundFee(UserFund fund,
                   RealmEntity entity,
                   FundRecordType type,
                   FundRecordStatus status,
                   FundRecordOperation operation,
                   BigDecimal amount,
                   String orderId,
                   String transactionId,
                   String description) {
        super(fund,
              null,
              entity,
              type,
              status,
              operation,
              amount,
              orderId,
              transactionId,
              description);
    }
}
