/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.info.VehicleType;
import com.creditcloud.user.entity.Vehicle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * record the changes of Vehicle //TODO
 * 是否同时记录Proof的变化情况,目前设定Proof是immutable所以是可行的，未来可能考虑proof可以更改或覆盖删除
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_VEHICLE_RECORD")
@NamedQueries({
    @NamedQuery(name = "VehicleRecord.listByVehicle",
                query = "select vr from VehicleRecord vr where vr.vehicle.id = :vehicleId order by vr.timeRecorded")
})
public class VehicleRecord extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(name = "VEHICLE_ID")
    private Vehicle vehicle;

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

    /**
     * 修改人，可能是用户自己上传后修改也可能是员工实地勘察后修改
     */
    @Column(nullable = true)
    private String modifiedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;
    
    public VehicleRecord(Vehicle vehicle,
                         String model,
                         VehicleType type,
                         String vehicleLicense,
                         String plateNumber,
                         int yearOfPurchase,
                         int priceOfPurchase,
                         int estimatedValue,
                         String description,
                         String modifiedBy,
                         Source source) {
        this.vehicle = vehicle;
        this.model = model;
        this.type = type;
        this.vehicleLicense = vehicleLicense;
        this.plateNumber = plateNumber;
        this.yearOfPurchase = yearOfPurchase;
        this.priceOfPurchase = priceOfPurchase;
        this.estimatedValue = estimatedValue;
        this.description = description;
        this.modifiedBy = modifiedBy;
        this.source = source;
    }
}
