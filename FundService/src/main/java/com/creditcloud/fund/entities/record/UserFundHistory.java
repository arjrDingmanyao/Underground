/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.fund.entities.UserFund;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 用户资金变化历史记录，按照天为单位来统计
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_USER_FUND_HISTORY")
@NamedQueries({
    @NamedQuery(name = "UserFundHistory.listByUserAndDate",
                query = "select fh from UserFundHistory fh where fh.fund.userId = :userId and fh.asOfDate between :from and :to order by fh.asOfDate ASC")
})
public class UserFundHistory extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserFund fund;

    /**
     * 记录日期
     */
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date asOfDate;

    /**
     * 可用余额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal availableAmount;

    /**
     * 冻结金额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal frozenAmount;

    /**
     * 待收总额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal dueInAmount;

    /**
     * 待还总额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal dueOutAmount;

    /**
     * 充值总额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal depositAmount;

    /**
     * 提现总额
     */
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal withdrawAmount;

    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal transferAmount;

    public UserFundHistory() {
    }

    public UserFundHistory(UserFund fund,
                           Date asOfDate,
                           BigDecimal availableAmount,
                           BigDecimal frozenAmount,
                           BigDecimal dueInAmount,
                           BigDecimal dueOutAmount,
                           BigDecimal depositAmount,
                           BigDecimal withdrawAmount,
                           BigDecimal transferAmount) {
        this.fund = fund;
        this.asOfDate = asOfDate;
        this.availableAmount = availableAmount;
        this.frozenAmount = frozenAmount;
        this.dueInAmount = dueInAmount;
        this.dueOutAmount = dueOutAmount;
        this.depositAmount = depositAmount;
        this.withdrawAmount = withdrawAmount;
        this.transferAmount = transferAmount;
    }

    public UserFund getFund() {
        return fund;
    }

    public String getUserId() {
        return fund.getUserId();
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public BigDecimal getDueInAmount() {
        return dueInAmount;
    }

    public BigDecimal getDueOutAmount() {
        return dueOutAmount;
    }

    public void setFund(UserFund fund) {
        this.fund = fund;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public void setDueInAmount(BigDecimal dueInAmount) {
        this.dueInAmount = dueInAmount;
    }

    public void setDueOutAmount(BigDecimal dueOutAmount) {
        this.dueOutAmount = dueOutAmount;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    /**
     * 资金总额=可用余额+冻结金额+代收总额
     *
     * @return
     */
    public BigDecimal getTotalAmount() {
        return availableAmount.add(dueInAmount).add(frozenAmount);
    }

    /**
     * 总收益=资金总额+总提现金额-总充值金额 TODO 对于借款人总收益可能为负
     *
     * @return
     */
    public BigDecimal getTotalYield() {
        return getTotalAmount().add(withdrawAmount).subtract(depositAmount);
    }
}
