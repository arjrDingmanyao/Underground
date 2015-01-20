/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.entities;

import com.creditcloud.common.entities.UUIDEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 接口数据报文日志 车险分期
 *
 * @author Administrator
 */
@Entity
@Table(name = "TB_CAR_INSURANCE_MESSAGE_RECORD")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "CarInsuranceMessageRecord.findByInsuranceNum", query = "select obj from CarInsuranceMessageRecord obj where obj.insuranceNum =:insuranceNum and obj.type=:type")
})
public class CarInsuranceMessageRecord extends UUIDEntity {

    @Column(length = 200)
    @Getter
    @Setter
    private String insuranceNum;

    @Column(length = 200)
    @Getter
    @Setter
    private String type;

    @Lob
    @Column(name = "send", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String send;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date sendDate;

    @Lob
    @Column(name = "receive", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String receive;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date receiveDate;

    public CarInsuranceMessageRecord(String insuranceNum, String type, String send, Date sendDate, String receive, Date receiveDate) {
	this.insuranceNum = insuranceNum;
	this.type = type;
	this.send = send;
	this.sendDate = sendDate;
	this.receive = receive;
	this.receiveDate = receiveDate;
    }
}
