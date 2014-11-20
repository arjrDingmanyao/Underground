/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.user.entity.Vehicle;
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
public class VehicleDAO extends AbstractDAO<Vehicle> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public VehicleDAO() {
        super(Vehicle.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Vehicle> listByUser(String userId) {
        return getEntityManager()
                .createNamedQuery("Vehicle.listByUser", Vehicle.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Vehicle> listByOwner(RealmEntity owner) {
        return getEntityManager()
                .createNamedQuery("Vehicle.listByOwner", Vehicle.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    public List<Vehicle> listByPlateNumber(String userId, String plateNumber) {
        return getEntityManager()
                .createNamedQuery("Vehicle.listByPlateNumber", Vehicle.class)
                .setParameter("userId", userId)
                .setParameter("plateNumber", plateNumber)
                .getResultList();
    }

    public void update(com.creditcloud.model.user.asset.Vehicle vehicle) {
        Vehicle result = find(vehicle.getId());
        if (result != null) {
            //user暂时不能更改
            result.setBrand(vehicle.getBrand());
            result.setDescription(vehicle.getDescription());
            result.setEstimatedValue(vehicle.getEstimatedValue());
            result.setModel(vehicle.getModel());
            result.setPlateNumber(vehicle.getPlateNumber());
            result.setPriceOfPurchase(vehicle.getPriceOfPurchase());
            result.setType(vehicle.getType());
            result.setVehicleLicense(vehicle.getVehicleLicense());
            result.setYearOfPurchase(vehicle.getYearOfPurchase());
            result.setLastModifiedBy(vehicle.getLastModifiedBy());
            result.setSource(vehicle.getSource());
            result.setMileage(vehicle.getMileage());
            result.setOperating(vehicle.isOperating());
            result.setOwner(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(vehicle.getOwner()));
            edit(result);
        }
    }
}
