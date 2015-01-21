/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceService;
import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRepaymentDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRepayDetail;
import com.creditcloud.carinsurance.model.CarInsuranceRepaymentModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceDuration;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.carinsurance.utils.DateUtils;
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

    /**
     * 设置定时器
     *
     */
    @Schedule(persistent = false, second = "1", minute = "*/1", hour = "*")
    private void minuteUpdateCheckBrach() {
	logger.debug("CarInsuranceServiceBean minuteUpdateCheckBrach.[time={}ms]", System.currentTimeMillis());
    }

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
    public CarInsuranceModel getByCarInsuranceModelById(String clientCode, String Id) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 接收保存车险分期信息 并计算出还款计划
     *
     * @param model
     */
    @Override
    public void create(CarInsuranceModel model) {
	CarInsurance carInsurance = CarInsuranceDTOUtils.convertCarInsurance(model);
	//1 根据分期类别 然后计算还款计划
	carInsurance = carInsuranceDAO.create(carInsurance);
	BigDecimal first = carInsurance.getTotalAmount();
	BigDecimal second = new BigDecimal(carInsurance.getDuration());
	//应该除法
	BigDecimal amountPrincipal = first.divide(second);
	logger.debug("create repayment carInsurance :\n{}", carInsurance);
	for (int i = 1; i <= carInsurance.getDuration(); i++) {
	    //计算出还款时间
	    Date dueDate = DateUtils.offset(new Date(), new CarInsuranceDuration(0, i, 0));
	    CarInsuranceRepayment repayment = new CarInsuranceRepayment(
		    new BigDecimal(0),
		    carInsurance,
		    i,
		    dueDate,
		    amountPrincipal,
		    CarInsuranceStatus.PAYING,
		    new BigDecimal(0),
		    new Date());
	    //保存到数据库
	    carInsuranceRepaymentDAO.create(repayment);
	}

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
	PagedResult<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listCarInsuranceDueRepay(from, to, pageInfo, status);
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
    public List<CarInsuranceRepaymentModel> getCarInsuranceDetailById(String carInsuranceid) {
	logger.debug(carInsuranceid);
	CarInsurance carInsurance = carInsuranceDAO.find(carInsuranceid);
	List<CarInsuranceRepayment> repayments = carInsuranceRepaymentDAO.listByCarInsurance(carInsurance);
	List<CarInsuranceRepaymentModel> result = new ArrayList<>(repayments.size());
	for (CarInsuranceRepayment repayment : repayments) {
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
	    if (repayment.getStatus() == CarInsuranceStatus.CLEARED) {
		//只要未还清 则计算在内
		repayedAmount = repayedAmount.add(repayment.getAmountPrincipal());
	    } else {
		//记录未还的期数
		User user = userService.findByUserId(appBean.getClientCode(), repayment.getCarInsurance().getUserId());
		repaymentModels.add(CarInsuranceDTOUtils.convertCarInsuranceRepaymentDTO(repayment, user));
	    }
	}
	//计算应还本金 principal =  借款总额-已还金额
	BigDecimal principal = carInsurance.getAmount().subtract(repayedAmount);
	//计算提还违约金 提还违约金=应还本金*费率(0.2%)
	BigDecimal penaltyRate = new BigDecimal(0.002);
	BigDecimal penalty = principal.multiply(penaltyRate);

	CarInsuranceRepayDetail repayDetail = new CarInsuranceRepayDetail(principal, repaymentModels, penalty);

	return repayDetail;
    }

}
