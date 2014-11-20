/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.user.entity.User;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_USER_RECORD")
public class UserRecord extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "USER_ID")
    private User user;

    private String name;

    private String loginName;

    private String IdNumber;

    private String mobile;

    private String email;

    private String modifiedBy;

    public UserRecord(User user,
                      String name,
                      String loginName,
                      String IdNumber,
                      String mobile,
                      String email,
                      String modifiedBy) {
        this.user = user;
        this.name = name;
        this.loginName = loginName;
        this.IdNumber = IdNumber;
        this.mobile = mobile;
        this.email = email;
        this.modifiedBy = modifiedBy;
    }
}
