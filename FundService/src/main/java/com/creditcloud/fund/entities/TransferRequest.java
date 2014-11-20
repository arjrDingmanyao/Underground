/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Index;

/**
 * 商户向用户转账的申请记录
 *
 * @author rooseek
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_TRANSFER_REQUEST")
@NamedQueries(
        @NamedQuery(name = "TransferRequest.listByStatus",
                    query = "select tr from TransferRequest tr where tr.status in :statusList order by tr.timeRecorded DESC"))
public class TransferRequest extends RecordScopeEntity {

    /**
     * 转账对象userId
     */
    @Index
    @Column(nullable = false)
    private String userId;

    /**
     * 转账金额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    @Min(0)
    private BigDecimal amount;

    /**
     * 商户转账子账户
     */
    @Column(nullable = false)
    private String account;

    /**
     * 转账申请状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundRecordStatus status;

    /**
     * 对应的orderId
     */
    private String orderId;

    /**
     * 转账申请员工
     */
    private String requestEmployee;

    /**
     * 转账审核员工
     */
    private String auditEmployee;

    /**
     * 转账说明
     */
    private String description;
}
