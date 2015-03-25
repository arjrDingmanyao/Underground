/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.local;

import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.carinsurance.utils.FeeUtils;
import com.creditcloud.config.Fee;
import com.creditcloud.config.FeeConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.constant.NumberConstant;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;

/**
 * 计算出车险分期的逾期罚息
 * @author Administrator
 */
@Stateless
@LocalBean
public class CarInsuranceFeeLocalBean {

    @EJB
    private ConfigManager configManager;

    /**
     * 逾期罚息
     * @param repayment
     * @return 
     */
    public BigDecimal overdueFee(CarInsuranceRepayment repayment) {
	if (repayment == null) {
	    return BigDecimal.ZERO;
	}
	if (LocalDate.now().toDate().compareTo(repayment.getDueDate()) <= 0) {
	    return BigDecimal.ZERO;
	}
	BigDecimal days = BigDecimal.ZERO;
	//计算天数
	if (repayment.getRepayDate() != null) {
	    long repayTime = repayment.getRepayDate().getTime();
	    long dueTime = repayment.getDueDate().getTime();
	    days = BigDecimal.valueOf((repayTime - dueTime) / DateUtils.MILLIS_PER_DAY);
	    if (days.compareTo(BigDecimal.ZERO) < 0) {
		return BigDecimal.ZERO;
	    }
	} else {
	    long nowTime = LocalDate.now().toDate().getTime();
	    long dueTime = repayment.getDueDate().getTime();
	    days = BigDecimal.valueOf((nowTime - dueTime) / DateUtils.MILLIS_PER_DAY);
	}
	BigDecimal penaltyAmount = BigDecimal.ZERO;
	//获取配置文件的费率信息
	Fee overduePenaltyFee = configManager.getCarInsuranceConfig().getPenaltyFee();
	penaltyAmount = FeeUtils.calculate(overduePenaltyFee, repayment.getAmountPrincipal()).multiply(days).setScale(NumberConstant.DEFAULT_SCALE, NumberConstant.ROUNDING_MODE);
	return penaltyAmount;
    }
}
