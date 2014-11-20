/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.Employee;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.security.SecurityUtils;
import com.creditcloud.model.constant.ClientConstant;
import com.creditcloud.model.constant.MobileConstant;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author sobranie
 */
@Slf4j
@Stateless
@LocalBean
public class EmployeeDAO extends AbstractDAO<Employee> {

    private static final String ADMIN_LOGIN_NAME = ClientConstant.ADMIN_LOGIN_NAME;

    private static final String ADMIN_NAME = ClientConstant.ADMIN_NAME;

    private static final String ADMIN_EMPLOYEE_ID = ClientConstant.ADMIN_EMPLOYEE_ID;

    private static final String ADMIN_DEFAULT_PASSWORD = "password";

    private static final String ADMIN_DEFAULT_IDNUMBER = ClientConstant.ADMIN_IDNUMER;

    /**
     * UserService PU
     */
    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public EmployeeDAO() {
        super(Employee.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean createAdminEmployee(final String clientCode) {
        boolean result = false;
        Employee admin = new Employee();
        admin.setClientCode(clientCode);
        admin.setLoginName(ADMIN_LOGIN_NAME);
        admin.setName(ADMIN_NAME);
        admin.setIdNumber(ADMIN_DEFAULT_IDNUMBER);
        admin.setEmployeeId(ADMIN_EMPLOYEE_ID);
        admin.setMobile(MobileConstant.DEFAULT_MOBILE);
        admin.setRegisterDate(new Date());
        admin.password(ADMIN_DEFAULT_PASSWORD);
        try {
            create(admin);
            result = true;
        } catch (Exception ex) {
            log.error("Exception happend when create admin employee for client {}", clientCode, ex);
        }
        return result;
    }

    public Employee findByLoginName(final String clientCode, final String loginName) {
        Employee employee = null;
        try {
            employee = getEntityManager().createNamedQuery("Employee.getEmployeeByLoginName", Employee.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("loginName", loginName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.debug("Couldn't find Employee using loginName: {} for Client: {}", loginName, clientCode);
        }
        return employee;
    }

    public Employee findByEmployeeId(final String clientCode, final String employeeId) {
        Employee employee = null;
        try {
            employee = getEntityManager().createNamedQuery("Employee.getEmployeeByEmployeeId", Employee.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("employeeId", employeeId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.debug("Couldn't find Employee using EmployeeId: {} for Client: {}", employeeId, clientCode);
        }
        return employee;
    }

    public Employee findByIdNumber(final String clientCode, final String IdNumber) {
        Employee employee = null;
        try {
            employee = getEntityManager().createNamedQuery("Employee.getEmployeeByIdNumber", Employee.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("idNumber", IdNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.debug("Couldn't find Employee using IdNumber: {} for Client: {}", IdNumber, clientCode);
        }

        return employee;
    }

    public Employee findByMobile(final String clientCode, final String mobile) {
        Employee employee = null;
        try {
            employee = getEntityManager().createNamedQuery("Employee.getEmployeeByMobile", Employee.class)
                    .setParameter("clientCode", clientCode)
                    .setParameter("mobile", mobile)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.debug("Couldn't find Employee using mobile: {} for Client: {}", mobile, clientCode);
        }

        return employee;
    }

    public Employee login(final String clientCode,
                          final String loginName,
                          final String password) {
        Employee target = findByLoginName(clientCode, loginName);
        if (target != null && SecurityUtils.matchPassphrase(target.getPassphrase(), target.getSalt(), password)) {
            target.setLastLoginDate(new Date());
            return target;
        } else {
            return null;
        }
    }

    public Employee loginMobile(String clientCode,
                                String mobile,
                                String password) {
        Employee target = findByMobile(clientCode, mobile);
        if (target != null && SecurityUtils.matchPassphrase(target.getPassphrase(), target.getSalt(), password)) {
            target.setLastLoginDate(new Date());
            return target;
        } else {
            return null;
        }
    }

    public List<Employee> getEmployeesByClientCode(final String clientCode) {
        return getEntityManager().createNamedQuery("Employee.listEmployeeByClient", Employee.class)
                .setParameter("clientCode", clientCode).getResultList();
    }

    public int countByClient(String clientCode) {
        Long result = getEntityManager()
                .createNamedQuery("Employee.countByClient", Long.class)
                .setParameter("clientCode", clientCode)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public boolean isAdmin(Employee employee) {
        if (employee != null) {
            return employee.getLoginName().equals(ADMIN_LOGIN_NAME);
        }
        return false;
    }
}
