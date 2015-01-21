/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.carinsurance.entities.dao;

import com.creditcloud.carinsurance.entities.CarInsuranceRequest;
import com.creditcloud.carinsurance.model.enums.CarInsuranceRequestStatus;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 * 车险分期 DAO
 *
 * @author Administrator
 */
@LocalBean
@Stateless
public class CarInsuranceRequestDAO extends AbstractDAO<CarInsuranceRequest> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ManagerPU")
    private EntityManager em;

    public CarInsuranceRequestDAO() {
	super(CarInsuranceRequest.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    /**
     * 查询需要根据状态信息
     *
     * @param info
     * @param status
     * @return
     */
    public PagedResult<CarInsuranceRequest> listByStatus(PageInfo info, CarInsuranceRequestStatus... status) {
	if (status == null || status.length == 0) {
	    return new PagedResult<>(Collections.EMPTY_LIST, 0);
	}
	Query query = getEntityManager().createQuery("select request from CarInsuranceRequest request where request.carInsuranceRequestStatus in :statusList order by request.createDate", CarInsuranceRequest.class)
		.setParameter("statusList", Arrays.asList(status));
	query.setFirstResult(info.getOffset());
	query.setMaxResults(info.getSize());

	int totalSize = countByStatus(status);
	return new PagedResult(query.getResultList(), totalSize);
    }

    /**
     * 根据所给出的状态统计总记录数
     *
     * @param status
     * @return
     */
    public int countByStatus(CarInsuranceRequestStatus... status) {
	if (status == null || status.length == 0) {
	    return 0;
	}
	Long result = getEntityManager()
		.createQuery("select count(request) from CarInsuranceRequest request where request.carInsuranceRequestStatus in :statusList", Long.class)
		.setParameter("statusList", Arrays.asList(status))
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    /**
     * 根据保单号查询保单
     *
     * @param insuranceNum
     * @return
     */
    public CarInsuranceRequest findByNum(String insuranceNum) {
	List<CarInsuranceRequest> list = getEntityManager().createNamedQuery("CarInsuranceRequest.findByNum", CarInsuranceRequest.class)
		.setParameter("insuranceNum", insuranceNum).getResultList();
	if (null != list && list.size() > 0) {
	    return list.get(0);
	} else {
	    return null;
	}
    }

}
