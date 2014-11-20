/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.fund.entities.embedded.AutoBidRange;
import com.creditcloud.model.constant.LoanConstant;
import com.creditcloud.model.constraints.IncrementalInteger;
import com.creditcloud.model.enums.loan.RepaymentMethod;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;

/**
 * 自动投标设置,一个user只对应到一个自动投标
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_USER_AUTO_BID")
@NamedQueries({
    /**
     * list query
     */
    @NamedQuery(name = "UserAutoBid.listByLoanAndLastBidTime",
                query = "select uab from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (uab.singleAmount <= :maxAmount and uab.singleAmount >= :minAmount) "
                        + " AND (:rate >= uab.range.minRate and :rate <= uab.range.maxRate )"
                        + " AND (:duration >= uab.range.minDuration and :duration <= uab.range.maxDuration )"
                        + " AND (:creditRank >= uab.range.minCredit and :creditRank <= uab.range.maxCredit )"
                        + " AND (:repayMethod member of uab.repayMethod ) "
                        + " AND ((uab.mortgaged = TRUE and :mortgaged = TRUE) or uab.mortgaged = FALSE ) "
                        + " ORDER BY uab.lastBidTime DESC"),
    @NamedQuery(name = "UserAutoBid.listByLoanAndActivedTime",
                query = "select uab from UserAutoBid uab where uab.active = TRUE  AND uab.enable = TRUE  "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (uab.singleAmount <= :maxAmount and uab.singleAmount >= :minAmount) "
                        + " AND (:rate >= uab.range.minRate and :rate <= uab.range.maxRate )"
                        + " AND (:duration >= uab.range.minDuration and :duration <= uab.range.maxDuration )"
                        + " AND (:creditRank >= uab.range.minCredit and :creditRank <= uab.range.maxCredit )"
                        + " AND (:repayMethod member of uab.repayMethod ) "
                        + " AND ((uab.mortgaged = TRUE and :mortgaged = TRUE) or uab.mortgaged = FALSE ) "
                        + " ORDER BY uab.activedTime ASC"),
    @NamedQuery(name = "UserAutoBid.listByStrategy",
                query = "select uab from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE  "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (:minRate <= uab.range.minRate and :maxRate >= uab.range.maxRate )"
                        + " AND (:minDuration <= uab.range.minDuration and :maxDuration >= uab.range.maxDuration )"
                        + " AND (:minCredit <= uab.range.minCredit and :maxCredit >= uab.range.maxCredit )"
                        + " AND NOT (exists (select method from uab.repayMethod method where NOT (method in :repayMethod) ) ) "
                        + " AND ((uab.mortgaged = TRUE and :mortgaged = TRUE) or :mortgaged = FALSE ) "
                        + " ORDER BY uab.activedTime DESC"),
    @NamedQuery(name = "UserAutoBid.listByValidActive",
                query = "select uab from UserAutoBid uab where uab.active = TRUE  AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " ORDER BY uab.activedTime DESC"),
    @NamedQuery(name = "UserAutoBid.listByActive",
                query = "select uab from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE ORDER BY uab.activedTime DESC"),
    @NamedQuery(name = "UserAutoBid.listByInvalidActive",
                query = "select uab from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE and uab.singleAmount + uab.reservedAmount > uab.fund.availableAmount order by uab.activedTime ASC"),
    /**
     * count query
     */
    @NamedQuery(name = "UserAutoBid.countByAutoBid",
                query = "select count(uab) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND NOT (:minRate > uab.range.maxRate or :maxRate < uab.range.minRate ) "
                        + " AND NOT (:minDuration > uab.range.maxDuration or :maxDuration < uab.range.minDuration ) "
                        + " AND NOT (:minCredit > uab.range.maxCredit or :maxCredit < uab.range.minCredit ) "
                        + " AND (exists (select method from uab.repayMethod method where method in :repayMethod ) ) "),
    @NamedQuery(name = "UserAutoBid.countRankOfAutoBid",
                query = "select count(uab) from UserAutoBid uab where uab.userId = :userId "
                        + " OR uab.active = TRUE  AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (:activatedTime > uab.activedTime ) "
                        + " AND NOT (:minRate > uab.range.maxRate or :maxRate < uab.range.minRate ) "
                        + " AND NOT (:minDuration > uab.range.maxDuration or :maxDuration < uab.range.minDuration ) "
                        + " AND NOT (:minCredit > uab.range.maxCredit or :maxCredit < uab.range.minCredit ) "
                        + " AND (exists (select method from uab.repayMethod method where method in :repayMethod ) ) "),
    @NamedQuery(name = "UserAutoBid.countByStrategy",
                query = "select count(uab) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE  "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (:minRate <= uab.range.minRate and :maxRate >= uab.range.maxRate )"
                        + " AND (:minDuration <= uab.range.minDuration and :maxDuration >= uab.range.maxDuration )"
                        + " AND (:minCredit <= uab.range.minCredit and :maxCredit >= uab.range.maxCredit )"
                        + " AND NOT (exists (select method from uab.repayMethod method where NOT (method in :repayMethod) ) ) "
                        + " AND ((uab.mortgaged = TRUE and :mortgaged = TRUE) or :mortgaged = FALSE ) "),
    @NamedQuery(name = "UserAutoBid.countByValidActive",
                query = "select count(uab) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "),
    @NamedQuery(name = "UserAutoBid.countByActive",
                query = "select count(uab) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "),
    /**
     * sum query
     */
    @NamedQuery(name = "UserAutoBid.sumByAutoBid",
                query = "select sum(uab.singleAmount) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND NOT (:minRate > uab.range.maxRate or :maxRate < uab.range.minRate ) "
                        + " AND NOT (:minDuration > uab.range.maxDuration or :maxDuration < uab.range.minDuration ) "
                        + " AND NOT (:minCredit > uab.range.maxCredit or :maxCredit < uab.range.minCredit ) "
                        + " AND (exists (select method from uab.repayMethod method where method in :repayMethod ) ) "),
    @NamedQuery(name = "UserAutoBid.sumRankOfAutoBid",
                query = "select sum(uab.singleAmount) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND NOT (uab.userId = :userId) "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (:activatedTime > uab.activedTime) "
                        + " AND NOT (:minRate > uab.range.maxRate or :maxRate < uab.range.minRate ) "
                        + " AND NOT (:minDuration > uab.range.maxDuration or :maxDuration < uab.range.minDuration ) "
                        + " AND NOT (:minCredit > uab.range.maxCredit or :maxCredit < uab.range.minCredit ) "
                        + " AND (exists (select method from uab.repayMethod method where method in :repayMethod ) ) "),
    @NamedQuery(name = "UserAutoBid.sumByStrategy",
                query = "select sum(uab.singleAmount) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "
                        + " AND (:minRate <= uab.range.minRate and :maxRate >= uab.range.maxRate )"
                        + " AND (:minDuration <= uab.range.minDuration and :maxDuration >= uab.range.maxDuration )"
                        + " AND (:minCredit <= uab.range.minCredit and :maxCredit >= uab.range.maxCredit )"
                        + " AND NOT (exists (select method from uab.repayMethod method where NOT (method in :repayMethod) ) ) "
                        + " AND ((uab.mortgaged = TRUE and :mortgaged = TRUE) or :mortgaged = FALSE ) "),
    @NamedQuery(name = "UserAutoBid.sumByValidActive",
                query = "select sum(uab.singleAmount) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE "
                        + " AND (uab.singleAmount + uab.reservedAmount) <= uab.fund.availableAmount "),
    @NamedQuery(name = "UserAutoBid.sumByActive",
                query = "select sum(uab.singleAmount) from UserAutoBid uab where uab.active = TRUE AND uab.enable = TRUE ")
})
public class UserAutoBid extends BaseEntity {

    @Id
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ID")
    private UserFund fund;

    /**
     * 系统管理用，例如对借款人还清贷款前disable自动投标
     */
    @Column(nullable = false)
    private boolean enable;

    /**
     * 是否激活
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * 单次投标金额
     */
    @IncrementalInteger(min = LoanConstant.MIN_INVEST_AMOUNT,
                        increment = LoanConstant.INVEST_AMOUNT_INCREMENT,
                        max = LoanConstant.MAX_INVEST_AMOUNT,
                        groups = Default.class)
    @Column(name = "SINGLE_AMOUNT", nullable = false)
    private int singleAmount;

    /**
     * 账户保留余额
     */
    @Min(0)
    @Column(name = "RESERVED_AMOUNT", nullable = false)
    private int reservedAmount;

    /**
     * 贷款还款方式
     */
    @ElementCollection(targetClass = RepaymentMethod.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "RF_AUTOBID_REPAYMETHOD", joinColumns =
                                                      @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "REPAYMENT_METHOD", nullable = false)
    private Collection<RepaymentMethod> repayMethod;

    @Column(nullable = false)
    @Valid
    private AutoBidRange range;

    /**
     * true必须有抵押,false可以没有抵押
     */
    @Column(nullable = false)
    private boolean mortgaged;
    
    /**
     * true代表打开全投
     */
    @Column(nullable = false)
    private boolean allIn;

    /**
     * 投标工具最近一次开启时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTIVED_TIME", nullable = true)
    private Date activedTime;

    /**
     * 最后一次投标时间，用来实现投标队列
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_BID_TIME", nullable = true)
    private Date lastBidTime;

    public UserAutoBid() {
    }

    public UserAutoBid(UserFund fund,
                       boolean active,
                       int singleAmount,
                       int reservedAmount,
                       Collection<RepaymentMethod> repayMethod,
                       AutoBidRange range,
                       boolean mortgaged,
                       boolean allIn,
                       Date activedTime,
                       Date lastBidTime,
                       boolean enable) {
        this.userId = fund.getUserId();
        this.fund = fund;
        this.active = active;
        this.singleAmount = singleAmount;
        this.reservedAmount = reservedAmount;
        this.repayMethod = repayMethod;
        this.mortgaged = mortgaged;
        this.allIn = allIn;
        this.activedTime = activedTime;
        this.lastBidTime = lastBidTime;
        this.range = range;
        this.enable = enable;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }

    public int getSingleAmount() {
        return singleAmount;
    }

    public Collection<RepaymentMethod> getRepayMethod() {
        return repayMethod;
    }

    public void setRepayMethod(Collection<RepaymentMethod> repayMethod) {
        this.repayMethod = repayMethod;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSingleAmount(int singleAmount) {
        this.singleAmount = singleAmount;
    }

    public Date getActivedTime() {
        return activedTime;
    }

    public void setActivedTime(Date activedTime) {
        this.activedTime = activedTime;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public void setReservedAmount(int reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public int getReservedAmount() {
        return reservedAmount;
    }

    public UserFund getFund() {
        return fund;
    }

    public void setFund(UserFund fund) {
        this.fund = fund;
    }

    public Date getLastBidTime() {
        return lastBidTime;
    }

    public void setLastBidTime(Date lastBidTime) {
        this.lastBidTime = lastBidTime;
    }

    public AutoBidRange getRange() {
        return range;
    }

    public void setRange(AutoBidRange range) {
        this.range = range;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
