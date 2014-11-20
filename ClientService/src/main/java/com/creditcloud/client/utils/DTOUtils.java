/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.utils;

import com.creditcloud.client.entities.Branch;
import com.creditcloud.client.entities.Client;
import com.creditcloud.client.entities.Employee;
import com.creditcloud.client.entities.EmployeeInfo;
import com.creditcloud.client.entities.Role;
import static com.creditcloud.common.utils.DTOUtils.convertContactInfo;
import static com.creditcloud.common.utils.DTOUtils.convertPersonalInfo;
import static com.creditcloud.common.utils.DTOUtils.getContactInfo;
import static com.creditcloud.common.utils.DTOUtils.getLocationDTO;
import static com.creditcloud.common.utils.DTOUtils.getPersonalInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sobranie
 */
public class DTOUtils {

    static Logger logger = LoggerFactory.getLogger(DTOUtils.class);

    public static com.creditcloud.model.client.Client getClientDTO(Client clientEntity) {
        com.creditcloud.model.client.Client result = null;
        if (clientEntity != null) {
            result = new com.creditcloud.model.client.Client(clientEntity.getName(),
                                                             clientEntity.getShortName(),
                                                             clientEntity.getTitle(),
                                                             clientEntity.getMobile(),
                                                             clientEntity.getSupportEmail(),
                                                             clientEntity.getSupportPhone(),
                                                             clientEntity.getInternalEmailIndicators(),
                                                             clientEntity.getCode(),
                                                             clientEntity.getUrl(),
                                                             clientEntity.isSecure(),
                                                             Locale.getDefault(),
                                                             clientEntity.getLogo());

        }
        return result;
    }

    /**
     * Create a new, unmanaged Client entity object out of the given Client
     *
     * @param client valid Client instance
     * @return
     */
    public static Client toClientEntity(com.creditcloud.model.client.Client client) {
        Client result = new Client();
        mergeClientEntity(result, client);
        return result;
    }

    /**
     * Copy properties value from ModelClient to EntityClient (Managed or
     * Unmanaged)
     *
     * @param clientEntity
     * @param client
     */
    public static void mergeClientEntity(Client clientEntity, com.creditcloud.model.client.Client client) {
        clientEntity.setCode(client.getCode());
        clientEntity.setName(client.getName());
        clientEntity.setShortName(client.getShortName());
        clientEntity.setUrl(client.getUrl());
        clientEntity.setSecure(client.isSecure());
        clientEntity.setMobile(client.getMobile());
        clientEntity.setSupportEmail(client.getSupportEmail());
        clientEntity.setSupportPhone(client.getSupportPhone());
        clientEntity.setInternalEmailIndicators(client.getInternalEmailIndicators());
        clientEntity.setTitle(client.getTitle());
        clientEntity.setLogo(client.getLogo());
    }

    /**
     * handle Branch
     *
     * @param branch
     * @return
     */
    public static com.creditcloud.model.client.Branch getBranchDTO(Branch branch) {
        com.creditcloud.model.client.Branch result = null;
        if (branch != null) {
            result = new com.creditcloud.model.client.Branch(branch.getId(),
                                                             branch.getClientCode(),
                                                             branch.getParentId(),
                                                             branch.getCode(),
                                                             branch.getName(),
                                                             branch.getType(),
                                                             getLocationDTO(branch.getLocation()),
                                                             getEmployeeDTO(branch.getPrincipal()),
                                                             getEmployeeDTO(branch.getContactPerson()),
                                                             branch.getDescription());
            result.setTimeCreated(branch.getTimeCreated());
            result.setTimeLastUpdated(branch.getTimeLastUpdated());
        }
        return result;
    }

    public static Branch convertBranchDTO(com.creditcloud.model.client.Branch branch, Employee principal, Employee contact) {
        Branch result = null;
        if (branch != null) {
            result = new Branch(branch.getParentId(),
                                branch.getName(),
                                branch.getCode(),
                                branch.getType(),
                                com.creditcloud.common.utils.DTOUtils.convertLocationDTO(branch.getLocation()),
                                principal,
                                contact,
                                branch.getDescription());
            result.setClientCode(branch.getClientCode());
            result.setId(branch.getId());
        }
        return result;
    }

    /**
     * handle Employee
     *
     * @param employee
     * @return
     */
    public static com.creditcloud.model.client.Employee getEmployeeDTO(Employee employee) {
        com.creditcloud.model.client.Employee result = null;
        if (employee != null) {
            result = new com.creditcloud.model.client.Employee(employee.getId(),
                                                               employee.getClientCode(),
                                                               employee.getLoginName(),
                                                               employee.getEmployeeId(),
                                                               employee.getName(),
                                                               employee.getIdNumber(),
                                                               employee.getMobile(),
                                                               null,
                                                               employee.getRegisterDate(),
                                                               employee.getLastLoginDate());
            result.setEnabled(employee.isEnabled());
        }
        return result;
    }

    public static Employee convertEmployeeDTO(com.creditcloud.model.client.Employee employee, Branch branch) {
        Employee result = null;
        if (employee != null) {
            result = new Employee();
            result.setBranch(branch);
            result.setClientCode(employee.getClientCode());
            result.setEmployeeId(employee.getEmployeeId());
            if (StringUtils.isNotBlank(employee.getId())) {
                result.setId(employee.getId());
            }
            result.setLoginName(employee.getLoginName());
            result.setMobile(employee.getMobile());
            result.setName(employee.getName());
            result.setIdNumber(employee.getIdNumber());
            result.setLastLoginDate(employee.getLastLoginDate());
            result.setRegisterDate(employee.getRegisterDate());
            result.setEnabled(employee.isEnabled());
        }
        return result;
    }

    /**
     * handle EmployeeInfo
     *
     * @param info
     * @return
     */
    public static com.creditcloud.model.client.EmployeeInfo getEmployeeInfo(EmployeeInfo info) {
        com.creditcloud.model.client.EmployeeInfo result = null;
        if (info != null) {
            result = new com.creditcloud.model.client.EmployeeInfo(info.getEmployeeId(),
                                                                   getPersonalInfo(info.getPersonal()),
                                                                   getContactInfo(info.getContact()));
        }
        return result;
    }

    public static EmployeeInfo convertEmployeeInfo(com.creditcloud.model.client.EmployeeInfo info, Employee employee) {
        EmployeeInfo result = null;
        if (info != null) {
            result = new EmployeeInfo(employee,
                                      convertPersonalInfo(info.getPersonal()),
                                      convertContactInfo(info.getContact()));
        }
        return result;
    }

    public static com.creditcloud.model.client.Role getRoleDTO(Role role, boolean includeMembers) {
        com.creditcloud.model.client.Role result = null;
        if (role != null) {
            result = new com.creditcloud.model.client.Role();
            result.setId(role.getId());
            result.setName(role.getName());
            result.setDescription(role.getDescription());
            result.setPrivileges(role.getPrivileges());
            if (includeMembers && !role.getMembers().isEmpty()) {
                Collection<com.creditcloud.model.client.Employee> members = new ArrayList<>();
                for (Employee employee : role.getMembers()) {
                    com.creditcloud.model.client.Employee employeeDTO = getEmployeeDTO(employee);
                    if (employeeDTO != null) {
                        members.add(employeeDTO);
                    } else {
                        logger.error("Employee {} as member of Role {} may not be valid", employee, role);
                    }
                }
                result.setMembers(members);
            } else {
                result.setMembers(Collections.EMPTY_LIST);
            }
        }
        return result;
    }
}
