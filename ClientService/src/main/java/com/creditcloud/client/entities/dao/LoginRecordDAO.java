/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.record.EmployeeLoginRecord;
import com.creditcloud.common.entities.dao.AbstractDAO;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author rooseek
 */
@Slf4j
@LocalBean
@Stateless
public class LoginRecordDAO extends AbstractDAO<EmployeeLoginRecord> {

    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public LoginRecordDAO() {
        super(EmployeeLoginRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByEmployee(String employeeId) {
        Long result = getEntityManager()
                .createNamedQuery("EmployeeLoginRecord.countByEmployee", Long.class)
                .setParameter("employeeId", employeeId)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * check whether it is the first successful login for an employee
     *
     * @param employeeId
     * @return
     */
    public boolean isFirstLogin(String employeeId) {
        return countByEmployee(employeeId) == 1;
    }
    
     public int countFailedLoginByEmployee(String employeeId, Date since) {
        Long result = getEntityManager()
                .createNamedQuery("EmployeeLoginRecord.countFailedLoginByEmployee", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("since", since)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }
}
