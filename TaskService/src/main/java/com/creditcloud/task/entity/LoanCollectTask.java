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
 * 催款任务，对应贷款某几期没有按时还款
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("LoanCollectTask")
public class LoanCollectTask extends Task {

    /**
     * 贷款对应的第几期还款
     */
    private String loanRepaymentId;
    public LoanCollectTask(String loanRepaymentId, 
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
              TaskType.LOAN_COLLECT,
              status,
              timeStarted,
              timeFinished, 
              taskProfit,
              employeeProfit,
              profitPayed);
        this.loanRepaymentId = loanRepaymentId;
    }
}
