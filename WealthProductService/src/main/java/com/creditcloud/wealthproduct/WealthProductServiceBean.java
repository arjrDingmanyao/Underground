/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct;

import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.wealthproduct.api.WealthProductService;
import com.creditcloud.wealthproduct.entities.Purchase;
import com.creditcloud.wealthproduct.entities.WealthProduct;
import com.creditcloud.wealthproduct.entities.dao.PurchaseDAO;
import com.creditcloud.wealthproduct.entities.dao.WealthProductDAO;
import com.creditcloud.wealthproduct.enums.PurchaseStatus;
import com.creditcloud.wealthproduct.enums.WealthProductStatus;
import com.creditcloud.wealthproduct.local.ApplicationBean;
import com.creditcloud.wealthproduct.utils.DTOUtils;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class WealthProductServiceBean extends BaseBean implements WealthProductService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    WealthProductDAO productDAO;

    @EJB
    PurchaseDAO purchaseDAO;

    @Override
    public com.creditcloud.wealthproduct.model.WealthProduct create(String clientCode, com.creditcloud.wealthproduct.model.WealthProduct product) {
        appBean.checkClientCode(clientCode);
        WealthProduct result = productDAO.addNew(DTOUtils.convertWealthProduct(product));
        return DTOUtils.getWealthProduct(result);
    }

    @Override
    public com.creditcloud.wealthproduct.model.WealthProduct update(String clientCode, com.creditcloud.wealthproduct.model.WealthProduct product) {
        appBean.checkClientCode(clientCode);
        WealthProduct result = productDAO.update(product);
        return DTOUtils.getWealthProduct(result);
    }

    @Override
    public boolean markStatus(String clientCode, String productId, WealthProductStatus status) {
        appBean.checkClientCode(clientCode);
        return productDAO.markStatus(productId, status);
    }

    @Override
    public PagedResult<com.creditcloud.wealthproduct.model.WealthProduct> listByStatus(String clientCode,
                                                                                       List<WealthProductStatus> statusList,
                                                                                       PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<WealthProduct> entities = productDAO.listByStatus(statusList, pageInfo);
        List<com.creditcloud.wealthproduct.model.WealthProduct> result = new ArrayList<>(entities.getResults().size());
        for (WealthProduct product : entities.getResults()) {
            result.add(DTOUtils.getWealthProduct(product));
        }

        return new PagedResult<>(result, entities.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.wealthproduct.model.Purchase> listPurchaseByProduct(String clientCode,
                                                                                           String productId,
                                                                                           List<PurchaseStatus> statusList,
                                                                                           PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);

        PagedResult<Purchase> entities = purchaseDAO.listByProduct(productId, statusList, pageInfo);
        List<com.creditcloud.wealthproduct.model.Purchase> result = new ArrayList<>(entities.getResults().size());
        for (Purchase purchase : entities.getResults()) {
            result.add(DTOUtils.getPurchase(purchase));
        }
        return new PagedResult<>(result, entities.getTotalSize());
    }

    @Override
    public com.creditcloud.wealthproduct.model.WealthProduct getById(String clientCode, String productId) {
        appBean.checkClientCode(clientCode);
        WealthProduct result = productDAO.find(productId);
        return DTOUtils.getWealthProduct(result);
    }
}
