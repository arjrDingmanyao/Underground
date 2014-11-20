/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client;

import com.creditcloud.client.api.BranchService;
import com.creditcloud.client.entities.Branch;
import com.creditcloud.client.entities.Employee;
import com.creditcloud.client.entities.dao.BranchDAO;
import com.creditcloud.client.entities.dao.EmployeeDAO;
import com.creditcloud.client.utils.DTOUtils;
import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.common.validation.InvalidException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class BranchServiceBean extends BaseBean implements BranchService {

    @EJB
    BranchDAO branchDAO;

    @EJB
    EmployeeDAO employeeDAO;

    @Override
    public com.creditcloud.model.client.Branch saveBranch(String clientCode, com.creditcloud.model.client.Branch branch) {
        try {
            getValidatorWrapper().tryValidate(branch);
            Employee principal = employeeDAO.find(branch.getPrincipal().getId());
            if (principal == null) {
                log.warn("principal {} not found for branch {} to save", branch.getPrincipal().getId(), branch.getId());
                return null;
            }
            Employee contact = employeeDAO.find(branch.getContactPerson().getId());
            if (contact == null) {
                log.warn("contact {} not found for branch {} to save", branch.getContactPerson().getId(), branch.getId());
                return null;
            }
            Branch branchEntity = DTOUtils.convertBranchDTO(branch, principal, contact);
            if (branch.getId() == null || branchDAO.find(branch.getId()) == null) {
                return DTOUtils.getBranchDTO(branchDAO.create(branchEntity));
            } else {
                branchEntity.setTimeLastUpdated(new Date());
                branchDAO.edit(branchEntity);
                return DTOUtils.getBranchDTO(branchDAO.find(branch.getId()));
            }
        } catch (InvalidException ex) {
            log.error("Branch is not valid.\n{},violation={}", branch.toString(), ex.getViolations());
        } catch (Exception ex) {
            log.error("Can't save Branch: {}", branch == null ? "null" : branch.toString(), ex);
        }
        return null;
    }

    @Override
    public com.creditcloud.model.client.Branch getById(String clientCode, String id) {
        Branch result = branchDAO.find(id);
        return DTOUtils.getBranchDTO(result);
    }

    @Override
    public List<com.creditcloud.model.client.Branch> listByClient(String clientCode) {
        List<Branch> branches = branchDAO.listByClient(clientCode);
        List<com.creditcloud.model.client.Branch> result = new ArrayList<>(branches.size());
        for (Branch branch : branches) {
            result.add(DTOUtils.getBranchDTO(branch));
        }
        return result;
    }

    @Override
    public com.creditcloud.model.client.Branch getByCode(String clientCode, String code) {
        Branch branch = branchDAO.getByCode(clientCode, code);
        return DTOUtils.getBranchDTO(branch);
    }

    @Override
    public com.creditcloud.model.client.Branch getByName(String clientCode, String name) {
        Branch branch = branchDAO.getByName(clientCode, name);
        return DTOUtils.getBranchDTO(branch);
    }

    @Override
    public List<com.creditcloud.model.client.Branch> listByPrincipal(String clientCode, String principalEmployeeId) {
        List<Branch> branches = branchDAO.listByPrincipal(principalEmployeeId);
        List<com.creditcloud.model.client.Branch> result = new ArrayList<>(branches.size());
        for (Branch branch : branches) {
            result.add(DTOUtils.getBranchDTO(branch));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.client.Branch> listByContact(String clientCode, String contactEmployeeId) {
        List<Branch> branches = branchDAO.listByContact(contactEmployeeId);
        List<com.creditcloud.model.client.Branch> result = new ArrayList<>(branches.size());
        for (Branch branch : branches) {
            result.add(DTOUtils.getBranchDTO(branch));
        }
        return result;
    }
}
