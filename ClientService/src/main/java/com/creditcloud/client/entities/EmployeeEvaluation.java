/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.BaseEntity;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 员工的绩效和业绩,应该是可以按照月、季度、半年、年来考核
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_EMPLOYEE_PERFORMANCE")
public class EmployeeEvaluation extends BaseEntity {

    @Id
    private String employeeId;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID")
    private Employee employee;

    public EmployeeEvaluation() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
