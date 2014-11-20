/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity.local;

import com.creditcloud.client.api.EmployeeService;
import com.creditcloud.model.enums.loan.TaskStatus;
import com.creditcloud.task.entity.Task;
import com.creditcloud.task.entity.dao.TaskDAO;
import com.creditcloud.task.model.TaskStatistics;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class TaskLocalBean {

    @EJB
    ApplicationBean appBean;

    @EJB
    TaskDAO taskDAO;

    @Inject
    Logger logger;

    
    @EJB
    EmployeeService employeeService;
    
    /**
     * assign a task to another employee
     *
     * @param taskId
     * @param employeeId
     * @return
     */
    public boolean assign(String taskId, String employeeId) {
        Task task = taskDAO.find(taskId);
        if (task != null) {
            task.setEmployeeId(employeeId);
            taskDAO.edit(task);

            return true;
        }
        return false;
    }

    /**
     * mark task as finished
     *
     * @param taskId
     * @param employeeId
     * @return
     */
    public boolean markFinished(String taskId, String employeeId) {
        Task task = taskDAO.find(taskId);
        if (task != null) {
            task.setTimeFinished(new Date());
            task.setStatus(TaskStatus.FINISHED);
            taskDAO.edit(task);
            return true;
        }
        return false;
    }


    /**
     * 获取员工的任务统计信息
     *
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    public TaskStatistics getTaskStatisticsByEmployee(String employeeId, Date from, Date to) {
        int totalTask = taskDAO.countByEmployeeAndTime(employeeId, from, to, TaskStatus.values());
        int finishedTask = taskDAO.countByEmployeeAndTime(employeeId, from, to, TaskStatus.FINISHED);
        int totalProfit = taskDAO.getEmployeeProfitByEmployeeAndTime(employeeId, from, to, TaskStatus.FINISHED);
        TaskStatistics result = new TaskStatistics(employeeId,
                                                   from,
                                                   to,
                                                   totalTask,
                                                   finishedTask,
                                                   totalProfit,
                                                   taskDAO.countMonthlyTaskByEmployee(employeeId, from, to));
        return result;
    }
}
