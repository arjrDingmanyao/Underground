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
 * 资金投标记录
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundInvest")
@NamedQueries({
    @NamedQuery(name = "FundInvest.getByOrderId",
                query = "select fi from FundInvest fi where fi.fund.userId = :userId and fi.orderId = :orderId"),
    @NamedQuery(name = "FundInvest.getByOperationAndStatus",
                query = "select fi from FundInvest fi where fi.fund.userId = :userId and fi.operation = :operation and fi.status = :status and fi.entity.realm = :realm and fi.entity.entityId = :entityId")
})
public class FundInvest extends FundRecord {

    public FundInvest() {
    }

    public FundInvest(UserFund fund,
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
              FundRecordType.INVEST,
              status,
              operation,
              amount,
              orderId,
              transactionId,
              description);
    }
}
