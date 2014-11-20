/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.utils;

import com.creditcloud.wealthproduct.entities.ProductFee;
import com.creditcloud.wealthproduct.entities.ProductSchedule;
import com.creditcloud.wealthproduct.entities.Purchase;
import com.creditcloud.wealthproduct.entities.PurchaseRepayment;
import com.creditcloud.wealthproduct.entities.WealthProduct;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle ProductSchedule
     *
     * @param schedule
     * @return
     */
    public static com.creditcloud.wealthproduct.model.ProductSchedule getProductSchedule(ProductSchedule schedule) {
        com.creditcloud.wealthproduct.model.ProductSchedule result = null;
        if (schedule != null) {
            result = new com.creditcloud.wealthproduct.model.ProductSchedule(schedule.getAppointStartTime(),
                                                                             schedule.getAppointEndTime(),
                                                                             schedule.getPurchaseStartTime(),
                                                                             schedule.getPurchaseEndTime(),
                                                                             schedule.getManageStartTime(),
                                                                             schedule.getManageEndTime());
        }
        return result;
    }
    
    public static ProductSchedule convertProductSchedule(com.creditcloud.wealthproduct.model.ProductSchedule schedule) {
        ProductSchedule result = null;
        if (schedule != null) {
            result = new ProductSchedule(schedule.getAppointStartTime(),
                                         schedule.getAppointEndTime(),
                                         schedule.getPurchaseStartTime(),
                                         schedule.getPurchaseEndTime(),
                                         schedule.getManageStartTime(),
                                         schedule.getManageEndTime());
        }
        return result;
    }

    /**
     * handle WealthProduct
     *
     * @param product
     * @return
     */
    public static com.creditcloud.wealthproduct.model.WealthProduct getWealthProduct(WealthProduct product) {
        com.creditcloud.wealthproduct.model.WealthProduct result = null;
        if (product != null) {
            result = new com.creditcloud.wealthproduct.model.WealthProduct(product.getId(),
                                                                           product.getTitle(),
                                                                           product.getReturnMethod(),
                                                                           product.getRepayMethod(),
                                                                           product.getStatus(),
                                                                           product.getUserId(),
                                                                           product.getRate(),
                                                                           com.creditcloud.common.utils.DTOUtils.getDurationDTO(product.getDuration()),
                                                                           product.getAmount(),
                                                                           getProductSchedule(product.getSchedule()),
                                                                           product.getDescription());
            result.setPurchaseAmount(product.getPurchaseAmount());
            result.setPurchaseNumber(product.getPurchaseNumber());
        }
        return result;
    }
    
    public static WealthProduct convertWealthProduct(com.creditcloud.wealthproduct.model.WealthProduct product) {
        WealthProduct result = null;
        if (product != null) {
            result = new WealthProduct(product.getTitle(),
                                       product.getReturnMethod(),
                                       product.getRepayMethod(),
                                       product.getStatus(),
                                       product.getUserId(),
                                       product.getRate(),
                                       com.creditcloud.common.utils.DTOUtils.convertDurationDTO(product.getDuration()),
                                       product.getAmount(),
                                       product.getPurchaseAmount(),
                                       product.getPurchaseNumber(),
                                       convertProductSchedule(product.getSchedule()),
                                       product.getDescription());
            result.setId(product.getId());
        }
        return result;
    }

    /**
     * handle Purchase
     *
     * @param purchase
     * @return
     */
    public static com.creditcloud.wealthproduct.model.Purchase getPurchase(Purchase purchase) {
        com.creditcloud.wealthproduct.model.Purchase result = null;
        if (purchase != null) {
            result = new com.creditcloud.wealthproduct.model.Purchase(purchase.getId(),
                                                                      purchase.getProduct().getId(),
                                                                      purchase.getUserId(),
                                                                      purchase.getAmount(),
                                                                      purchase.getStatus(),
                                                                      purchase.getSubmitTime());
            result.setRate(purchase.getProduct().getRate());
            result.setDuration(com.creditcloud.common.utils.DTOUtils.getDurationDTO(purchase.getProduct().getDuration()));
        }
        return result;
    }

    /**
     * handle ProductFee
     *
     * @param fee
     * @return
     */
    public static com.creditcloud.wealthproduct.model.ProductFee getProductFee(ProductFee fee) {
        com.creditcloud.wealthproduct.model.ProductFee result = null;
        if (fee != null) {
            result = new com.creditcloud.wealthproduct.model.ProductFee(fee.getProductId(),
                                                                        fee.getPurchaseFee(),
                                                                        fee.getManageFee(),
                                                                        fee.getRedeemFee(),
                                                                        fee.getAdvanceRedeemFee());
        }
        return result;
    }

    /**
     * handle PurchaseRepayment
     *
     * @param repayment
     * @return
     */
    public static com.creditcloud.wealthproduct.model.PurchaseRepayment getPurchaseRepayment(PurchaseRepayment repayment) {
        com.creditcloud.wealthproduct.model.PurchaseRepayment result = null;
        if (repayment != null) {
            result = new com.creditcloud.wealthproduct.model.PurchaseRepayment(repayment.getPurchase().getId(),
                                                                               repayment.getPeriod(),
                                                                               com.creditcloud.common.utils.DTOUtils.getRepaymentDTO(repayment.getRepayment()),
                                                                               repayment.getRepayAmount(),
                                                                               repayment.getRepayDate(),
                                                                               repayment.getStatus());
        }
        return result;
    }
}
