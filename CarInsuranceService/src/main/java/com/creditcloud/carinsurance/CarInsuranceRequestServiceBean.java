/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance;

import com.creditcloud.carinsurance.api.CarInsuranceRequestService;
import com.creditcloud.carinsurance.api.CarInsuranceService;
import com.creditcloud.carinsurance.entities.CarInsuranceRequest;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceDAO;
import com.creditcloud.carinsurance.entities.dao.CarInsuranceRequestDAO;
import com.creditcloud.carinsurance.local.ApplicationBean;
import com.creditcloud.carinsurance.model.CarInsuranceModel;
import com.creditcloud.carinsurance.model.CarInsuranceRequestModel;
import com.creditcloud.carinsurance.model.enums.CarInsuranceChagreBackType;
import com.creditcloud.carinsurance.model.enums.CarInsuranceRequestStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.carinsurance.model.enums.CarInsuranceType;
import com.creditcloud.carinsurance.utils.CarInsuranceDTOUtils;
import com.creditcloud.model.client.Employee;
import com.creditcloud.model.constant.EmailConstant;
import com.creditcloud.model.constant.MobileConstant;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.user.User;
import com.creditcloud.user.api.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * 车险申请
 *
 * @author Administrator
 */
@Remote
@Stateless
public class CarInsuranceRequestServiceBean implements CarInsuranceRequestService {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @EJB
    UserService userService;

    @EJB
    private CarInsuranceDAO carInsuranceDAO;

    @EJB
    private CarInsuranceRequestDAO carInsuranceRequestDAO;

    @EJB
    private CarInsuranceService carInsuranceService;

    /**
     * 保存
     *
     * @param model
     * @return
     */
    @Override
    public CarInsuranceRequestModel create(CarInsuranceRequestModel model) {
	CarInsuranceRequest request = CarInsuranceDTOUtils.convertCarInsuranceRequest(model);
	CarInsuranceRequest result = carInsuranceRequestDAO.create(request);

	logger.debug("CarInsuranceRequest persist success:\n{}", result);
	return CarInsuranceDTOUtils.convertCarInsuranceRequestDTO(result);
    }

    /**
     * 更新
     *
     * @param model
     */
    @Override
    public void edit(CarInsuranceRequestModel model) {
	CarInsuranceRequest request = CarInsuranceDTOUtils.convertCarInsuranceRequest(model);
	if (request.getId() != null) {
	    logger.debug("edit CarInsuranceRequest success :\n {}", request);
	    carInsuranceRequestDAO.edit(request);
	} else {
	    logger.error("edit CarInsuranceRequest failure :\n{}", request);
	}

    }

    /**
     * 根据车险申请的状态信息 获取列表
     *
     * @param status CarInsuranceRequestStatus
     * @return
     */
    @Override
    public List<CarInsuranceRequestModel> listAllByStatus(CarInsuranceRequestStatus... status) {
	List<CarInsuranceRequestModel> result = new ArrayList<CarInsuranceRequestModel>();
	List<CarInsuranceRequest> list = carInsuranceRequestDAO.listByStatus(PageInfo.ALL, status).getResults();
	for (CarInsuranceRequest request : list) {
	    result.add(CarInsuranceDTOUtils.convertCarInsuranceRequestDTO(request));
	}
	return result;
    }

    /**
     * 根据id 获取一个对象
     *
     * @param id
     * @return
     */
    @Override
    public CarInsuranceRequestModel getCarInsuranceRequestModelById(String id) {
	CarInsuranceRequest request = carInsuranceRequestDAO.find(id);
	CarInsuranceRequestModel model = CarInsuranceDTOUtils.convertCarInsuranceRequestDTO(request);
	return model;
    }

    @Override
    public boolean approve(Employee employee, String requestId) {
	logger.debug("approve CarInsuranceRequest called.[employee={}]", employee, requestId);
	//update loan request status
	CarInsuranceRequest request = carInsuranceRequestDAO.find(requestId);
	if (request != null) {
//	    String loginName = MobileConstant.MOBILE_USER_LOGINNAME_PREFIX.concat(request.getMobile());
	    String loginName = "arjr".concat(request.getMobile());
	    logger.debug("车险分期创建的 loginname:{}", loginName);
	    //1 创建一个新用户，
	    User newUser = new User(null,
		    appBean.getClientCode(),
		    request.getName(),
		    loginName,
		    request.getIdNumber(),
		    request.getMobile(),
		    EmailConstant.DEFAULT_EMAIL,
		    Source.BACK,
		    employee.getId(),
		    employee.getId());
	    User user = null;
	    try {
		user = userService.addUser(appBean.getClientCode(), newUser);
		//2 添加一个车险分期信息
		CarInsuranceModel model = new CarInsuranceModel(
			"",
			request.getInsuranceNum(),
			user,
			user.getLoginName(),
			user.getName(),
			user.getMobile(),
			request.getAmount(),
			request.getRate(),
			CarInsuranceType.PERIOD,
			request.getFirstPayment(),
			request.getDuration(),
			request.getDurationType(),
			CarInsuranceChagreBackType.UNASSIGNED,
			request.getAcceptanceDate(),
			CarInsuranceStatus.INITIATED,
			request.getTitle(),
			request.getTotalAmount());
		//创建一个车险分期，在创建车险分期的同时生成还款计划
		carInsuranceService.create(model);
		logger.debug("车险分期批准通过./n{}", model);
		//修改申请标的状态为已批准
		request.setCarInsuranceRequestStatus(CarInsuranceRequestStatus.APPROVED);
		carInsuranceRequestDAO.edit(request);
		//该车险申请成功后,发送短信给用户
	    /*
		 * 在短信恢复正常前暂不发送该类信息 smsService.sendMessage(appBean.getClient(),
		 * SMSType.NOTIFICATION_LOANREQUEST_STATUS,
		 * userBridge.getMobile(request.getUserId()),
		 * TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(request.getTimeSubmit()),
		 * request.getTitle(), LoanRequestStatus.APPROVED.getKey());
		 */
		return true;
	    } catch (Exception ex) {
		logger.error("在车险分期中,自动创建该用户失败.\n{}", user);
		return false;
	    }
	} else {
	    logger.error("CarInsuranceRequest not exist or deleted. CarInsuranceRequest Id :{}", requestId);
	    return false;
	}

    }

}
