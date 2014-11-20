/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.GPSCoordinates;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.info.EstateType;
import com.creditcloud.user.entity.RealEstate;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * record the changes of RealEstate
 *
 * //TODO 是否同时记录Proof的变化情况,目前设定Proof是immutable所以是可行的，未来可能考虑proof可以更改或覆盖删除
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_REALESTATE_RECORD")
@NamedQueries({
    @NamedQuery(name = "RealEstateRecord.listByEstate",
                query = "select rer from RealEstateRecord rer where rer.estate.id = :estateId order by rer.timeRecorded")
})
public class RealEstateRecord extends RecordScopeEntity implements GPSCoordinates {

    @ManyToOne
    @JoinColumn(name = "REAL_ESTATE_ID")
    private RealEstate estate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstateType type;

    /**
     * 地址
     */
    @Column(nullable = false)
    private String location;

    /**
     * 建筑面积
     */
    @Column(nullable = false)
    private double area;

    /**
     * 是否有房贷
     */
    @Column(nullable = false)
    private boolean loan;

    /**
     * 现估值
     */
    @Column(nullable = false)
    private int estimatedValue;

    @Column(nullable = true)
    private String description;

    /**
     * 修改人，可能是用户自己上传后修改也可能是员工实地勘察后修改
     */
    @Column(nullable = true)
    private String modifiedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(precision = 12, scale = 8)
    private BigDecimal longitude;

    @Column(precision = 12, scale = 8)
    private BigDecimal latitude;

    public RealEstateRecord() {
    }

    public RealEstateRecord(RealEstate estate,
                            EstateType type,
                            String location,
                            double area,
                            boolean loan,
                            int estimatedValue,
                            String description,
                            String modifiedBy,
                            Source source,
                            BigDecimal longitude,
                            BigDecimal latitude) {
        this.estate = estate;
        this.type = type;
        this.location = location;
        this.area = area;
        this.loan = loan;
        this.estimatedValue = estimatedValue;
        this.description = description;
        this.modifiedBy = modifiedBy;
        this.source = source;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public RealEstate getEstate() {
        return estate;
    }

    public EstateType getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public double getArea() {
        return area;
    }

    public boolean isLoan() {
        return loan;
    }

    public int getEstimatedValue() {
        return estimatedValue;
    }

    public String getDescription() {
        return description;
    }

    public Source getSource() {
        return source;
    }

    public void setEstate(RealEstate estate) {
        this.estate = estate;
    }

    public void setType(EstateType type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setLoan(boolean loan) {
        this.loan = loan;
    }

    public void setEstimatedValue(int estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
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
