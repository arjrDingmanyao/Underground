/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client;

import com.creditcloud.client.api.EmployeeInfoService;
import com.creditcloud.client.entities.Employee;
import com.creditcloud.client.entities.dao.EmployeeDAO;
import com.creditcloud.client.entities.dao.EmployeeInfoDAO;
import com.creditcloud.client.utils.DTOUtils;
import com.creditcloud.model.client.EmployeeInfo;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author rooseek
 */
@Slf4j
@Remote
@Stateless
public class EmployeeInfoServiceBean implements EmployeeInfoService {

    @EJB
    EmployeeInfoDAO infoDAO;

    @EJB
    EmployeeDAO employeeDAO;

    @Override
    public EmployeeInfo getEmployeeInfoById(String clientCode, String employeeId) {
        return DTOUtils.getEmployeeInfo(infoDAO.find(employeeId));
    }

    @Override
    public boolean updateEmployeeInfo(String clientCode, EmployeeInfo info) {
        Employee employee = employeeDAO.find(info.getEmployeeId());
        if (employee == null) {
            log.warn("employee with id {} not found.", info.getEmployeeId());
            return false;
        }
        infoDAO.update(DTOUtils.convertEmployeeInfo(info, employee));
        return true;
    }

    @Override
    public boolean updatePersonalInfo(String clientCode, String employeeId, com.creditcloud.model.PersonalInfo info) {
        log.debug("update personal info.[employeeId={}]", employeeId);
        infoDAO.update(clientCode, info);
        return true;
    }

    @Override
    public boolean updateContactInfo(String clientCode, String employeeId, com.creditcloud.model.ContactInfo info) {
        log.debug("update contact info.[employeeId={}]", employeeId);
        infoDAO.update(clientCode, info);
        return true;
    }
}
