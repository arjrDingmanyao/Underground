/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.RealEstate;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class RealEstateDAO extends AbstractDAO<RealEstate> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public RealEstateDAO() {
        super(RealEstate.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<RealEstate> listByUser(String userId) {
        return getEntityManager()
                .createNamedQuery("RealEstate.listByUser", RealEstate.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void update(com.creditcloud.model.user.asset.RealEstate estate) {
        RealEstate result = find(estate.getId());
        if (result != null) {
            result.setArea(estate.getArea());
            result.setDescription(estate.getDescription());
            result.setEstimatedValue(estate.getEstimatedValue());
            result.setLoan(estate.isLoan());
            result.setLocation(estate.getLocation());
            result.setType(estate.getType());
            result.setLongitude(estate.getLongitude());
            result.setLatitude(estate.getLatitude());
            result.setLastModifiedBy(estate.getLastModifiedBy());
            result.setSource(estate.getSource());
            edit(result);
        }
    }
}
