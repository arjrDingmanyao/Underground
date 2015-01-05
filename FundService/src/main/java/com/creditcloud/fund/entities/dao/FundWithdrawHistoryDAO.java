/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.fund.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.fund.entities.FundWithdrawHistory;
import com.creditcloud.fund.local.ApplicationBean;
import com.creditcloud.fund.model.enums.FundRecordStatus;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 * 提现历史记录DAO
 *
 * @author Administrator
 */
@LocalBean
@Stateless
public class FundWithdrawHistoryDAO extends AbstractDAO<FundWithdrawHistory> {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @PersistenceContext(unitName = "FundPU")
    private EntityManager em;

    public FundWithdrawHistoryDAO() {
	super(FundWithdrawHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    /**
     * 分页获取
     *
     * @param pageInfo
     * @return
     */
    public PagedResult<FundWithdrawHistory> listByPageInfo(PageInfo pageInfo) {
	List<FundRecordStatus> statusList = new ArrayList<FundRecordStatus>();
	statusList.add(FundRecordStatus.SUCCESSFUL);
	statusList.add(FundRecordStatus.REJECTED);
	
	List<FundWithdrawHistory> result = getEntityManager()
		.createNamedQuery("FundWithdrawHistory.listByPageInfoByStatus", FundWithdrawHistory.class)
		.setParameter("statusList", statusList)
		.setFirstResult(pageInfo.getOffset())
		.setMaxResults(pageInfo.getSize())
		.getResultList();
	int totalSize = countAll();
	return new PagedResult<>(result, totalSize);
    }

    public int countAll() {
	List<FundRecordStatus> statusList = new ArrayList<FundRecordStatus>();
	statusList.add(FundRecordStatus.SUCCESSFUL);
	statusList.add(FundRecordStatus.REJECTED);
	
	Long result = getEntityManager()
		.createNamedQuery("FundWithdrawHistory.countByStatus", Long.class)
		.setParameter("statusList", statusList)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    /**
     * 修改状态
     *
     * @param status
     * @param id
     * @return
     */
    public boolean markStatus(FundRecordStatus status, String id) {
	int result = getEntityManager()
		.createNamedQuery("FundWithdrawHistory.markStatus")
		.setParameter("status", status)
		.setParameter("id", id)
		.executeUpdate();
	logger.debug("FundWithdrawHistory markStatus {}:{}", id, status);
	if (appBean.isEnableManualFlush()) {
	    getEntityManager().flush();
	}
	return result > 0;
    }

    /**
     * 根据订单id获取
     *
     * @param orderId
     * @return
     */
    public FundWithdrawHistory findByOrderId(String orderId) {
	FundWithdrawHistory result =null;
	logger.debug("findByOrderId ：{}",orderId);
	try {
	   result = getEntityManager()
		.createNamedQuery("FundWithdrawHistory.findByOrderId", FundWithdrawHistory.class)
		.setParameter("orderId", orderId)
		.getSingleResult();
	} catch (Exception e) {
	    
	}
		
	return result;
    }

}
