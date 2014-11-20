/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task;

import com.creditcloud.model.ElementCount;
import com.creditcloud.model.enums.loan.TaskStatus;
import com.creditcloud.model.enums.loan.TaskType;
import static com.creditcloud.model.enums.loan.TaskType.LOAN_COLLECT;
import static com.creditcloud.model.enums.loan.TaskType.LOAN_REQUEST;
import static com.creditcloud.model.enums.loan.TaskType.USER;
import static com.creditcloud.model.enums.loan.TaskType.USER_AND_REQUEST;
import com.creditcloud.task.api.TaskService;
import com.creditcloud.task.entity.LoanCollectTask;
import com.creditcloud.task.entity.SimpleRequestTask;
import com.creditcloud.task.entity.Task;
import com.creditcloud.task.entity.UserRequestTask;
import com.creditcloud.task.entity.UserTask;
import com.creditcloud.task.entity.dao.TaskDAO;
import com.creditcloud.task.entity.local.ApplicationBean;
import com.creditcloud.task.entity.local.TaskLocalBean;
import com.creditcloud.task.entity.utils.DTOUtils;
import com.creditcloud.task.model.TaskStatistics;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class TaskServiceBean implements TaskService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    TaskDAO taskDAO;

    @EJB
    TaskLocalBean taskLocalBean;

    @Override
    public com.creditcloud.task.model.Task addNew(String clientCode, com.creditcloud.task.model.Task task) {
        appBean.checkClientCode(clientCode);
        Task result = taskDAO.create(convertTaskDTO(task));
        return DTOUtils.getTaskDTO(result);
    }

    @Override
    public List<com.creditcloud.task.model.Task> listByEmployee(String clientCode, String employeeId, TaskStatus... status) {
        appBean.checkClientCode(clientCode);
        List<Task> tasks = taskDAO.listByEmployee(employeeId, status);
        List<com.creditcloud.task.model.Task> result = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            result.add(getTaskDTO(task));
        }
        return result;
    }

    @Override
    public com.creditcloud.task.model.Task getTaskById(String clientCode, String taskId) {
        appBean.checkClientCode(clientCode);
        return getTaskDTO(taskDAO.find(taskId));
    }

    @Override
    public boolean updateTask(String clientCode, com.creditcloud.task.model.Task task) {
        appBean.checkClientCode(clientCode);
        taskDAO.edit(convertTaskDTO(task));
        return true;
    }

    @Override
    public List<com.creditcloud.task.model.Task> listByEmployee(String clientCode, String employeeId, TaskType... type) {
        appBean.checkClientCode(clientCode);
        List<Task> tasks = taskDAO.listByEmployee(employeeId, type);
        List<com.creditcloud.task.model.Task> result = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            result.add(getTaskDTO(task));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.task.model.Task> listByEmployee(String clientCode, String employeeId, Date from, Date to, TaskStatus... status) {
        appBean.checkClientCode(clientCode);
        List<Task> tasks = taskDAO.listByEmployee(employeeId, from, to, status);
        List<com.creditcloud.task.model.Task> result = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            result.add(getTaskDTO(task));
        }
        return result;
    }

    @Override
    public List<ElementCount<Date>> countMonthlyTaskByEmployee(String clientCode, String employeeId, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return taskDAO.countMonthlyTaskByEmployee(employeeId, from, to);
    }

    @Override
    public TaskStatistics getTaskStatistics(String clientCode, String employeeId, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return taskLocalBean.getTaskStatisticsByEmployee(employeeId, from, to);
    }

    /**
     * facility method to get Task from entity to model
     *
     * @param task
     * @return
     */
    private com.creditcloud.task.model.Task getTaskDTO(Task task) {
        switch (task.getType()) {
            case USER:
                return DTOUtils.getUserTask((UserTask) task);
            case LOAN_REQUEST:
                return DTOUtils.getSimpleRequestTask((SimpleRequestTask) task);
            case USER_AND_REQUEST:
                return DTOUtils.getUserRequestTask((UserRequestTask) task);
            case LOAN_COLLECT:
                return DTOUtils.getLoanRepayTask((LoanCollectTask) task);
            default:
                return DTOUtils.getTaskDTO(task);
        }
    }

    /**
     * facility method to get Task from model to entity
     *
     * @param task
     * @return
     */
    private Task convertTaskDTO(com.creditcloud.task.model.Task task) {
        switch (task.getType()) {
            case USER:
                return DTOUtils.convertUserTask((com.creditcloud.task.model.UserTask) task);
            case LOAN_REQUEST:
                return DTOUtils.convertSimpleRequestTask((com.creditcloud.task.model.SimpleRequestTask) task);
            case USER_AND_REQUEST:
                return DTOUtils.convertUserRequestTask((com.creditcloud.task.model.UserRequestTask) task);
            case LOAN_COLLECT:
                return DTOUtils.convertLoanRepayTask((com.creditcloud.task.model.LoanCollectTask) task);
            default:
                return DTOUtils.convertTaskDTO(task);
        }
    }

    @Override
    public List<com.creditcloud.task.model.Task> listByRequestAndStatus(String clientCode, String requestId, TaskStatus... status) {
        appBean.checkClientCode(clientCode);
        List<UserRequestTask> taskList = taskDAO.listByRequestAndStatus(requestId, status);
        List<com.creditcloud.task.model.Task> result = new ArrayList<>(taskList.size());
        for (UserRequestTask task : taskList) {
            result.add(getTaskDTO(task));
        }
        return result;
    }
}
