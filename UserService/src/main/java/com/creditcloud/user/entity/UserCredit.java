/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.user.entity.embedded.Assessment;
import com.creditcloud.user.entity.listener.UserCreditListener;
import com.creditcloud.user.entity.record.UserCreditRecord;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 用户信用及认证<p>
 * 信用额度目前是只针对贷款而言
 *
 * @author rooseek
 */
@Entity
@EntityListeners(UserCreditListener.class)
@Table(name = "TB_USER_CREDIT")
@NamedQueries({
    @NamedQuery(name = "UserCredit.countEachByRank",
                query = "select uc.creditRank as rank, count(uc) from UserCredit uc group by rank order by count(uc)")
})
public class UserCredit extends BaseEntity {

    @Id
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ID")
    private User user;

    //信用等级
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditRank creditRank;

    //信用评分,是所有Certificate的Assessment按某种算法计算得出
    @Column(nullable = false)
    @Valid
    private Assessment assessment;

    //信用额度
    @Min(0)
    @Column(nullable = false)
    private int creditLimit;

    //可用额度
    @Min(0)
    @Column(nullable = false)
    private int creditAvailable;

    @Column(nullable = false)
    private String lastModifiedBy;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeLastUpdated;

    @OneToMany(mappedBy = "credit",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<UserCreditRecord> changeRecord;

    @PrePersist
    private void setup() {
        Date date = new Date();
        this.timeCreated = date;
        this.timeLastUpdated = date;
    }

    public UserCredit() {
    }

    public UserCredit(User user,
                      CreditRank creditRank,
                      Assessment assessment,
                      int creditLimit,
                      int creditAvailable,
                      String lastModifiedBy) {
        this.userId = user.getId();
        this.user = user;
        this.creditRank = creditRank;
        this.assessment = assessment;
        this.creditLimit = creditLimit;
        this.creditAvailable = creditAvailable;
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(creditRank)
                .append(assessment)
                .append(creditLimit)
                .append(creditAvailable)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final UserCredit other = (UserCredit) obj;

        return new EqualsBuilder()
                .append(creditRank, other.creditRank)
                .append(assessment, other.assessment)
                .append(creditLimit, other.creditLimit)
                .append(creditAvailable, other.creditAvailable)
                .isEquals();
    }

    public CreditRank getCreditRank() {
        return creditRank;
    }

    public int getCreditLimit() {
        return creditLimit;
    }

    public int getCreditAvailable() {
        return creditAvailable;
    }

    public void setCreditRank(CreditRank creditRank) {
        this.creditRank = creditRank;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCreditAvailable(int creditAvailable) {
        this.creditAvailable = creditAvailable;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public Date getTimeLastUpdated() {
        return timeLastUpdated;
    }

    public Collection<UserCreditRecord> getChangeRecord() {
        return changeRecord;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setTimeLastUpdated(Date timeLastUpdated) {
        this.timeLastUpdated = timeLastUpdated;
    }

    public void setChangeRecord(Collection<UserCreditRecord> changeRecord) {
        this.changeRecord = changeRecord;
    }
}
