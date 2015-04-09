/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.criteria.ParamInfo;
import com.creditcloud.model.criteria.SortInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.sms.entities.SMSMessage;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 * 短信发送日志存储DAO
 *
 * @author Administrator
 */
@Stateless
@LocalBean
public class SMSMessageDAO extends AbstractDAO<SMSMessage> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "SmsPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SMSMessageDAO() {
	super(SMSMessage.class);
    }

    /**
     * 分页查询
     *
     * @param pageInfo
     * @return
     */
    public PagedResult<SMSMessage> findByPage(PageInfo pageInfo) {
	SortInfo sortInfo = new SortInfo();
	sortInfo.by("sentTime", false);
	ParamInfo paramInfo = new ParamInfo();
	CriteriaInfo info = new CriteriaInfo(paramInfo, pageInfo, sortInfo);
	return this.findAll(info);
    }

    public PagedResult<SMSMessage> findByPageAndDateRange(PageInfo pageInfo, Date from, Date to, String receiver) {
	Query query = getEntityManager()
		.createNamedQuery("SMSMessage.listByDaterangeOrSecrch", SMSMessage.class)
		.setParameter("from", from)
		.setParameter("to", to)
		.setParameter("receiver",receiver)
		.setParameter("content",receiver)
		.setFirstResult(pageInfo.getOffset())
		.setMaxResults(pageInfo.getSize());
	int totalSize = countByPageAndDateRange(from, to, receiver);
	return new PagedResult<>(query.getResultList(), totalSize);

    }

    public int countByPageAndDateRange(Date from, Date to, String receiver) {
	Long result = getEntityManager()
		.createNamedQuery("SMSMessage.countByDaterangeOrSecrch", Long.class)
		.setParameter("from", from)
		.setParameter("to", to)
		.setParameter("receiver", receiver)
		.setParameter("content",receiver)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

}
