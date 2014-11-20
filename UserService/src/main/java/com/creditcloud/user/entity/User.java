/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.Authenticatable;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.common.entities.utils.IdNumberConverter;
import com.creditcloud.common.entities.utils.MobileConverter;
import com.creditcloud.common.security.SecurityUtils;
import com.creditcloud.model.constraints.EmailAddress;
import com.creditcloud.model.constraints.IdNumber;
import com.creditcloud.model.constraints.LoginName;
import com.creditcloud.model.constraints.MobileNumber;
import com.creditcloud.model.constraints.RealName;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.validation.group.BackSourceCheck;
import com.creditcloud.model.validation.group.IndividualUserCheck;
import com.creditcloud.user.entity.listener.UserListener;
import com.creditcloud.user.entity.record.UserRecord;
import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * <p>
 * 根据Source的不同，对User进行不同的验证
 * 目前对于来源是Source.Mobile的User,只验证name,idNumber,mobileNumber
 * 同时将loginName设置为mobileNumber
 *
 * @author rooseek
 * @see com.creditcloud.model.User
 */
@Entity
@EntityListeners(UserListener.class)
@Table(name = "TB_USER")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "User.listByEmployee",
                query = "select u from User u where u.employeeId = :employeeId and u.source in :sourceList order by u.registerDate"),
    @NamedQuery(name = "User.listByClient",
                query = "select u from User u where u.clientCode = :clientCode order by u.registerDate"),
    @NamedQuery(name = "User.listDisabledUsersByClient",
                query = "select u from User u where u.clientCode = :clientCode and u.enabled = FALSE order by u.registerDate"),
    @NamedQuery(name = "User.listByReferral",
                query = "select u from User u where u.referralEntity = :referralEntity and u.registerDate between :from and :to order by u.registerDate desc"),
    @NamedQuery(name = "User.listReferral",
                query = "select u.referralEntity as referral from User u where u.referralEntity IS NOT NULL and u.registerDate between :from and :to group by referral"),
    @NamedQuery(name = "User.listByLoginDate",
                query = "select u from User u where u.lastLoginDate between :from and :to ORDER BY u.lastLoginDate desc"),
    @NamedQuery(name = "User.listByRegisterDate",
                query = "select u from User u where (:allUser = true or (:allUser = false and u.registryRewarded = FALSE)) and u.registerDate between :from and :to order by u.registerDate"),
    /**
     * get
     */
    @NamedQuery(name = "User.findByLoginName",
                query = "SELECT u FROM User u WHERE u.loginName = :loginName"),
    @NamedQuery(name = "User.findByMobile",
                query = "SELECT u FROM User u WHERE u.mobile = :mobile"),
    @NamedQuery(name = "User.findByIdNumber",
                query = "SELECT u FROM User u WHERE u.idNumber = :idNumber"),
    @NamedQuery(name = "User.findByEmail",
                query = "SELECT u from User u where u.email = :email"),
    /**
     * count
     */
    @NamedQuery(name = "User.getUserCountByMobile",
                query = "SELECT COUNT(u) from User u WHERE u.mobile = :mobile"),
    @NamedQuery(name = "User.getUserCountByLoginName",
                query = "SELECT COUNT(u) from User u WHERE u.loginName = :loginName"),
    @NamedQuery(name = "User.getUserCountByIdNumber",
                query = "SELECT COUNT(u) from User u WHERE u.idNumber = :idNumber"),
    @NamedQuery(name = "User.getUserCountByEmail",
                query = "SELECT COUNT(u) from User u WHERE u.email = :email"),
    @NamedQuery(name = "User.countByEmployee",
                query = "select count(u) from User u where u.employeeId = :employeeId and u.source in :sourceList"),
    @NamedQuery(name = "User.countByClient",
                query = "select count(u) from User u where u.clientCode = :clientCode"),
    @NamedQuery(name = "User.countDisabledUsersByClient",
                query = "select count(u) from User u where u.clientCode = :clientCode and u.enabled = FALSE"),
    @NamedQuery(name = "User.countByReferral",
                query = "select count(u) from User u where u.referralEntity = :referralEntity and u.registerDate between :from and :to"),
    @NamedQuery(name = "User.countReferral",
                //TODO seems impossible to use count distinct for embedded entity
                query = "select count(DISTINCT(u.referralEntity.entityId)) from User u where u.referralEntity IS NOT NULL and u.registerDate between :from and :to"),
    @NamedQuery(name = "User.countByRegisterDate",
                query = "select count(u) from User u where (:allUser = true or (:allUser = false and u.registryRewarded = FALSE)) and u.registerDate between :from and :to"),
    @NamedQuery(name = "User.countByLoginDate",
                query = "select count(u) from User u where u.lastLoginDate between :from and :to"),
    /**
     * 统计
     */
    @NamedQuery(name = "User.countEachBySource",
                query = "select u.source as source , count(u) from User u  group by source"),
    @NamedQuery(name = "User.countEachByEmployee",
                query = "select u.employeeId as employee ,count(u) from User u where u.employeeId is NOT null and u.source in :sourceList group by employee order by count(u) DESC"),
    @NamedQuery(name = "User.dailyRegister",
                query = "select cast(u.registerDate as date) as d, count(u) from User u where u.registerDate between :from and :to group by d order by d"),
    @NamedQuery(name = "User.countAllByReferral",
                query = "select u.referralEntity as referral, count(u) from User u where (:allUser = true or (:allUser = false and u.referralRewarded = FALSE)) and u.referralEntity IS NOT NULL and u.registerDate between :from and :to group by referral"),
    @NamedQuery(name = "User.listAllByReferral",
                query = "select u.referralEntity as referral, u from User u where (:allUser = true or (:allUser = false and u.referralRewarded = FALSE)) and u.referralEntity IS NOT NULL and u.registerDate between :from and :to order by referral"),

    /**
     * update
     */
    @NamedQuery(name = "User.markReferralRewarded",
                query = "update User u set u.referralRewarded = TRUE where u.id in :ids"),
    @NamedQuery(name = "User.markRegistryRewarded",
                query = "update User u set u.registryRewarded = TRUE where u.id in :ids")
})
public class User extends Authenticatable {

    @RealName
    @Column(nullable = true, length = 15)
    private String name;

    @LoginName
    @Column(unique = true, nullable = false, length = 30)
    private String loginName;

    @IdNumber
    @Column(unique = true, nullable = true)
    @Converter(name = "idNumberConvert",
               converterClass = IdNumberConverter.class)
    @Convert("idNumberConvert")
    private String idNumber;

    @MobileNumber(groups = {Default.class})
    @NotNull(groups = {IndividualUserCheck.class})
    @Column(unique = true)
    @Converter(name = "mobileConvert",
               converterClass = MobileConverter.class)
    @Convert("mobileConvert")
    private String mobile;

    @EmailAddress
    @Column(unique = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    /**
     * 对于非Source.Web来源的user,此项是经办员工id
     */
    @NotNull(groups = {BackSourceCheck.class})
    private String employeeId;

    private String lastModifiedBy;

    @OneToMany(mappedBy = "user",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<UserRecord> changeRecord;

    /**
     * 推荐人，可以为空，也可以为用户、员工等
     */
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column
                                           = @Column(name = "REFERRAL_REALM")),
        @AttributeOverride(name = "entityId", column
                                              = @Column(name = "REFERRAL_ID"))
    })
    private RealmEntity referralEntity;

    /**
     * 默认是个人用户
     */
    private boolean enterprise;

    /**
     * 是否已经注册奖励
     */
    private boolean registryRewarded;

    /**
     * 是否已经推荐奖励
     */
    private boolean referralRewarded;

    public User() {
    }

    public User(String name,
                String loginName,
                String idNumber,
                String mobile,
                String email,
                Source source,
                String employeeId,
                String lastModifiedBy,
                boolean enabled,
                RealmEntity referralEntity,
                boolean enterprise) {
        this.name = name;
        this.loginName = loginName;
        this.idNumber = idNumber;
        this.mobile = mobile;
        this.email = email;
        this.source = source;
        this.employeeId = employeeId;
        this.lastModifiedBy = lastModifiedBy;
        this.enabled = enabled;
        this.referralEntity = referralEntity;
        this.enterprise = enterprise;
    }

    public void setRegistryRewarded(boolean registryRewarded) {
        this.registryRewarded = registryRewarded;
    }

    public void setReferralRewarded(boolean referralRewarded) {
        this.referralRewarded = referralRewarded;
    }

    public boolean isRegistryRewarded() {
        return registryRewarded;
    }

    public boolean isReferralRewarded() {
        return referralRewarded;
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * We DO NOT save password in any meaning.
     *
     * Just calculate the salt & passphrase accordingly.
     *
     * @param password
     */
    @Transient
    public void password(final String password) {
        //use idNumber as the default salt
        salt = SecurityUtils.getSalt(idNumber);
        passphrase = SecurityUtils.getPassphrase(salt, password);
    }

    public Collection<UserRecord> getChangeRecord() {
        return changeRecord;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public RealmEntity getReferralEntity() {
        return referralEntity;
    }

    public void setReferralEntity(RealmEntity referralEntity) {
        this.referralEntity = referralEntity;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(loginName)
                .append(idNumber)
                .append(mobile)
                .append(email)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return new EqualsBuilder()
                .append(name, other.name)
                .append(loginName, other.loginName)
                .append(idNumber, other.idNumber)
                .append(mobile, other.mobile)
                .append(email, other.email)
                .isEquals();
    }
}
