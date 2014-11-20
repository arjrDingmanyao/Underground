/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.record.VehicleRecord;
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
public class VehicleRecordDAO extends AbstractDAO<VehicleRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public VehicleRecordDAO() {
        super(VehicleRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VehicleRecord> listByVehicle(String vehicleId) {
        return getEntityManager()
                .createNamedQuery("VehicleRecord.listByVehicle", VehicleRecord.class)
                .setParameter("vehicleId", vehicleId)
                .getResultList();
    }
}
