/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.GPSCoordinates;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.credit.ProofContentType;
import com.creditcloud.model.enums.user.credit.ProofType;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PROOF")
@NamedQueries({
    /**
     * get
     */
    @NamedQuery(name = "Proof.getByUserAndOwnerAndContent",
                query = "select p from Proof p where p.certificate.credit.userId = :userId and p.owner = :owner and p.proofType = :proofType and p.contentType = :contentType and p.content = :content"),
    @NamedQuery(name = "Proof.getByUserAndOwnerAndContent2",
                query = "select p from Proof p where p.certificate.credit.userId = :userId and (p.owner = :owner or p.owner IS NULL) and p.proofType = :proofType and p.contentType = :contentType and p.content = :content"),

    /**
     * list
     */
    @NamedQuery(name = "Proof.listByUserAndType",
                query = "select p from Proof p where p.certificate.credit.userId = :userId and p.certificate.type in :certificateTypes and p.contentType in :contentTypes order by p.submitTime DESC"),
    @NamedQuery(name = "Proof.listByUserAndOwnerAndType",
                query = "select p from Proof p where p.certificate.credit.userId = :userId and p.owner = :owner and p.contentType in :typeList order by p.submitTime DESC"),
    @NamedQuery(name = "Proof.listCover",
                query = "select p from Proof p where p.certificate.credit.userId = :userId and p.owner = :owner and p.cover = TRUE order by p.submitTime DESC"),

    /**
     * count
     */
    @NamedQuery(name = "Proof.countEachBySource",
                query = "select p.source as source, count(p) from Proof p group by source order by count(p) DESC"),
    @NamedQuery(name = "Proof.countEachByEmployee",
                query = "select p.employee as employee, count(p) from Proof p where p.source in :sourceList group by employee order by count(p) DESC"),
    @NamedQuery(name = "Proof.countEachByCertificateType",
                query = "select p.certificate.type as ctype, count(p) from Proof p where p.source in :sourceList group by ctype order by count(p) DESC"),
    @NamedQuery(name = "Proof.countEachByProofType",
                query = "select p.proofType as ptype ,count(p) from Proof p where p.source in :sourceList group by ptype order by count(p) DESC"),
    /**
     * update
     */
    @NamedQuery(name = "Proof.markAsCover",
                query = "update Proof p set p.cover = (case when (p.id = :id) then TRUE else FALSE end ) "
            + "where p.certificate.credit.userId = :userId and p.owner = :owner")
})
public class Proof extends UUIDEntity implements GPSCoordinates {

    @ManyToOne
    @JoinColumn(name = "CERTIFICATE_ID")
    private Certificate certificate;

    /**
     * Proof除了关联到具体的user,还可以进一步关联到user的贷款申请、住房、车辆等实体<p>
     * 如果为空则表示只关联到user自己，如身份认证等
     */
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column
                = @Column(name = "OWNER_REALM")),
        @AttributeOverride(name = "entityId", column
                = @Column(name = "OWNER_ID"))
    })
    @Column(nullable = true)
    private RealmEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProofType proofType;

    //证明内容类型
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProofContentType contentType;

    /**
     * 证明内容,对于图片就是图片名
     */
    @Column(nullable = false)
    private String content;

    @Column(nullable = true, length = 500)
    private String description;

    //证明来源
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    //提交时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date submitTime;

    //上传证明的员工,如果是用户本人上传则为空
    @Index
    @Column(nullable = true)
    private String employee;

    /**
     * @see com.creditcloud.model.Proof
     */
    @Column(nullable = true)
    private boolean mosaic;

    /**
     * 经度
     */
    @Column(precision = 12, scale = 8)
    private BigDecimal longitude;

    /**
     * 维度
     */
    @Column(precision = 12, scale = 8)
    private BigDecimal latitude;

    private boolean cover;

    public Proof(Certificate certificate,
                 RealmEntity owner,
                 ProofType proofType,
                 ProofContentType contentType,
                 String content,
                 String description,
                 Source source,
                 Date submitTime,
                 String employee,
                 boolean mosaic,
                 BigDecimal longitude,
                 BigDecimal latitude,
                 boolean cover) {
        this.certificate = certificate;
        this.owner = owner;
        this.proofType = proofType;
        this.contentType = contentType;
        this.content = content;
        this.description = description;
        this.source = source;
        this.submitTime = submitTime;
        this.employee = employee;
        this.mosaic = mosaic;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cover = cover;
    }
}
