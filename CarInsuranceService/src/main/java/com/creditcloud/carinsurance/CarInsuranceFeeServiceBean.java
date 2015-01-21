/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceFeeService;
import com.creditcloud.carinsurance.entities.CarInsuranceFee;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceFeeDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRepaymentDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.model.CarInsuranceFeeModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.model.user.User;
import com.creditcloud.user.api.UserService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Administrator
 */
@Remote
@Stateless
public class CarInsuranceFeeServiceBean implements CarInsuranceFeeService {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @EJB
    private CarInsuranceRepaymentDAO carInsuranceRepaymentDAO;

    @EJB
    private CarInsuranceFeeDAO carInsuranceFeeDAO;

    @EJB
    private UserService userService;

    /**
     * 获取所有
     *
     * @return
     */
    public List<CarInsuranceFeeModel> getAll() {
	List<CarInsuranceFee> feeList = carInsuranceFeeDAO.findAll();
	List<CarInsuranceFeeModel> list = new ArrayList<CarInsuranceFeeModel>();

	for (CarInsuranceFee fee : feeList) {
	    if (fee.getCarInsuranceRepayment() == null) {
		logger.info("fee null++++++++++++++++:" + fee.getId());
	    }
	    logger.info("fee " + fee.getCarInsuranceRepayment().getDueDate());
	    User user = userService.findByUserId(appBean.getClientCode(), fee.getCarInsuranceRepayment().getCarInsurance().getUserId());
	    CarInsuranceFeeModel model = CarInsuranceDTOUtils.convertCarInsuranceFeeDTO(fee, user);
	    list.add(model);
	}
	return list;
    }

    /**
     * 根据id查找返回一个
     *
     * @param id
     * @return
     */
    public CarInsuranceFeeModel findById(String id) {
	CarInsuranceFee fee = carInsuranceFeeDAO.find(id);

	return CarInsuranceDTOUtils.convertCarInsuranceFeeDTO(fee);

    }

    /**
     * 新修改车险手续费的状态
     *
     * @param id
     * @param status 状态
     * @return
     */
    @Override
    public boolean updateCarInsuranceFeeSatatus(String id, CarInsuranceStatus status) {
	CarInsuranceFee fee = carInsuranceFeeDAO.find(id);
	if (fee == null) {
	    return false;
	} else {
	    fee.setStatus(status);
	    carInsuranceFeeDAO.edit(fee);
	    return true;
	}
    }

    /**
     * 保存
     *
     * @param fee
     */
    @Override
    public void create(CarInsuranceFeeModel model) {
	if (model != null) {
	    CarInsuranceFee fee = new CarInsuranceFee();
	    CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.find(model.getCarInsuranceRepayment().getId());
	    fee.setCarInsuranceRepayment(repayment);
	    fee.setFeeAmount(model.getFeeAmount());
	    fee.setStatus(model.getStatus());
	    carInsuranceFeeDAO.create(fee);
	    logger.debug("save CarInsuranceFee success model:{}", model);
	} else {
	    logger.error("save CarInsuranceFee failure model:{}", model);
	}

    }

    /**
     * 批量更新手续费状态
     *
     * @param feeFileExcel
     */
    public void bacthUpdateCarInsuranceFeeSatatus(File feeFileExcel) {
	try {
	    FileInputStream file = new FileInputStream(feeFileExcel);
	    XSSFWorkbook workbook = new XSSFWorkbook(file);
	    XSSFSheet sheet = workbook.getSheetAt(0);
	    Iterator<Row> rowIterator = sheet.iterator();
	    while (rowIterator.hasNext()) {
		Row row = rowIterator.next();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
		    Cell cell = cellIterator.next();
		    switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
			    System.out.print(cell.getNumericCellValue() + "\t");
			    break;
			case Cell.CELL_TYPE_STRING:
			    System.out.print(cell.getStringCellValue() + "\t");
			    break;
		    }
		}
		System.out.println("");
	    }
	    file.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
