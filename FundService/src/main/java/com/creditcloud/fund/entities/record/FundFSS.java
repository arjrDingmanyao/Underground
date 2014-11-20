/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

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
@DiscriminatorValue("FundFSS")
@NamedQueries({
    @NamedQuery(name = "FundFSS.getByOrderId",
                query = "select fd from FundFSS fd where fd.fund.userId = :userId and fd.orderId = :orderId")
})
public class FundFSS extends FundRecord {

    public FundFSS() {
    }

    public FundFSS(UserFund fund,
                   FundRecordType type,
                   FundRecordStatus status,
                   FundRecordOperation operation,
                   BigDecimal amount,
                   String orderId) {
        super(fund,
              null,
              null,
              type,
              status,
              operation,
              amount,
              orderId,
              null, null);
    }
}
