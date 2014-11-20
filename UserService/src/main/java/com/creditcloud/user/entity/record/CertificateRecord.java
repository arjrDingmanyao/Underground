/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.enums.user.credit.CertificateStatus;
import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.embedded.Assessment;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * 认证的审核历史记录
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_CERTIFICATE_RECORD")
@NamedQueries({
    @NamedQuery(name = "CertificateRecord.listByCertificateId",
                query = "select cr from CertificateRecord cr where cr.certificate.id = :certificateId order by cr.timeRecorded")
})
public class CertificateRecord extends RecordScopeEntity{

    @ManyToOne
    @JoinColumn(name = "CERTIFICATE_ID")
    private Certificate certificate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    //审核人，是员工Id
    @Column(nullable = false)
    private String auditor;

    //审核人审核备注信息
    @Column(nullable = true, length = 500)
    private String auditInfo;
    
    @Column(nullable=false)
    @Valid
    private Assessment assessment;

    public CertificateRecord(Certificate certificate,
                             CertificateStatus status,
                             String auditor,
                             String auditInfo,
                             Assessment assessment) {
        this.certificate = certificate;
        this.status = status;
        this.auditor = auditor;
        this.auditInfo = auditInfo;
        this.assessment = assessment;
    }

    public CertificateRecord() {
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public String getAuditor() {
        return auditor;
    }

    public String getAuditInfo() {
        return auditInfo;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public void setAuditInfo(String auditInfo) {
        this.auditInfo = auditInfo;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
