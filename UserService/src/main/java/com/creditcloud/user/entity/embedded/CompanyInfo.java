/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.embedded;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.model.enums.user.info.CompanyIndustry;
import com.creditcloud.model.enums.user.info.CompanySize;
import com.creditcloud.model.enums.user.info.CompanyType;
import com.creditcloud.model.validation.group.LoanRequestCheck;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 *
 * @author rooseek
 */
@Embeddable
public class CompanyInfo extends BaseEntity {
    //公司或单位名称
    @NotNull(message = "公司或单位名称不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "COMPANY_NAME")
    private String name;

    //公司类别
    @NotNull(message = "公司类别不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "COMPANY_TYPE")
    private CompanyType type;

    //公司行业
    @NotNull(message = "公司行业不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "COMPANY_INDUSTRY")
    private CompanyIndustry industry;

    //公司规模
    @NotNull(message = "公司规模不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "COMPANY_SIZE")
    private CompanySize companySize;

    //公司电话
    @NotNull(message = "公司电话不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "COMPANY_PHONE")
    private String phone;

    //公司地址
    @NotNull(message = "公司地址不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "COMPANY_ADDRESS")
    private String address;

    public CompanyInfo() {
    }

    /**
     *
     * @param name 公司或单位名称
     * @param type 公司类别
     * @param industry 公司行业
     * @param companySize 公司规模
     * @param phone 公司电话
     * @param address 公司地址
     */
    public CompanyInfo(String name,
                       CompanyType type,
                       CompanyIndustry industry,
                       CompanySize companySize,
                       String phone,
                       String address) {
        this.name = name;
        this.type = type;
        this.industry = industry;
        this.companySize = companySize;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public CompanyType getType() {
        return type;
    }

    public CompanyIndustry getIndustry() {
        return industry;
    }

    public CompanySize getCompanySize() {
        return companySize;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }

    public void setIndustry(CompanyIndustry industry) {
        this.industry = industry;
    }

    public void setCompanySize(CompanySize companySize) {
        this.companySize = companySize;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
