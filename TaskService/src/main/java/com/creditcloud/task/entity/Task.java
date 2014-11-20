/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.task.entity;

import com.creditcloud.common.entities.TimeScopeEntity;
import com.creditcloud.model.enums.loan.TaskStatus;
import com.creditcloud.model.enums.loan.TaskType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Task")
@DiscriminatorColumn(name = "DTYPE")
@Table(name = "TB_TASK")
@NamedQueries({
    /**
     * list methods
     */
    @NamedQuery(name = "Task.listByEmployeeAndStatus",
                query = "select t from Task t where t.employeeId = :employeeId and t.status in :statusList order by t.timeCreated DESC"),
    @NamedQuery(name = "Task.listByEmployeeAndStatusAndTime",
                query = "select t from Task t where t.employeeId = :employeeId and t.status in :statusList and t.timeStarted between :from and :to order by t.timeStarted DESC, t.status DESC"),
    @NamedQuery(name = "Task.listByEmployeeAndType",
                query = "select t from Task t where t.employeeId = :employeeId and t.type in :typeList order by t.timeCreated DESC"),
    /**
     * count methods
     */
    @NamedQuery(name = "Task.countByEmployeeAndStatus",
                query = "select count(t) from Task t where t.employeeId = :employeeId and t.status in :statusList"),
    @NamedQuery(name = "Task.countByEmployeeAndTime",
                query = "select count(t) from Task t where t.employeeId = :employeeId and t.status in :statusList and t.timeFinished between :from and :to"),
    @NamedQuery(name = "Task.countMonthlyByEmployee",
                query = "select concat(FUNC('YEAR', t.timeFinished),FUNC('MONTH', t.timeFinished)) as year_month, count(t) from Task t where t.employeeId = :employeeId and t.status in :statusList and t.timeFinished between :from and :to group by year_month"),
    /**
     * sum methods
     */
    @NamedQuery(name = "Task.sumEmployeeProfitByEmployeeAndTime",
                query = "select sum(t.employeeProfit) from Task t where t.employeeId = :employeeId and t.status in :statusList and t.timeFinished between :from and :to"),
    @NamedQuery(name = "Task.sumEmployeeProfitByEmployee",
                query = "select sum(t.employeeProfit) from Task t where t.employeeId = :employeeId and t.status in :statusList"),
    @NamedQuery(name = "Task.sumTaskProfitByEmployeeAndTime",
                query = "select sum(t.taskProfit) from Task t where t.employeeId = :employeId and t.status in :statusList and t.timeFinished between :from and :to"),
    @NamedQuery(name = "Task.sumTaskProfitByEmployee",
                query = "select sum(t.taskProfit) from Task t where t.employeeId = :employeId and t.status in :statusList"),})
public class Task extends TimeScopeEntity {

    /**
     * 任务可以再拆分为多个子任务
     */
    @Column(nullable = true)
    private String parentId;

    /**
     * 任务对应的员工
     */
    @Column(nullable = true)
    private String employeeId;

    /**
     * 任务标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 任务描述
     */
    @Column(nullable = true)
    private String description;

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;


    /**
     * 员工接到任务后开始执行任务开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date timeStarted;

    /**
     * 员工完成任务时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date timeFinished;

    /**
     * 该任务的收益，是向用户的收费
     */
    private int taskProfit;

    /**
     * 员工完成该任务后的奖励
     */
    private int employeeProfit;

    /**
     * 任务收费是否已收取
     */
    private boolean profitPayed;

    @PreUpdate
    public void update() {
        this.timeLastUpdated = new Date();
    }

    public Task(String parentId,
                String employeeId, 
                String title, 
                String description, 
                TaskType type, 
                TaskStatus status,
                Date timeStarted,
                Date timeFinished,
                int taskProfit, 
                int employeeProfit, 
                boolean profitPayed) {
        this.parentId = parentId;
        this.employeeId = employeeId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.timeStarted = timeStarted;
        this.timeFinished = timeFinished;
        this.taskProfit = taskProfit;
        this.employeeProfit = employeeProfit;
        this.profitPayed = profitPayed;
    }
}
