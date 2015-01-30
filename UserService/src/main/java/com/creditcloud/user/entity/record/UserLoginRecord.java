/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.LoginRecord;
import com.creditcloud.user.entity.User;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * 记录成功与失败的登陆事件
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_USER_LOGIN_RECORD")
@NamedQueries({
    @NamedQuery(name = "UserLoginRecord.countByUser",
	    query = "select count(ulr) from UserLoginRecord ulr where ulr.user.id = :userId"),
    @NamedQuery(name = "UserLoginRecord.countFailedLoginByUser",
	    query = "select count(ulr) from UserLoginRecord ulr where ulr.user.id = :userId and ulr.record.success = false and ulr.record.loginTime > :since"),

    /**
     * 统计
     */
    @NamedQuery(name = "UserLoginRecord.listByLoginDate",
	    query = "select distinct ulr.user.id from UserLoginRecord ulr where ulr.record.loginTime between :from and :to"),
    @NamedQuery(name = "UserLoginRecord.dailyLogin",
	    query = "select cast(ulr.record.loginTime as date) as d, count(ulr) from UserLoginRecord ulr where ulr.record.success = true and ulr.record.loginTime between :from and :to group by d order by d"),
    @NamedQuery(name = "UserLoginRecord.dailyLoginUser",
	    query = "select cast(ulr.record.loginTime as date) as d, count(distinct(ulr.user)) from UserLoginRecord ulr where ulr.record.success = true and ulr.record.loginTime between :from and :to group by d order by d"),
    //用户登录统计
    @NamedQuery(name = "UserLoginRecord.listByLoginDateRange",
	    query = "select cast(ulr.record.loginTime as date) as d,max(ulr.record.loginTime),ulr from UserLoginRecord ulr where ulr.record.success = true and ulr.record.loginTime between :from and :to group by d,ulr.record.credential order by ulr.record.loginTime"),
    @NamedQuery(name = "UserLoginRecord.countByLoginDateRange",
	    query = "select count(ulr) from UserLoginRecord ulr where ulr.record.success = true and ulr.record.loginTime between :from and :to order by ulr.record.loginTime")
})
public class UserLoginRecord extends UUIDEntity {

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @Valid
    @Column(nullable = false)
    private LoginRecord record;

    public UserLoginRecord() {
    }

    public UserLoginRecord(User user, LoginRecord record) {
	this.user = user;
	this.record = record;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public LoginRecord getRecord() {
	return record;
    }

    public void setRecord(LoginRecord record) {
	this.record = record;
    }
}
