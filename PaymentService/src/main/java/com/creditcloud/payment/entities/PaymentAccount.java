package com.creditcloud.payment.entities;

import com.creditcloud.common.entities.BaseEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.annotations.Index;

@Entity
@Table(name = "TB_PAYMENTACCOUNT", uniqueConstraints = {
    @javax.persistence.UniqueConstraint(columnNames = {"ClientCode", "userId"}),
    @javax.persistence.UniqueConstraint(columnNames = {"ClientCode", "accountId"})})
@NamedQueries({
    @javax.persistence.NamedQuery(name = "PaymentAccount.getByUserId", query = "select p from PaymentAccount p where p.clientCode = :clientCode and p.userId = :userId")})
public class PaymentAccount
        extends BaseEntity {

    @Id
    private String accountId;

    @Column(name = "ClientCode")
    private String clientCode;

    @Index
    private String userId;

    private String accountName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreate;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PaymentAccount)) {
            return false;
        }
        PaymentAccount other = (PaymentAccount) o;
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
        Object this$accountName = getAccountName();
        Object other$accountName = other.getAccountName();
        if (this$accountName == null ? other$accountName != null : !this$accountName.equals(other$accountName)) {
            return false;
        }
        Object this$timeCreate = getTimeCreate();
        Object other$timeCreate = other.getTimeCreate();
        return this$timeCreate == null ? other$timeCreate == null : this$timeCreate.equals(other$timeCreate);
    }

    public boolean canEqual(Object other) {
        return other instanceof PaymentAccount;
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        Object $accountId = getAccountId();
        result = result * 31 + ($accountId == null ? 0 : $accountId.hashCode());
        Object $clientCode = getClientCode();
        result = result * 31 + ($clientCode == null ? 0 : $clientCode.hashCode());
        Object $userId = getUserId();
        result = result * 31 + ($userId == null ? 0 : $userId.hashCode());
        Object $accountName = getAccountName();
        result = result * 31 + ($accountName == null ? 0 : $accountName.hashCode());
        Object $timeCreate = getTimeCreate();
        result = result * 31 + ($timeCreate == null ? 0 : $timeCreate.hashCode());
        return result;
    }

    public String toString() {
        return "PaymentAccount(accountId=" + getAccountId() + ", clientCode=" + getClientCode() + ", userId=" + getUserId() + ", accountName=" + getAccountName() + ", timeCreate=" + getTimeCreate() + ")";
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return this.accountId;
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

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getTimeCreate() {
        return this.timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public PaymentAccount(String accountId, String clientCode, String userId, String accountName, Date timeCreate) {
        this.accountId = accountId;
        this.clientCode = clientCode;
        this.userId = userId;
        this.accountName = accountName;
        this.timeCreate = timeCreate;
    }

    public PaymentAccount() {
    }
}
