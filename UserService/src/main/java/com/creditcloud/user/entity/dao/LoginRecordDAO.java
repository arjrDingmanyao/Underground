/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.user.entity.record.UserLoginRecord;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.joda.time.LocalDate;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class LoginRecordDAO extends AbstractDAO<UserLoginRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public LoginRecordDAO() {
	super(UserLoginRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public int countByUser(String userId) {
	Long result = getEntityManager()
		.createNamedQuery("UserLoginRecord.countByUser", Long.class)
		.setParameter("userId", userId)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    public int countFailedLoginByUser(String userId, Date since) {
	Long result = getEntityManager()
		.createNamedQuery("UserLoginRecord.countFailedLoginByUser", Long.class)
		.setParameter("userId", userId)
		.setParameter("since", since)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    public List<ElementCount<LocalDate>> dailyLogin(Date from, Date to) {
	List<Object[]> objects = getEntityManager()
		.createNamedQuery("UserLoginRecord.dailyLogin")
		.setParameter("from", from)
		.setParameter("to", to)
		.getResultList();
	List<ElementCount<LocalDate>> result = new ArrayList<>(objects.size());
	for (Object[] object : objects) {
	    Date day = (Date) object[0];
	    int count = ((Long) object[1]).intValue();
	    result.add(new ElementCount<>(new LocalDate(day), count));
	}
	return result;
    }

    public List<ElementCount<LocalDate>> dailyLoginUser(Date from, Date to) {
	List<Object[]> objects = getEntityManager()
		.createNamedQuery("UserLoginRecord.dailyLoginUser")
		.setParameter("from", from)
		.setParameter("to", to)
		.getResultList();
	List<ElementCount<LocalDate>> result = new ArrayList<>(objects.size());
	for (Object[] object : objects) {
	    Date day = (Date) object[0];
	    int count = ((Long) object[1]).intValue();
	    result.add(new ElementCount<>(new LocalDate(day), count));
	}
	return result;
    }

    public List<String> listByLoginDate(Date from, Date to) {
	List<String> result = getEntityManager()
		.createNamedQuery("UserLoginRecord.listByLoginDate", String.class)
		.setParameter("from", from)
		.setParameter("to", to)
		.getResultList();
	return result;
    }

    /**
     * 根据日期统计用户登录
     *
     * @param from
     * @param to
     * @param pageInfo
     * @return
     */
    public PagedResult<UserLoginRecord> listByLoginDateRange(Date from, Date to, PageInfo pageInfo) {
	List<UserLoginRecord> result = getEntityManager()
		.createNamedQuery("UserLoginRecord.listByLoginDateRange", UserLoginRecord.class)
		.setParameter("from", from)
		.setParameter("to", to)
		.setFirstResult(pageInfo.getOffset())
		.setMaxResults(pageInfo.getSize())
		.getResultList();
	return new PagedResult<>(result, countByLoginDateRange(from, to));
    }

    public int countByLoginDateRange(Date from, Date to) {
	Long result = getEntityManager()
		.createNamedQuery("UserLoginRecord.countByLoginDateRange", Long.class)
		.setParameter("from", from)
		.setParameter("to", to)
		.getSingleResult();
	return result == null ? 0 : result.intValue();
    }

    /**
     * check whether it is the first successful login for an user
     *
     * @param userId
     * @return
     */
    public boolean isFirstLogin(String userId) {
	return countByUser(userId) == 1;
    }
}
