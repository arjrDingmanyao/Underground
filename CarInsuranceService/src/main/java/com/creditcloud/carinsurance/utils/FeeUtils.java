/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.utils;

import com.creditcloud.config.Fee;
import com.creditcloud.model.constant.NumberConstant;
import java.math.BigDecimal;

/**
 * 车险分期逾期费用
 *
 * @author Administrator
 */
public class FeeUtils {

    public static BigDecimal calculate(Fee fee, BigDecimal amount) {
	if (fee == null || amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
	    return BigDecimal.ZERO;
	}
	return amount.multiply(fee.getRate()).setScale(NumberConstant.DEFAULT_SCALE, NumberConstant.ROUNDING_MODE);
    }
}
