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
 * 贷款申请相关任务，只实地调查贷款申请本身
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("SimpleRequestTask")
public class SimpleRequestTask extends Task {

    /**
     * 任务相关联的贷款请求
     */
    private String requestId;

    public SimpleRequestTask(String requestId,
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
              TaskType.LOAN_REQUEST,
              status,
              timeStarted,
              timeFinished,
              taskProfit,
              employeeProfit,
              profitPayed);
        this.requestId = requestId;
    }
}
