/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.entities;

import com.creditcloud.common.entities.TimeScopeEntity;
import com.creditcloud.model.user.corporation.CorporationConstant;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class Corporation extends TimeScopeEntity {

    //企业全称
    @Column(name = "NAME", nullable = true)
    @Size(max = CorporationConstant.MAX_CORP_NAME)
    private String name;

    //企业名简称
    @Column(name = "SHORTNAME", nullable = true)
    @Size(max = CorporationConstant.MAX_CORP_NAME)
    private String shortName;

    //组织结构代码
    @Column(name = "ORGCODE", nullable = true)
    @Size(max = CorporationConstant.ORG_CODE_LEN)
    private String orgCode;

    //营业执照编号
    @Column(name = "BUSICODE", nullable = true)
    @Size(max = CorporationConstant.BUSI_CODE_LEN)
    private String busiCode;

    //税务登记号
    @Column(name = "TAXCODE", nullable = true)
    @Size(max = CorporationConstant.TAX_CODE_LEN)
    private String taxCode;

    @Column(name = "DESCRIPTION", nullable = true)
    @Size(max = CorporationConstant.MAX_CORP_DESC)
    private String description;

    public Corporation(String name,
                       String shortName,
                       String orgCode,
                       String busiCode,
                       String taxCode,
                       String description) {
        this.name = name;
        this.shortName = shortName;
        this.orgCode = orgCode;
        this.busiCode = busiCode;
        this.taxCode = taxCode;
        this.description = description;
    }
}
