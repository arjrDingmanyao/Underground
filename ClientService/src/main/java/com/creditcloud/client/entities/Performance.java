/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.TimeScopeEntity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 员工具体的绩效,有类别之分,最简单的例子就是每日出勤情况
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_PERFORMANCE")
public class Performance extends TimeScopeEntity {
    @ManyToOne
    @JoinColumn(name="EMPLOYEE_ID")
    private EmployeeEvaluation evaluation;

    public Performance() {
    }

    public EmployeeEvaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EmployeeEvaluation evaluation) {
        this.evaluation = evaluation;
    }
}
