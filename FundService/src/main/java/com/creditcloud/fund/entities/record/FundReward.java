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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * 奖励,如用积分兑换可用金额
 *
 * @author rooseek
 */
@Entity
@DiscriminatorValue("FundReward")
@NamedQueries({
    @NamedQuery(name="FundReward.countByTypeAndStatus",
                query = "select count(fr) from FundRecord fr where fr.entity = :target and fr.type = :type and fr.status = :status")
})
public class FundReward extends FundRecord {

    public FundReward() {
    }

    public FundReward(UserFund fund,
                      FundAccount account,
                      RealmEntity entity,
                      FundRecordType type,
                      FundRecordStatus status,
                      BigDecimal amount,
                      String orderId,
                      String transactionId,
                      String description) {
        super(fund,
              account,
              entity,
              type,
              status,
              FundRecordOperation.IN,
              amount,
              orderId,
              transactionId,
              description);
    }
}
