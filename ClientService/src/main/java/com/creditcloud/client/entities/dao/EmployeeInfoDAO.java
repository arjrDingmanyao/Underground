/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.dao;

import com.creditcloud.client.entities.Employee;
import com.creditcloud.client.entities.EmployeeInfo;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.info.ContactInfo;
import com.creditcloud.common.entities.embedded.info.PersonalInfo;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.constraints.validator.ChineseIdNumber;
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
@Stateless
@LocalBean
public class EmployeeInfoDAO extends AbstractDAO<EmployeeInfo> {

    @PersistenceContext(unitName = "ClientPU")
    private EntityManager em;

    public EmployeeInfoDAO() {
        super(EmployeeInfo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void addNew(Employee employee) {
        PersonalInfo personal = new PersonalInfo(ChineseIdNumber.isMale(employee.getIdNumber()),
                                                 null,
                                                 ChineseIdNumber.getDateOfBirth(employee.getIdNumber()),
                                                 null,
                                                 false,
                                                 null,
                                                 null,
                                                 null);
        EmployeeInfo info = new EmployeeInfo(employee,
                                             personal,
                                             null);
        try {
            getValidatorWrapper().tryValidate(info);
            create(info);
        } catch (InvalidException ex) {
            log.warn("employee information {} is not valid!", info.toString(), ex);
        } catch (Exception ex) {
            log.warn("Add new employee information failed!!!\n{}", info.toString(), ex);
        }
    }

    /**
     * update whole EmployeeInfo
     *
     * @param info
     */
    public void update(EmployeeInfo info) {
        EmployeeInfo oldOne = find(info.getEmployeeId());
        if (oldOne != null) {
            edit(info);
        } else {
            //do nothing
            //persist is preferable than merge in EJB, call addNew if you want to insert
            log.warn("Fail to update employee info, employee not exist.[employee={}]", info);

        }
    }

    /**
     * update PersonalInfo
     *
     * @param employeeId
     * @param personal
     */
    public void update(String employeeId, com.creditcloud.model.PersonalInfo personal) {
        EmployeeInfo info = find(employeeId);
        if (info != null) {
            PersonalInfo result = com.creditcloud.common.utils.DTOUtils.convertPersonalInfo(personal);
            info.setPersonal(result);
            edit(info);
        } else {
            log.warn("update failed, employee info not exist.[employeeId={}]", employeeId);
        }
    }

    /**
     * update contact info
     *
     * @param employeeId
     * @param contact
     */
    public void update(String employeeId, com.creditcloud.model.ContactInfo contact) {
        EmployeeInfo info = find(employeeId);
        if (info != null) {
            ContactInfo result = com.creditcloud.common.utils.DTOUtils.convertContactInfo(contact);
            info.setContact(result);
            edit(info);
        } else {
            log.warn("update failed, employee info not exist.[employeeId={}]", employeeId);
        }
    }
}
