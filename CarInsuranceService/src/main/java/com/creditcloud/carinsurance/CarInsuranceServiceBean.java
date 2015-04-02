/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceRepaymentService;
import com.creditcloud.carinsurance.api.CarInsuranceService;
import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.entities.CarInsuranceFee;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceFeeDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRepaymentDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.local.CarInsuranceFeeLocalBean;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRepayDetail;
import com.creditcloud.carinsurance.model.CarInsuranceRepaymentModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceDuration;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.carinsurance.utils.DateUtils;
import com.creditcloud.config.api.ConfigManager;
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
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * 车险分期 服务bean
 *
 * @author Administrator
 */
@Remote
@Stateless
public class CarInsuranceServiceBean implements CarInsuranceService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    private CarInsuranceDAO carInsuranceDAO;

    @EJB
    private CarInsuranceRepaymentDAO carInsuranceRepaymentDAO;

    @EJB
    UserService userService;

    @EJB
    CarInsuranceRepaymentService carInsuranceRepaymentService;

    @EJB
    private CarInsuranceFeeDAO carInsuranceFeeDAO;

    @EJB
    private ConfigManager configManager;

    @EJB
    private CarInsuranceFeeLocalBean carInsuranceFeeLocalBean;

    /**
     * 根据车险分期的 时间和状态查询数据
     *
     * @param startDate
     * @param endDate
     * @车险分期状态
     * @param carInsuranceStatus
     *
     * @return
     */
    @Override
    public List<CarInsuranceModel> getCarInsuranceList(String startDate, String endDate, CarInsuranceStatus carInsuranceStatus) {
	System.out.println("getCarInsuranceList");
	List<CarInsurance> list = carInsuranceDAO.listCarInsurance(DateUtils.FIRST_DATE,
		new Date(),
		PageInfo.ALL,
		carInsuranceStatus).getResults();

	List<CarInsuranceModel> carInsuranceModelRequests = new ArrayList<>();
	for (CarInsurance request : list) {
	    CarInsuranceModel model = CarInsuranceDTOUtils.convertCarInsuranceDTO(request, userService.findByUserId(this.appBean.getClientCode(), request.getUserId()));
	    carInsuranceModelRequests.add(model);
	}
	return carInsuranceModelRequests;
    }

    /**
     * @根据保单获取 车险分期信息
     * @num不是id号
     */
    @Override
    public CarInsuranceModel getByInsuranceNum(String insuranceNum) {
	CarInsurance insurance = carInsuranceDAO.findByNum(insuranceNum);
	CarInsuranceModel model = null;
	if (insurance != null) {
	    User user = userService.findByUserId(appBean.getClientCode(), insurance.getUserId());
	    model = CarInsuranceDTOUtils.convertCarInsuranceDTO(insurance, user);
	}
	return model;
    }

    /**
     * 更新一个实体
     *
     * @param model
     */
    @Override
    public void edit(CarInsuranceModel model) {
	CarInsurance carInsurance = CarInsuranceDTOUtils.convertCarInsurance(model);
	carInsuranceDAO.edit(carInsurance);
    }

    @Override
    public CarInsuranceModel getCarInsuranceModelById(String id) {
	CarInsurance carInsurance = carInsuranceDAO.find(id);
	CarInsuranceModel model = null;
	if (carInsurance != null) {
	    User user = userService.findByUserId(appBean.getClientCode(), carInsurance.getUserId());
	    model = CarInsuranceDTOUtils.convertCarInsuranceDTO(carInsurance, user);
	}
	return model;
    }

    /**
     * 接收保存车险分期信息 并计算出还款计划和添加手续费记录
     *
     * @param model
     */
    @Override
    public void create(CarInsuranceModel model) {
	CarInsurance carInsurance = CarInsuranceDTOUtils.convertCarInsurance(model);
	//1 根据分期类别 然后计算还款计划
	carInsurance.setTimeRecord(new Date());
	carInsurance = carInsuranceDAO.create(carInsurance);
	BigDecimal firstValue = carInsurance.getAmount();
	BigDecimal secondValue = new BigDecimal(carInsurance.getDuration());
	//应该除法 防止出现无限小数 保留2位小数
	BigDecimal amountPrincipal = firstValue.divide(secondValue, 2);
	logger.debug("create repayment carInsurance :\n{}", carInsurance);
	for (int i = 1; i <= carInsurance.getDuration(); i++) {
	    //1 计算出还款时间
	    Date dueDate = DateUtils.offset(new Date(), new CarInsuranceDuration(0, i, 0));
	    CarInsuranceRepayment repayment;
	    if (i == carInsurance.getDuration()) {
		BigDecimal lastAmountPrincipal = firstValue.subtract(amountPrincipal.multiply(new BigDecimal(carInsurance.getDuration() - 1)));
		repayment = new CarInsuranceRepayment(
			new BigDecimal(0),
			carInsurance,
			i,
			dueDate,
			lastAmountPrincipal,
			CarInsuranceStatus.PAYING,
			new BigDecimal(0));
		//保存还款计划到数据库
		carInsuranceRepaymentDAO.create(repayment);
	    } else {
		repayment = new CarInsuranceRepayment(
			new BigDecimal(0),
			carInsurance,
			i,
			dueDate,
			amountPrincipal,
			CarInsuranceStatus.PAYING,
			new BigDecimal(0));
		//保存还款计划到数据库
		carInsuranceRepaymentDAO.create(repayment);
	    }
	    //2 生成手续费记录
	    BigDecimal feeAmount = new BigDecimal(0);
	    CarInsuranceFee fee = new CarInsuranceFee();
	    switch (carInsurance.getDurationType()) {
		case THREEMONTH:
		    feeAmount = carInsurance.getAmount().multiply(new BigDecimal(configManager.getCarInsuranceConfig().getPeriodFee().getThreemonth()));
		    fee.setFeeAmount(feeAmount);
		    fee.setStatus(CarInsuranceStatus.PAYING);
		    fee.setCarInsuranceRepayment(repayment);
		    carInsuranceFeeDAO.create(fee);
		    break;
		case SIXMONTH:
		    feeAmount = carInsurance.getAmount().multiply(new BigDecimal(configManager.getCarInsuranceConfig().getPeriodFee().getSixmonth()));
		    fee.setFeeAmount(feeAmount);
		    fee.setStatus(CarInsuranceStatus.PAYING);
		    fee.setCarInsuranceRepayment(repayment);
		    carInsuranceFeeDAO.create(fee);
		    break;
		case TENMONTH:
		    feeAmount = carInsurance.getAmount().multiply(new BigDecimal(configManager.getCarInsuranceConfig().getPeriodFee().getTenmonth()));
		    fee.setFeeAmount(feeAmount);
		    fee.setStatus(CarInsuranceStatus.PAYING);
		    fee.setCarInsuranceRepayment(repayment);
		    carInsuranceFeeDAO.create(fee);
		    break;
		default:
		    logger.info("当前车险分期的期数与分期的还款不匹配,请确保数据完整性后方可操作.");
		    break;
	    }
	    //手续费记录 END
	}
    }

    public boolean advanceRepayAll(String id) {
	Boolean bool = false;
	//1 修改车险还款为已还清
	CarInsurance carInsurance = carInsuranceDAO.find(id);
	//2 修改该分期所有的还款计划为已还清 并添加费用
	if (carInsurance != null && carInsurance.getCarInsuranceStatus().equals(CarInsuranceStatus.PAYING)) {
	    List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listByCarInsurance(carInsurance);
	    for (CarInsuranceRepayment repayment : repayments) {
		switch (repayment.getStatus()) {
		    case PAYING:
			//判断如果是还款中的状态才可以还款
			carInsuranceRepaymentService.advanceRepay(repayment.getId());
			break;
		    case CANCELED:
			logger.debug("该车险还款计划已还清repayment: {}", repayment);
			break;
		    default:
		    //nothing
		}

	    }
	    bool = true;
	} else {
	    logger.debug("该车险分期不存在或者已还清 {},状态 {}", id, carInsurance.getCarInsuranceStatus());
	}
	return bool;
    }

    //############################ market使用 ################################
    @Override
    public PagedResult<CarInsuranceModel> listCarInsuranceByUser(String clientCode, String userId, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	PagedResult<CarInsurance> pagedResult = carInsuranceDAO.listByUser(userId, pageInfo);
	List<CarInsuranceModel> lists = new ArrayList<CarInsuranceModel>(pagedResult.getResults().size());
	for (CarInsurance carInsurance : pagedResult.getResults()) {
	    User user = userService.findByUserId(appBean.getClientCode(), carInsurance.getUserId());
	    lists.add(CarInsuranceDTOUtils.convertCarInsuranceDTO(carInsurance, user));
	}

	return new PagedResult<>(lists, pagedResult.getTotalSize());

    }

    /**
     * 获取用户的车险分期
     *
     * @param clientCode
     * @param userId
     * @param from
     * @param to
     * @param pageInfo
     * @param status
     * @return
     */
    @Override
    public PagedResult<CarInsuranceModel> listCarInsuranceByUser(String clientCode, String userId, Date from, Date to, PageInfo pageInfo, CarInsuranceStatus... status) {
	appBean.checkClientCode(clientCode);
	PagedResult<CarInsurance> pagedResult = carInsuranceDAO.listCarInsurance(from, to, pageInfo, status);
	List<CarInsuranceModel> lists = new ArrayList<CarInsuranceModel>(pagedResult.getResults().size());
	for (CarInsurance carInsurance : pagedResult.getResults()) {
	    User user = userService.findByUserId(appBean.getClientCode(), carInsurance.getUserId());
	    lists.add(CarInsuranceDTOUtils.convertCarInsuranceDTO(carInsurance, user));
	}
	return new PagedResult<>(lists, pagedResult.getTotalSize());
    }

    /**
     * 根据 是否到期获取 还款计划 通过之前DAoLocalBean抽取出来
     */
    @Override
    public PagedResult<CarInsuranceRepaymentModel> listCarInsuranceDueRepayByUser(String clientCode, String userId, Date from, Date to, PageInfo pageInfo, CarInsuranceStatus... status) {
	appBean.checkClientCode(clientCode);
	logger.debug("listCarInsuranceDueRepayByUser.[clientCode={}][userId={}][from={}][to={}][pageInfo={}][status={}]", clientCode, userId, from, to, pageInfo, Arrays.asList(status));
	PagedResult<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listCarInsuranceDueRepayByUser(userId, from, to, pageInfo, status);
	List<CarInsuranceRepaymentModel> result = new ArrayList<>(repayments.getResults().size());
	for (CarInsuranceRepayment repayment : repayments.getResults()) {
	    User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	    result.add(CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user));
	}
	return new PagedResult<>(result, repayments.getTotalSize());
    }

    /**
     * 根据id获取车险还款计划的模型信息
     *
     * @param id
     * @return
     */
    @Override
    public CarInsuranceRepaymentModel listCarInsuranceRepaymentById(String id) {

	CarInsuranceRepayment repayment = carInsuranceRepaymentDAO.find(id);
	//计算逾期罚息
	BigDecimal penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	repayment.setAmountInterest(penaltyAmount);
	User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	CarInsuranceRepaymentModel model = CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user);

	return model;
    }

    /**
     * 根据车险分期id获取车险还款计划的模型信息列表
     *
     * @param carInsuranceid
     * @return
     */
    @Override
    public List<CarInsuranceRepaymentModel> getCarInsuranceRepaymentDetailById(String carInsuranceid) {
	logger.debug(carInsuranceid);
	CarInsurance carInsurance = carInsuranceDAO.find(carInsuranceid);
	List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listByCarInsurance(carInsurance);
	List<CarInsuranceRepaymentModel> result = new ArrayList<>(repayments.size());
	for (CarInsuranceRepayment repayment : repayments) {
	    //计算逾期罚息
	    BigDecimal penaltyAmount = carInsuranceFeeLocalBean.overdueFee(repayment);
	    repayment.setAmountInterest(penaltyAmount);
	    User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
	    result.add(CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user));
	}
	return result;
    }

    /**
     * 获取提前一次性还清 明细
     *
     * @param carInsuranceid
     * @return
     */
    public CarInsuranceRepayDetail getCarInsuranceRepayDetailById(String carInsuranceid) {
	CarInsurance carInsurance = carInsuranceDAO.find(carInsuranceid);
	List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listByCarInsurance(carInsurance);
	/**
	 * 统计未还期数
	 *
	 */
	List<CarInsuranceRepaymentModel> repaymentModels = new ArrayList<>();

	//计算出已还清的金额
	BigDecimal repayedAmount = BigDecimal.ZERO;
	for (CarInsuranceRepayment repayment : repayments) {
	    logger.debug("{}还款状态{}", repayment.getCarInsurance().getTitle(), repayment.getStatus());
	    if (repayment.getStatus() == CarInsuranceStatus.PAYING) {
		//统计正在还款的
		User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
		repaymentModels.add(CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user));
	    } else if (repayment.getStatus() == CarInsuranceStatus.CLEARED) {
		//统计已还清的金额
		repayedAmount = repayedAmount.add(repayment.getAmountPrincipal());
	    }
	}
	//计算应还本金 principal =  借款总额-已还金额
	BigDecimal principal = carInsurance.getAmount().subtract(repayedAmount);
	//计算提还违约金 提还违约金=应还本金*费率(0.2%)
	BigDecimal breachRate = configManager.getCarInsuranceConfig().getAdvanceBreachFee().getRate();
	BigDecimal penalty = principal.multiply(breachRate);

	CarInsuranceRepayDetail repayDetail = new CarInsuranceRepayDetail(principal, repaymentModels, penalty);

	return repayDetail;
    }

}
