/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@DiscriminatorValue("FundCreditAssign")
@NamedQueries({
    @NamedQuery(name = "FundCreditAssign.getByOrderId",
                query = "select fcr from FundCreditAssign fcr where fcr.fund.userId = :userId and fcr.orderId = :orderId"),
    @NamedQuery(name = "FundCreditAssign.getByOperationAndStatus",
                query = "select fcr from FundCreditAssign fcr where fcr.fund.userId = :userId and fcr.operation = :operation and fcr.status = :status and fcr.entity.realm = :realm and fcr.entity.entityId = :entityId")
})
public class FundCreditAssign extends FundRecord{
    public FundCreditAssign(){
        
    }

    public FundCreditAssign(UserFund fund, 
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
              FundRecordType.CREDIT_ASSIGN, 
              status, 
              operation, 
              amount, 
              orderId, 
              transactionId,
	      availableAmount, 
              description);
    }
}
