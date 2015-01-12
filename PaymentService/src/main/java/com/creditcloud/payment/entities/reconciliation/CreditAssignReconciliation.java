package com.creditcloud.payment.entities.reconciliation;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.common.entities.utils.LocalDateConverter;
import com.creditcloud.model.enums.TransStat;
import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.LocalDate;

@Entity
@Table(name = "TB_PAYMENT_ASSIGN_RECONCILIATION", uniqueConstraints = {
    @javax.persistence.UniqueConstraint(columnNames = {"OrdId"})})
@NamedQueries({
    @javax.persistence.NamedQuery(name = "CreditAssignReconciliation.getByOrderId", query = "select car from CreditAssignReconciliation car where car.OrdId = :OrdId"),
    @javax.persistence.NamedQuery(name = "CreditAssignReconciliation.listByOrdDate", query = "select car from CreditAssignReconciliation car where car.OrdDate between :from and :to order by car.timeRecorded desc"),
    @javax.persistence.NamedQuery(name = "CreditAssignReconciliation.countByOrdDate", query = "select count(car) from CreditAssignReconciliation car where car.OrdDate between :from and :to")})
public class CreditAssignReconciliation
        extends RecordScopeEntity {

    @Column(nullable = false)
    private String OrdId;

    @Column(nullable = false)
    @Converter(name = "localDateConverter", converterClass = LocalDateConverter.class)
    @Convert("localDateConverter")
    private LocalDate OrdDate;

    @Column(nullable = false)
    private String SellCustId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal CreditAmt;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal CreditDealAmt;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal Fee;

    @Column(nullable = false)
    private String BuyCustId;

    @Column(nullable = false)
    private TransStat TransStat;

    @Column(nullable = false)
    private String PnrDate;

    @Column(nullable = false)
    private String PnrSeqId;

    public String toString() {
        return "CreditAssignReconciliation(OrdId=" + getOrdId() + ", OrdDate=" + getOrdDate() + ", SellCustId=" + getSellCustId() + ", CreditAmt=" + getCreditAmt() + ", CreditDealAmt=" + getCreditDealAmt() + ", Fee=" + getFee() + ", BuyCustId=" + getBuyCustId() + ", TransStat=" + getTransStat() + ", PnrDate=" + getPnrDate() + ", PnrSeqId=" + getPnrSeqId() + ")";
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        Object $OrdId = getOrdId();
        result = result * 31 + ($OrdId == null ? 0 : $OrdId.hashCode());
        Object $OrdDate = getOrdDate();
        result = result * 31 + ($OrdDate == null ? 0 : $OrdDate.hashCode());
        Object $SellCustId = getSellCustId();
        result = result * 31 + ($SellCustId == null ? 0 : $SellCustId.hashCode());
        Object $CreditAmt = getCreditAmt();
        result = result * 31 + ($CreditAmt == null ? 0 : $CreditAmt.hashCode());
        Object $CreditDealAmt = getCreditDealAmt();
        result = result * 31 + ($CreditDealAmt == null ? 0 : $CreditDealAmt.hashCode());
        Object $Fee = getFee();
        result = result * 31 + ($Fee == null ? 0 : $Fee.hashCode());
        Object $BuyCustId = getBuyCustId();
        result = result * 31 + ($BuyCustId == null ? 0 : $BuyCustId.hashCode());
        Object $TransStat = getTransStat();
        result = result * 31 + ($TransStat == null ? 0 : $TransStat.hashCode());
        Object $PnrDate = getPnrDate();
        result = result * 31 + ($PnrDate == null ? 0 : $PnrDate.hashCode());
        Object $PnrSeqId = getPnrSeqId();
        result = result * 31 + ($PnrSeqId == null ? 0 : $PnrSeqId.hashCode());
        return result;
    }

    public boolean canEqual(Object other) {
        return other instanceof CreditAssignReconciliation;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreditAssignReconciliation)) {
            return false;
        }
        CreditAssignReconciliation other = (CreditAssignReconciliation) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$OrdId = getOrdId();
        Object other$OrdId = other.getOrdId();
        if (this$OrdId == null ? other$OrdId != null : !this$OrdId.equals(other$OrdId)) {
            return false;
        }
        Object this$OrdDate = getOrdDate();
        Object other$OrdDate = other.getOrdDate();
        if (this$OrdDate == null ? other$OrdDate != null : !this$OrdDate.equals(other$OrdDate)) {
            return false;
        }
        Object this$SellCustId = getSellCustId();
        Object other$SellCustId = other.getSellCustId();
        if (this$SellCustId == null ? other$SellCustId != null : !this$SellCustId.equals(other$SellCustId)) {
            return false;
        }
        Object this$CreditAmt = getCreditAmt();
        Object other$CreditAmt = other.getCreditAmt();
        if (this$CreditAmt == null ? other$CreditAmt != null : !this$CreditAmt.equals(other$CreditAmt)) {
            return false;
        }
        Object this$CreditDealAmt = getCreditDealAmt();
        Object other$CreditDealAmt = other.getCreditDealAmt();
        if (this$CreditDealAmt == null ? other$CreditDealAmt != null : !this$CreditDealAmt.equals(other$CreditDealAmt)) {
            return false;
        }
        Object this$Fee = getFee();
        Object other$Fee = other.getFee();
        if (this$Fee == null ? other$Fee != null : !this$Fee.equals(other$Fee)) {
            return false;
        }
        Object this$BuyCustId = getBuyCustId();
        Object other$BuyCustId = other.getBuyCustId();
        if (this$BuyCustId == null ? other$BuyCustId != null : !this$BuyCustId.equals(other$BuyCustId)) {
            return false;
        }
        Object this$TransStat = getTransStat();
        Object other$TransStat = other.getTransStat();
        if (this$TransStat == null ? other$TransStat != null : !this$TransStat.equals(other$TransStat)) {
            return false;
        }
        Object this$PnrDate = getPnrDate();
        Object other$PnrDate = other.getPnrDate();
        if (this$PnrDate == null ? other$PnrDate != null : !this$PnrDate.equals(other$PnrDate)) {
            return false;
        }
        Object this$PnrSeqId = getPnrSeqId();
        Object other$PnrSeqId = other.getPnrSeqId();
        return this$PnrSeqId == null ? other$PnrSeqId == null : this$PnrSeqId.equals(other$PnrSeqId);
    }

    @ConstructorProperties({"OrdId", "OrdDate", "SellCustId", "CreditAmt", "CreditDealAmt", "Fee", "BuyCustId", "TransStat", "PnrDate", "PnrSeqId"})
    public CreditAssignReconciliation(String OrdId, LocalDate OrdDate, String SellCustId, BigDecimal CreditAmt, BigDecimal CreditDealAmt, BigDecimal Fee, String BuyCustId, TransStat TransStat, String PnrDate, String PnrSeqId) {
        this.OrdId = OrdId;
        this.OrdDate = OrdDate;
        this.SellCustId = SellCustId;
        this.CreditAmt = CreditAmt;
        this.CreditDealAmt = CreditDealAmt;
        this.Fee = Fee;
        this.BuyCustId = BuyCustId;
        this.TransStat = TransStat;
        this.PnrDate = PnrDate;
        this.PnrSeqId = PnrSeqId;
    }

    public String getOrdId() {
        return this.OrdId;
    }

    public void setOrdId(String OrdId) {
        this.OrdId = OrdId;
    }

    public LocalDate getOrdDate() {
        return this.OrdDate;
    }

    public void setOrdDate(LocalDate OrdDate) {
        this.OrdDate = OrdDate;
    }

    public String getSellCustId() {
        return this.SellCustId;
    }

    public void setSellCustId(String SellCustId) {
        this.SellCustId = SellCustId;
    }

    public BigDecimal getCreditAmt() {
        return this.CreditAmt;
    }

    public void setCreditAmt(BigDecimal CreditAmt) {
        this.CreditAmt = CreditAmt;
    }

    public BigDecimal getCreditDealAmt() {
        return this.CreditDealAmt;
    }

    public void setCreditDealAmt(BigDecimal CreditDealAmt) {
        this.CreditDealAmt = CreditDealAmt;
    }

    public BigDecimal getFee() {
        return this.Fee;
    }

    public void setFee(BigDecimal Fee) {
        this.Fee = Fee;
    }

    public String getBuyCustId() {
        return this.BuyCustId;
    }

    public void setBuyCustId(String BuyCustId) {
        this.BuyCustId = BuyCustId;
    }

    public void setTransStat(TransStat TransStat) {
        this.TransStat = TransStat;
    }

    public TransStat getTransStat() {
        return this.TransStat;
    }

    public String getPnrDate() {
        return this.PnrDate;
    }

    public void setPnrDate(String PnrDate) {
        this.PnrDate = PnrDate;
    }

    public String getPnrSeqId() {
        return this.PnrSeqId;
    }

    public void setPnrSeqId(String PnrSeqId) {
        this.PnrSeqId = PnrSeqId;
    }

    public CreditAssignReconciliation() {
    }
}
