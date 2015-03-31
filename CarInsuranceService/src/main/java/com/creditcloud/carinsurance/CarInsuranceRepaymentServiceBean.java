/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceRepaymentService;
import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceFeeDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRepaymentDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.local.CarInsuranceFeeLocalBean;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRepaymentModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.model.results.RepayCarInsuranceResult;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.user.User;
import com.creditcloud.user.api.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @EJB
    private ConfigManager configManager;

    @EJB
    private CarInsuranceFeeLocalBean carInsuranceFeeLocalBean;

    /**
     *
     * @获取所有
     */
    @Override
    public List<CarInsuranceRepaymentModel> getAll() {
	List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.findAll();
	List<CarInsuranceRepaymentModel> list = new ArrayList<CarInsuranceRepaymentModel>();
	for (CarInsuranceRepayment repayment : repayments) {
	    BigDecimal penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	    repayment.setAmountInterest(penaltyAmount);
	    CarInsurance carInsurance = repayment.getCarInsurance();
	    User user = userService.findByUserId(appBean.getClientCode(), carInsurance.getUserId());
	    CarInsuranceModel carInsuranceModel = CarInsuranceDTOUtils.convertCarInsuranceDTO(carInsurance, user);
	    //封装还款计划
	    CarInsuranceRepaymentModel crm = new CarInsuranceRepaymentModel(repayment.getId(),
		    repayment.getAmountInterest(),
		    repayment.getAmountBreach(),
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
	BigDecimal penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	repayment.setAmountInterest(penaltyAmount);
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
    public synchronized RepayCarInsuranceResult repay(String repaymentId) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(repaymentId);
	/**
	 * @ feeAmount 每一期的分期手续费
	 * @ 分期手续费的计算方式，分期总额*0.8%/每期
	 */
	boolean islast = false;
	String insuranceNum = "";
	BigDecimal penaltyAmount = new BigDecimal(0);
	if (repayment != null) {
	    penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	    //车险分期的保单记录 如果是最后一期 则需要修改状态为已还清
	    CarInsurance carInsurance = repayment.getCarInsurance();
	    switch (repayment.getStatus()) {
		case INITIATED:
		    break;
		case PAYING:
		    repayment.setRepayDate(new Date());
		    //1 把实还金额修改当前应还金额 如果有预期罚金的 
		    //如果不是逾期状态不会有逾期罚息 为了防止时间错误保险，在正常还款也计算逾期罚息，但一般为0
		    repayment.setAmountInterest(penaltyAmount);
		    repayment.setRepayAmount(repayment.getAmountPrincipal().add(penaltyAmount));
		    repayment.setStatus(CarInsuranceStatus.CLEARED);
		    carInsuranceRepaymentDAO.edit(repayment);

		    insuranceNum = carInsurance.getInsuranceNum();
		    //2 判断是否是最后一期
		    switch (carInsurance.getDurationType()) {
			case THREEMONTH:
			    if (repayment.getCurrentPeriod() == 3) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case SIXMONTH:
			    if (repayment.getCurrentPeriod() == 6) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case TENMONTH:
			    if (repayment.getCurrentPeriod() == 10) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			default:
			    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
			    break;
		    }
		    carInsuranceDAO.edit(carInsurance);
		    break;
		case OVERDUE:
		    //如果是逾期计算逾期罚金费用
		    penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
		    repayment.setAmountInterest(penaltyAmount);
		    repayment.setRepayAmount(repayment.getAmountPrincipal().add(penaltyAmount));
		    repayment.setRepayDate(new Date());
		    repayment.setStatus(CarInsuranceStatus.CLEARED);
		    carInsuranceRepaymentDAO.edit(repayment);

		    insuranceNum = carInsurance.getInsuranceNum();
		    //2 判断是否是最后一期
		    switch (carInsurance.getDurationType()) {
			case THREEMONTH:
			    if (repayment.getCurrentPeriod() == 3) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case SIXMONTH:
			    if (repayment.getCurrentPeriod() == 6) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case TENMONTH:
			    if (repayment.getCurrentPeriod() == 10) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			default:
			    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
			    break;
		    }
		    carInsuranceDAO.edit(carInsurance);
		    break;
		case CLEARED:
		    break;
		case BREACH:
		    break;
		case ARCHIVED:
		    break;
		case CANCELED:
		    break;
		default:

	    }

	}
	User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	CarInsuranceRepaymentModel repaymentModel = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);
	return new RepayCarInsuranceResult(islast, insuranceNum, repaymentModel);
    }

    /**
     * 提还违约金
     *
     * @param repaymentId
     * @return
     */
    @Override
    public synchronized RepayCarInsuranceResult advanceRepay(String repaymentId) {
	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.findById(repaymentId);
	/**
	 * @ feeAmount 每一期的分期手续费
	 * @ 分期手续费的计算方式，分期总额*0.8%/每期
	 */
	boolean islast = false;
	String insuranceNum = "";
	BigDecimal penaltyAmount = new BigDecimal(0);
	if (repayment != null) {
	    //计算提还违约金 提还违约金=应还本金*费率(0.2%)
	    BigDecimal breachRate = new BigDecimal(0.02);
	    BigDecimal breachAmount = repayment.getAmountPrincipal().multiply(breachRate);
	    penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	    //车险分期的保单记录 如果是最后一期 则需要修改状态为已还清
	    CarInsurance carInsurance = repayment.getCarInsurance();
	    switch (repayment.getStatus()) {
		case INITIATED:
		    break;
		case PAYING:
		    repayment.setRepayDate(new Date());
		    repayment.setAmountBreach(breachAmount);
		    //1 把实还金额修改当前应还金额 如果有预期罚金的 
		    //如果不是逾期状态不会有逾期罚息 为了防止时间错误保险，在正常还款也计算逾期罚息，但一般为0
		    repayment.setAmountInterest(penaltyAmount);
		    repayment.setRepayAmount(repayment.getAmountPrincipal().add(penaltyAmount));
		    repayment.setStatus(CarInsuranceStatus.CLEARED);
		    carInsuranceRepaymentDAO.edit(repayment);

		    insuranceNum = carInsurance.getInsuranceNum();
		    //2 判断是否是最后一期
		    switch (carInsurance.getDurationType()) {
			case THREEMONTH:
			    if (repayment.getCurrentPeriod() == 3) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case SIXMONTH:
			    if (repayment.getCurrentPeriod() == 6) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case TENMONTH:
			    if (repayment.getCurrentPeriod() == 10) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			default:
			    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
			    break;
		    }
		    carInsuranceDAO.edit(carInsurance);
		    break;
		case OVERDUE:
		    //如果是逾期计算逾期罚金费用
		    penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
		    repayment.setAmountInterest(penaltyAmount);
		    repayment.setRepayAmount(repayment.getAmountPrincipal().add(penaltyAmount));
		    repayment.setRepayDate(new Date());
		    repayment.setStatus(CarInsuranceStatus.CLEARED);
		    carInsuranceRepaymentDAO.edit(repayment);

		    insuranceNum = carInsurance.getInsuranceNum();
		    //2 判断是否是最后一期
		    switch (carInsurance.getDurationType()) {
			case THREEMONTH:
			    if (repayment.getCurrentPeriod() == 3) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case SIXMONTH:
			    if (repayment.getCurrentPeriod() == 6) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			case TENMONTH:
			    if (repayment.getCurrentPeriod() == 10) {
				islast = true;
				//修改车险主信息为已还清
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.CLEARED);
			    } else {
				carInsurance.setCarInsuranceStatus(CarInsuranceStatus.PAYING);
			    }
			    break;
			default:
			    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
			    break;
		    }
		    carInsuranceDAO.edit(carInsurance);
		    break;
		case CLEARED:
		    break;
		case BREACH:
		    break;
		case ARCHIVED:
		    break;
		case CANCELED:
		    break;
		default:

	    }

	}
	User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	CarInsuranceRepaymentModel repaymentModel = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);
	return new RepayCarInsuranceResult(islast, insuranceNum, repaymentModel);
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
	carInsuranceRepaymentDAO.markStatus(status, repayIds);
	//这里如果逾期也同时要修改车险费逾期
	Set<String> carInsurances = new HashSet<>();
	for (String id : repayIds) {
	    CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.find(id);
	    carInsurances.add(repayment.getCarInsurance().getId());
	}
	if (!carInsurances.isEmpty()) {
	    //更新车险分期
	    switch (status) {
		case OVERDUE:
		    carInsuranceDAO.markStatus(CarInsuranceStatus.OVERDUE, carInsurances.toArray(new String[carInsurances.size()]));
		    break;
		case BREACH:
		    carInsuranceDAO.markStatus(CarInsuranceStatus.BREACH, carInsurances.toArray(new String[carInsurances.size()]));
		    break;
	    }
	}
	return true;
    }

}
