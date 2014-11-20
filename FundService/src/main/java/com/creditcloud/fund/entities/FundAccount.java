/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.embedded.BankAccount;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 资金银行账号
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_FUND_ACCOUNT",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"account"})})
@NamedQueries({
    /**
     * count query
     */
    @NamedQuery(name = "FundAccount.countByUser",
                query = "select count(fa) from FundAccount fa where fa.fund.userId = :userId"),
    /**
     * list query
     */
    @NamedQuery(name = "FundAccount.listByUser",
                query = "select fa from FundAccount fa where fa.fund.userId = :userId order by fa.defaultAccount DESC ,fa.account.bank ASC"),
    /**
     * get query
     */
    @NamedQuery(name = "FundAccount.getByUserAndAccount",
                query = "select fa from FundAccount fa where fa.fund.userId = :userId and fa.account.account = :account"),
    @NamedQuery(name = "FundAccount.getDefaultByUser",
                query = "select fa from FundAccount fa where fa.fund.userId = :userId and fa.defaultAccount = TRUE"),
    /**
     * delete query
     */
    @NamedQuery(name = "FundAccount.deleteByUserAndAccount",
                query = "delete from FundAccount fa where fa.fund.userId = :userId and fa.account.account = :account"),
    @NamedQuery(name = "FundAccount.deleteByUser",
                query = "delete from FundAccount fa where fa.fund.userId = :userId")
})
public class FundAccount extends RecordScopeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",
                nullable = false)
    private UserFund fund;

    @Column(nullable = false)
    private BankAccount account;

    /**
     * 是否已经验证通过
     */
    private boolean valid;

    /**
     * 是否为首选默认银行账号,充值提现时将显示在第一个
     */
    private boolean defaultAccount;

    public FundAccount() {
    }

    public FundAccount(UserFund userFund,
                       BankAccount bankAccount,
                       boolean valid,
                       boolean isDefault) {
        this.fund = userFund;
        this.account = bankAccount;
        this.valid = valid;
        this.defaultAccount = isDefault;
    }

    public UserFund getFund() {
        return fund;
    }

    public BankAccount getAccount() {
        return account;
    }

    public void setFund(UserFund fund) {
        this.fund = fund;
    }

    public void setAccount(BankAccount account) {
        this.account = account;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(boolean defaultAccount) {
        this.defaultAccount = defaultAccount;
    }
}
