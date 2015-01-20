/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.utils;

import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.entities.CarInsuranceFee;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.CarInsuranceRequest;
import com.creditcloud.carinsurance.model.CarInsuranceFeeModel;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRepaymentModel;
import com.creditcloud.carinsurance.model.CarInsuranceRequestModel;
import com.creditcloud.model.user.User;

/**
 * DTO model和实体类之间的转换
 *
 * @author Administrator
 */
public class CarInsuranceDTOUtils {

    /**
     * 车险分期 把一个实体转换成DTO
     */
    public static CarInsuranceModel convertCarInsuranceDTO(CarInsurance carInsurance, User user) {
	CarInsuranceModel result = null;
	if (carInsurance != null && user != null && carInsurance.getUserId().equalsIgnoreCase(user.getId())) {
	    result = new CarInsuranceModel(
		    carInsurance.getId(),
		    carInsurance.getInsuranceNum(),
		    user,
		    carInsurance.getLoginname(),
		    carInsurance.getUsername(),
		    carInsurance.getMobile(),
		    carInsurance.getAmount(),
		    carInsurance.getRate(),
		    carInsurance.getCarInsuranceType(),
		    carInsurance.getFirstPayment(),
		    carInsurance.getDuration(),
		    carInsurance.getDurationType(),
		    carInsurance.getChagreBackType(),
		    carInsurance.getCreateDate(),
		    carInsurance.getCarInsuranceStatus(),
		    carInsurance.getTitle(),
		    carInsurance.getTotalAmount()
	    );
	} else {

	}
	return result;

    }

    /**
     * 把一个mdoel转换成实体类
     *
     * @param carInsurance
     * @param user
     * @return
     */
    public static CarInsurance convertCarInsurance(CarInsuranceModel model) {
	CarInsurance result = null;
	if (model != null) {
	    result = new CarInsurance(
		    model.getInsuranceNum(),
		    model.getUser().getId(),
		    model.getLoginname(),
		    model.getUsername(),
		    model.getMobile(),
		    model.getAmount(),
		    model.getRate(),
		    model.getCarInsuranceType(),
		    model.getFirstPayment(),
		    model.getDuration(),
		    model.getDurationType(),
		    model.getCreateDate(),
		    model.getCarInsuranceStatus(),
		    model.getTitle(),
		    model.getTotalAmount()
	    );
	}
	return result;

    }

    /**
     * 把手续费实体转换成 手续费的MOdel DTO
     */
    public static CarInsuranceFeeModel convertCarInsuranceFeeDTO(CarInsuranceFee carInsuranceFee) {
	CarInsuranceFeeModel result = null;
	if (carInsuranceFee != null) {
	    CarInsuranceRepaymentModel repaymentModel = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(carInsuranceFee.getCarInsuranceRepayment(), null);
	    result = new CarInsuranceFeeModel(carInsuranceFee.getId(),
		    repaymentModel,
		    carInsuranceFee.getFeeAmount(),
		    carInsuranceFee.getStatus());

	}
	return result;
    }

    public static CarInsuranceFeeModel convertCarInsuranceFeeDTO(CarInsuranceFee carInsuranceFee, User user) {
	CarInsuranceFeeModel result = null;
	if (carInsuranceFee != null) {
	    CarInsuranceRepaymentModel repaymentModel = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(carInsuranceFee.getCarInsuranceRepayment(), user);
	    result = new CarInsuranceFeeModel(carInsuranceFee.getId(),
		    repaymentModel,
		    carInsuranceFee.getFeeAmount(),
		    carInsuranceFee.getStatus());

	}
	return result;
    }

    /**
     * 车险还款计划 DTO转换
     *
     * @param repayment
     * @param user
     * @return
     */
    public static CarInsuranceRepaymentModel convertCarInsuranceRepaymentDTO(CarInsuranceRepayment repayment, User user) {
	CarInsuranceRepaymentModel result = null;
	if (repayment != null) {
	    CarInsuranceModel carIsuranceModel = CarInsuranceDTOUtils.convertCarInsuranceDTO(repayment.getCarInsurance(), user);
	    result = new CarInsuranceRepaymentModel(repayment.getId(),
		    repayment.getAmountInterest(),
		    carIsuranceModel,
		    repayment.getCurrentPeriod(),
		    repayment.getDueDate(),
		    repayment.getAmountPrincipal(),
		    repayment.getStatus(),
		    repayment.getRepayAmount(),
		    repayment.getRepayDate(),
		    repayment.getOrderId());
	}
	return result;
    }

    /**
     * 把一个车险申请模型转换为实体类
     *
     * @param model
     * @return
     */
    public static CarInsuranceRequest convertCarInsuranceRequest(CarInsuranceRequestModel model) {
	CarInsuranceRequest carInsuranceRequest = null;
	if (model != null) {
	    carInsuranceRequest = new CarInsuranceRequest(
		    model.getInsuranceNum(),
		    model.getName(),
		    model.getIdNumber(),
		    model.getMobile(),
		    model.getAmount(),
		    model.getRate(),
		    model.getCarInsuranceType(),
		    model.getFirstPayment(),
		    model.getDuration(),
		    model.getDurationType(),
		    model.getAcceptanceDate(),
		    model.getCreateDate(),
		    model.getCarInsuranceRequestStatus(),
		    model.getCarInsuranceStatus(),
		    model.getTitle(),
		    model.getTotalAmount());
	}
	return carInsuranceRequest;
    }

    /**
     * 把一个实体类转换陈DTO
     *
     * @param request
     * @return
     */
    public static CarInsuranceRequestModel convertCarInsuranceRequestDTO(CarInsuranceRequest request) {
	CarInsuranceRequestModel result = null;
	if (request != null) {
	    result = new CarInsuranceRequestModel(
		    request.getId(),
		    request.getInsuranceNum(),
		    request.getName(),
		    request.getIdNumber(),
		    request.getMobile(),
		    request.getAmount(),
		    request.getRate(),
		    request.getCarInsuranceType(),
		    request.getCarInsurancePayStatus(),
		    request.getFirstPayment(),
		    request.getDuration(),
		    request.getDurationType(),
		    request.getAcceptanceDate(),
		    request.getCreateDate(),
		    request.getCarInsuranceRequestStatus(),
		    request.getCarInsuranceStatus(),
		    request.getTitle(),
		    request.getTotalAmount());
	}
	return result;
    }

}
