/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.appoint.entities.AppointRequest;
import com.creditcloud.appoint.enums.AppointRequestStatus;
import com.creditcloud.appoint.model.BranchAppointStat;
import com.creditcloud.appoint.model.DailyAppointStat;
import com.creditcloud.appoint.model.UserAppointStat;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.ElementSum;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.joda.time.LocalDate;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class AppointRequestDAO extends AbstractDAO<AppointRequest> {
    
    @PersistenceContext(unitName = "AppointPU")
    private EntityManager em;
    
    public AppointRequestDAO() {
        super(AppointRequest.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public void markStatus(AppointRequestStatus status, String... ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        getEntityManager()
                .createNamedQuery("AppointRequest.markStatus")
                .setParameter("status", status)
                .setParameter("ids", ids)
                .executeUpdate();
        getEntityManager().flush();
    }
    
    public PagedResult<AppointRequest> listByAppointment(String appointmentId, PageInfo pageInfo, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        
        Query query = getEntityManager()
                .createNamedQuery("AppointRequest.listByAppointment", AppointRequest.class)
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = countByAppointment(appointmentId, status);
        return new PagedResult<>(query.getResultList(), totalSize);
    }
    
    public int countByAppointment(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.countByAppointment", Long.class)
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public long sumByAppointment(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.sumByAppointment", Long.class)
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }
    
    public PagedResult<AppointRequest> listByUser(String userId, PageInfo pageInfo, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        
        Query query = getEntityManager()
                .createNamedQuery("AppointRequest.listByUser", AppointRequest.class)
                .setParameter("userId", userId)
                .setParameter("statusList", Arrays.asList(status))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = countByUser(userId, status);
        return new PagedResult<>(query.getResultList(), totalSize);
    }
    
    public int countByUser(String userId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.countByUser", Long.class)
                .setParameter("userId", userId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public long sumByUser(String userId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.sumByUser", Long.class)
                .setParameter("userId", userId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }
    
    public PagedResult<AppointRequest> listByAppointmentAndUser(String appointmentId, String userId, PageInfo pageInfo, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        
        Query query = getEntityManager()
                .createNamedQuery("AppointRequest.listByAppointmentAndUser", AppointRequest.class)
                .setParameter("appointmentId", appointmentId)
                .setParameter("userId", userId)
                .setParameter("statusList", Arrays.asList(status))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize());
        int totalSize = countByUser(userId, status);
        return new PagedResult<>(query.getResultList(), totalSize);
    }
    
    public int countByAppointmentAndUser(String appointmentId, String userId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.countByAppointmentAndUser", Long.class)
                .setParameter("userId", userId)
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public long sumByAppointmentAndUser(String appointmentId, String userId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.sumByAppointmentAndUser", Long.class)
                .setParameter("userId", userId)
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.longValue();
    }
    
    public List<ElementCount<String>> countUserByBranch(AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<ElementCount<String>> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.countUserByBranch")
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(branch, count == null ? 0 : count.intValue()));
        }
        
        return result;
    }
    
    public List<ElementCount<String>> countUserByBranchAndAppointment(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<ElementCount<String>> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.countUserByBranchAndAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(branch, count == null ? 0 : count.intValue()));
        }
        
        return result;
    }
    
    public List<ElementCount<String>> countEachByAppointmentAndBranch(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<ElementCount<String>> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.countEachByBranchAndAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(branch, count == null ? 0 : count.intValue()));
        }
        
        return result;
    }
    
    public List<ElementSum<String>> sumEachByAppointmentAndBranch(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<ElementSum<String>> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.sumEachByBranchAndAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long sum = (Long) object[1];
            result.add(new ElementSum<>(branch, sum == null ? BigDecimal.ZERO : BigDecimal.valueOf(sum)));
        }
        
        return result;
    }
    
    public List<BranchAppointStat> getBranchAppointStat(AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<BranchAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getBranchStat")
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new BranchAppointStat(branch, count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return result;
    }
    
    public List<BranchAppointStat> getBranchAppointStat(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<BranchAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getBranchStatByAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
        for (Object[] object : objects) {
            String branch = (String) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new BranchAppointStat(branch, count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return result;
    }
    
    public int countUser(AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.countUser", Long.class)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public int countUserByAppointment(String appointmentId, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("AppointRequest.countUserByAppointment", Long.class)
                .setParameter("statusList", Arrays.asList(status))
                .setParameter("appointmentId", appointmentId)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
    
    public PagedResult<UserAppointStat> getUserStat(String appointmentId, PageInfo pageInfo, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<UserAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getUserStatByAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        for (Object[] object : objects) {
            String user = (String) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new UserAppointStat(user, count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return new PagedResult<>(result, countUserByAppointment(appointmentId, status));
    }
    
    public PagedResult<UserAppointStat> getUserStat(PageInfo pageInfo, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        List<UserAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getUserStat")
                .setParameter("statusList", Arrays.asList(status))
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        for (Object[] object : objects) {
            String user = (String) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new UserAppointStat(user, count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return new PagedResult<>(result, countUser(status));
    }
    
    public List<DailyAppointStat> getDailyStat(Date from, Date to, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<DailyAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getDailyStat")
                .setParameter("statusList", Arrays.asList(status))
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
        for (Object[] object : objects) {
            Date date = (Date) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new DailyAppointStat(LocalDate.fromDateFields(date), count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return result;
    }
    
    public List<DailyAppointStat> getDailyStat(String appointmentId, Date from, Date to, AppointRequestStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<DailyAppointStat> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("AppointRequest.getDailyStatByAppointment")
                .setParameter("appointmentId", appointmentId)
                .setParameter("statusList", Arrays.asList(status))
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
        for (Object[] object : objects) {
            Date date = (Date) object[0];
            Long sum = (Long) object[1];
            Long count = (Long) object[2];
            result.add(new DailyAppointStat(LocalDate.fromDateFields(date), count == null ? 0 : count.intValue(), sum == null ? 0 : sum.longValue()));
        }
        return result;
    }
}
