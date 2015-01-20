/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.entities.dao;

import com.creditcloud.carinsurance.entities.CarInsuranceMessageRecord;
import com.creditcloud.common.entities.dao.AbstractDAO;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 * 接口日志DAO 车险分期
 *
 * @author Administrator
 */
@LocalBean
@Stateless
public class CarInsuranceMessageRecordDAO extends AbstractDAO<CarInsuranceMessageRecord> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ManagerPU")
    private EntityManager em;

    public CarInsuranceMessageRecordDAO() {
	super(CarInsuranceMessageRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    /**
     * 根据业务id查找
     *
     * @param serialNo
     * @return
     */
    public CarInsuranceMessageRecord findByInsuranceNum(String serialNo, String type) {
	List<CarInsuranceMessageRecord> list = getEntityManager().createNamedQuery("CarInsuranceMessageRecord.findByInsuranceNum", CarInsuranceMessageRecord.class)
		.setParameter("serialNo", serialNo).setParameter("type", type).getResultList();
	if (null != list && list.size() > 0) {
	    return list.get(0);
	} else {
	    return null;
	}
    }
}
