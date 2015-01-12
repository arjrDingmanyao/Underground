package com.creditcloud.payment.entities;

import com.creditcloud.common.entities.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import org.eclipse.persistence.annotations.Index;

@Entity
@Table(name = "TB_FSSACCOUNT", uniqueConstraints = {
    @javax.persistence.UniqueConstraint(columnNames = {"ClientCode", "userId"}),
    @javax.persistence.UniqueConstraint(columnNames = {"ClientCode", "accountId"})})
@NamedQueries({
    @javax.persistence.NamedQuery(name = "FssAccount.getByUserId", query = "select f from FssAccount f where f.clientCode = :clientCode and f.userId = :userId"),
    @javax.persistence.NamedQuery(name = "FssAccount.updateAsset", query = "update FssAccount fa set fa.balance = :totalAsset , fa.totalProfit = :totalProfit , fa.timeUpdated = CURRENT_TIMESTAMP where fa.clientCode = :clientCode and fa.userId = :userId"),
    @javax.persistence.NamedQuery(name = "FssAccount.outOfDateFssAccounts", query = "select fa.userId as userId from FssAccount fa where fa.clientCode = :clientCode and fa.timeUpdated < :timeUpdate"),
    @javax.persistence.NamedQuery(name = "FssAccount.fssStats", query = "select sum(f.balance), sum(f.totalProfit), sum(f.totalDeposit), sum(f.totalWithdraw) from FssAccount f where f.clientCode = :clientCode")})
public class FssAccount
        extends BaseEntity {

    @Id
    private String accountId;

    @Column(name = "ClientCode")
    private String clientCode;

    @Index
    private String userId;

    @Min(0L)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Min(0L)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalProfit;

    @Min(0L)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalDeposit;

    @Min(0L)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalWithdraw;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timeCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timeUpdated;

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        Object $accountId = getAccountId();
        result = result * 31 + ($accountId == null ? 0 : $accountId.hashCode());
        Object $clientCode = getClientCode();
        result = result * 31 + ($clientCode == null ? 0 : $clientCode.hashCode());
        Object $userId = getUserId();
        result = result * 31 + ($userId == null ? 0 : $userId.hashCode());
        Object $balance = getBalance();
        result = result * 31 + ($balance == null ? 0 : $balance.hashCode());
        Object $totalProfit = getTotalProfit();
        result = result * 31 + ($totalProfit == null ? 0 : $totalProfit.hashCode());
        Object $totalDeposit = getTotalDeposit();
        result = result * 31 + ($totalDeposit == null ? 0 : $totalDeposit.hashCode());
        Object $totalWithdraw = getTotalWithdraw();
        result = result * 31 + ($totalWithdraw == null ? 0 : $totalWithdraw.hashCode());
        Object $timeCreated = getTimeCreated();
        result = result * 31 + ($timeCreated == null ? 0 : $timeCreated.hashCode());
        Object $timeUpdated = getTimeUpdated();
        result = result * 31 + ($timeUpdated == null ? 0 : $timeUpdated.hashCode());
        return result;
    }

    public boolean canEqual(Object other) {
        return other instanceof FssAccount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FssAccount)) {
            return false;
        }
        FssAccount other = (FssAccount) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$accountId = getAccountId();
        Object other$accountId = other.getAccountId();
        if (this$accountId == null ? other$accountId != null : !this$accountId.equals(other$accountId)) {
            return false;
        }
        Object this$clientCode = getClientCode();
        Object other$clientCode = other.getClientCode();
        if (this$clientCode == null ? other$clientCode != null : !this$clientCode.equals(other$clientCode)) {
            return false;
        }
        Object this$userId = getUserId();
        Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) {
            return false;
        }
        Object this$balance = getBalance();
        Object other$balance = other.getBalance();
        if (this$balance == null ? other$balance != null : !this$balance.equals(other$balance)) {
            return false;
        }
        Object this$totalProfit = getTotalProfit();
        Object other$totalProfit = other.getTotalProfit();
        if (this$totalProfit == null ? other$totalProfit != null : !this$totalProfit.equals(other$totalProfit)) {
            return false;
        }
        Object this$totalDeposit = getTotalDeposit();
        Object other$totalDeposit = other.getTotalDeposit();
        if (this$totalDeposit == null ? other$totalDeposit != null : !this$totalDeposit.equals(other$totalDeposit)) {
            return false;
        }
        Object this$totalWithdraw = getTotalWithdraw();
        Object other$totalWithdraw = other.getTotalWithdraw();
        if (this$totalWithdraw == null ? other$totalWithdraw != null : !this$totalWithdraw.equals(other$totalWithdraw)) {
            return false;
        }
        Object this$timeCreated = getTimeCreated();
        Object other$timeCreated = other.getTimeCreated();
        if (this$timeCreated == null ? other$timeCreated != null : !this$timeCreated.equals(other$timeCreated)) {
            return false;
        }
        Object this$timeUpdated = getTimeUpdated();
        Object other$timeUpdated = other.getTimeUpdated();
        return this$timeUpdated == null ? other$timeUpdated == null : this$timeUpdated.equals(other$timeUpdated);
    }

    public String toString() {
        return "FssAccount(accountId=" + getAccountId() + ", clientCode=" + getClientCode() + ", userId=" + getUserId() + ", balance=" + getBalance() + ", totalProfit=" + getTotalProfit() + ", totalDeposit=" + getTotalDeposit() + ", totalWithdraw=" + getTotalWithdraw() + ", timeCreated=" + getTimeCreated() + ", timeUpdated=" + getTimeUpdated() + ")";
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getClientCode() {
        return this.clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalProfit() {
        return this.totalProfit;
    }

    public void setTotalDeposit(BigDecimal totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public BigDecimal getTotalDeposit() {
        return this.totalDeposit;
    }

    public BigDecimal getTotalWithdraw() {
        return this.totalWithdraw;
    }

    public void setTotalWithdraw(BigDecimal totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public Date getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeUpdated() {
        return this.timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
}
