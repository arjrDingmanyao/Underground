/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.GPSCoordinates;
import com.creditcloud.model.enums.Source;
import com.creditcloud.user.entity.User;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 主要记录居住地址更改和实地勘察
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_PERSONALINFO_RECORD")
public class PersonalInfoRecord extends RecordScopeEntity implements GPSCoordinates {

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    //现居住地址
    private String currentAddress;

    /**
     * 修改人，可能是用户自己上传后修改也可能是员工实地勘察后修改
     */
    @Column(nullable = false)
    private String modifiedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(precision = 12, scale = 8)
    private BigDecimal longitude;

    @Column(precision = 12, scale = 8)
    private BigDecimal latitude;

    public PersonalInfoRecord(User user,
                              String currentAddress,
                              String modifiedBy,
                              Source source,
                              BigDecimal longitude,
                              BigDecimal latitude) {
        this.user = user;
        this.currentAddress = currentAddress;
        this.modifiedBy = modifiedBy;
        this.source = source;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public PersonalInfoRecord() {
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public BigDecimal getLatitude() {
        return latitude;
    }

    @Override
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
