/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.wealthproduct.entities.WealthProduct;
import com.creditcloud.wealthproduct.enums.WealthProductStatus;
import com.creditcloud.wealthproduct.utils.DTOUtils;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class WealthProductDAO extends AbstractDAO<WealthProduct> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public WealthProductDAO() {
        super(WealthProduct.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @param product
     * @return
     */
    public WealthProduct addNew(WealthProduct product) {
        try {
            //first validate
            getValidatorWrapper().tryValidate(product);

            if (product.getId() != null) {
                logger.warn("fail to add new wealth product, productId {} is not null", product.getId());
                return null;
            }
            WealthProduct result = create(product);
            logger.debug("new wealth product added.\n {}", result);
            return result;
        } catch (InvalidException ex) {
            logger.error("wealth product is not valid!\n {}", product, ex);
        } catch (Exception ex) {
            logger.error("Add new wealth product failed!\n {}", product, ex);
        }

        return null;
    }

    /**
     *
     * @param product
     * @return
     */
    public WealthProduct update(com.creditcloud.wealthproduct.model.WealthProduct product) {
        try {
            //first validate
            getValidatorWrapper().tryValidate(product);

            String productId = product.getId();
            if (productId == null) {
                logger.warn("productId can not be null for update!");
                return null;
            }
            if (find(productId) == null) {
                logger.warn("productId {} not exist for update.", productId);
                return null;
            }

            WealthProduct existProduct = find(productId);
            //only following fields can be updated
            existProduct.setDescription(product.getDescription());
            existProduct.setDuration(com.creditcloud.common.utils.DTOUtils.convertDurationDTO(product.getDuration()));
            existProduct.setRate(product.getRate());
            existProduct.setRepayMethod(product.getRepayMethod());
            existProduct.setReturnMethod(product.getReturnMethod());
            existProduct.setSchedule(DTOUtils.convertProductSchedule(product.getSchedule()));
            existProduct.setTitle(product.getTitle());
            existProduct.setStatus(product.getStatus());

            edit(existProduct);
            logger.debug("update wealth product.\n {}", existProduct);
            return find(product.getId());
        } catch (InvalidException ex) {
            logger.error("wealth product {} is not valid!", product, ex);
        } catch (Exception ex) {
            logger.error("update wealth product failed!\n {}", product, ex);
        }
        return null;
    }

    /**
     *
     * @param productId
     * @param status
     * @return
     */
    public boolean markStatus(String productId, WealthProductStatus status) {
        int result = getEntityManager()
                .createNamedQuery("WealthProduct.markStatus")
                .setParameter("productId", productId)
                .setParameter("status", status)
                .executeUpdate();
        return result > 0;
    }

    public long sumByStatus(List<WealthProductStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("WealthProduct.sumByStatus", Long.class)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }

    public int countByStatus(List<WealthProductStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("WealthProduct.countByStatus", Long.class)
                .setParameter("statusList", statusList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<WealthProduct> listByStatus(List<WealthProductStatus> statusList, PageInfo pageInfo) {
        if (statusList == null || statusList.isEmpty()) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<WealthProduct> result = getEntityManager()
                .createNamedQuery("WealthProduct.listByStatus", WealthProduct.class)
                .setParameter("statusList", statusList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        int totalSize = countByStatus(statusList);

        return new PagedResult<>(result, totalSize);
    }
}
