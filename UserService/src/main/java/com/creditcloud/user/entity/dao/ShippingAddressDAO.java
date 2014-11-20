/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.user.entity.ShippingAddress;
import com.creditcloud.user.utils.DTOUtils;
import java.util.Date;
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
public class ShippingAddressDAO extends AbstractDAO<ShippingAddress> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public ShippingAddressDAO() {
        super(ShippingAddress.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ShippingAddress addNew(com.creditcloud.user.model.ShippingAddress shippingAddress) {
        try {
            getValidatorWrapper().tryValidate(shippingAddress);
            ShippingAddress result = create(DTOUtils.convertShippingAddress(shippingAddress));
            if (result.isDefaultAddress()) {
                //mark default
                markDefault(result.getUserId(), result.getId());
            }
            logger.debug("new ShippingAddress added.\n {}", result);
            return result;
        } catch (InvalidException ex) {
            logger.warn("ShippingAddress {} is not valid!", shippingAddress, ex);
        } catch (Exception ex) {
            logger.warn("Add new ShippingAddress failed!!!\n{}", shippingAddress, ex);
        }
        return null;
    }

    public ShippingAddress update(com.creditcloud.user.model.ShippingAddress shippingAddress) {
        try {
            if (shippingAddress.getId() == null) {
                logger.warn("fail to update shippingAddress as id is null.\n {}", shippingAddress);
                return null;
            }
            ShippingAddress result = find(shippingAddress.getId());
            if (result == null) {
                logger.warn("fail to find shippingAddress to update, id {}", shippingAddress.getId());
                return null;
            }
            getValidatorWrapper().tryValidate(shippingAddress);
            boolean resetDefault = (!result.isDefaultAddress() && shippingAddress.isDefaultAddress());
            result = DTOUtils.convertShippingAddress(shippingAddress);
            result.setTimeLastUpdated(new Date());
            edit(result);
            if (resetDefault) {
                //reset default
                markDefault(shippingAddress.getUserId(), shippingAddress.getId());
            }
            result = find(shippingAddress.getId());
            logger.debug("ShippingAddress updated.\n {}", result);
            return result;
        } catch (InvalidException ex) {
            logger.warn("ShippingAddress {} is not valid!", shippingAddress, ex);
        } catch (Exception ex) {
            logger.warn("update ShippingAddress failed!!!\n{}", shippingAddress, ex);
        }
        return null;
    }

    public List<ShippingAddress> listByUser(String userId) {
        return getEntityManager()
                .createNamedQuery("ShippingAddress.listByUser", ShippingAddress.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public boolean markDefault(String userId, String addressId) {
        int result = getEntityManager()
                .createNamedQuery("ShippingAddress.markDefault")
                .setParameter("userId", userId)
                .setParameter("addressId", addressId)
                .executeUpdate();
        return result > 0;
    }

    public ShippingAddress getDefault(String userId) {
        List<ShippingAddress> result = getEntityManager()
                .createNamedQuery("ShippingAddress.getDefault", ShippingAddress.class)
                .setParameter("userId", userId)
                .getResultList();
        if (result.isEmpty()) {
            //TODO return random 
            return null;
        }
        return result.get(0);
    }
}
