/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.TimeScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.info.VehicleType;
import com.creditcloud.user.entity.record.VehicleRecord;
import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author rooseek
 */
@Data
@Entity
@NoArgsConstructor
//@EntityListeners(VehicleListener.class)
@Table(name = "TB_VEHICLE")
@NamedQueries({
    @NamedQuery(name="Vehicle.listByOwner",
                query = "select v from Vehicle v where v.owner = :owner order by v.timeCreated DESC"),
    @NamedQuery(name = "Vehicle.listByUser",
                query = "select v from Vehicle v where v.user.id = :userId order by v.yearOfPurchase"),
    @NamedQuery(name = "Vehicle.listByPlateNumber",
                query = "select v from Vehicle v where v.user.id = :userId and v.plateNumber = :plateNumber order by v.timeCreated desc")
})
public class Vehicle extends TimeScopeEntity {

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    /**
     * 相关联的实体，例如贷款申请
     */
    @AttributeOverrides({
        @AttributeOverride(name = "realm", column
                = @Column(name = "OWNER_REALM")),
        @AttributeOverride(name = "entityId", column
                = @Column(name = "OWNER_ID"))
    })
    @Column(nullable = true)
    private RealmEntity owner;

    /**
     * 车辆品牌（奥迪）
     */
    @Column(nullable = false)
    private String brand;

    /**
     * 车辆性质（运营 or 非运营）
     */
    @Column
    private boolean operating;

    /**
     * 行驶里程(公里)
     */
    @Column
    private int mileage;

    /**
     * 车辆基本型号信息:奥迪A8L
     */
    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    /**
     * 机动车行驶证
     */
    @Column(nullable = false)
    private String vehicleLicense;

    /*
     * 车牌号
     */
    @Column(nullable = false)
    private String plateNumber;

    /**
     * 购车年份
     */
    @Column(nullable = false)
    private int yearOfPurchase;

    /**
     * 购车价格
     */
    @Column(nullable = false)
    private int priceOfPurchase;

    /**
     * 现估值
     */
    @Column(nullable = false)
    private int estimatedValue;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    /**
     * 最后更改信息的员工，如果是用户本人更改则为空
     */
    @Column(nullable = true)
    private String lastModifiedBy;

    @OneToMany(mappedBy = "vehicle",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<VehicleRecord> changeRecord;

    public Vehicle(User user,
                   RealmEntity owner,
                   String brand,
                   boolean operating,
                   int mileage,
                   String model,
                   VehicleType type,
                   String vehicleLicense,
                   String plateNumber,
                   int yearOfPurchase,
                   int priceOfPurchase,
                   int estimatedValue,
                   String description,
                   Source source,
                   String lastModifiedBy) {
        this.user = user;
        this.owner = owner;
        this.brand = brand;
        this.operating = operating;
        this.mileage = mileage;
        this.model = model;
        this.type = type;
        this.vehicleLicense = vehicleLicense;
        this.plateNumber = plateNumber;
        this.yearOfPurchase = yearOfPurchase;
        this.priceOfPurchase = priceOfPurchase;
        this.estimatedValue = estimatedValue;
        this.description = description;
        this.source = source;
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(model)
                .append(type)
                .append(vehicleLicense)
                .append(plateNumber)
                .append(yearOfPurchase)
                .append(priceOfPurchase)
                .append(estimatedValue)
                .append(description).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final Vehicle other = (Vehicle) obj;

        return new EqualsBuilder()
                .append(model, other.model)
                .append(type, other.type)
                .append(vehicleLicense, other.vehicleLicense)
                .append(plateNumber, other.plateNumber)
                .append(yearOfPurchase, other.yearOfPurchase)
                .append(priceOfPurchase, other.priceOfPurchase)
                .append(estimatedValue, other.estimatedValue)
                .append(description, other.description).isEquals();
    }

}
