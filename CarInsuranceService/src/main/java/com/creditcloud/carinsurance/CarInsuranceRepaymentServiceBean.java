/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceRepaymentService;
import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.entities.CarInsuranceFee;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceFeeDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRepaymentDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRepaymentModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.user.User;
import com.creditcloud.user.api.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

/**
 * 车险分期 还款计划
 *
 * @author Administrator
 */
@Remote
@Stateless
public class CarInsuranceRepaymentServiceBean implements CarInsuranceRepaymentService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    private CarInsuranceRepaymentDAO carInsuranceRepaymentDAO;

    @EJB
    private CarInsuranceDAO carInsuranceDAO;

    @EJB
    private CarInsuranceFeeDAO carInsuranceFeeDAO;

    @EJB
    UserService userService;

    /**
     *
     * @获取所有
     */
    @Override
    public List<CarInsuranceRepaymentModel> getAll() {
	List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.findAll();
	List<CarInsuranceRepaymentModel> list = new ArrayList<CarInsuranceRepaymentModel>();
	for (CarInsuranceRepayment repayment : repayments) {
	    CarInsurance carInsurance = repayment.getCarInsurance();
	    User user = userService.findByUserId(appBean.getClientCode(), carInsurance.getUserId());
	    CarInsuranceModel carInsuranceModel = CarInsuranceDTOUtils.convertCarInsuranceDTO(carInsurance, user);
	    //封装还款计划
	    CarInsuranceRepaymentModel crm = new CarInsuranceRepaymentModel(repayment.getId(),
		    repayment.getAmountInterest(),
		    carInsuranceModel,
		    repayment.getCurrentPeriod(),
		    repayment.getDueDate(),
		    repayment.getAmountPrincipal(),
		    repayment.getStatus(),
		    repayment.getRepayAmount(),
		    repayment.getRepayDate(),
		    repayment.getOrderId());
	    list.add(crm);
	}
	return list;
    }

    /**
     *
     * 根据id查找
     *
     * @param id
     * @return
     */
    @Override
    public CarInsuranceRepaymentModel getCarInsuranceRepaymentModelById(String id) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(id);
	User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	CarInsuranceRepaymentModel model = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);
	return model;
    }

    /**
     * 更新
     *
     * @param model
     */
    @Override
    public void updateCarInsuranceRepaymentModel(CarInsuranceRepaymentModel model) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(model.getId());
	if (repayment != null) {
	    repayment.setRepayAmount(model.getRepayAmount());
	    repayment.setStatus(model.getStatus());
	    repayment.setRepayDate(model.getRepayDate());
	    //更新还款状态
	    carInsuranceRepaymentDAO.edit(repayment);
	} else {
	    logger.debug("车险分期还款失败 CarInsuranceRepayment not exist or deleted \n CarInsuranceRepaymentID:{}", model.getId());
	}
    }

    @Override
    public void updateCarInsuranceRepaymentModelFoyOrderId(CarInsuranceRepaymentModel model) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(model.getId());
	if (repayment != null) {
	    repayment.setOrderId(model.getOrderId());
	    //更新orderId
	    carInsuranceRepaymentDAO.edit(repayment);
	} else {
	    logger.debug("车险分期还款失败 CarInsuranceRepayment not exist or deleted \n CarInsuranceRepaymentID:{}", model.getId());
	}
    }

    @Override
    public void repay(String repaymentId) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(repaymentId);
	/**
	 * @ feeAmount 每一期的分期手续费
	 * @ 分期手续费的计算方式，分期总额*0.8%/每期
	 */
	BigDecimal feeAmount = new BigDecimal(0);
	if (repayment != null) {
	    repayment.setRepayAmount(repayment.getAmountPrincipal());
	    repayment.setRepayDate(new Date());
	    //1 把实还金额修改当前应还金额 如果有预期罚金的 还需要做一些计算
	    repayment.setRepayAmount(repayment.getAmountPrincipal());
	    repayment.setStatus(CarInsuranceStatus.CLEARED);
	    carInsuranceRepaymentDAO.edit(repayment);

	    CarInsurance temp = repayment.getCarInsurance();
	    //2 判断是否是最后一期
	    switch (temp.getDurationType()) {
		case THREEMONTH:
		    feeAmount = (temp.getAmount().multiply(new BigDecimal(0.008))).divide(new BigDecimal(3), 10, BigDecimal.ROUND_HALF_UP);
		    if (repayment.getCurrentPeriod() == 3) {
			//修改车险主信息为已还清
			temp.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			carInsuranceDAO.edit(temp);
		    } else {
			temp.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			carInsuranceDAO.edit(temp);
		    }
		    break;
		case SIXMONTH:
		    feeAmount = (temp.getAmount().multiply(new BigDecimal(0.007))).divide(new BigDecimal(3), 10, BigDecimal.ROUND_HALF_DOWN);
		    if (repayment.getCurrentPeriod() == 6) {
			//修改车险主信息为已还清
			temp.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			carInsuranceDAO.edit(temp);
		    } else {
			temp.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			carInsuranceDAO.edit(temp);
		    }
		    break;
		case TENMONTH:
		    feeAmount = (temp.getAmount().multiply(new BigDecimal(0.006))).divide(new BigDecimal(3), 10, BigDecimal.ROUND_HALF_DOWN);
		    if (repayment.getCurrentPeriod() == 10) {
			//修改车险主信息为已还清
			temp.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			carInsuranceDAO.edit(temp);
		    } else {
			temp.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			carInsuranceDAO.edit(temp);
		    }
		    break;
		default:
		    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
		    break;
	    }
	    //3 添加一条手续费记录
	    CarInsuranceFee fee = new CarInsuranceFee();
	    fee.setFeeAmount(feeAmount);
	    fee.setStatus(CarInsuranceStatus.PAYING);
	    fee.setCarInsuranceRepayment(repayment);
	    carInsuranceFeeDAO.create(fee);
	}

    }

    /**
     * 根据orderID获取
     *
     * @param orderId
     * @return
     */
    @Override
    public CarInsuranceRepaymentModel getCarInsuranceRepaymentModelByOrderId(String orderId) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findByOrderId(orderId);
	CarInsuranceRepaymentModel model = null;
	if (repayment != null) {
	    User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	    model = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);
	}
	return model;
    }

    /**
     * 或去逾期的还款
     *
     * @param clientCode
     * @param from
     * @param to
     * @param pageInfo
     * @param status
     * @return
     */
    @Override
    public PagedResult<CarInsuranceRepaymentModel> listDueRepay(String clientCode, LocalDate from, LocalDate to, PageInfo pageInfo, CarInsuranceStatus... status) {
	appBean.checkClientCode(clientCode);
	logger.debug("listDueRepay.[clientCode={}][from={}][to={}][pageInfo={}][status={}]", clientCode, from, to, pageInfo, Arrays.asList(status));
	PagedResult<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listDueRepay(from.toDate(), to.toDate(), pageInfo, status);
	List<CarInsuranceRepaymentModel> result = new ArrayList<>(repayments.getResults().size());
	for (CarInsuranceRepayment repayment : repayments.getResults()) {
	    User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	    CarInsuranceRepaymentModel model = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);
	    result.add(model);
	}
	return new PagedResult<>(result, repayments.getTotalSize());
    }
    
    
    @Override
    public boolean markStatus(String clientCode, CarInsuranceStatus status, String... repayIds) {
        appBean.checkClientCode(clientCode);
        //更新loanRepayment
        carInsuranceRepaymentDAO.markStatus(status, repayIds);
	//这里如果逾期也同时要修改车险费逾期
        return true;
    }

}
