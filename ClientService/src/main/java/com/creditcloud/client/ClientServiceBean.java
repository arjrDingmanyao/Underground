/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.client.entities.Client;
import com.creditcloud.client.entities.dao.BranchDAO;
import com.creditcloud.client.entities.dao.ClientDAO;
import com.creditcloud.client.entities.dao.EmployeeDAO;
import com.creditcloud.client.utils.DTOUtils;
import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.constant.ImageConstant;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author martin
 */
@Remote
@Stateless
public class ClientServiceBean extends BaseBean implements ClientService {

    @Inject
    Logger logger;

    @EJB
    ClientDAO clientDAO;

    @EJB
    BranchDAO branchDAO;

    @EJB
    EmployeeDAO employeeDAO;

    @Override
    public com.creditcloud.model.client.Client getClient(String clientCode) {
        Client client = clientDAO.getClientByCode(clientCode);
        if (client != null) {
            return DTOUtils.getClientDTO(client);
        } else {
            com.creditcloud.model.client.Client result = new com.creditcloud.model.client.Client();
            result.setCode(clientCode);
            result.setLocale(Locale.SIMPLIFIED_CHINESE);
            result.setUrl("www.creditcloud.com");
            result.setName("-");
            result.setShortName("-");
            saveClient(result);
            logger.info("Created default Client record.[clientCode={}]", clientCode);
            return result;
        }
    }

    @Override
    public void saveClient(com.creditcloud.model.client.Client client) {
        try {
            getValidatorWrapper().tryValidate(client);
        } catch (InvalidException ex) {
            logger.error("Client is not valid.\n{}\n{}", client.toString(), ex.getMessage());
            return;
        } catch (Exception ex) {
            logger.error("Can't save Client: {}", client == null ? "null" : client.toString(), ex);
            return;
        }
        Client clientEntity = clientDAO.getClientByCode(client.getCode());
        if (clientEntity == null) {
            clientDAO.create(DTOUtils.toClientEntity(client));
            //生成Client时生成对应的Admin员工
            employeeDAO.createAdminEmployee(client.getCode());
        } else {
            DTOUtils.mergeClientEntity(clientEntity, client);
        }
    }

    @Override
    public String getIcon(String clientCode) {
        //TODO store icon somewhere, in DB or in ImageService?
        return ImageConstant.DEFAULT_LOGO;
    }
}
