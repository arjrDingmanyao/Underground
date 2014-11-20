/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.fund.entities.record.UserFundHistory;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户资金
 *
 * @author rooseek
 *
 * 如果添加新column需要同步更新下表column
 * @see UserFundHistory
 */
@Entity
@Table(name = "TB_USER_FUND")
@NamedQueries({
    @NamedQuery(name = "UserFund.freeze",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount - :amount , "
                + " uf.frozenAmount = uf.frozenAmount + :amount , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP  "
                + " where uf.userId = :userId and uf.availableAmount >= :amount"),
    @NamedQuery(name = "UserFund.release",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount + :amount , "
                + " uf.frozenAmount = uf.frozenAmount - :amount , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and uf.frozenAmount >= :amount"),
     @NamedQuery(name = "UserFund.directRelease",
                query = "update UserFund uf set "
                + " uf.frozenAmount = uf.frozenAmount - :amount , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and uf.frozenAmount >= :amount"),
    @NamedQuery(name = "UserFund.deposit",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount + :amount, "
                + " uf.depositAmount = uf.depositAmount + :amount , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP  "
                + " where uf.userId = :userId"),
    @NamedQuery(name = "UserFund.dueIn",
                query = "update UserFund uf set "
                + " uf.dueInAmount = (case when (:add = TRUE) then (uf.dueInAmount + :amount) else (uf.dueInAmount - :amount) end) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and (:add = TRUE or :add = FALSE and uf.dueInAmount >= :amount)"),
    @NamedQuery(name = "UserFund.dueOut",
                query = "update UserFund uf set "
                + " uf.dueOutAmount = (case when (:add = TRUE) then (uf.dueOutAmount + :amount) else (uf.dueOutAmount - :amount) end) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and (:add = TRUE or :add = FALSE and uf.dueOutAmount >= :amount)"),
    @NamedQuery(name = "UserFund.available",
                query = "update UserFund uf set "
                + " uf.availableAmount = (case when (:add = TRUE) then (uf.availableAmount + :amount) else (uf.availableAmount - :amount) end) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and (:add = TRUE or :add = FALSE and uf.availableAmount >= :amount)"),
    @NamedQuery(name = "UserFund.withdraw",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount - :out , "
                + " uf.withdrawAmount = uf.withdrawAmount + :withdraw , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and uf.availableAmount >= :out"),
    @NamedQuery(name = "UserFund.calibrate",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount + :diffAvailable, "
                + " uf.frozenAmount = uf.frozenAmount + :diffFreeze , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId"),
    @NamedQuery(name = "UserFund.transfer",
                query = "update UserFund uf set "
                + " uf.transferAmount  = (case when (:income = TRUE) then (uf.transferAmount + :amount) else (uf.transferAmount - :amount) end ) , "
                + " uf.availableAmount = (case when(:income = TRUE) then (uf.availableAmount + :amount) else (uf.availableAmount - :amount) end ) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :userId and (:income = TRUE OR :income = FALSE and uf.availableAmount >= :amount)"),
    @NamedQuery(name = "UserFund.settleInvest",
                query = "update UserFund uf set "
                + " uf.dueOutAmount = (case when (uf.userId = :loanUserId) then (uf.dueOutAmount + :dueAmount) else (uf.dueOutAmount) end) , "
                + " uf.dueInAmount = (case when (uf.userId = :investUserId) then (uf.dueInAmount + :dueAmount) else (uf.dueInAmount) end) , "
                + " uf.availableAmount = (case when (uf.userId = :investUserId) then (uf.availableAmount - :investAmount) "
                + "                            when (uf.userId = :loanUserId) then (uf.availableAmount + :loanAmount) "
                + "                            else (uf.availableAmount) end) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId in (:investUserId, :loanUserId)"),
    @NamedQuery(name = "UserFund.settleInvestUmp",
                query = "update UserFund uf set "
                + " uf.dueOutAmount = (case when (uf.userId = :loanUserId) then (uf.dueOutAmount + :dueAmount) else (uf.dueOutAmount) end) , "
                + " uf.dueInAmount = (case when (uf.userId = :investUserId) then (uf.dueInAmount + :dueAmount) else (uf.dueInAmount) end) , "
                + " uf.availableAmount = (case when (uf.userId = :investUserId) then (uf.availableAmount - :investAmount) "
                + "                            else (uf.availableAmount) end) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId in (:investUserId, :loanUserId)"),
    @NamedQuery(name = "UserFund.settleInvestUmpRefund",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount + :loanAmount, "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :loanUserId"),
    @NamedQuery(name = "UserFund.repayInvest",
                query = "update UserFund uf set "
                + " uf.dueOutAmount = (case when (uf.userId = :loanUserId) then (uf.dueOutAmount - :repayAmount) else (uf.dueOutAmount) end ) , "
                + " uf.dueInAmount = (case when (uf.userId = :investUserId) then (uf.dueInAmount - :repayAmount) else (uf.dueInAmount) end ) , "
                + " uf.availableAmount = (case when (uf.userId = :investUserId) then (uf.availableAmount + :inAmount) "
                + "                            when (uf.userId = :loanUserId) then (uf.availableAmount - :outAmount) "
                + "                            else (uf.availableAmount) end ) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP  "
                + " where uf.userId in (:investUserId, :loanUserId)"),
    @NamedQuery(name = "UserFund.repayOnly",
                query = "update UserFund uf set "
                + " uf.availableAmount = uf.availableAmount - :repayAmount, "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP  "
                + " where uf.userId = :loanUserId"),
    @NamedQuery(name = "UserFund.repayInvestOnly",
                query = "update UserFund uf set "
                + " uf.dueOutAmount = (case when (uf.userId = :loanUserId) then (uf.dueOutAmount - :repayAmount) else (uf.dueOutAmount) end ) , "
                + " uf.dueInAmount = (case when (uf.userId = :investUserId) then (uf.dueInAmount - :repayAmount) else (uf.dueInAmount) end ) , "
                + " uf.availableAmount = (case when (uf.userId = :investUserId) then (uf.availableAmount + :inAmount) "
                + "                            else (uf.availableAmount) end ) , "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP  "
                + " where uf.userId in (:investUserId, :loanUserId)"),
    @NamedQuery(name = "UserFund.disburseInvest",
                query = "update UserFund uf set uf.dueInAmount = uf.dueInAmount - :repayAmount, "
                + " uf.availableAmount = uf.availableAmount + :inAmount, "
                + " uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + " where uf.userId = :investUserId"),
    @NamedQuery(name = "UserFund.creditAssign",
                query = "update UserFund uf set "
                + "uf.dueInAmount = (case when (uf.userId = :inUserId) then (uf.dueInAmount - :dueAmount) "
                + "                       when (uf.userId = :outUserId) then (uf.dueInAmount + :dueAmount) "
                + "                       else (uf.dueInAmount) end ), "
                + "uf.availableAmount = (case when (uf.userId = :inUserId) then (uf.availableAmount + :inAmount) "
                + "                           when(uf.userId = :outUserId) then (uf.availableAmount - :outAmount) "
                + "                           else (uf.availableAmount) end),"
                + "uf.timeLastUpdated = CURRENT_TIMESTAMP "
                + "where uf.userId in (:inUserId, :outUserId)"),
    /**
     * statistics query
     */
    @NamedQuery(name = "UserFund.sumAvailable",
                query = "select sum(uf.availableAmount) from UserFund uf"),
    /**
     * list
     */
    @NamedQuery(name = "UserFund.listByUser",
                query = "select u from UserFund u where u.userId in :userIds"),
    /**
     * count
     */
    @NamedQuery(name = "UserFund.countByUser",
                query = "select count(u) from UserFund u where u.userId in :userIds")
})
@Data
@NoArgsConstructor
public class UserFund extends BaseEntity {

    @Id
    private String userId;

    /**
     * 可用余额
     */
    @Min(0)
    @Column(name = "AVAILABLE_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal availableAmount;

    /**
     * 冻结金额
     */
    @Min(0)
    @Column(name = "FROZEN_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal frozenAmount;

    /**
     * 待收总额
     */
    @Column(name = "DUE_IN_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal dueInAmount;

    /**
     * 待还总额
     */
    @Column(name = "DUE_OUT_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal dueOutAmount;

    /**
     * 充值总额
     */
    @Min(0)
    @Column(name = "DEPOSIT_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal depositAmount;

    /**
     * 提现总额
     */
    @Min(0)
    @Column(name = "WITHDRAW_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal withdrawAmount;

    /**
     * 商户给用户的转账收入或者用户给商户的转账支出<p>
     * 可能小于零
     */
    @Column(name = "TRANSFER_AMOUNT",
            nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal transferAmount;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeLastUpdated;

    @PrePersist
    private void setup() {
        Date date = new Date();
        this.timeCreated = date;
        this.timeLastUpdated = date;
    }

    public UserFund(String userId,
                    BigDecimal availableAmount,
                    BigDecimal frozenAmount,
                    BigDecimal dueInAmount,
                    BigDecimal dueOutAmount,
                    BigDecimal depositAmount,
                    BigDecimal withdrawAmount,
                    BigDecimal transferAmount) {
        this.userId = userId;
        this.availableAmount = availableAmount;
        this.frozenAmount = frozenAmount;
        this.dueInAmount = dueInAmount;
        this.dueOutAmount = dueOutAmount;
        this.depositAmount = depositAmount;
        this.withdrawAmount = withdrawAmount;
        this.transferAmount = transferAmount;
    }
}
