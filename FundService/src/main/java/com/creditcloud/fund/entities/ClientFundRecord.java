/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.fund.model.enums.FundRecordOperation;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.fund.model.enums.FundRecordType;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Index;

/**
 * 商户资金记录
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_CLIENT_FUND_RECORD")
@NamedQueries({
    /**
     * get query
     */
    @NamedQuery(name = "ClientFundRecord.getByAccountAndTypeAndOrderId",
                query = "select cfr from ClientFundRecord cfr where cfr.account = :account and cfr.type = :type and cfr.orderId = :orderId"),
    @NamedQuery(name = "ClientFundRecord.getByEntity",
                query = "select fr from ClientFundRecord fr where fr.entity = :entity and fr.type = :type and fr.operation = :operation and fr.status = :status"),
    /**
     * count query
     */
    @NamedQuery(name = "ClientFundRecord.countByAccountAndType",
                query = "select count(cfr) from ClientFundRecord cfr where cfr.account in :accountList and cfr.type in :typeList and cfr.timeRecorded between :from and :to "),
    /**
     * list query
     */
    @NamedQuery(name = "ClientFundRecord.listByAccountAndType",
                query = "select cfr from ClientFundRecord cfr where cfr.account in :accountList and cfr.type in :typeList and cfr.timeRecorded between :from and :to order by cfr.timeRecorded DESC"),
    @NamedQuery(name = "ClientFundRecord.listWithdrawRequest",
                query = "select cfr from ClientFundRecord cfr where cfr.type = :type and cfr.operation = :operation and cfr.status in :statusList order by cfr.timeRecorded DESC")
})
public class ClientFundRecord extends RecordScopeEntity {

    /**
     * 出账入账账户 对于汇付就是商户子账户，如”SPEDT00001"
     */
    @Index
    @Column(nullable = false)
    private String account;

    /**
     * 关联的实体
     */
    private RealmEntity entity;

    /**
     * 关联的用户，如果是商户之间转账则为空
     */
    @Index
    private String userId;

    /**
     * 类型
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundRecordType type;

    /**
     * 操作
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundRecordOperation operation;

    /**
     * 状态
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundRecordStatus status;

    /**
     * 金额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal amount;

    /**
     * 订单号
     */
    @Index
    @Column(nullable = false)
    private String orderId;

    /**
     * 交易号
     */
    private String transactionId;

    private String description;

    public ClientFundRecord(String account,
                            RealmEntity entity,
                            String userId,
                            FundRecordType type,
                            FundRecordOperation operation,
                            FundRecordStatus status,
                            BigDecimal amount,
                            String orderId,
                            String transactionId,
                            String description) {
        this.account = account;
        this.entity = entity;
        this.userId = userId;
        this.type = type;
        this.operation = operation;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.description = description;
    }
}
