/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.common.security.SecurityUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * TODO 可以将密码/密保/身份/手机/邮箱等验证信息统一到此类
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_USER_AUTHENTICATE")
public class UserAuthenticate extends BaseEntity {

    @Id
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ID")
    private User user;

    /**
     * 实名验证是否通过
     */
    @Column(nullable = false)
    private boolean IDAuthenticated;

    /**
     * 手机验证是否通过
     */
    @Column(nullable = false)
    private boolean mobileAuthenticated;

    /**
     * 邮箱验证是否通过
     */
    @Column(nullable = false)
    private boolean emailAuthenticated;

    /**
     * 微博认证是否通过
     */
    @Transient
    private boolean weiboAuthenticated;

    /**
     * 微信认证是否通过
     */
    @Transient
    private boolean wechatAuthenticated;

    /**
     * 支付或提现密码
     */
    @Column(nullable = true, length = 40)
    protected String paymentPassphrase;

    @Column(nullable = true, length = 120)
    protected String paymentSalt;

    public UserAuthenticate() {
    }

    public UserAuthenticate(User user,
                            boolean IDAuthenticated,
                            boolean mobileAuthenticated,
                            boolean emailAuthenticated) {
        this.userId = user.getId();
        this.user = user;
        this.IDAuthenticated = IDAuthenticated;
        this.mobileAuthenticated = mobileAuthenticated;
        this.emailAuthenticated = emailAuthenticated;
    }

    public void setWeiboAuthenticated(boolean weiboAuthenticated) {
        this.weiboAuthenticated = weiboAuthenticated;
    }

    public void setWechatAuthenticated(boolean wechatAuthenticated) {
        this.wechatAuthenticated = wechatAuthenticated;
    }

    public boolean isWeiboAuthenticated() {
        return weiboAuthenticated;
    }

    public boolean isWechatAuthenticated() {
        return wechatAuthenticated;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isIDAuthenticated() {
        return IDAuthenticated;
    }

    public boolean isMobileAuthenticated() {
        return mobileAuthenticated;
    }

    public boolean isEmailAuthenticated() {
        return emailAuthenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIDAuthenticated(boolean IDAuthenticated) {
        this.IDAuthenticated = IDAuthenticated;
    }

    public void setMobileAuthenticated(boolean mobileAuthenticated) {
        this.mobileAuthenticated = mobileAuthenticated;
    }

    public void setEmailAuthenticated(boolean emailAuthenticated) {
        this.emailAuthenticated = emailAuthenticated;
    }

    public String getPaymentPassphrase() {
        return paymentPassphrase;
    }

    public String getPaymentSalt() {
        return paymentSalt;
    }

    public void setPaymentPassphrase(String paymentPassphrase) {
        this.paymentPassphrase = paymentPassphrase;
    }

    public void setPaymentSalt(String paymentSalt) {
        this.paymentSalt = paymentSalt;
    }

    @Transient
    public void paymentPassword(final String IdNumber, final String paymentPassword) {
        //use idNumber as the default salt
        paymentSalt = SecurityUtils.getSalt(IdNumber);
        paymentPassphrase = SecurityUtils.getPassphrase(paymentSalt, paymentPassword);
    }
}
