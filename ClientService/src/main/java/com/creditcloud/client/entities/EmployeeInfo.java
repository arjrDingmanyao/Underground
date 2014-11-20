/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.embedded.info.PersonalInfo;
import com.creditcloud.common.entities.BaseEntity;
import com.creditcloud.common.entities.embedded.info.ContactInfo;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * TODO 员工的info与用户会有很多不同,有些更丰富如薪酬有些则没有如FinanceInfo。
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_EMPLOYEE_INFO")
public class EmployeeInfo extends BaseEntity {

    @Id
    private String employeeId;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID")
    private Employee employee;

    @Valid
    private PersonalInfo personal;

    @Valid
    private ContactInfo contact;

    public EmployeeInfo() {
    }

    public EmployeeInfo(Employee employee,
                        PersonalInfo personal,
                        ContactInfo contact) {

        this.employeeId = employee.getId();
        this.employee = employee;
        this.personal = personal;
        this.contact = contact;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public PersonalInfo getPersonal() {
        return personal;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setPersonal(PersonalInfo personal) {
        this.personal = personal;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }
}
