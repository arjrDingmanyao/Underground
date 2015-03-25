package com.creditcloud.carinsurance.entities.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.creditcloud.carinsurance.entities.CarInsurance;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 * 车险分期
 *
 * @author wangwei
 */
@Stateless
@LocalBean
public class CarInsuranceDAO extends AbstractDAO<CarInsurance> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ManagerPU")
    private EntityManager em;

    public CarInsuranceDAO() {
	super(CarInsurance.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public int countByUser(String userId) {
	Long result = getEntityManager()
		.createNamedQuery("CarInsurance.countByUser", Long.class)
		.setParameter("userId", userId)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    /**
     * 查询全部车险分期列表
     *
     * @param from
     * @param to
     * @param info
     * @param status
     * @return
     */
    public PagedResult<CarInsurance> listCarInsurance(Date from, Date to, PageInfo info, CarInsuranceStatus... status) {
	if (status == null || status.length == 0) {
	    return new PagedResult<>(Collections.EMPTY_LIST, 0);
	}
	Query query = getEntityManager()
		.createNamedQuery("CarInsurance.listCarInsurance", CarInsurance.class);
//                .setParameter("statusList", Arrays.asList(status))
//                .setParameter("from", from)
//                .setParameter("to", to);
	query.setFirstResult(info.getOffset());
	query.setMaxResults(info.getSize());

	int totalSize = countByStatus(from, to, status);
	return new PagedResult(query.getResultList(), totalSize);
    }

    /**
     * 按InsuranceStatus统计一定时间内提交的车险分期
     *
     * @param from
     * @param to
     * @param status
     * @return
     */
    public int countByStatus(Date from, Date to, CarInsuranceStatus... status) {
	if (status == null || status.length == 0) {
	    return 0;
	}
	Long result = getEntityManager()
		.createNamedQuery("CarInsurance.countCarInsurance", Long.class)
		//                .setParameter("statusList", Arrays.asList(status))
		//                .setParameter("from", from)
		//                .setParameter("to", to)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    /**
     * 根据保单号查询保单
     *
     * @param id
     * @return
     */
    public CarInsurance findByNum(String insuranceNum) {
	List<CarInsurance> list = getEntityManager().createNamedQuery("CarInsurance.findByNum", CarInsurance.class)
		.setParameter("insuranceNum", insuranceNum).getResultList();
	if (null != list && list.size() > 0) {
	    return list.get(0);
	} else {
	    return null;
	}
    }

    /**
     * 根据用户 查询
     *
     * @param userId
     * @param pageInfo
     * @return
     */
    public PagedResult<CarInsurance> listByUser(String userId, PageInfo pageInfo) {
	//get results
	Query query = getEntityManager()
		.createNamedQuery("CarInsurance.listByUser", CarInsurance.class)
		.setParameter("userId", userId);
	query.setFirstResult(pageInfo.getOffset());
	query.setMaxResults(pageInfo.getSize());
	List<CarInsurance> carInsurances = query.getResultList();

	//get total size
	int totalSize = countByUser(userId);
	return new PagedResult<>(carInsurances, totalSize);
    }

    /**
     * 用于修改车险分期的状态,只修改状态字段
     *
     * @param status
     * @param ids
     * @return
     */
    public boolean markStatus(CarInsuranceStatus status, String... ids) {
	if (ids == null || ids.length == 0) {
	    return false;
	}
	int result = getEntityManager()
		.createNamedQuery("CarInsurance.markStatus")
		.setParameter("status", status)
		.setParameter("ids", Arrays.asList(ids))
		.executeUpdate();
	getEntityManager().flush();
	return result > 0;
    }

}
