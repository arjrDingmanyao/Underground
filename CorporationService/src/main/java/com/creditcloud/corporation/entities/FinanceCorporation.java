/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 融资企业
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_CORP_FINANCE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"FACTORING_CORPORATION", "NAME"}),
    @UniqueConstraint(columnNames = {"FACTORING_CORPORATION", "ORGCODE"}),
    @UniqueConstraint(columnNames = {"FACTORING_CORPORATION", "BUSICODE"}),
    @UniqueConstraint(columnNames = {"FACTORING_CORPORATION", "TAXCODE"}),
    @UniqueConstraint(columnNames = {"FACTORING_CORPORATION", "BANKLICENSE"})
})
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "FinanceCorporation.listByFactoringCorporation",
                query = "select fc from FinanceCorporation fc where fc.factoringCorporation = :factoringCorporation order by fc.timeCreated desc"),
    /**
     * count
     */
    @NamedQuery(name = "FinanceCorporation.countByFactoringCorporation",
                query = "select count(fc) from FinanceCorporation fc where fc.factoringCorporation = :factoringCorporation"),
    /**
     * get
     */
    @NamedQuery(name = "FinanceCorporation.getByName",
                query = "select fc from FinanceCorporation fc where fc.name = :name and fc.factoringCorporation = :factoringCorporation"),
    @NamedQuery(name = "FinanceCorporation.getByOrgCode",
                query = "select fc from FinanceCorporation fc where fc.orgCode = :orgCode and fc.factoringCorporation = :factoringCorporation"),
    @NamedQuery(name = "FinanceCorporation.getByBusiCode",
                query = "select fc from FinanceCorporation fc where fc.busiCode = :busiCode and fc.factoringCorporation = :factoringCorporation"),
    @NamedQuery(name = "FinanceCorporation.getByTaxCode",
                query = "select fc from FinanceCorporation fc where fc.taxCode = :taxCode and fc.factoringCorporation = :factoringCorporation"),
    @NamedQuery(name = "FinanceCorporation.getByBankLicense",
                query = "select fc from FinanceCorporation fc where fc.bankLicense = :bankLicense and fc.factoringCorporation = :factoringCorporation")
})
public class FinanceCorporation extends Corporation {

    //银行开户许可证
    @Column(name = "BANKLICENSE", nullable = false)
    private String bankLicense;

    // 融资企业对应的保理企业
    @Column(name = "FACTORING_CORPORATION", nullable = false)
    private String factoringCorporation;

    public FinanceCorporation(String name,
                              String shortName,
                              String orgCode,
                              String busiCode,
                              String taxCode,
                              String description,
                              String bankLicense,
                              String factoringCorporation) {
        super(name, shortName, orgCode, busiCode, taxCode, description);
        this.bankLicense = bankLicense;
        this.factoringCorporation = factoringCorporation;
    }

}
