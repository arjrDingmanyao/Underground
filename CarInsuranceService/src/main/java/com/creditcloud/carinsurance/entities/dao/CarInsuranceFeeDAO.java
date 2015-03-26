/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.entities.dao;

import com.creditcloud.carinsurance.entities.CarInsuranceFee;
import com.creditcloud.carinsurance.entities.CarInsuranceRepayment;
import com.creditcloud.common.entities.dao.AbstractDAO;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 * 车险分期手续费
 *
 * @author Administrator
 */
@Stateless
@LocalBean
public class CarInsuranceFeeDAO extends AbstractDAO<CarInsuranceFee> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ManagerPU")
    private EntityManager em;

    public CarInsuranceFeeDAO() {
        super(CarInsuranceFee.class);
    }
    
    public CarInsuranceFee findByInSuranceNumAndCurrentPeriod(String insuranceNum,int currentPeriod){
	Query query = getEntityManager()
		.createNamedQuery("CarInsuranceFee.findByInSuranceNumAndCurrentPeriod", CarInsuranceFee.class)
		.setParameter("insuranceNum", insuranceNum)
		.setParameter("currentPeriod", currentPeriod);
	return (CarInsuranceFee) query.getSingleResult();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
}
