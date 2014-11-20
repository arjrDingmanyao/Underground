/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.common.entities.embedded.info.ContactInfo;
import com.creditcloud.common.entities.embedded.info.PersonalInfo;
import com.creditcloud.common.entities.embedded.info.SocialInfo;
import com.creditcloud.model.validation.group.LoanRequestCheck;
import com.creditcloud.user.entity.embedded.CareerInfo;
import com.creditcloud.user.entity.embedded.FinanceInfo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Extended information for user
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_USER_INFO")
@NamedQueries({
    @NamedQuery(name = "UserInfo.findInfoByIdNumber",
                query = "select ui from UserInfo ui where ui.user.idNumber = :idNumber")
})
public class UserInfo extends BaseEntity {

    @Id
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ID")
    private User user;

    //个人详细信息
    @NotNull(message = "详细信息不能为空",
             groups = LoanRequestCheck.class)
    @Valid
    private PersonalInfo personal;

    //个人资产及财务信息
    @NotNull(message = "财产信息不能为空",
             groups = LoanRequestCheck.class)
    @Valid
    private FinanceInfo finance;

    //工作信息
    @NotNull(message = "职业信息不能为空",
             groups = LoanRequestCheck.class)
    @Valid
    private CareerInfo career;

    //联系人信息
    @NotNull(message = "联系人信息不能为空",
             groups = LoanRequestCheck.class)
    @Valid
    private ContactInfo contact;

    //社交信息
    @Valid
    private SocialInfo social;

    /**
     * 平台自定义域,可以用于存储平台相关的用户信息,一般存json格式key-value
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String priv;

    /**
     *
     * @param user     用户
     * @param personal 个人详细信息
     * @param finance  个人资产及财务信息
     * @param career   工作信息
     * @param contact  联系人信息
     * @param social   社交信息
     */
    public UserInfo(User user,
                    PersonalInfo personal,
                    FinanceInfo finance,
                    CareerInfo career,
                    ContactInfo contact,
                    SocialInfo social) {
        this.userId = user.getId();
        this.user = user;
        this.personal = personal;
        this.finance = finance;
        this.career = career;
        this.contact = contact;
        this.social = social;
    }
}
