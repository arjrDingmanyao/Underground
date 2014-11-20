/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.record;

import com.creditcloud.client.entities.Employee;
import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.common.entities.embedded.LoginRecord;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * 记录成功与失败的登陆
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_EMPLOYEE_LOGIN_RECORD")
@NamedQueries({
    @NamedQuery(name = "EmployeeLoginRecord.countByEmployee",
                query = "select count(elr) from EmployeeLoginRecord elr where elr.employee.id = :employeeId"),
    @NamedQuery(name = "EmployeeLoginRecord.countFailedLoginByEmployee",
                query = "select count(elr) from EmployeeLoginRecord elr where elr.employee.id = :employee and elr.record.success = false and elr.record.loginTime > :since")
})
public class EmployeeLoginRecord extends UUIDEntity {

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = true)
    private Employee employee;

    @Valid
    @Column(nullable = false)
    private LoginRecord record;

    public EmployeeLoginRecord(Employee employee, LoginRecord record) {
        this.employee = employee;
        this.record = record;
    }

    public EmployeeLoginRecord() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public LoginRecord getRecord() {
        return record;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setRecord(LoginRecord record) {
        this.record = record;
    }
}
