/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.TimeScopeEntity;
import com.creditcloud.model.enums.user.credit.CertificateStatus;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.user.entity.embedded.Assessment;
import com.creditcloud.user.entity.listener.CertificateListener;
import com.creditcloud.user.entity.record.CertificateRecord;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 一个用户会对应到很多项认证
 *
 * @author rooseek
 */
@Entity
@EntityListeners(CertificateListener.class)
@Table(name = "TB_CERTIFICATE",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"USER_ID", "type"})})
@NamedQueries({
    @NamedQuery(name = "Certificate.findByUserAndType",
                query = "select c from Certificate c where c.credit.userId = :userId and c.type = :type"),
    @NamedQuery(name = "Certificate.listByUser",
                query = "select c from Certificate c where c.credit.userId = :userId order by c.timeCreated")
})
public class Certificate extends TimeScopeEntity {

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserCredit credit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateType type;

    //认证的当前状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    //认证对应的评估
    @Column(nullable = false)
    @Valid
    private Assessment assessment;

    @Column(nullable = false)
    private String auditor;

    @Column(nullable = true, length = 500)
    private String auditInfo;

    @OneToMany(mappedBy = "certificate",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<CertificateRecord> changeRecord;

    public Certificate() {
    }

    public Certificate(UserCredit credit,
                       CertificateType type,
                       CertificateStatus status,
                       String auditor,
                       String auditInfo,
                       Assessment assessment) {
        this.credit = credit;
        this.type = type;
        this.status = status;
        this.assessment = assessment;
        this.auditor = auditor;
        this.auditInfo = auditInfo;
    }

    public UserCredit getCredit() {
        return credit;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setCredit(UserCredit credit) {
        this.credit = credit;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public CertificateType getType() {
        return type;
    }

    public void setType(CertificateType type) {
        this.type = type;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public void setChangeRecord(Collection<CertificateRecord> changeRecord) {
        this.changeRecord = changeRecord;
    }

    public Collection<CertificateRecord> getChangeRecord() {
        return changeRecord;
    }

    public String getAuditor() {
        return auditor;
    }

    public String getAuditInfo() {
        return auditInfo;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public void setAuditInfo(String auditInfo) {
        this.auditInfo = auditInfo;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(status)
                .append(assessment)
                .append(auditInfo)
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
        final Certificate other = (Certificate) obj;

        return new EqualsBuilder()
                .append(status, other.status)
                .append(assessment, other.assessment)
                .append(auditInfo, other.auditInfo)
                .isEquals();
    }
}
