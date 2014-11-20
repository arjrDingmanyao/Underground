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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户相关任务,如验证用户信息，对用户进行回访调查等
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("UserTask")
public class UserTask extends Task {

    /**
     * 任务相关联的客户
     */
    private String userId;

    public UserTask(String userId,
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
              TaskType.USER,
              status,
              timeStarted,
              timeFinished,
              taskProfit,
              employeeProfit,
              profitPayed);
        this.userId = userId;
    }
}
