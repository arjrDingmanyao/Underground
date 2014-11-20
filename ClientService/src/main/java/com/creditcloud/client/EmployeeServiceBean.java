/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client;

import com.creditcloud.client.api.EmployeeService;
import com.creditcloud.client.entities.Client;
import com.creditcloud.client.entities.Employee;
import com.creditcloud.client.entities.dao.BranchDAO;
import com.creditcloud.client.entities.dao.ClientDAO;
import com.creditcloud.client.entities.dao.EmployeeDAO;
import com.creditcloud.client.entities.dao.LoginRecordDAO;
import com.creditcloud.client.entities.dao.RoleDAO;
import com.creditcloud.client.entities.record.EmployeeLoginRecord;
import com.creditcloud.client.local.ApplicationBean;
import com.creditcloud.client.utils.DTOUtils;
import com.creditcloud.common.entities.embedded.LoginRecord;
import com.creditcloud.common.security.SecurityLevel;
import com.creditcloud.common.security.SecurityUtils;
import com.creditcloud.model.client.EmployeeLoginResult;
import com.creditcloud.model.client.Role;
import com.creditcloud.model.constant.LoginConstant;
import com.creditcloud.model.enums.LoginResult;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.client.Privilege;
import com.creditcloud.sms.SMSType;
import com.creditcloud.sms.api.SMSService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author sobranie
 */
@Slf4j
@Remote
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EmployeeServiceBean implements EmployeeService {

    @EJB
    ApplicationBean appBean;

    @EJB
    EmployeeDAO employeeDAO;

    @EJB
    ClientDAO clientDAO;

    @EJB
    RoleDAO roleDAO;

    @EJB
    SMSService smsService;

    @EJB
    LoginRecordDAO loginRecordDAO;

    @EJB
    BranchDAO branchDAO;
    
    private volatile boolean employeeDirty = true;

    private volatile boolean roleDirty = true;

    private final Map<String, com.creditcloud.model.client.Employee> employeeCache = new HashMap<>();

    private final Map<String, com.creditcloud.model.client.Role> roleCache = new HashMap<>();

    @PostConstruct
    void init() {
        updateEmployeeCache(appBean.getClientCode());
        updateRoleCache(appBean.getClientCode(), false);
    }

    private synchronized void updateEmployeeCache(String clientCode) {
        if (employeeDirty == false) {
            log.debug("employee cache alreay latest, no need to update.");
            return;
        }
        log.debug("update employee cache.");
        employeeCache.clear();
        for (Employee employee : employeeDAO.getEmployeesByClientCode(clientCode)) {
            employeeCache.put(employee.getId(), DTOUtils.getEmployeeDTO(employee));
        }
        employeeDirty = false;
    }

    private synchronized void updateRoleCache(String clientCode, boolean includeMembers) {
        if (roleDirty == false) {
            log.debug("role cache already latest, no need to update.");
            return;
        }
        log.debug("update role cache.");
        roleCache.clear();
        for (com.creditcloud.client.entities.Role role : roleDAO.findAll()) {
            roleCache.put(role.getId(), DTOUtils.getRoleDTO(role, includeMembers));
        }
        roleDirty = false;
    }

    @Override
    public List<com.creditcloud.model.client.Employee> listByClient(String clientCode) {
        long startTime = System.currentTimeMillis();
        log.debug("listEmployeeByClient called by client {}", clientCode);
        if (employeeDirty) {
            updateEmployeeCache(clientCode);
        }
        log.debug("listEmployeeByClient done in {}ms, return {} employees", System.currentTimeMillis() - startTime, employeeCache.size());
        return new ArrayList<>(employeeCache.values());
    }

    @Override
    public com.creditcloud.model.client.Employee saveEmployee(String clientCode, com.creditcloud.model.client.Employee employee) {
        Employee employeeEntity;
        if (employee.getId() != null && findById(clientCode, employee.getId()) != null) {
            employeeEntity = employeeDAO.find(employee.getId());
            //只修改Employee的直接属性，Branch等不管
            employeeEntity.setEmployeeId(employee.getEmployeeId());
            employeeEntity.setLoginName(employee.getLoginName());
            employeeEntity.setName(employee.getName());
            employeeEntity.setIdNumber(employee.getIdNumber().toUpperCase());
            employeeEntity.setMobile(employee.getMobile());
            employeeDAO.edit(employeeEntity);
            markEmployeeDirty();
            return findById(clientCode, employee.getId());
        } else {
            employeeEntity = DTOUtils.convertEmployeeDTO(employee, employee.getBranchId() == null
                    ? null : branchDAO.find(employee.getBranchId()));
            String randomPassword = SecurityUtils.randomPassword(SecurityLevel.GOOD);
            employeeEntity.password(randomPassword);
            employeeEntity.setRegisterDate(new Date());
            employeeEntity.setId(null);
            employeeEntity.setIdNumber(employee.getIdNumber().toUpperCase());
            //need to change password on next successful login
            employeeEntity.setNeedChangePassword(true);
            employeeEntity.setEnabled(true);
            Employee result = employeeDAO.create(employeeEntity);
            employee = DTOUtils.getEmployeeDTO(result);
            //send sms
            Client client = clientDAO.getClientByCode(clientCode);
            smsService.sendMessage(DTOUtils.getClientDTO(client),
                                   SMSType.NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED,
                                   employee.getMobile(),
                                   client.getShortName(),
                                   employee.getLoginName(),
                                   randomPassword);
            markEmployeeDirty();
            return employee;
        }
    }

    @Override
    public EmployeeLoginResult login(String clientCode,
                                     String loginName,
                                     String password,
                                     Map<String, String> info,
                                     Source source) {
        Employee employee = employeeDAO.login(clientCode,
                                              loginName,
                                              password);
        if (employee == null) {
            log.warn("login not exist, or password unmatch.[loginName={}]", loginName);
            loginRecordDAO.create(new EmployeeLoginRecord(null,
                                                          new LoginRecord(loginName,
                                                                          new Date(),
                                                                          getLoginInfo(info, source),
                                                                          source,
                                                                          false)));
            return new EmployeeLoginResult(LoginResult.FAILED, null);
        }
        
        if (!employeeDAO.isAdmin(employee) && !employee.isEnabled()) {
            //管理员无法禁用
            return new EmployeeLoginResult(LoginResult.EMPLOYEE_DISABLED, null);
        }

        //add login record
        loginRecordDAO.create(new EmployeeLoginRecord(employee,
                                                      new LoginRecord(loginName,
                                                                      new Date(),
                                                                      getLoginInfo(info, source),
                                                                      source,
                                                                      true)));

        //check whether it is first login in
        if (employee.isNeedChangePassword()) {
            log.info("employee {} need to change password.", loginName);
            return new EmployeeLoginResult(LoginResult.NEED_CHANGE_PASSWORD, DTOUtils.getEmployeeDTO(employee));
        } else {
            return new EmployeeLoginResult(LoginResult.SUCCESSFUL, DTOUtils.getEmployeeDTO(employee));
        }

    }

    @Override
    public EmployeeLoginResult loginMobile(String clientCode,
                                           String mobile,
                                           String password,
                                           Map<String, String> info,
                                           Source source) {
        Employee employee = employeeDAO.loginMobile(clientCode,
                                                    mobile,
                                                    password);
        if (employee == null) {
            log.warn("login not exist, or password unmatch.[mobile={}]", mobile);
            loginRecordDAO.create(new EmployeeLoginRecord(null,
                                                          new LoginRecord(mobile,
                                                                          new Date(),
                                                                          getLoginInfo(info, source),
                                                                          source,
                                                                          false)));
            return new EmployeeLoginResult(LoginResult.FAILED, null);
        }

        //add login record
        loginRecordDAO.create(new EmployeeLoginRecord(employee,
                                                      new LoginRecord(mobile,
                                                                      new Date(),
                                                                      getLoginInfo(info, source),
                                                                      source,
                                                                      true)));

        //check whether it is first login in
        if (employee.isNeedChangePassword()) {
            log.info("employee {} need to change password.", employee.getLoginName());
            return new EmployeeLoginResult(LoginResult.NEED_CHANGE_PASSWORD, DTOUtils.getEmployeeDTO(employee));
        } else {
            return new EmployeeLoginResult(LoginResult.SUCCESSFUL, DTOUtils.getEmployeeDTO(employee));
        }

    }

    @Override
    public boolean changePassword(String clientCode,
                                  String loginName,
                                  String password,
                                  String newPassword) {
        Employee employee = employeeDAO.login(clientCode, loginName, password);
        if (employee != null) {
            log.info("employee {} change password.", loginName);
            employee.password(newPassword);
            employee.setNeedChangePassword(false);
            employeeDAO.edit(employee);
            markEmployeeDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPassword(String clientCode, String id) {
        Client client = clientDAO.getClientByCode(clientCode);
        if (client == null) {
            log.error("Reset password failed, no such clientCode.[clientCode={}]", clientCode);
            return false;
        }
        log.debug("reset password for employee.[clientCode={}][id={}]", clientCode, id);
        Employee employee = employeeDAO.find(id);
        if (employee == null) {
            log.warn("employee with id {} not found", id);
            return false;
        }
        //generate random password for employee
        String randomPassword = SecurityUtils.randomPassword(SecurityLevel.GOOD);
        employee.password(randomPassword);
        employee.setNeedChangePassword(true);
        //send sms
        
        smsService.sendMessage(DTOUtils.getClientDTO(client),
                               SMSType.CREDITMANAGER_RESET_PASSWORD,
                               employee.getMobile(),
                               randomPassword);
        markEmployeeDirty();
        return true;
         
    }

    @Override
    public Set<Privilege> resolvePrivileges(String clientCode, String id) {
        Set<Privilege> result = new HashSet<>();
        for (Role role : getRolesForEmployee(clientCode, id)) {
            result.addAll(role.getPrivileges());
        }
        return result;
    }

    @Override
    public com.creditcloud.model.client.Employee findById(String clientCode, String id) {
        Employee employee = employeeDAO.find(id);
        if (employee != null && employee.getClientCode().equalsIgnoreCase(clientCode)) {
            return DTOUtils.getEmployeeDTO(employee);
        } else {
            log.warn("Employee {} not available or clientCode not match", id);
            return null;
        }
    }

    @Override
    public com.creditcloud.model.client.Employee findByEmpId(String clientCode, String empId) {
        return DTOUtils.getEmployeeDTO(employeeDAO.findByEmployeeId(clientCode, empId));
    }

    @Override
    public com.creditcloud.model.client.Employee findByLoginName(String clientCode, String loginName) {
        return DTOUtils.getEmployeeDTO(employeeDAO.findByLoginName(clientCode, loginName));
    }

    @Override
    public com.creditcloud.model.client.Employee findByIdNumber(String clientCode, String IdNumber) {
        return DTOUtils.getEmployeeDTO(employeeDAO.findByIdNumber(clientCode, IdNumber.toUpperCase()));
    }

    @Override
    public com.creditcloud.model.client.Employee findByMobile(String clientCode, String mobile) {
        return DTOUtils.getEmployeeDTO(employeeDAO.findByMobile(clientCode, mobile));
    }

    @Override
    public boolean removeById(String clientCode, String id) {
        boolean result = false;
        Employee employee = employeeDAO.find(id);
        if (employee != null && employee.getClientCode().equalsIgnoreCase(clientCode)) {
            employeeDAO.remove(employee);
            result = true;
        } else {
            log.warn("Remove employee {} for client {} failed.", id, clientCode);
        }
        markEmployeeDirty();
        return result;
    }

    private String getLoginInfo(Map<String, String> loginInfo, Source source) {
        switch (source) {
            /**
             * 对于employee登录方式只可能是back的CreditManager和mobile，
             * back端返回ip,mobile端返回imei号
             */
            case BACK:
                return loginInfo.get(LoginConstant.IP);
            case MOBILE:
                return loginInfo.get(LoginConstant.IMEI);
            default:
                throw new IllegalArgumentException(String.format("Invalid login source %s for employee.", source));
        }
    }

    @Override
    public Collection<Role> getRolesForEmployee(String clientCode, String id) {
        Collection<Role> result;
        Employee employee = employeeDAO.find(id);
        if (employee != null && !employee.getRoles().isEmpty()) {
            result = new ArrayList<>();
            for (com.creditcloud.client.entities.Role role : employee.getRoles()) {
                Role roleDTO = DTOUtils.getRoleDTO(role, false);
                if (roleDTO != null) {
                    result.add(roleDTO);
                } else {
                    log.error("Role {} for Employee {} turn to a null DTO", role, employee);
                }
            }
        } else {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public Collection<Role> getAllRoles(String clientCode) {
        long startTime = System.currentTimeMillis();
        log.debug("getAllRoles called by client {}", clientCode);
        if (roleDirty) {
            updateRoleCache(clientCode, false);
        }

        log.debug("getAllRoles done in {}ms, return {} roles", System.currentTimeMillis() - startTime, roleCache.size());
        return new ArrayList<>(roleCache.values());
    }

    @Override
    public Role getRoleById(String clientCode, String roleId, boolean includeMembers) {
        return DTOUtils.getRoleDTO(roleDAO.find(roleId, includeMembers), includeMembers);
    }

    @Override
    public void setRoles(String clientCode, String employeeId, String... roleIds) {
        Employee employee = employeeDAO.find(employeeId);
        if (employee != null) {
            //先清空
            for (com.creditcloud.client.entities.Role originRole : employee.getRoles()) {
                originRole.getMembers().remove(employee);
            }
            employee.getRoles().clear();
            //再添加
            for (String roleId : roleIds) {
                com.creditcloud.client.entities.Role role = roleDAO.find(roleId);
                if (role != null) {
                    employee.getRoles().add(role);
                    role.getMembers().add(employee);
                } else {
                    log.error("Can't find role by Id {}", roleId);
                }
            }
        }
        markEmployeeDirty();
        markRoleDirty();
    }

    @Override
    public void saveRole(String clientCode, Role role) {
        com.creditcloud.client.entities.Role entityRole;
        if (role.getId() != null && roleDAO.find(role.getId()) != null) {
            entityRole = roleDAO.find(role.getId());
            //update properties
            entityRole.setName(role.getName());
            entityRole.setDescription(role.getDescription());
            entityRole.setClientCode(clientCode);
            entityRole.setPrivileges(role.getPrivileges());
            roleDAO.edit(entityRole);
        } else {
            entityRole = new com.creditcloud.client.entities.Role();
            entityRole.setName(role.getName());
            entityRole.setDescription(role.getDescription());
            entityRole.setClientCode(clientCode);
            entityRole.setPrivileges(role.getPrivileges());
            roleDAO.create(entityRole);
        }
        markRoleDirty();
    }

    @Override
    public int countByClient(String clientCode) {
        if (employeeDirty) {
            updateEmployeeCache(clientCode);
        }
        return employeeCache.size();
    }

    @Override
    public void enableEmployee(String clientCode, String employeeId) {
        Employee employee = employeeDAO.find(employeeId);
        if (employee != null) {
            //这里设置最后登录时间为当前是为了刷新登录失败记录的统计时段，相当于变相给用户若干次重新登录的（错误）机会
            employee.setLastLoginDate(new Date());
            employee.setEnabled(true);
            markEmployeeDirty();
        }
    }

    @Override
    public void disableEmployee(String clientCode, String employeeId) {
        Employee employee = employeeDAO.find(employeeId);
        if (employee != null) {
            employee.setEnabled(false);
            markEmployeeDirty();
        }
    }

    private void markEmployeeDirty() {
        employeeDirty = true;
    }

    private void markRoleDirty() {
        roleDirty = true;
    }
}
