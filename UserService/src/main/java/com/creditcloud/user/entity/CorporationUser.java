/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.model.user.corporation.CorporationConstant;
import com.creditcloud.model.user.corporation.CorporationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId; 
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_CORPORATION_USER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"NAME"}),
    @UniqueConstraint(columnNames = {"ORGCODE"}),
    @UniqueConstraint(columnNames = {"BUSICODE"}),
    @UniqueConstraint(columnNames = {"TAXCODE"})})
@NoArgsConstructor
@Data
@NamedQueries({
    /**
     * get query
     */
    @NamedQuery(name = "CorporationUser.getByBusiCode",
                query = "select c from CorporationUser c where c.busiCode = :busiCode "),
    @NamedQuery(name = "CorporationUser.getByTaxCode",
                query = "select c from CorporationUser c where c.taxCode = :taxCode "),
    @NamedQuery(name = "CorporationUser.getByOrgCode",
                query = "select c from CorporationUser c where c.orgCode = :orgCode "),
    @NamedQuery(name = "CorporationUser.getByName",
                query = "select c from CorporationUser c where c.name = :name "),
    /**
     * count query
     */
    @NamedQuery(name = "CorporationUser.countByType",
                query = "select count(c) from CorporationUser c where c.type in :typeList"),
    @NamedQuery(name = "CorporationUser.countByLegalPerson",
                query = "select count(c) from CorporationUser c where c.legalPersonId = :legalPersonId"),
    /**
     * list query
     */
    @NamedQuery(name = "CorporationUser.listByType",
                query = "select c from CorporationUser c where c.type in :typeList order by c.user.registerDate DESC"),
    @NamedQuery(name = "CorporationUser.listLegalPerson",
                query = "select DISTINCT(c.legalPersonId) from CorporationUser c ORDER BY c.user.registerDate DESC"),
    @NamedQuery(name = "CorporationUser.listByLegalPerson",
                query = "select c from CorporationUser c where c.legalPersonId = :legalPersonId ORDER BY c.user.registerDate DESC"),
    @NamedQuery(name = "CorporationUser.listByRtpo",
                query = "select c from CorporationUser c where c.rtpo = :rtpo ORDER BY c.user.registerDate DESC")
})
public class CorporationUser extends BaseEntity {

    @Id
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    //企业全称
    @Column(name = "NAME", nullable = false)
    @Size(max = CorporationConstant.MAX_CORP_NAME)
    private String name;

    //企业名简称
    @Size(max = CorporationConstant.MAX_CORP_NAME)
    private String shortName;

    //组织结构代码
    @Column(name = "ORGCODE", nullable = false)
    @Size(max = CorporationConstant.ORG_CODE_LEN)
    private String orgCode;

    //营业执照编号
    @Column(name = "BUSICODE", nullable = false)
    @Size(max = CorporationConstant.BUSI_CODE_LEN)
    private String busiCode;

    //税务登记号
    @Column(name = "TAXCODE", nullable = false)
    @Size(max = CorporationConstant.TAX_CODE_LEN)
    private String taxCode;

    //合同章特征码
    @Column(name = "CONTRACTSEALCODE", nullable = true)
    @Size(max = CorporationConstant.CONTRACT_SEAL_CODE_LEN)
    private String contractSealCode;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CorporationType type;

    /**
     * 可以額外指定一个用户为企业法人
     */
    @Column(nullable = true)
    private String legalPersonId;

    //营业执照上的公司类型
    @Column(nullable = true)
    @Size(max = CorporationConstant.MAX_CORP_CTG_LEN)
    private String category;

    @Column(nullable = false)
    private Boolean rtpo;
    public CorporationUser(User user,
                           String name,
                           String shortName,
                           String orgCode,
                           String busiCode,
                           String taxCode,
                           CorporationType type,
                           String category,
                           String legalPersonId,
                           boolean rtpo) {
        this.user = user;
        this.userId = user == null ? null : user.getId();
        this.name = name;
        this.shortName = shortName;
        this.orgCode = orgCode;
        this.busiCode = busiCode;
        this.taxCode = taxCode;
        this.type = type;
        this.category = category;
        this.legalPersonId = legalPersonId;
        this.rtpo = rtpo;
    }
}
