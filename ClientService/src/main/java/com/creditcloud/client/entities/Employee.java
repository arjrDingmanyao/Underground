/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.Authenticatable;
import com.creditcloud.common.entities.utils.IdNumberConverter;
import com.creditcloud.common.entities.utils.MobileConverter;
import com.creditcloud.common.security.SecurityUtils;
import com.creditcloud.model.constraints.IdNumber;
import com.creditcloud.model.constraints.LoginName;
import com.creditcloud.model.constraints.MobileNumber;
import com.creditcloud.model.constraints.RealName;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 *
 * @author sobranie
 */
@Entity
@Table(name = "TB_EMPLOYEE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ClientCode", "LOGIN_NAME"}),
    @UniqueConstraint(columnNames = {"ClientCode", "EMP_ID"}),
    @UniqueConstraint(columnNames = {"ClientCode", "MOBILE"}),
    @UniqueConstraint(columnNames = {"ClientCode", "IDNUMBER"})
})
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Employee.listEmployeeByClient",
                query = "SELECT e from Employee e WHERE e.clientCode = :clientCode"),
    /**
     * get
     */
    @NamedQuery(name = "Employee.getEmployeeByLoginName",
                query = "SELECT e from Employee e WHERE e.clientCode = :clientCode AND e.loginName = :loginName"),
    @NamedQuery(name = "Employee.getEmployeeByEmployeeId",
                query = "SELECT e from Employee e WHERE e.clientCode = :clientCode AND e.employeeId = :employeeId"),
    @NamedQuery(name = "Employee.getEmployeeByIdNumber",
                query = "select e from Employee e where e.clientCode = :clientCode and e.idNumber = :idNumber"),
    @NamedQuery(name = "Employee.getEmployeeByMobile",
                query = "select e from Employee e where e.clientCode = :clientCode and e.mobile = :mobile"),
    /**
     * count
     */
    @NamedQuery(name = "Employee.countByClient",
                query = "select count(e) from Employee e where e.clientCode = :clientCode")
})
public class Employee extends Authenticatable {

    @LoginName
    @Column(name = "LOGIN_NAME", nullable = false, length = 60)
    private String loginName;

    /**
     * employee id used inside the client
     */
    @Column(name = "EMP_ID", nullable = false, length = 60)
    private String employeeId;

    /**
     * the real name of this person
     */
    @RealName
    @Column(nullable = false, length = 20)
    @Size(min = 2, max = 20)
    private String name;

    /**
     * Mobile number of this employee
     */
    @Column(name = "MOBILE", nullable = false)
    @MobileNumber
    @Converter(name = "mobileConvert",
               converterClass = MobileConverter.class)
    @Convert("mobileConvert")
    private String mobile;

    /**
     * IdNumber for this employee
     */
    @IdNumber
    @Column(name = "IDNUMBER", nullable = false)
    @Converter(name = "idNumberConvert",
               converterClass = IdNumberConverter.class)
    @Convert("idNumberConvert")
    private String idNumber;

    /**
     * 所属的Base Branch
     */
    @ManyToOne
    @JoinColumn(name = "BRANCH_ID", nullable = true)
    private Branch branch;

    /**
     * The roles that this employee been granted
     */
    @ManyToMany
    @JoinTable(name = "RF_EMP_ROLE", joinColumns
            = @JoinColumn(name = "EMP_ID"), inverseJoinColumns
            = @JoinColumn(name = "ROLE_ID"))
    private Collection<Role> roles;

    public Employee() {
    }

    /**
     * 设置password对应的盐值和密文
     *
     * @param password
     */
    public void password(final String password) {
        salt = SecurityUtils.getSalt(idNumber);
        passphrase = SecurityUtils.getPassphrase(salt, password);
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
