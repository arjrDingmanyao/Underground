/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.enums.loan.TaskStatus;
import com.creditcloud.model.enums.loan.TaskType;
import com.creditcloud.task.entity.Task;
import com.creditcloud.task.entity.UserRequestTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class TaskDAO extends AbstractDAO<Task> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "TaskPU")
    private EntityManager em;

    private final static List<TaskStatus> STATUS_LIST = Arrays.asList(TaskStatus.FINISHED);

    private static final GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();

    public TaskDAO() {
        super(Task.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 统计一段时间内员工完成的任务总数
     *
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    public int countByEmployeeAndTime(String employeeId, Date from, Date to, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.countByEmployeeAndTime", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    /**
     * 按月份统计员工完成的任务数
     *
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    public List<ElementCount<Date>> countMonthlyTaskByEmployee(String employeeId, Date from, Date to) {
        List<ElementCount<Date>> result = new ArrayList<>();
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("Task.countMonthlyByEmployee")
                .setParameter("employeeId", employeeId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("statusList", STATUS_LIST)
                .getResultList();
        for (Object[] object : objects) {
            String str = (String) object[0];
            calendar.set(Integer.valueOf(str.substring(0, 4)),
                         Integer.valueOf(str.substring(4)) - 1,
                         1,
                         0,
                         0,
                         0);
            Date date = calendar.getTime();
            Long count = (Long) object[1];
            result.add(new ElementCount<>(date, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    public int countByStatus(String employeeId, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.countByEmployeeAndStatus", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public List<Task> listByEmployee(String employeeId, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Task.listByEmployeeAndStatus", Task.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
    }

    public List<Task> listByEmployee(String employeeId, Date from, Date to, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Task.listByEmployeeAndStatusAndTime", Task.class)
                .setParameter("employeeId", employeeId)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();

    }

    public List<Task> listByEmployee(String employeeId, TaskType... type) {
        if (type == null || type.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Task.listByEmployeeAndType", Task.class)
                .setParameter("employeeId", employeeId)
                .setParameter("typeList", Arrays.asList(type))
                .getResultList();
    }

    public List<UserRequestTask> listByRequestAndStatus(String requestId, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("UserRequestTask.listByRequestAndStatus", UserRequestTask.class)
                .setParameter("requestId", requestId)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
    }
    
    /**
     * 获得员工完成任务自己部分收益之和
     *
     * @param employeeId
     * @return
     */
    public int getEmployeeProfitByEmployee(String employeeId, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.sumEmployeeProfitByEmployee", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 获得员工一段时间内完成任务自己部分收益之和
     *
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    public int getEmployeeProfitByEmployeeAndTime(String employeeId, Date from, Date to, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.sumEmployeeProfitByEmployeeAndTime", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 获得员工完成任务的任务收益之和
     *
     * @param employeeId
     * @return
     */
    public int getTaskProfitByEmployee(String employeeId, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.sumTaskProfitByEmployee", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 获得一段时间内员工完成任务的任务收益之和
     *
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    public int getTaskProfitByEmployeeAndTime(String employeeId, Date from, Date to, TaskStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Task.sumTaskProfitByEmployeeAndTime", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("statusList", Arrays.asList(status))
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
}
