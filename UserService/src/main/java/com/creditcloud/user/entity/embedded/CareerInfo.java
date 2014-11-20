/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.embedded;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.model.constraints.EmailAddress;
import com.creditcloud.model.enums.user.info.CareerStatus;
import com.creditcloud.model.enums.user.info.MonthlySalary;
import com.creditcloud.model.enums.user.info.YearOfService;
import com.creditcloud.model.validation.group.LoanRequestCheck;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Embeddable
public class CareerInfo extends BaseEntity {

    //职业状态
    @NotNull(message = "职业状态不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "CAREER_STATUS")
    private CareerStatus careerStatus;

    //公司信息
    @NotNull(message = "公司信息不能为空",
             groups = LoanRequestCheck.class)
    @Valid
    private CompanyInfo company;

    //工作省份
    @NotNull(message = "工作省/市不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "WORK_PROVINCE")
    private String province;

    //工作城市
    @NotNull(message = "工作市/区不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "WORK_CITY")
    private String city;

    //职位
    @NotNull(message = "职位不能为空",
             groups = LoanRequestCheck.class)
    @Column(name = "JOB_POSITION")
    private String position;

    //月收入
    @NotNull(message = "月收入不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "MONTHLY_SALARY")
    private MonthlySalary salary;

    //在现单位工作年限
    @NotNull(message = "在现单位工作年限不能为空",
             groups = LoanRequestCheck.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "YEAR_OF_SERVICE")
    private YearOfService yearOfService;

    //工作邮箱
    @EmailAddress(message = "工作邮箱格式不正确",
                  groups = LoanRequestCheck.class)
    @Column(name = "WORK_MAIL")
    private String workMail;

    //从出道开始总工作年限
    @Column(name = "TOTAL_YEAR_OF_SERVICE")
    private YearOfService totalYearOfService;

    /**
     *
     * @param careerStatus  职业状态
     * @param company       公司信息
     * @param province      工作省市
     * @param city          工作城市
     * @param position      职位
     * @param salary        月收入
     * @param yearOfService 在现单位工作年限
     * @param workMail      工作邮箱
     */
    public CareerInfo(CareerStatus careerStatus,
                      CompanyInfo company,
                      String province,
                      String city,
                      String position,
                      MonthlySalary salary,
                      YearOfService yearOfService,
                      String workMail) {
        this.careerStatus = careerStatus;
        this.company = company;
        this.province = province;
        this.city = city;
        this.position = position;
        this.salary = salary;
        this.yearOfService = yearOfService;
        this.workMail = workMail;
    }
}
