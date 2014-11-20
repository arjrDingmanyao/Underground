/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.embedded;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.model.constraints.IncrementalInteger;
import com.creditcloud.model.enums.user.credit.CreditRank;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 自动投标利率，期限，信用上下界设置
 *
 * @author rooseek
 */
@Embeddable
public class AutoBidRange extends BaseEntity {

    /**
     * 利率下限
     */
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_RATE,
                        increment = 1,
                        max = LoanConstant.MAX_LOAN_RATE)
    @Column(nullable = false)
    private int minRate;

    /**
     * 利率上限
     */
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_RATE,
                        increment = 1,
                        max = LoanConstant.MAX_LOAN_RATE)
    @Column(nullable = false)
    private int maxRate;

    /**
     * 期限下限<p>
     * TODO 这样可能导致无法自动投标给以天为单位的贷款
     */
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_DURATION,
                        increment = 1,
                        max = LoanConstant.MAX_LOAN_DURATION)
    @Column(nullable = false)
    private int minDuration;

    /**
     * 期限上限
     */
    @IncrementalInteger(min = LoanConstant.MIN_LOAN_DURATION,
                        increment = 1,
                        max = LoanConstant.MAX_LOAN_DURATION)
    @Column(nullable = false)
    private int maxDuration;

    /**
     * 借款人信用下限
     */
    //TODO store as EnumType.STRING and try compare in JPQL
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CreditRank minCredit;

    /**
     * 借款人信用上限
     */
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CreditRank maxCredit;

    public AutoBidRange(int minRate, int maxRate, int minDuration, int maxDuration, CreditRank minCredit, CreditRank maxCredit) {
        this.minRate = minRate;
        this.maxRate = maxRate;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.minCredit = minCredit;
        this.maxCredit = maxCredit;
    }

    public AutoBidRange() {
    }

    public int getMinRate() {
        return minRate;
    }

    public int getMaxRate() {
        return maxRate;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public CreditRank getMinCredit() {
        return minCredit;
    }

    public CreditRank getMaxCredit() {
        return maxCredit;
    }

    public void setMinRate(int minRate) {
        this.minRate = minRate;
    }

    public void setMaxRate(int maxRate) {
        this.maxRate = maxRate;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setMinCredit(CreditRank minCredit) {
        this.minCredit = minCredit;
    }

    public void setMaxCredit(CreditRank maxCredit) {
        this.maxCredit = maxCredit;
    }
}
