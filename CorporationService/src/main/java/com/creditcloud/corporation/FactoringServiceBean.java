/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation;

import com.creditcloud.corporation.api.FactoringService;
import com.creditcloud.corporation.dao.FactoringDAO;
import com.creditcloud.corporation.entities.Factoring;
import com.creditcloud.corporation.local.ApplicationBean;
import com.creditcloud.corporation.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class FactoringServiceBean implements FactoringService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    FactoringDAO factoringDAO;

    @Override
    public com.creditcloud.corporation.factoring.Factoring save(String clientCode, com.creditcloud.corporation.factoring.Factoring factoring) {
        appBean.checkClientCode(clientCode);
        if (factoring == null) {
            logger.debug("factoring shall not be null!");
            return null;
        }
        if (factoring.getId() == null) {
            logger.debug("id can not be null for factoring.\n {}", factoring);
            return null;
        }
        //not found, create new one
        if (factoringDAO.find(factoring.getId()) == null) {
            Factoring entity = factoringDAO.create(DTOUtils.convertFactoring(factoring));
            com.creditcloud.corporation.factoring.Factoring result = DTOUtils.getFactoring(entity);
            logger.debug("new factoring created.\n{}", result);
            return result;
        }
        logger.debug("update factoring.\n {}", factoring);
        Factoring result = factoringDAO.find(factoring.getId());
        result.setCentralBankRegisterNo(factoring.getCentralBankRegisterNo());
        result.setDebtDescription(factoring.getDebtDescription());
        result.setDescription(factoring.getDescription());
        result.setFactoringCorporation(factoring.getFactoringCorporation());
        result.setFinanceCorporation(factoring.getFinanceCorporation());
        result.setReceivables(factoring.getReceivables());
        result.setType(factoring.getType());
        result.setTimeLastUpdated(new Date());
        result.setAntiDescription(factoring.getAntiDescription());
        result.setHistoryDescription(factoring.getHistoryDescription());
        factoringDAO.edit(result);
        return DTOUtils.getFactoring(result);
    }

    @Override
    public com.creditcloud.corporation.factoring.Factoring getById(String clientCode, String Id) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFactoring(factoringDAO.find(Id));
    }

    @Override
    public PagedResult<com.creditcloud.corporation.factoring.Factoring> listByFactoringCorporation(String clientCode, String factoringCorporation, PageInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<Factoring> factorings = factoringDAO.listByFactoringCorporation(factoringCorporation, info);
        List<com.creditcloud.corporation.factoring.Factoring> result = new ArrayList<>(factorings.getResults().size());
        for (Factoring factoring : factorings.getResults()) {
            result.add(DTOUtils.getFactoring(factoring));
        }
        return new PagedResult<>(result, factorings.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.corporation.factoring.Factoring> listByFinanceCorporation(String clientCode, String financeCorporation, PageInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<Factoring> factorings = factoringDAO.listByFinanceCorporation(financeCorporation, info);
        List<com.creditcloud.corporation.factoring.Factoring> result = new ArrayList<>(factorings.getResults().size());
        for (Factoring factoring : factorings.getResults()) {
            result.add(DTOUtils.getFactoring(factoring));
        }
        return new PagedResult<>(result, factorings.getTotalSize());
    }

}
