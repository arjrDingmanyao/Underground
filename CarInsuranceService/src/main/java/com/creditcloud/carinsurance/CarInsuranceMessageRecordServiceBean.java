/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceMessageRecordService;
import com.creditcloud.carinsurance.entities.CarInsuranceMessageRecord;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceMessageRecordDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.model.CarInsuranceMessageRecordModel;
import com.creditcloud.carinsurance.utils.CarInsuranceMessageRecordDTOUtils;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * 车险报文日志记录器
 *
 * @author Administrator
 */
@Remote
@Stateless
public class CarInsuranceMessageRecordServiceBean implements CarInsuranceMessageRecordService {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @EJB
    private CarInsuranceMessageRecordDAO carInsuranceMessageRecordDAO;

    /**
     * 保存
     *
     * @param model
     * @return
     */
    public CarInsuranceMessageRecordModel create(CarInsuranceMessageRecordModel model) {
	CarInsuranceMessageRecord record = CarInsuranceMessageRecordDTOUtils.convertCarInsuranceMessageRecord(model);
	record = carInsuranceMessageRecordDAO.create(record);
	return CarInsuranceMessageRecordDTOUtils.convertCarInsuranceMessageRecordDTO(record);
    }

    /**
     *
     * @param model
     * @return
     */
    public void edit(CarInsuranceMessageRecordModel model) {
	CarInsuranceMessageRecord record = carInsuranceMessageRecordDAO.find(model.getId());
	if (record != null) {
	    record.setInsuranceNum(model.getInsuranceNum());
	    record.setType(model.getType());
	    record.setSend(model.getSend());
	    record.setSendDate(model.getSendDate());
	    record.setReceive(model.getReceive());
	    record.setReceiveDate(model.getReceiveDate());
	    carInsuranceMessageRecordDAO.edit(record);
	} else {
	    logger.debug("CarInsuranceMessageRecord not exist ID{}", model.getId());
	}
    }

    /**
     * 根据业务头把包单号查询
     *
     * @param insuranceNum
     * @param type
     * @return
     */
    public CarInsuranceMessageRecordModel findByInsuranceNum(String insuranceNum, String type) {
	CarInsuranceMessageRecord record = carInsuranceMessageRecordDAO.findByInsuranceNum(insuranceNum, type);
	return CarInsuranceMessageRecordDTOUtils.convertCarInsuranceMessageRecordDTO(record);
    }
}
