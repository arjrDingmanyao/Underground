/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.utils;

import com.creditcloud.carinsurance.entities.CarInsuranceMessageRecord;
import com.creditcloud.carinsurance.model.CarInsuranceMessageRecordModel;

/**
 * 车险分期日志报文
 *
 * @author Administrator
 */
public class CarInsuranceMessageRecordDTOUtils {

    /**
     *
     * @param record
     * @return
     */
    public static CarInsuranceMessageRecordModel convertCarInsuranceMessageRecordDTO(CarInsuranceMessageRecord record) {
	CarInsuranceMessageRecordModel result = null;
	result = new CarInsuranceMessageRecordModel(record.getId(),
		record.getInsuranceNum(),
		record.getType(),
		record.getSend(),
		record.getSendDate(),
		record.getReceive(), record.getReceiveDate());

	return result;

    }

    /**
     *
     * @param model
     * @return
     */
    public static CarInsuranceMessageRecord convertCarInsuranceMessageRecord(CarInsuranceMessageRecordModel model) {
	CarInsuranceMessageRecord record = null;
	record = new CarInsuranceMessageRecord(
		model.getInsuranceNum(),
		model.getType(),
		model.getSend(),
		model.getSendDate(),
		model.getReceive(), model.getReceiveDate());

	return record;

    }
}
