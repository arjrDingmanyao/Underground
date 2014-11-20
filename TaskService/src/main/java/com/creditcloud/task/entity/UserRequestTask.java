/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity;

import com.creditcloud.model.enums.loan.TaskStatus;
import com.creditcloud.model.enums.loan.TaskType;
import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对loanrequest实地验证，同时对用户信息实地验证
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("UserRequestTask")
@NamedQueries({
    /**
     * list methods
     */
    @NamedQuery(name = "UserRequestTask.listByRequestAndStatus",
                query = "select t from UserRequestTask t where t.requestId = :requestId and t.status in :statusList order by t.timeCreated DESC"),})
public class UserRequestTask extends Task {

    /**
     * 任务相关联的客户
     */
    private String userId;

    /**
     * 任务相关联的贷款请求
     */
    private String requestId;

    public UserRequestTask(String userId,
                           String requestId,
                           String parentId,
                           String employeeId,
                           String title,
                           String description,
                           TaskStatus status,
                           Date timeStarted,
                           Date timeFinished,
                           int taskProfit,
                           int employeeProfit,
                           boolean profitPayed) {
        super(parentId,
              employeeId,
              title,
              description,
              TaskType.USER_AND_REQUEST,
              status,
              timeStarted,
              timeFinished,
              taskProfit,
              employeeProfit,
              profitPayed);
        this.userId = userId;
        this.requestId = requestId;
    }
}
