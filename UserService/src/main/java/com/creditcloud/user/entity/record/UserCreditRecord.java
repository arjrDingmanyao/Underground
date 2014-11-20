/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.user.entity.UserCredit;
import com.creditcloud.user.entity.embedded.Assessment;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_USERCREDIT_RECORD")
public class UserCreditRecord extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(name = "USER_CREDIT_ID")
    private UserCredit credit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditRank creditRank;

    @Column(nullable = false)
    @Valid
    private Assessment assessment;

    @Min(0)
    @Column(nullable = false)
    private int creditLimit;

    @Min(0)
    @Column(nullable = false)
    private int creditAvailable;

    @Column(nullable = false)
    private String modifiedBy;

    public UserCreditRecord() {
    }

    public UserCreditRecord(UserCredit credit,
                            CreditRank creditRank,
                            Assessment assessment,
                            int creditLimit,
                            int creditAvailable,
                            String modifiedBy) {
        this.credit = credit;
        this.creditRank = creditRank;
        this.assessment = assessment;
        this.creditLimit = creditLimit;
        this.creditAvailable = creditAvailable;
        this.modifiedBy = modifiedBy;
    }

    public UserCredit getCredit() {
        return credit;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setCredit(UserCredit credit) {
        this.credit = credit;
    }

    public void setCreditRank(CreditRank creditRank) {
        this.creditRank = creditRank;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCreditAvailable(int creditAvailable) {
        this.creditAvailable = creditAvailable;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }
}
