/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity.utils;

import com.creditcloud.task.entity.LoanCollectTask;
import com.creditcloud.task.entity.SimpleRequestTask;
import com.creditcloud.task.entity.Task;
import com.creditcloud.task.entity.UserRequestTask;
import com.creditcloud.task.entity.UserTask;

/**
 *
 * @author sobranie
 */
public class DTOUtils {

    /**
     * handle Task
     *
     * @param task
     * @return
     */
    public static com.creditcloud.task.model.Task getTaskDTO(Task task) {
        com.creditcloud.task.model.Task result = null;
        if (task != null) {
            result = new com.creditcloud.task.model.Task(task.getId(),
                                                         task.getParentId(),
                                                         task.getEmployeeId(),
                                                         task.getTitle(),
                                                         task.getDescription(),
                                                         task.getType(),
                                                         task.getStatus(),
                                                         task.getTimeStarted(),
                                                         task.getTimeFinished(),
                                                         task.getTaskProfit(),
                                                         task.getEmployeeProfit(),
                                                         task.isProfitPayed());
            result.setTimeCreated(task.getTimeCreated());
            result.setTimeLastUpdated(task.getTimeLastUpdated());
        }

        return result;
    }

    public static Task convertTaskDTO(com.creditcloud.task.model.Task task) {
        Task result = null;
        if (task != null) {
            result = new Task(task.getParentId(),
                              task.getEmployeeId(),
                              task.getTitle(),
                              task.getDescription(),
                              task.getType(),
                              task.getStatus(),
                              task.getTimeStarted(),
                              task.getTimeFinished(),
                              task.getTaskProfit(),
                              task.getEmployeeProfit(),
                              task.isProfitPayed());
            result.setId(task.getId());
        }

        return result;
    }

    /**
     * handle SimpleRequestTask
     *
     * @param task
     * @param request
     * @param user
     * @return
     */
    public static com.creditcloud.task.model.SimpleRequestTask getSimpleRequestTask(SimpleRequestTask task) {
        com.creditcloud.task.model.SimpleRequestTask result = null;
        if (task != null) {
            result = new com.creditcloud.task.model.SimpleRequestTask(task.getRequestId(),
                                                                      task.getId(),
                                                                      task.getParentId(),
                                                                      task.getEmployeeId(),
                                                                      task.getTitle(),
                                                                      task.getDescription(),
                                                                      task.getStatus(),
                                                                      task.getTimeStarted(),
                                                                      task.getTimeFinished(),
                                                                      task.getTaskProfit(),
                                                                      task.getEmployeeProfit(),
                                                                      task.isProfitPayed());
            result.setTimeCreated(task.getTimeCreated());
            result.setTimeLastUpdated(task.getTimeLastUpdated());
        }
        return result;
    }

    public static SimpleRequestTask convertSimpleRequestTask(com.creditcloud.task.model.SimpleRequestTask task) {
        SimpleRequestTask result = null;
        if (task != null) {
            result = new SimpleRequestTask(task.getRequestId(),
                                           task.getParentId(),
                                           task.getEmployeeId(),
                                           task.getTitle(),
                                           task.getDescription(),
                                           task.getStatus(),
                                           task.getTimeStarted(),
                                           task.getTimeFinished(),
                                           task.getTaskProfit(),
                                           task.getEmployeeProfit(),
                                           task.isProfitPayed());
            result.setId(task.getId());
        }
        return result;
    }

    /**
     * handle UserRequestTask
     *
     * @param task
     * @return
     */
    public static com.creditcloud.task.model.UserRequestTask getUserRequestTask(UserRequestTask task) {
        com.creditcloud.task.model.UserRequestTask result = null;
        if (task != null) {
            result = new com.creditcloud.task.model.UserRequestTask(task.getUserId(),
                                                                    task.getRequestId(),
                                                                    task.getId(),
                                                                    task.getParentId(),
                                                                    task.getEmployeeId(),
                                                                    task.getTitle(),
                                                                    task.getDescription(),
                                                                    task.getStatus(),
                                                                    task.getTimeStarted(),
                                                                    task.getTimeFinished(),
                                                                    task.getTaskProfit(),
                                                                    task.getEmployeeProfit(),
                                                                    task.isProfitPayed());
            result.setTimeCreated(task.getTimeCreated());
            result.setTimeLastUpdated(task.getTimeLastUpdated());
        }
        return result;
    }

    public static UserRequestTask convertUserRequestTask(com.creditcloud.task.model.UserRequestTask task) {
        UserRequestTask result = null;
        if (task != null) {
            result = new UserRequestTask(task.getUserId(),
                                         task.getRequestId(),
                                         task.getParentId(),
                                         task.getEmployeeId(),
                                         task.getTitle(),
                                         task.getDescription(),
                                         task.getStatus(),
                                         task.getTimeStarted(),
                                         task.getTimeFinished(),
                                         task.getTaskProfit(),
                                         task.getEmployeeProfit(),
                                         task.isProfitPayed());
            result.setId(task.getId());
        }
        return result;
    }

    /**
     * handle UserTask
     *
     * @param task
     * @param userInfo
     * @return
     */
    public static com.creditcloud.task.model.UserTask getUserTask(UserTask task) {
        com.creditcloud.task.model.UserTask result = null;
        if (task != null) {
            result = new com.creditcloud.task.model.UserTask(task.getUserId(),
                                                             task.getId(),
                                                             task.getParentId(),
                                                             task.getEmployeeId(),
                                                             task.getTitle(),
                                                             task.getDescription(),
                                                             task.getStatus(),
                                                             task.getTimeStarted(),
                                                             task.getTimeFinished(),
                                                             task.getTaskProfit(),
                                                             task.getEmployeeProfit(),
                                                             task.isProfitPayed());
            result.setTimeCreated(task.getTimeCreated());
            result.setTimeLastUpdated(task.getTimeLastUpdated());
        }
        return result;
    }

    public static UserTask convertUserTask(com.creditcloud.task.model.UserTask task) {
        UserTask result = null;
        if (task != null) {
            result = new UserTask(task.getUserId(),
                                  task.getParentId(),
                                  task.getEmployeeId(),
                                  task.getTitle(),
                                  task.getDescription(),
                                  task.getStatus(),
                                  task.getTimeStarted(),
                                  task.getTimeFinished(),
                                  task.getTaskProfit(),
                                  task.getEmployeeProfit(),
                                  task.isProfitPayed());
            result.setId(task.getId());
        }
        return result;
    }

    /**
     * handle LoanCollectTask
     *
     * @param task
     * @return
     */
    public static com.creditcloud.task.model.LoanCollectTask getLoanRepayTask(LoanCollectTask task) {
        com.creditcloud.task.model.LoanCollectTask result = null;
        if (task != null) {
            result = new com.creditcloud.task.model.LoanCollectTask(task.getLoanRepaymentId(),
                                                                    task.getId(),
                                                                    task.getParentId(),
                                                                    task.getEmployeeId(),
                                                                    task.getTitle(),
                                                                    task.getDescription(),
                                                                    task.getStatus(),
                                                                    task.getTimeStarted(),
                                                                    task.getTimeFinished(),
                                                                    task.getTaskProfit(),
                                                                    task.getEmployeeProfit(),
                                                                    task.isProfitPayed());
            result.setTimeCreated(task.getTimeCreated());
            result.setTimeLastUpdated(task.getTimeLastUpdated());
        }
        return result;
    }

    public static LoanCollectTask convertLoanRepayTask(com.creditcloud.task.model.LoanCollectTask task) {
        LoanCollectTask result = null;
        if (task != null) {
            result = new LoanCollectTask(task.getLoanRepaymentId(),
                                         task.getParentId(),
                                         task.getEmployeeId(),
                                         task.getTitle(),
                                         task.getDescription(),
                                         task.getStatus(),
                                         task.getTimeStarted(),
                                         task.getTimeFinished(),
                                         task.getTaskProfit(),
                                         task.getEmployeeProfit(),
                                         task.isProfitPayed());
            result.setId(task.getId());
        }
        return result;
    }
}